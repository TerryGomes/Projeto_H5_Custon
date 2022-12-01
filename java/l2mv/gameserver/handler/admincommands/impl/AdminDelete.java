package l2mv.gameserver.handler.admincommands.impl;

import org.apache.commons.lang3.math.NumberUtils;

import l2mv.gameserver.Config;
import l2mv.gameserver.handler.admincommands.IAdminCommandHandler;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Spawner;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.tables.SpawnTable;

public class AdminDelete implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_delete
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (!activeChar.getPlayerAccess().CanEditNPC)
		{
			return false;
		}

		switch (command)
		{
		case admin_delete:
			GameObject obj = wordList.length == 1 ? activeChar.getTarget() : GameObjectsStorage.getNpc(NumberUtils.toInt(wordList[1]));
			if (obj != null && obj.isNpc())
			{
				NpcInstance target = (NpcInstance) obj;
				if (Config.SAVE_GM_SPAWN)
				{
					SpawnTable.getInstance().deleteSpawn(target.getSpawnedLoc(), target.getNpcId());
				}
				target.deleteMe();

				Spawner spawn = target.getSpawn();

				if (spawn != null)
				{
					spawn.stopRespawn();
				}
			}
			else
			{
				activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			}
			break;
		}

		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}