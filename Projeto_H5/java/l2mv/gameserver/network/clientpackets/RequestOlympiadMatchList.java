package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.olympiad.Olympiad;
import l2mv.gameserver.network.serverpackets.ExReceiveOlympiad;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

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
