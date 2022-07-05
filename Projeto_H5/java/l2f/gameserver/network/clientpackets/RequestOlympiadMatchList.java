package l2f.gameserver.network.clientpackets;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.olympiad.Olympiad;
import l2f.gameserver.network.serverpackets.ExReceiveOlympiad;
import l2f.gameserver.network.serverpackets.components.SystemMsg;

public class RequestOlympiadMatchList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		// trigger
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		if (!Olympiad.inCompPeriod() || Olympiad.isOlympiadEnd())
		{
			player.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
			return;
		}

		if (player.isInOlympiadMode())
		{
			return;
		}

		player.sendPacket(new ExReceiveOlympiad.MatchList());
	}
}
