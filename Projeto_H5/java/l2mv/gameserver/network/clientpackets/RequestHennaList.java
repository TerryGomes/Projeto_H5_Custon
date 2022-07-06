package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.HennaEquipList;

public class RequestHennaList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		// readD(); - unknown
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		player.sendPacket(new HennaEquipList(player));
	}
}