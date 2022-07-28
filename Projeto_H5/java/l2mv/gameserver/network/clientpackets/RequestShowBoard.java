package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.handler.bbs.CommunityBoardManager;
import l2mv.gameserver.handler.bbs.ICommunityBoardHandler;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

public class RequestShowBoard extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _unknown;

	/**
	 * packet type id 0x5E
	 *
	 * sample
	 *
	 * 5E
	 * 01 00 00 00
	 *
	 * format:		cd
	 */
	@Override
	public void readImpl()
	{
		this._unknown = this.readD();
	}

	@Override
	public void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null || activeChar.isBlocked() || activeChar.isCursedWeaponEquipped())
		{
			return;
		}
		activeChar.isntAfk();

		if (Config.COMMUNITYBOARD_ENABLED && !activeChar.isJailed())
		{
			ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler(Config.BBS_DEFAULT);
			if (handler != null)
			{
				handler.onBypassCommand(activeChar, Config.BBS_DEFAULT);
			}
		}
		else
		{
			activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE));
		}
	}
}
