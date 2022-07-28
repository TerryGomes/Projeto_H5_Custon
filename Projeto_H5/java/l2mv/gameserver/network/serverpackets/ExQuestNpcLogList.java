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
		this._questId = state.getQuest().getQuestIntId();
		int cond = state.getCond();
		List<QuestNpcLogInfo> vars = state.getQuest().getNpcLogList(cond);
		if (vars == null)
		{
			return;
		}

		this._logList = new ArrayList<int[]>(vars.size());
		for (QuestNpcLogInfo entry : vars)
		{
			int[] i = new int[2];
			i[0] = entry.getNpcIds()[0] + 1000000;
			i[1] = state.getInt(entry.getVarName());
			this._logList.add(i);
		}
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xC5);
		this.writeD(this._questId);
		this.writeC(this._logList.size());
		for (int i = 0; i < this._logList.size(); i++)
		{
			int[] values = this._logList.get(i);
			this.writeD(values[0]);
			this.writeC(0); // npc index?
			this.writeD(values[1]);
		}
	}
}
