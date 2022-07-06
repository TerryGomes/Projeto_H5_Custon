package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2mv.gameserver.model.quest.QuestNpcLogInfo;
import l2mv.gameserver.model.quest.QuestState;

/**
 * @author VISTALL
 * @date 14:50/26.02.2011
 */
public class ExQuestNpcLogList extends L2GameServerPacket
{
	private int _questId;
	private List<int[]> _logList = Collections.emptyList();

	public ExQuestNpcLogList(QuestState state)
	{
		_questId = state.getQuest().getQuestIntId();
		int cond = state.getCond();
		List<QuestNpcLogInfo> vars = state.getQuest().getNpcLogList(cond);
		if (vars == null)
		{
			return;
		}

		_logList = new ArrayList<int[]>(vars.size());
		for (QuestNpcLogInfo entry : vars)
		{
			int[] i = new int[2];
			i[0] = entry.getNpcIds()[0] + 1000000;
			i[1] = state.getInt(entry.getVarName());
			_logList.add(i);
		}
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0xC5);
		writeD(_questId);
		writeC(_logList.size());
		for (int i = 0; i < _logList.size(); i++)
		{
			int[] values = _logList.get(i);
			writeD(values[0]);
			writeC(0); // npc index?
			writeD(values[1]);
		}
	}
}
