package l2f.gameserver.network.serverpackets.components;

import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.L2GameServerPacket;

public interface IStaticPacket
{
	L2GameServerPacket packet(Player player);
}
