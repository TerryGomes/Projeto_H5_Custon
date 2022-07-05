package quests;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

/**
 * @see http://www.linedia.ru/wiki/Matras'_Curiosity
 */
public class _132_MatrasCuriosity extends Quest implements ScriptFile
{
	// npc
	private static final int Matras = 32245;

	// monster
	private static final int Ranku = 25542;
	private static final int Demon_Prince = 25540;

	// quest items
	private static final int Rankus_Blueprint = 9800;
	private static final int Demon_Princes_Blueprint = 9801;

	// items
	private static final int Rough_Ore_of_Fire = 10521;
	private static final int Rough_Ore_of_Water = 10522;
	private static final int Rough_Ore_of_Earth = 10523;
	private static final int Rough_Ore_of_Wind = 10524;
	private static final int Rough_Ore_of_Darkness = 10525;
	private static final int Rough_Ore_of_Divinity = 10526;

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

	public _132_MatrasCuriosity()
	{
		super(PARTY_ALL);

		addStartNpc(Matras);

		addKillId(Ranku);
		addKillId(Demon_Prince);

		addQuestItem(new int[]
		{
			Rankus_Blueprint,
			Demon_Princes_Blueprint
		});
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("32245-02.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			String is_given = st.getPlayer().getVar("q132_Rough_Ore_is_given");
			if (is_given != null)
			{
				htmltext = "32245-02a.htm";
			}
			else
			{
				st.getPlayer().setVar("q132_Rough_Ore_is_given", "1", -1);
			}
		}
		else if (event.equalsIgnoreCase("32245-04.htm"))
		{
			st.setCond(3);
			st.setState(STARTED);
			st.startQuestTimer("talk_timer", 10000);
		}
		else if (event.equalsIgnoreCase("talk_timer"))
		{
			htmltext = "Matras wishes to talk to you.";
		}
		else if (event.equalsIgnoreCase("get_reward"))
		{
			st.playSound(SOUND_FINISH);
			st.giveItems(Rough_Ore_of_Fire, 1, false);
			st.giveItems(Rough_Ore_of_Water, 1, false);
			st.giveItems(Rough_Ore_of_Earth, 1, false);
			st.giveItems(Rough_Ore_of_Wind, 1, false);
			st.giveItems(Rough_Ore_of_Darkness, 1, false);
			st.giveItems(Rough_Ore_of_Divinity, 1, false);
			st.giveItems(ADENA_ID, 31210);
			st.exitCurrentQuest(false);
			return null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == Matras)
		{
			if (cond < 1 && st.getPlayer().getLevel() >= 78)
			{ // Квест с 78 уровня, в клиенте опечатка
				htmltext = "32245-01.htm";
			}
			else if (cond == 1)
			{
				htmltext = "32245-02a.htm";
			}
			else if (cond == 2 && st.getQuestItemsCount(Rankus_Blueprint) > 0 && st.getQuestItemsCount(Demon_Princes_Blueprint) > 0)
			{
				htmltext = "32245-03.htm";
			}
			else if (cond == 3)
			{
				if (st.isRunningQuestTimer("talk_timer"))
				{
					htmltext = "32245-04.htm";
				}
				else
				{
					htmltext = "32245-04a.htm";
				}
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getCond() == 1)
		{
			if (npc.getNpcId() == Ranku && st.getQuestItemsCount(Rankus_Blueprint) < 1)
			{
				st.playSound(SOUND_ITEMGET);
				st.playSound(SOUND_MIDDLE);
				st.giveItems(Rankus_Blueprint, 1, false);
			}
			if (npc.getNpcId() == Demon_Prince && st.getQuestItemsCount(Demon_Princes_Blueprint) < 1)
			{
				st.playSound(SOUND_ITEMGET);
				st.playSound(SOUND_MIDDLE);
				st.giveItems(Demon_Princes_Blueprint, 1, false);
			}
			if (st.getQuestItemsCount(Rankus_Blueprint) > 0 && st.getQuestItemsCount(Demon_Princes_Blueprint) > 0)
			{
				st.setCond(2);
				st.setState(STARTED);
			}
		}
		return null;
	}
}