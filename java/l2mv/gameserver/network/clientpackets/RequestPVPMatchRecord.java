package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.TeamType;
import l2mv.gameserver.model.entity.events.impl.UndergroundColiseumBattleEvent;
import l2mv.gameserver.network.serverpackets.ExPVPMatchRecord;

public class RequestPVPMatchRecord extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{

	}

	@Override
	protected void runImpl()
	{
		final Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		final UndergroundColiseumBattleEvent battleEvent = player.getEvent(UndergroundColiseumBattleEvent.class);
		if (battleEvent == null)
		{
			return;
		}

		player.sendPacket(new ExPVPMatchRecord(ExPVPMatchRecord.UPDATE, TeamType.NONE, battleEvent));
	}
}