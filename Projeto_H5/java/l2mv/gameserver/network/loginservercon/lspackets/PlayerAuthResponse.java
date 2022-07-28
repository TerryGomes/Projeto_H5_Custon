package l2mv.gameserver.network.loginservercon.lspackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.cache.Msg;
import l2mv.gameserver.dao.AccountBonusDAO;
import l2mv.gameserver.database.merge.DataMerge;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.actor.instances.player.Bonus;
import l2mv.gameserver.network.GameClient;
import l2mv.gameserver.network.loginservercon.AuthServerCommunication;
import l2mv.gameserver.network.loginservercon.ReceivablePacket;
import l2mv.gameserver.network.loginservercon.SessionKey;
import l2mv.gameserver.network.loginservercon.gspackets.PlayerInGame;
import l2mv.gameserver.network.serverpackets.CharacterSelectionInfo;
import l2mv.gameserver.network.serverpackets.LoginFail;
import l2mv.gameserver.network.serverpackets.ServerClose;

public class PlayerAuthResponse extends ReceivablePacket
{
	private String account;
	private boolean authed;
	private int playOkId1;
	private int playOkId2;
	private int loginOkId1;
	private int loginOkId2;
	private double bonus;
	private int bonusExpire;
	private int _serverId;
	private String hwid;

	@Override
	public void readImpl()
	{
		this.account = this.readS();
		this.authed = this.readC() == 1;
		if (this.authed)
		{
			this.playOkId1 = this.readD();
			this.playOkId2 = this.readD();
			this.loginOkId1 = this.readD();
			this.loginOkId2 = this.readD();
			this.bonus = this.readF();
			this.bonusExpire = this.readD();
		}
		this._serverId = this.readD();
		this.hwid = this.readS();
	}

	@Override
	protected void runImpl()
	{
		final SessionKey skey = new SessionKey(this.loginOkId1, this.loginOkId2, this.playOkId1, this.playOkId2);
		final GameClient client = AuthServerCommunication.getInstance().removeWaitingClient(this.account);
		if (client == null)
		{
			return;
		}

		if (this.authed && client.getSessionKey().equals(skey))
		{
			client.setAuthed(true);
			client.setState(GameClient.GameClientState.AUTHED);
			switch (Config.SERVICES_RATE_TYPE)
			{
			case Bonus.NO_BONUS:
				this.bonus = 0;
				this.bonusExpire = 0;
				break;
			case Bonus.BONUS_GLOBAL_ON_GAMESERVER:
				int[] bonuses = AccountBonusDAO.getInstance().select(this.account);
				this.bonus = bonuses[0];
				this.bonusExpire = bonuses[1];
				break;
			}
			client.setBonus((int) this.bonus);
			client.setBonusExpire(this.bonusExpire);
			client.setServerId(this._serverId);

			if (ConfigHolder.getBool("EnableMerge"))
			{
				DataMerge.getInstance().checkMergeToComplete(this.account);
			}

			GameClient oldClient = AuthServerCommunication.getInstance().addAuthedClient(client);
			if (oldClient != null)
			{
				oldClient.setAuthed(false);
				final Player activeChar = oldClient.getActiveChar();
				if (activeChar != null)
				{
					activeChar.sendPacket(Msg.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT);
					activeChar.logout();
				}
				else
				{
					oldClient.close(ServerClose.STATIC);
				}
			}

			this.sendPacket(new PlayerInGame(client.getLogin()));

			CharacterSelectionInfo csi = new CharacterSelectionInfo(client.getLogin(), client.getSessionKey().playOkID1);
			client.sendPacket(csi);
			client.setCharSelection(csi.getCharInfo());
		}
		else
		{
			client.close(new LoginFail(LoginFail.ACCESS_FAILED_TRY_LATER));
		}
	}
}