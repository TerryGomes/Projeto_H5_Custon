package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _117_OceanOfDistantStar extends Quest implements ScriptFile
{
	// NPC
	private static final int Abey = 32053;
	private static final int GhostEngineer = 32055;
	private static final int Obi = 32052;
	private static final int GhostEngineer2 = 32054;
	private static final int Box = 32076;
	// Quest Items
	private static final int BookOfGreyStar = 8495;
	private static final int EngravedHammer = 8488;
	// Mobs
	private static final int BanditWarrior = 22023;
	private static final int BanditInspector = 22024;

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

	public _117_OceanOfDistantStar()
	{
		super(false);

		addStartNpc(Abey);

		addTalkId(GhostEngineer);
		addTalkId(Obi);
		addTalkId(Box);
		addTalkId(GhostEngineer2);

		addKillId(BanditWarrior);
		addKillId(BanditInspector);

		addQuestItem(new int[]
		{
			BookOfGreyStar,
			EngravedHammer
		});
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("railman_abu_q0117_0104.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("ghost_of_railroadman2_q0117_0201.htm"))
		{
			st.setCond(2);
		}
		else if (event.equalsIgnoreCase("railman_obi_q0117_0301.htm"))
		{
			st.setCond(3);
		}
		else if (event.equalsIgnoreCase("railman_abu_q0117_0401.htm"))
		{
			st.setCond(4);
		}
		else if (event.equalsIgnoreCase("q_box_of_railroad_q0117_0501.htm"))
		{
			st.setCond(5);
			st.giveItems(EngravedHammer, 1);
		}
		else if (event.equalsIgnoreCase("railman_abu_q0117_0601.htm"))
		{
			st.setCond(6);
		}
		else if (event.equalsIgnoreCase("railman_obi_q0117_0701.htm"))
		{
			st.setCond(7);
		}
		else if (event.equalsIgnoreCase("railman_obi_q0117_0801.htm"))
		{
			st.takeItems(BookOfGreyStar, -1);
			st.setCond(9);
		}
		else if (event.equalsIgnoreCase("ghost_of_railroadman2_q0117_0901.htm"))
		{
			st.takeItems(EngravedHammer, -1);
			st.setCond(10);
		}
		else if (event.equalsIgnoreCase("ghost_of_railroadman_q0117_1002.htm"))
		{
			st.giveItems(ADENA_ID, 17647, true);
			st.addExpAndSp(107387, 7369);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = 0;
		if (id != CREATED)
		{
			cond = st.getCond();
		}
		switch (npcId)
		{
		case Abey:
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() >= 39)
				{
					htmltext = "railman_abu_q0117_0101.htm";
				}
				else
				{
					htmltext = "railman_abu_q0117_0103.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 3)
			{
				htmltext = "railman_abu_q0117_0301.htm";
			}
			else if (cond == 5 && st.getQuestItemsCount(EngravedHammer) > 0)
			{
				htmltext = "railman_abu_q0117_0501.htm";
			}
			else if (cond == 6 && st.getQuestItemsCount(EngravedHammer) > 0)
			{
				htmltext = "railman_abu_q0117_0601.htm";
			}
			break;
		case GhostEngineer:
			if (cond == 1)
			{
				htmltext = "ghost_of_railroadman2_q0117_0101.htm";
			}
			else if (cond == 9 && st.getQuestItemsCount(EngravedHammer) > 0)
			{
				htmltext = "ghost_of_railroadman2_q0117_0801.htm";
			}
			break;
		case Obi:
			if (cond == 2)
			{
				htmltext = "railman_obi_q0117_0201.htm";
			}
			else if (cond == 6 && st.getQuestItemsCount(EngravedHammer) > 0)
			{
				htmltext = "railman_obi_q0117_0601.htm";
			}
			else if (cond == 7 && st.getQuestItemsCount(EngravedHammer) > 0)
			{
				htmltext = "railman_obi_q0117_0701.htm";
			}
			else if (cond == 8 && st.getQuestItemsCount(BookOfGreyStar) > 0)
			{
				htmltext = "railman_obi_q0117_0704.htm";
			}
			break;
		default:
			if (npcId == Box && cond == 4)
			{
				htmltext = "q_box_of_railroad_q0117_0401.htm";
			}
			else if (npcId == GhostEngineer2 && cond == 10)
			{
				htmltext = "ghost_of_railroadman_q0117_0901.htm";
			}
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getCond() == 7 && Rnd.chance(30))
		{
			if (st.getQuestItemsCount(BookOfGreyStar) < 1)
			{
				st.giveItems(BookOfGreyStar, 1);
				st.playSound(SOUND_ITEMGET);
			}
			st.setCond(8);
			st.setState(STARTED);
		}
		return null;
	}
}