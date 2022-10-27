package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.network.serverpackets.QuestList;

public class RequestQuestList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		this.sendPacket(new QuestList(this.getClient().getActiveChar()));
	}
}