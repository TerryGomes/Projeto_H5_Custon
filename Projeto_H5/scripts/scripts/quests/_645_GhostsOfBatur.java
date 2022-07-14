package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _645_GhostsOfBatur extends Quest implements ScriptFile
{
	// Npc
	private static final int Karuda = 32017;
	// Items
	private static final int CursedBurialItems = 14861;
	// Mobs
	private static final int[] MOBS =
	{
		22703,
		22704,
		22705,
		22706,
		22707
	};

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

	public _645_GhostsOfBatur()
	{
		super(true);

		addStartNpc(Karuda);
		for (int i : MOBS)
		{
			addKillId(i);
		}
		addQuestItem(CursedBurialItems);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("karuda_q0645_0103.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		if (cond == 0)
		{
			if (st.getPlayer().getLevel() < 61)
			{
				htmltext = "karuda_q0645_0102.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "karuda_q0645_0101.htm";
			}
		}
		else
		{
			if (cond == 2)
			{
				st.setCond(1);
			}

			if (st.getQuestItemsCount(CursedBurialItems) == 0)
			{
				htmltext = "karuda_q0645_0106.htm";
			}
			else
			{
				htmltext = "karuda_q0645_0105.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getCond() > 0)
		{
			if (Rnd.chance(15))
			{
				st.giveItems(CursedBurialItems, 1, true);
			}
		}
		return null;
	}
}