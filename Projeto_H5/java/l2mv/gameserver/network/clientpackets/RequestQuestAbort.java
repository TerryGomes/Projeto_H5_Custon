package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.instancemanager.QuestManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;

public class RequestQuestAbort extends L2GameClientPacket
{
	private int _questID;

	@Override
	protected void readImpl()
	{
		_questID = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		Quest quest = QuestManager.getQuest(_questID);
		if (activeChar == null || quest == null || activeChar.isBlocked() || !quest.canAbortByPacket())
		{
			return;
		}

		QuestState qs = activeChar.getQuestState(quest.getClass());
		if (qs != null && !qs.isCompleted())
		{
			qs.abortQuest();
		}
	}
}