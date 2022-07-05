package l2f.gameserver.network.clientpackets;

import l2f.gameserver.data.xml.holder.EventHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.events.EventType;
import l2f.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2f.gameserver.network.serverpackets.ExReplyDominionInfo;
import l2f.gameserver.network.serverpackets.ExShowOwnthingPos;

public class RequestExDominionInfo extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		activeChar.sendPacket(new ExReplyDominionInfo());

		DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
		if (runnerEvent.isInProgress())
		{
			activeChar.sendPacket(new ExShowOwnthingPos());
		}
	}
}