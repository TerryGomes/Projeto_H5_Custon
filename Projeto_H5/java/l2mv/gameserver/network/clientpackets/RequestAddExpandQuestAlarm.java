package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.instancemanager.QuestManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.ExQuestNpcLogList;

/**
 * @author VISTALL
 * @date 14:47/26.02.2011
 */
public class RequestAddExpandQuestAlarm extends L2GameClientPacket
{
	private int _questId;

	@Override
	protected void readImpl()
	{
		_questId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		Quest quest = QuestManager.getQuest(_questId);
		if (quest == null)
		{
			return;
		}

		QuestState state = player.getQuestState(quest.getClass());
		if (state == null)
		{
			return;
		}

		player.sendPacket(new ExQuestNpcLogList(state));
	}
}
