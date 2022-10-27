package l2mv.gameserver.network.serverpackets.components;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.L2GameServerPacket;

public interface IStaticPacket
{
	L2GameServerPacket packet(Player player);
}
