package quests;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _641_AttackSailren extends Quest implements ScriptFile
{
	// NPC
	private static int STATUE = 32109;

	// MOBS
	private static int VEL1 = 22196;
	private static int VEL2 = 22197;
	private static int VEL3 = 22198;
	private static int VEL4 = 22218;
	private static int VEL5 = 22223;
	private static int PTE = 22199;
	// items
	private static int FRAGMENTS = 8782;
	private static int GAZKH = 8784;

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

	public _641_AttackSailren()
	{
		super(true);

		addStartNpc(STATUE);

		addKillId(VEL1);
		addKillId(VEL2);
		addKillId(VEL3);
		addKillId(VEL4);
		addKillId(VEL5);
		addKillId(PTE);

		addQuestItem(FRAGMENTS);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("statue_of_shilen_q0641_05.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("statue_of_shilen_q0641_08.htm"))
		{
			st.playSound(SOUND_FINISH);
			st.takeItems(FRAGMENTS, -1);
			st.giveItems(GAZKH, 1);
			st.exitCurrentQuest(true);
			st.unset("cond");
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		switch (cond)
		{
		case 0:
		{
			QuestState qs = st.getPlayer().getQuestState(_126_IntheNameofEvilPart2.class);
			if (qs == null || !qs.isCompleted())
			{
				htmltext = "statue_of_shilen_q0641_02.htm";
			}
			else if (st.getPlayer().getLevel() >= 77)
			{
				htmltext = "statue_of_shilen_q0641_01.htm";
			}
			else
			{
				st.exitCurrentQuest(true);
			}
			break;
		}
		case 1:
			htmltext = "statue_of_shilen_q0641_05.htm";
			break;
		case 2:
			htmltext = "statue_of_shilen_q0641_07.htm";
			break;
		default:
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getQuestItemsCount(FRAGMENTS) < 30)
		{
			st.giveItems(FRAGMENTS, 1);
			if (st.getQuestItemsCount(FRAGMENTS) == 30)
			{
				st.playSound(SOUND_MIDDLE);
				st.setCond(2);
				st.setState(STARTED);
			}
			else
			{
				st.playSound(SOUND_ITEMGET);
			}
		}
		return null;
	}
}