package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.handler.bbs.CommunityBoardManager;
import l2mv.gameserver.handler.bbs.ICommunityBoardHandler;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

/**
 * Format SSSSSS
 */
public class RequestBBSwrite extends L2GameClientPacket
{
	private String _url;
	private String _arg1;
	private String _arg2;
	private String _arg3;
	private String _arg4;
	private String _arg5;

	@Override
	public void readImpl()
	{
		_url = readS();
		_arg1 = readS();
		_arg2 = readS();
		_arg3 = readS();
		_arg4 = readS();
		_arg5 = readS();
	}

	@Override
	public void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler(_url);
		if (handler != null)
		{
			if (!Config.COMMUNITYBOARD_ENABLED)
			{
				activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE));
			}
			else
			{
				handler.onWriteCommand(activeChar, _url, _arg1, _arg2, _arg3, _arg4, _arg5);
			}
		}
	}
}