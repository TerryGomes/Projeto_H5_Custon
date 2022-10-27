package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _368_TrespassingIntoTheSacredArea extends Quest implements ScriptFile
{
	// NPCs
	private static int RESTINA = 30926;
	// Items
	private static int BLADE_STAKATO_FANG = 5881;
	// Chances
	private static int BLADE_STAKATO_FANG_BASECHANCE = 10;

	public _368_TrespassingIntoTheSacredArea()
	{
		super(false);
		addStartNpc(RESTINA);
		for (int Blade_Stakato_id = 20794; Blade_Stakato_id <= 20797; Blade_Stakato_id++)
		{
			addKillId(Blade_Stakato_id);
		}
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		if (npc.getNpcId() != RESTINA)
		{
			return htmltext;
		}
		if (st.getState() == CREATED)
		{
			if (st.getPlayer().getLevel() < 36)
			{
				htmltext = "30926-00.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "30926-01.htm";
				st.setCond(0);
			}
		}
		else
		{
			long _count = st.getQuestItemsCount(BLADE_STAKATO_FANG);
			if (_count > 0)
			{
				htmltext = "30926-04.htm";
				st.takeItems(BLADE_STAKATO_FANG, -1);
				st.giveItems(ADENA_ID, _count * 2250);
				st.playSound(SOUND_MIDDLE);
			}
			else
			{
				htmltext = "30926-03.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int _state = st.getState();
		if (event.equalsIgnoreCase("30926-02.htm") && _state == CREATED)
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30926-05.htm") && _state == STARTED)
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return event;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if (qs.getState() != STARTED)
		{
			return null;
		}

		if (Rnd.chance(npc.getNpcId() - 20794 + BLADE_STAKATO_FANG_BASECHANCE))
		{
			qs.giveItems(BLADE_STAKATO_FANG, 1);
			qs.playSound(SOUND_ITEMGET);
		}
		return null;
	}

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
}
