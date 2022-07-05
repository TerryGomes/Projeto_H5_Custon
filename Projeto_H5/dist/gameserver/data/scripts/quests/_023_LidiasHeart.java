package quests;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _023_LidiasHeart extends Quest implements ScriptFile
{
	// ~~~~~~ npcId list: ~~~~~~
	int Innocentin = 31328;
	int BrokenBookshelf = 31526;
	int GhostofvonHellmann = 31524;
	int Tombstone = 31523;
	int Violet = 31386;
	int Box = 31530;

	// ~~~~~ itemId List ~~~~~
	int MapForestofDeadman = 7063;
	int SilverKey = 7149;
	int LidiaHairPin = 7148;
	int LidiaDiary = 7064;
	int SilverSpear = 7150;

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

	public _023_LidiasHeart()
	{
		super(false);

		addStartNpc(Innocentin);

		addTalkId(Innocentin);
		addTalkId(BrokenBookshelf);
		addTalkId(GhostofvonHellmann);
		addTalkId(Tombstone);
		addTalkId(Violet);
		addTalkId(Box);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("31328-02.htm"))
		{
			st.giveItems(MapForestofDeadman, 1);
			st.giveItems(SilverKey, 1);
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("31328-03.htm"))
		{
			st.setCond(2);
		}
		else if (event.equals("31526-01.htm"))
		{
			st.setCond(3);
		}
		else if (event.equals("31526-05.htm"))
		{
			st.giveItems(LidiaHairPin, 1);
			if (st.getQuestItemsCount(LidiaDiary) != 0)
			{
				st.setCond(4);
			}
		}
		else if (event.equals("31526-11.htm"))
		{
			st.giveItems(LidiaDiary, 1);
			if (st.getQuestItemsCount(LidiaHairPin) != 0)
			{
				st.setCond(4);
			}
		}
		else if (event.equals("31328-19.htm"))
		{
			st.setCond(6);
		}
		else if (event.equals("31524-04.htm"))
		{
			st.setCond(7);
			st.takeItems(LidiaDiary, -1);
		}
		else if (event.equals("31523-02.htm"))
		{
			st.addSpawn(GhostofvonHellmann, 120000);
		}
		else if (event.equals("31523-05.htm"))
		{
			st.startQuestTimer("viwer_timer", 10000);
		}
		else if (event.equals("viwer_timer"))
		{
			st.setCond(8);
			htmltext = "31523-06.htm";
		}
		else if (event.equals("31530-02.htm"))
		{
			st.setCond(10);
			st.takeItems(SilverKey, -1);
			st.giveItems(SilverSpear, 1);
		}
		else if (event.equals("i7064-02.htm"))
		{
			htmltext = "i7064-02.htm";
		}
		else if (event.equals("31526-13.htm"))
		{
			st.startQuestTimer("read_book", 120000);
		}
		else if (event.equals("read_book"))
		{
			htmltext = "i7064.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == Innocentin)
		{
			switch (cond)
			{
			case 0:
			{
				QuestState TragedyInVonHellmannForest = st.getPlayer().getQuestState(_022_TragedyInVonHellmannForest.class);
				if (TragedyInVonHellmannForest != null)
				{
					if (TragedyInVonHellmannForest.isCompleted())
					{
						htmltext = "31328-01.htm";
					}
					else
					{
						htmltext = "31328-00.htm";
					}
				}
				break;
			}
			case 1:
				htmltext = "31328-03.htm";
				break;
			case 2:
				htmltext = "31328-07.htm";
				break;
			case 4:
				htmltext = "31328-08.htm";
				break;
			case 6:
				htmltext = "31328-19.htm";
				break;
			default:
				break;
			}
		}
		else if (npcId == BrokenBookshelf)
		{
			switch (cond)
			{
			case 2:
				if (st.getQuestItemsCount(SilverKey) != 0)
				{
					htmltext = "31526-00.htm";
				}
				break;
			case 3:
				if (st.getQuestItemsCount(LidiaHairPin) == 0 && st.getQuestItemsCount(LidiaDiary) != 0)
				{
					htmltext = "31526-12.htm";
				}
				else if (st.getQuestItemsCount(LidiaHairPin) != 0 && st.getQuestItemsCount(LidiaDiary) == 0)
				{
					htmltext = "31526-06.htm";
				}
				else if (st.getQuestItemsCount(LidiaHairPin) == 0 && st.getQuestItemsCount(LidiaDiary) == 0)
				{
					htmltext = "31526-02.htm";
				}
				break;
			case 4:
				htmltext = "31526-13.htm";
				break;
			default:
				break;
			}
		}
		else if (npcId == GhostofvonHellmann)
		{
			if (cond == 6)
			{
				htmltext = "31524-01.htm";
			}
			else if (cond == 7)
			{
				htmltext = "31524-05.htm";
			}
		}
		else if (npcId == Tombstone)
		{
			if (cond == 6)
			{
				if (st.isRunningQuestTimer("spawn_timer"))
				{
					htmltext = "31523-03.htm";
				}
				else
				{
					htmltext = "31523-01.htm";
				}
			}
			if (cond == 7)
			{
				htmltext = "31523-04.htm";
			}
			else if (cond == 8)
			{
				htmltext = "31523-06.htm";
			}
		}
		else if (npcId == Violet)
		{
			switch (cond)
			{
			case 8:
				htmltext = "31386-01.htm";
				st.setCond(9);
				break;
			case 9:
				htmltext = "31386-02.htm";
				break;
			case 10:
				if (st.getQuestItemsCount(SilverSpear) != 0)
				{
					htmltext = "31386-03.htm";
					st.takeItems(SilverSpear, -1);
					st.giveItems(ADENA_ID, 350000);
					st.addExpAndSp(456893, 42112);
					st.playSound(SOUND_FINISH);
					st.exitCurrentQuest(false);
				}
				else
				{
					htmltext = "You have no Silver Spear...";
				}
				break;
			default:
				break;
			}
		}
		else if (npcId == Box)
		{
			if (cond == 9)
			{
				if (st.getQuestItemsCount(SilverKey) != 0)
				{
					htmltext = "31530-01.htm";
				}
				else
				{
					htmltext = "You have no key...";
				}
			}
			else if (cond == 10)
			{
				htmltext = "31386-03.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		return null;
	}
}