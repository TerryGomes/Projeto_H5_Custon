package quests;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _636_TruthBeyond extends Quest implements ScriptFile
{
	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}

	// Npc
	public final int ELIYAH = 31329;
	public final int FLAURON = 32010;

	// Items
	public final int MARK = 8067;
	public final int VISITORSMARK = 8064;

	public _636_TruthBeyond()
	{
		super(false);

		addStartNpc(ELIYAH);
		addTalkId(FLAURON);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equals("priest_eliyah_q0636_05.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("falsepriest_flauron_q0636_02.htm"))
		{
			st.playSound(SOUND_FINISH);
			st.giveItems(VISITORSMARK, 1);
			st.exitCurrentQuest(true);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getCond();
		if (npcId == ELIYAH && cond == 0)
		{
			if (st.getQuestItemsCount(VISITORSMARK) == 0 && st.getQuestItemsCount(MARK) == 0)
			{
				if (st.getPlayer().getLevel() > 72)
				{
					htmltext = "priest_eliyah_q0636_01.htm";
				}
				else
				{
					htmltext = "priest_eliyah_q0636_03.htm";
					st.exitCurrentQuest(true);
				}
			}
			else
			{
				htmltext = "priest_eliyah_q0636_06.htm";
			}
		}
		else if (npcId == FLAURON)
		{
			if (cond == 1)
			{
				htmltext = "falsepriest_flauron_q0636_01.htm";
				st.setCond(2);
			}
			else
			{
				htmltext = "falsepriest_flauron_q0636_03.htm";
			}
		}
		return htmltext;
	}
}