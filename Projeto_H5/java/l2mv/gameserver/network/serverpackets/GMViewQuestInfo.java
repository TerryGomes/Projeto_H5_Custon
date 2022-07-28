package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;

public class GMViewQuestInfo extends L2GameServerPacket
{
	private final Player _cha;

	public GMViewQuestInfo(Player cha)
	{
		this._cha = cha;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x99);
		this.writeS(this._cha.getName());

		Quest[] quests = this._cha.getAllActiveQuests();

		if (quests.length == 0)
		{
			this.writeH(0);
			this.writeH(0);
			return;
		}

		this.writeH(quests.length);
		for (Quest q : quests)
		{
			this.writeD(q.getQuestIntId());
			QuestState qs = this._cha.getQuestState(q.getName());
			this.writeD(qs == null ? 0 : qs.getInt("cond"));
		}

		this.writeH(0); // количество элементов типа: ddQd , как-то связано с предметами
	}
}