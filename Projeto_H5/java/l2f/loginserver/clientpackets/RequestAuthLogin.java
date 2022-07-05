package l2f.loginserver.clientpackets;

import javax.crypto.Cipher;

import l2f.loginserver.Config;
import l2f.loginserver.GameServerManager;
import l2f.loginserver.IpBanManager;
import l2f.loginserver.L2LoginClient;
import l2f.loginserver.accounts.Account;
import l2f.loginserver.accounts.SessionManager;
import l2f.loginserver.accounts.SessionManager.Session;
import l2f.loginserver.crypt.PasswordHash;
import l2f.loginserver.gameservercon.GameServer;
import l2f.loginserver.gameservercon.SendablePacket;
import l2f.loginserver.gameservercon.lspackets.GetAccountInfo;
import l2f.loginserver.gameservercon.lspackets.OnWrongAccountPassword;
import l2f.loginserver.merge.AccountMerge;
import l2f.loginserver.serverpackets.LoginFail;
import l2f.loginserver.serverpackets.LoginFail.LoginFailReason;
import l2f.loginserver.serverpackets.LoginOk;

/**
 * Format: b[128]ddddddhc
 * b[128]: the rsa encrypted block with the login an password
 */
public class RequestAuthLogin extends L2LoginClientPacket
{
	private final byte[] _raw = new byte[128];

	@Override
	protected void readImpl()
	{
		readB(_raw);
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readH();
		readC();
	}

	@SuppressWarnings("unused")
	@Override
	protected void runImpl() throws Exception
	{
		final L2LoginClient client = getClient();
		byte[] decrypted;
		try
		{
			Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
			rsaCipher.init(Cipher.DECRYPT_MODE, client.getRSAPrivateKey());
			decrypted = rsaCipher.doFinal(_raw, 0x00, 0x80);
		}
		catch (Exception e)
		{
			client.closeNow(true);
			return;
		}

		String user = new String(decrypted, 0x5E, 14).trim();
		user = user.toLowerCase();
		String password = new String(decrypted, 0x6C, 16).trim();
		int ncotp = decrypted[0x7c];
		ncotp |= decrypted[0x7d] << 8;
		ncotp |= decrypted[0x7e] << 16;
		ncotp |= decrypted[0x7f] << 24;

		final int currentTime = (int) (System.currentTimeMillis() / 1000L);
		final Account account = new Account(user);
		account.restore();

		String passwordHash = Config.DEFAULT_CRYPT.encrypt(password);

		if (account.getPasswordHash() != null)
		{
			afterConnection(account, passwordHash, password, client, user);
			return;
		}

		boolean any1On = false;
		for (GameServer gs : GameServerManager.getInstance().getGameServers())
		{
			if (gs.isAuthed())
			{
				any1On = true;
			}
		}
		if (!any1On)
		{
			return;
		}

		if (Config.ENABLE_MERGE)
		{
			final Account mergeAccount = AccountMerge.getInstance().tryMergeAccount(user);
			if (mergeAccount != null)
			{
				afterConnection(mergeAccount, passwordHash, password, client, user);
				return;
			}
		}

		if (Config.AUTO_CREATE_ACCOUNTS && user.matches(Config.ANAME_TEMPLATE) && password.matches(Config.APASSWD_TEMPLATE) && !user.equalsIgnoreCase(password))
		{
			account.setAllowedIP("");
			account.setAllowedHwid("");
			account.setPasswordHash(password);
			account.save();
			afterConnection(account, passwordHash, password, client, user);
			return;
		}

		client.close(LoginFailReason.REASON_USER_OR_PASS_WRONG);
	}

	public static void afterConnection(Account account, String passwordHash, String password, L2LoginClient client, String user)
	{
		boolean passwordCorrect = account.getPasswordHash().equals(passwordHash);
		int currentTime = (int) (System.currentTimeMillis() / 1000L);

		if (!passwordCorrect)
		{
			// check if the password is not encrypted by one of the older but supported algorithms
			for (PasswordHash c : Config.LEGACY_CRYPT)
			{
				if (c.compare(password, account.getPasswordHash()))
				{
					passwordCorrect = true;
					account.setPasswordHash(passwordHash);
					break;
				}
			}
		}
		if (password.equals(account.getPasswordHash()))
		{
			passwordCorrect = true;
		}

		if (!IpBanManager.getInstance().tryLogin(client.getIpAddress(), passwordCorrect))
		{
			client.closeNow(false);
			return;
		}

		if (!passwordCorrect)
		{
			_log.warn("IP: " + client.getConnection().getSocket().getInetAddress() + " wrote Wrong Password(Written: " + password + ", Current: " + account.getPasswordHash() + ") Password to Account: " + account.getLogin());
			final SendablePacket wrongPasswordPacket = new OnWrongAccountPassword(account.getLogin(), password);
			for (GameServer gs : GameServerManager.getInstance().getGameServers())
			{
				if (gs.getProtocol() >= 2 && gs.isAuthed())
				{
					gs.sendPacket(wrongPasswordPacket);
				}
			}
			client.close(LoginFail.LoginFailReason.REASON_USER_OR_PASS_WRONG);
			return;
		}

		if ((account.getAccessLevel() < 0) || (account.getBanExpire() > currentTime))
		{
			client.close(LoginFailReason.REASON_ACCESS_FAILED);
			return;
		}

		if (!account.isAllowedIP(client.getIpAddress()))
		{
			client.close(LoginFailReason.REASON_ATTEMPTED_RESTRICTED_IP);
			return;
		}

		for (GameServer gs : GameServerManager.getInstance().getGameServers())
		{
			if (gs.getProtocol() >= 2 && gs.isAuthed())
			{
				gs.sendPacket(new GetAccountInfo(user));
			}
		}

		account.setLastAccess(currentTime);
		account.setLastIP(client.getIpAddress());

		Session session = SessionManager.getInstance().openSession(account);

		client.setAuthed(true);
		client.setLogin(user);
		client.setAccount(account);
		client.setSessionKey(session.getSessionKey());
		client.setState(L2LoginClient.LoginClientState.AUTHED);
		client.sendPacket(new LoginOk(client.getSessionKey()));
	}
}