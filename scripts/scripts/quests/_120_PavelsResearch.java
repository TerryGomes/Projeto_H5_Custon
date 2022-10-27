package quests;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.scripts.ScriptFile;

public class _120_PavelsResearch extends Quest implements ScriptFile
{
	// NPC
	private static final int Yumi = 32041;
	private static final int Weather1 = 32042;
	private static final int Weather2 = 32043;
	private static final int Weather3 = 32044;
	private static final int BookShelf = 32045;
	private static final int Stones = 32046;
	private static final int Wendy = 32047;
	// ITEMS
	private static final int EarPhoenix = 6324;
	private static final int Report = 8058;
	private static final int Report2 = 8059;
	private static final int Enigma = 8060;
	private static final int Flower = 8290;
	private static final int Heart = 8291;
	private static final int Necklace = 8292;

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

	public _120_PavelsResearch()
	{
		super(false);

		addStartNpc(Stones);

		addQuestItem(Report, Report2, Enigma, Flower, Heart, Necklace);

		addTalkId(BookShelf);
		addTalkId(Stones);
		addTalkId(Weather1);
		addTalkId(Weather2);
		addTalkId(Weather3);
		addTalkId(Wendy);
		addTalkId(Yumi);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("32041-03.htm"))
		{
			st.setCond(3);
			st.playSound("ItemSound.quest_middle");
		}
		else if (event.equalsIgnoreCase("32041-04.htm"))
		{
			st.setCond(4);
			st.playSound("ItemSound.quest_middle");
		}
		else if (event.equalsIgnoreCase("32041-12.htm"))
		{
			st.setCond(8);
			st.playSound("ItemSound.quest_middle");
		}
		else if (event.equalsIgnoreCase("32041-16.htm"))
		{
			st.setCond(16);
			st.giveItems(Enigma, 1);
			st.playSound("ItemSound.quest_middle");
		}
		else if (event.equalsIgnoreCase("32041-22.htm"))
		{
			st.setCond(17);
			st.takeItems(Enigma, 1);
			st.playSound("ItemSound.quest_middle");
		}
		else if (event.equalsIgnoreCase("32041-32.htm"))
		{

			st.takeItems(Necklace, 1);
			st.giveItems(EarPhoenix, 1);
			st.giveItems(ADENA_ID, 783720);
			st.addExpAndSp(3447315, 272615);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		else if (event.equalsIgnoreCase("32042-06.htm"))
		{
			if (st.getCond() == 10)
			{
				if (st.getInt("talk") + st.getInt("talk1") == 2)
				{
					st.setCond(11);
					st.set("talk", "0");
					st.set("talk1", "0");
					st.playSound("ItemSound.quest_middle");
				}
				else
				{
					htmltext = "32042-03.htm";
				}
			}
		}
		else if (event.equalsIgnoreCase("32042-10.htm"))
		{
			if (st.getInt("talk") + st.getInt("talk1") + st.getInt("talk2") == 3)
			{
				htmltext = "32042-14.htm";
			}
		}
		else if (event.equalsIgnoreCase("32042-11.htm"))
		{
			if (st.getInt("talk") == 0)
			{
				st.set("talk", "1");
			}
		}
		else if (event.equalsIgnoreCase("32042-12.htm"))
		{
			if (st.getInt("talk1") == 0)
			{
				st.set("talk1", "1");
			}
		}
		else if (event.equalsIgnoreCase("32042-13.htm"))
		{
			if (st.getInt("talk2") == 0)
			{
				st.set("talk2", "1");
			}
		}
		else if (event.equalsIgnoreCase("32042-15.htm"))
		{
			st.setCond(12);
			st.set("talk", "0");
			st.set("talk1", "0");
			st.set("talk2", "0");
			st.playSound("ItemSound.quest_middle");
		}
		else if (event.equalsIgnoreCase("32043-06.htm"))
		{
			if (st.getCond() == 17)
			{
				if (st.getInt("talk") + st.getInt("talk1") == 2)
				{
					st.setCond(18);
					st.set("talk", "0");
					st.set("talk1", "0");
					st.playSound("ItemSound.quest_middle");
				}
				else
				{
					htmltext = "32043-03.htm";
				}
			}
		}
		else if (event.equalsIgnoreCase("32043-15.htm"))
		{
			if (st.getInt("talk") + st.getInt("talk1") == 2)
			{
				htmltext = "32043-29.htm";
			}
		}
		else if (event.equalsIgnoreCase("32043-18.htm"))
		{
			if (st.getInt("talk") == 1)
			{
				htmltext = "32043-21.htm";
			}
		}
		else if (event.equalsIgnoreCase("32043-20.htm"))
		{
			st.set("talk", "1");
			st.playSound("AmbSound.ed_drone_02");
		}
		else if (event.equalsIgnoreCase("32043-28.htm"))
		{
			st.set("talk1", "1");
		}
		else if (event.equalsIgnoreCase("32043-30.htm"))
		{
			st.setCond(19);
			st.set("talk", "0");
			st.set("talk1", "0");
		}
		else if (event.equalsIgnoreCase("32044-06.htm"))
		{
			if (st.getCond() == 20)
			{
				if (st.getInt("talk") + st.getInt("talk1") == 2)
				{
					st.setCond(21);
					st.set("talk", "0");
					st.set("talk1", "0");
					st.playSound("ItemSound.quest_middle");
				}
				else
				{
					htmltext = "32044-03.htm";
				}
			}
		}
		else if (event.equalsIgnoreCase("32044-08.htm"))
		{
			if (st.getInt("talk") + st.getInt("talk1") == 2)
			{
				htmltext = "32044-11.htm";
			}
		}
		else if (event.equalsIgnoreCase("32044-09.htm"))
		{
			if (st.getInt("talk") == 0)
			{
				st.set("talk", "1");
			}
		}
		else if (event.equalsIgnoreCase("32044-10.htm"))
		{
			if (st.getInt("talk1") == 0)
			{
				st.set("talk1", "1");
			}
		}
		else if (event.equalsIgnoreCase("32044-17.htm"))
		{
			st.setCond(22);
			st.set("talk", "0");
			st.set("talk1", "0");
			st.playSound("ItemSound.quest_middle");
		}
		else if (event.equalsIgnoreCase("32045-02.htm"))
		{
			st.setCond(15);
			st.playSound("ItemSound.quest_middle");
			st.giveItems(Report, 1);
			Player player = st.getPlayer();
			if (player != null)
			{
				npc.broadcastPacket(new MagicSkillUse(npc, player, 5073, 5, 1500, 0));
			}
		}
		else if (event.equalsIgnoreCase("32046-04.htm") || event.equalsIgnoreCase("32046-05.htm"))
		{
			st.exitCurrentQuest(true);
		}
		else if (event.equalsIgnoreCase("32046-06.htm"))
		{
			if (st.getPlayer().getLevel() >= 50)
			{
				st.playSound("ItemSound.quest_accept");
				st.setCond(1);
				st.setState(STARTED);
			}
			else
			{
				htmltext = "32046-00.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("32046-08.htm"))
		{
			st.setCond(2);
			st.playSound("ItemSound.quest_middle");
		}
		else if (event.equalsIgnoreCase("32046-12.htm"))
		{
			st.setCond(6);
			st.playSound("ItemSound.quest_middle");
			st.giveItems(Flower, 1);
		}
		else if (event.equalsIgnoreCase("32046-22.htm"))
		{
			st.setCond(10);
			st.playSound("ItemSound.quest_middle");
		}
		else if (event.equalsIgnoreCase("32046-29.htm"))
		{
			st.setCond(13);
			st.playSound("ItemSound.quest_middle");
		}
		else if (event.equalsIgnoreCase("32046-35.htm"))
		{
			st.setCond(20);
			st.playSound("ItemSound.quest_middle");
		}
		else if (event.equalsIgnoreCase("32046-38.htm"))
		{
			st.setCond(23);
			st.playSound("ItemSound.quest_middle");
			st.giveItems(Heart, 1);
		}
		else if (event.equalsIgnoreCase("32047-06.htm"))
		{
			st.setCond(5);
			st.playSound("ItemSound.quest_middle");
		}
		else if (event.equalsIgnoreCase("32047-10.htm"))
		{
			st.setCond(7);
			st.playSound("ItemSound.quest_middle");
			st.takeItems(Flower, 1);
		}
		else if (event.equalsIgnoreCase("32047-15.htm"))
		{
			st.setCond(9);
			st.playSound("ItemSound.quest_middle");
		}
		else if (event.equalsIgnoreCase("32047-18.htm"))
		{
			st.setCond(14);
			st.playSound("ItemSound.quest_middle");
		}
		else if (event.equalsIgnoreCase("32047-26.htm"))
		{
			st.setCond(24);
			st.playSound("ItemSound.quest_middle");
			st.takeItems(Heart, 1);
		}
		else if (event.equalsIgnoreCase("32047-32.htm"))
		{
			st.setCond(25);
			st.playSound("ItemSound.quest_middle");
			st.giveItems(Necklace, 1);
		}
		else if (event.equalsIgnoreCase("w1_1"))
		{
			st.set("talk", "1");
			htmltext = "32042-04.htm";
		}
		else if (event.equalsIgnoreCase("w1_2"))
		{
			st.set("talk1", "1");
			htmltext = "32042-05.htm";
		}
		else if (event.equalsIgnoreCase("w2_1"))
		{
			st.set("talk", "1");
			htmltext = "32043-04.htm";
		}
		else if (event.equalsIgnoreCase("w2_2"))
		{
			st.set("talk1", "1");
			htmltext = "32043-05.htm";
		}
		else if (event.equalsIgnoreCase("w3_1"))
		{
			st.set("talk", "1");
			htmltext = "32044-04.htm";
		}
		else if (event.equalsIgnoreCase("w3_2"))
		{
			st.set("talk1", "1");
			htmltext = "32044-05.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "<html><head><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>";
		int cond = st.getCond();
		switch (npcId)
		{
		case Stones:
		{
			QuestState q = st.getPlayer().getQuestState(_114_ResurrectionOfAnOldManager.class);
			if (q == null)
			{
				return htmltext;
			}
			else
			{
				switch (cond)
				{
				case 0:
					if (st.getPlayer().getLevel() >= 70 && q.isCompleted())
					{
						htmltext = "32046-01.htm";
					}
					else
					{
						htmltext = "32046-00.htm";
						st.exitCurrentQuest(true);
					}
					break;
				case 1:
					htmltext = "32046-06.htm";
					break;
				case 2:
					htmltext = "32046-09.htm";
					break;
				case 5:
					htmltext = "32046-10.htm";
					break;
				case 6:
					htmltext = "32046-13.htm";
					break;
				case 9:
					htmltext = "32046-14.htm";
					break;
				case 10:
					htmltext = "32046-23.htm";
					break;
				case 12:
					htmltext = "32046-26.htm";
					break;
				case 13:
					htmltext = "32046-30.htm";
					break;
				case 19:
					htmltext = "32046-31.htm";
					break;
				case 20:
					htmltext = "32046-36.htm";
					break;
				case 22:
					htmltext = "32046-37.htm";
					break;
				case 23:
					htmltext = "32046-39.htm";
					break;
				default:
					break;
				}
			}
			break;
		}
		case Wendy:
			switch (cond)
			{
			case 2:
			case 3:
			case 4:
				htmltext = "32047-01.htm";
				break;
			case 5:
				htmltext = "32047-07.htm";
				break;
			case 6:
				htmltext = "32047-08.htm";
				break;
			case 7:
				htmltext = "32047-11.htm";
				break;
			case 8:
				htmltext = "32047-12.htm";
				break;
			case 9:
				htmltext = "32047-15.htm";
				break;
			case 13:
				htmltext = "32047-16.htm";
				break;
			case 14:
				htmltext = "32047-19.htm";
				break;
			case 15:
				htmltext = "32047-20.htm";
				break;
			case 23:
				htmltext = "32047-21.htm";
				break;
			case 24:
				htmltext = "32047-26.htm";
				break;
			case 25:
				htmltext = "32047-33.htm";
				break;
			default:
				break;
			}
			break;
		case Yumi:
			switch (cond)
			{
			case 2:
				htmltext = "32041-01.htm";
				break;
			case 3:
				htmltext = "32041-05.htm";
				break;
			case 4:
				htmltext = "32041-06.htm";
				break;
			case 7:
				htmltext = "32041-07.htm";
				break;
			case 8:
				htmltext = "32041-13.htm";
				break;
			case 15:
				htmltext = "32041-14.htm";
				break;
			case 16:
				if (st.getQuestItemsCount(Report2) == 0)
				{
					htmltext = "32041-17.htm";
				}
				else
				{
					htmltext = "32041-18.htm";
				}
				break;
			case 17:
				htmltext = "32041-22.htm";
				break;
			case 25:
				htmltext = "32041-26.htm";
				break;
			default:
				break;
			}
			break;
		case Weather1:
			switch (cond)
			{
			case 10:
				htmltext = "32042-01.htm";
				break;
			case 11:
				if (st.getInt("talk") + st.getInt("talk1") + st.getInt("talk2") == 3)
				{
					htmltext = "32042-14.htm";
				}
				else
				{
					htmltext = "32042-06.htm";
				}
				break;
			case 12:
				htmltext = "32042-15.htm";
				break;
			default:
				break;
			}
			break;
		case Weather2:
			switch (cond)
			{
			case 17:
				htmltext = "32043-01.htm";
				break;
			case 18:
				if (st.getInt("talk") + st.getInt("talk1") == 2)
				{
					htmltext = "32043-29.htm";
				}
				else
				{
					htmltext = "32043-06.htm";
				}
				break;
			case 19:
				htmltext = "32043-30.htm";
				break;
			default:
				break;
			}
			break;
		case Weather3:
			switch (cond)
			{
			case 20:
				htmltext = "32044-01.htm";
				break;
			case 21:
				htmltext = "32044-06.htm";
				break;
			case 22:
				htmltext = "32044-18.htm";
				break;
			default:
				break;
			}
			break;
		case BookShelf:
			if (cond == 14)
			{
				htmltext = "32045-01.htm";
			}
			else if (cond == 15)
			{
				htmltext = "32045-03.htm";
			}
			break;
		default:
			break;
		}
		return htmltext;
	}
}