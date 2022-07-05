package l2f.loginserver.gameservercon.lspackets;

import l2f.loginserver.SessionKey;
import l2f.loginserver.accounts.Account;
import l2f.loginserver.accounts.SessionManager.Session;
import l2f.loginserver.gameservercon.SendablePacket;

public class PlayerAuthResponse extends SendablePacket
{
	private final String login;
	private final boolean authed;
	private int playOkID1;
	private int playOkID2;
	private int loginOkID1;
	private int loginOkID2;
	private double bonus;
	private int bonusExpire;
	private int _serverId;
	private String hwid;

	public PlayerAuthResponse(Session session, boolean authed)
	{
		Account account = session.getAccount();
		login = account.getLogin();
		this.authed = authed;
		if (authed)
		{
			final SessionKey skey = session.getSessionKey();
			playOkID1 = skey.playOkID1;
			playOkID2 = skey.playOkID2;
			loginOkID1 = skey.loginOkID1;
			loginOkID2 = skey.loginOkID2;
			bonus = account.getBonus();
			bonusExpire = account.getBonusExpire();
		}
		_serverId = account.getLastServer();
		hwid = account.getAllowedHwid();
	}

	public PlayerAuthResponse(String account)
	{
		login = account;
		authed = false;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x02);
		writeS(login);
		writeC(authed ? 1 : 0);
		if (authed)
		{
			writeD(playOkID1);
			writeD(playOkID2);
			writeD(loginOkID1);
			writeD(loginOkID2);
			writeF(bonus);
			writeD(bonusExpire);
		}
		writeD(_serverId); // Synerge - Send the server id so the client can know to which server he is logged in
		writeS(hwid);
	}
}
