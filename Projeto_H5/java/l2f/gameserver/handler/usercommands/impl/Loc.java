package l2f.gameserver.handler.usercommands.impl;

import l2f.gameserver.handler.usercommands.IUserCommandHandler;
import l2f.gameserver.instancemanager.MapRegionManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.templates.mapregion.RestartArea;

/**
 * Support for /loc command
 */
public class Loc implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		0
	};

	@Override
	public boolean useUserCommand(int id, Player activeChar)
	{
		if (COMMAND_IDS[0] != id)
		{
			return false;
		}

		RestartArea ra = MapRegionManager.getInstance().getRegionData(RestartArea.class, activeChar);
		int msgId = ra != null ? ra.getRestartPoint().get(activeChar.getRace()).getMsgId() : 0;
		if (msgId > 0)
		{
			activeChar.sendPacket(new SystemMessage2(SystemMsg.valueOf(msgId)).addInteger(activeChar.getX()).addInteger(activeChar.getY()).addInteger(activeChar.getZ()));
		}
		else
		{
			activeChar.sendMessage("Current location : " + activeChar.getX() + ", " + activeChar.getY() + ", " + activeChar.getZ());
		}

		return true;
	}

	@Override
	public final int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}