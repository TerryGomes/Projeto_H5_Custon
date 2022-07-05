package l2f.gameserver.tables;

import java.util.ArrayList;
import java.util.List;

import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.L2GameServerPacket;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;

public class GmListTable
{
	public static List<Player> getAllGMs()
	{
		List<Player> gmList = new ArrayList<Player>();
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player.isGM())
			{
				gmList.add(player);
			}
		}

		return gmList;
	}

	public static List<Player> getAllVisibleGMs()
	{
		List<Player> gmList = new ArrayList<Player>();
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player.isGM() && player.getVarInt("gmOnList", 1) == 1)
			{
				gmList.add(player);
			}
		}

		return gmList;
	}

	public static void sendListToPlayer(Player player)
	{
		List<Player> gmList = getAllVisibleGMs();
		if (gmList.isEmpty())
		{
			player.sendPacket(SystemMsg.THERE_ARE_NOT_ANY_GMS_THAT_ARE_PROVIDING_CUSTOMER_SERVICE_CURRENTLY);
			return;
		}

		player.sendPacket(SystemMsg._GM_LIST_);
		for (Player gm : gmList)
		{
			player.sendPacket(new SystemMessage2(SystemMsg.GM_S1).addString(gm.getName()));
		}
	}

	public static void broadcastToGMs(L2GameServerPacket packet)
	{
		for (Player gm : getAllGMs())
		{
			gm.sendPacket(packet);
		}
	}

	public static void broadcastMessageToGMs(String message)
	{
		for (Player gm : getAllGMs())
		{
			gm.sendMessage(message);
		}
	}
}