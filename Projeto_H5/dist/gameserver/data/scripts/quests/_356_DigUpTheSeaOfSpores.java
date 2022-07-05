package quests;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _356_DigUpTheSeaOfSpores extends Quest implements ScriptFile
{
	// NPC
	private static final int GAUEN = 30717;

	// MOBS
	private static final int SPORE_ZOMBIE = 20562;
	private static final int ROTTING_TREE = 20558;

	// QUEST ITEMS
	private static final int CARNIVORE_SPORE = 5865;
	private static final int HERBIBOROUS_SPORE = 5866;

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

	public _356_DigUpTheSeaOfSpores()
	{
		super(false);
		addStartNpc(GAUEN);

		addKillId(SPORE_ZOMBIE);
		addKillId(ROTTING_TREE);

		addQuestItem(CARNIVORE_SPORE);
		addQuestItem(HERBIBOROUS_SPORE);

	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		long carn = st.getQuestItemsCount(CARNIVORE_SPORE);
		long herb = st.getQuestItemsCount(HERBIBOROUS_SPORE);
		if (event.equalsIgnoreCase("magister_gauen_q0356_06.htm"))
		{
			if (st.getPlayer().getLevel() >= 43)
			{
				st.setCond(1);
				st.setState(STARTED);
				st.playSound(SOUND_ACCEPT);
			}
			else
			{
				htmltext = "magister_gauen_q0356_01.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if ((event.equalsIgnoreCase("magister_gauen_q0356_20.htm") || event.equalsIgnoreCase("magister_gauen_q0356_17.htm")) && carn >= 50 && herb >= 50)
		{
			st.takeItems(CARNIVORE_SPORE, -1);
			st.takeItems(HERBIBOROUS_SPORE, -1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
			if (event.equalsIgnoreCase("magister_gauen_q0356_17.htm"))
			{
				st.giveItems(ADENA_ID, 44000);
			}
			else
			{
				st.addExpAndSp(36000, 2600);
			}
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
			htmltext = "magister_gauen_q0356_02.htm";
		}
		else if (cond != 3)
		{
			htmltext = "magister_gauen_q0356_07.htm";
		}
		else if (cond == 3)
		{
			htmltext = "magister_gauen_q0356_10.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		long carn = st.getQuestItemsCount(CARNIVORE_SPORE);
		long herb = st.getQuestItemsCount(HERBIBOROUS_SPORE);
		if (npcId == SPORE_ZOMBIE)
		{
			if (carn < 50)
			{
				st.giveItems(CARNIVORE_SPORE, 1);
				if (carn == 49)
				{
					st.playSound(SOUND_MIDDLE);
					if (herb >= 50)
					{
						st.setCond(3);
						st.setState(STARTED);
					}
				}
				else
				{
					st.playSound(SOUND_ITEMGET);
				}
			}
		}
		else if (npcId == ROTTING_TREE)
		{
			if (herb < 50)
			{
				st.giveItems(HERBIBOROUS_SPORE, 1);
				if (herb == 49)
				{
					st.playSound(SOUND_MIDDLE);
					if (carn >= 50)
					{
						st.setCond(3);
						st.setState(STARTED);
					}
				}
				else
				{
					st.playSound(SOUND_ITEMGET);
				}
			}
		}

		return null;
	}
}