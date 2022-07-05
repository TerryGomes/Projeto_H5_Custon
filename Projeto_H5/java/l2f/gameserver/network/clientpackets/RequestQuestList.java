package l2f.gameserver.network.clientpackets;

import l2f.gameserver.network.serverpackets.QuestList;

public class RequestQuestList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		sendPacket(new QuestList(getClient().getActiveChar()));
	}
}