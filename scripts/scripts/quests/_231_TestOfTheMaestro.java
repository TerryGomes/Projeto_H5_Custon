package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * Квест на вторую профессию Test Of The Maestro
 */
public class _231_TestOfTheMaestro extends Quest implements ScriptFile
{
	// NPC
	private static final int Lockirin = 30531;
	private static final int Balanki = 30533;
	private static final int Arin = 30536;
	private static final int Filaur = 30535;
	private static final int Spiron = 30532;
	private static final int Croto = 30671;
	private static final int Kamur = 30675;
	private static final int Dubabah = 30672;
	private static final int Toma = 30556;
	private static final int Lorain = 30673;

	// Quest Items
	private static final int RecommendationOfBalanki = 2864;
	private static final int RecommendationOfFilaur = 2865;
	private static final int RecommendationOfArin = 2866;
	private static final int LetterOfSolderDetachment = 2868;
	private static final int PaintOfKamuru = 2869;
	private static final int NecklaceOfKamuru = 2870;
	private static final int PaintOfTeleportDevice = 2871;
	private static final int TeleportDevice = 2872;
	private static final int ArchitectureOfCruma = 2873;
	private static final int ReportOfCruma = 2874;
	private static final int IngredientsOfAntidote = 2875;
	private static final int StingerWaspNeedle = 2876;
	private static final int MarshSpidersWeb = 2877;
	private static final int BloodOfLeech = 2878;
	private static final int BrokenTeleportDevice = 2916;

	// Items
	private static final int DD = 7562;
	private static final int MarkOfMaestro = 2867;

	// MOB
	private static final int QuestMonsterEvilEyeLord = 27133;
	private static final int GiantMistLeech = 20225;
	private static final int StingerWasp = 20229;
	private static final int MarshSpider = 20233;

	// Drop Cond
	// [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	private static final int[][] DROPLIST_COND =
	{
		{
			4,
			5,
			QuestMonsterEvilEyeLord,
			0,
			NecklaceOfKamuru,
			1,
			100,
			1
		},
		{
			13,
			0,
			GiantMistLeech,
			0,
			BloodOfLeech,
			10,
			100,
			1
		},
		{
			13,
			0,
			StingerWasp,
			0,
			StingerWaspNeedle,
			10,
			100,
			1
		},
		{
			13,
			0,
			MarshSpider,
			0,
			MarshSpidersWeb,
			10,
			100,
			1
		}
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

	public _231_TestOfTheMaestro()
	{
		super(false);

		addStartNpc(Lockirin);
		addTalkId(Balanki);
		addTalkId(Arin);
		addTalkId(Filaur);
		addTalkId(Spiron);
		addTalkId(Croto);
		addTalkId(Kamur);
		addTalkId(Dubabah);
		addTalkId(Toma);
		addTalkId(Lorain);

		// Mob Drop
		for (int i = 0; i < DROPLIST_COND.length; i++)
		{
			addKillId(DROPLIST_COND[i][2]);
			addQuestItem(DROPLIST_COND[i][4]);
		}

		addQuestItem(new int[]
		{
			PaintOfKamuru,
			LetterOfSolderDetachment,
			PaintOfTeleportDevice,
			BrokenTeleportDevice,
			TeleportDevice,
			ArchitectureOfCruma,
			IngredientsOfAntidote,
			RecommendationOfBalanki,
			RecommendationOfFilaur,
			RecommendationOfArin,
			ReportOfCruma
		});
	}

	public void recommendationCount(QuestState st)
	{
		if (st.getQuestItemsCount(RecommendationOfArin) != 0 && st.getQuestItemsCount(RecommendationOfFilaur) != 0 && st.getQuestItemsCount(RecommendationOfBalanki) != 0)
		{
			st.setCond(17);
		}
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("30531-04.htm"))
		{
			if (!st.getPlayer().getVarB("dd3"))
			{
				st.giveItems(DD, 23);
				st.getPlayer().setVar("dd3", "1", -1);
			}
			st.setCond(1);
			st.setState(STARTED);
			st.playSound("ItemSound.quest_accept");
		}
		else if (event.equalsIgnoreCase("30533-02.htm"))
		{
			st.setCond(2);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30671-02.htm"))
		{
			st.giveItems(PaintOfKamuru, 1);
			st.setCond(3);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30556-05.htm"))
		{
			st.takeItems(PaintOfTeleportDevice, -1);
			st.giveItems(BrokenTeleportDevice, 1);
			st.setCond(9);
			st.setState(STARTED);
			st.getPlayer().teleToLocation(140352, -194133, -2028);
		}
		else if (event.equalsIgnoreCase("30673-04.htm"))
		{
			st.takeItems(BloodOfLeech, -1);
			st.takeItems(StingerWaspNeedle, -1);
			st.takeItems(MarshSpidersWeb, -1);
			st.takeItems(IngredientsOfAntidote, -1);
			st.giveItems(ReportOfCruma, 1);
			st.setCond(15);
			st.setState(STARTED);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getCond();
		switch (npcId)
		{
		case Lockirin:
			if (st.getQuestItemsCount(MarkOfMaestro) != 0)
			{
				htmltext = "completed";
				st.exitCurrentQuest(true);
			}
			else if (cond == 0)
			{
				if (st.getPlayer().getClassId().getId() == 0x38)
				{
					if (st.getPlayer().getLevel() >= 39)
					{
						htmltext = "30531-03.htm";
					}
					else
					{
						htmltext = "30531-01.htm";
						st.exitCurrentQuest(true);
					}
				}
				else
				{
					htmltext = "30531-02.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond >= 1 && cond <= 16)
			{
				htmltext = "30531-05.htm";
			}
			else if (cond == 17)
			{
				if (!st.getPlayer().getVarB("prof2.3"))
				{
					st.addExpAndSp(1029122, 70620);
					st.giveItems(ADENA_ID, 186077);
					st.getPlayer().setVar("prof2.3", "1", -1);
				}
				htmltext = "30531-06.htm";
				st.takeItems(RecommendationOfBalanki, -1);
				st.takeItems(RecommendationOfFilaur, -1);
				st.takeItems(RecommendationOfArin, -1);
				st.giveItems(MarkOfMaestro, 1);
				st.playSound("ItemSound.quest_finish");
				st.exitCurrentQuest(true);
			}
			break;
		case Balanki:
			if ((cond == 1 || cond == 11 || cond == 16) && st.getQuestItemsCount(RecommendationOfBalanki) == 0)
			{
				htmltext = "30533-01.htm";
			}
			else
			{
				switch (cond)
				{
				case 2:
					htmltext = "30533-03.htm";
					break;
				case 6:
					st.takeItems(LetterOfSolderDetachment, -1);
					st.giveItems(RecommendationOfBalanki, 1);
					htmltext = "30533-04.htm";
					st.setCond(7);
					recommendationCount(st);
					st.setState(STARTED);
					break;
				case 7:
				case 17:
					htmltext = "30533-05.htm";
					break;
				default:
					break;
				}
			}
			break;
		case Arin:
			if ((cond == 1 || cond == 7 || cond == 16) && st.getQuestItemsCount(RecommendationOfArin) == 0)
			{
				st.giveItems(PaintOfTeleportDevice, 1);
				htmltext = "30536-01.htm";
				st.setCond(8);
				st.setState(STARTED);
			}
			else
			{
				switch (cond)
				{
				case 8:
					htmltext = "30536-02.htm";
					break;
				case 10:
					st.takeItems(TeleportDevice, -1);
					st.giveItems(RecommendationOfArin, 1);
					htmltext = "30536-03.htm";
					st.setCond(11);
					recommendationCount(st);
					st.setState(STARTED);
					break;
				case 11:
				case 17:
					htmltext = "30536-04.htm";
					break;
				default:
					break;
				}
			}
			break;
		case Filaur:
			if ((cond == 1 || cond == 7 || cond == 11) && st.getQuestItemsCount(RecommendationOfFilaur) == 0)
			{
				st.giveItems(ArchitectureOfCruma, 1);
				htmltext = "30535-01.htm";
				st.setCond(12);
				st.setState(STARTED);
			}
			else if (cond == 12)
			{
				htmltext = "30535-02.htm";
			}
			else if (cond == 15)
			{
				st.takeItems(ReportOfCruma, 1);
				st.giveItems(RecommendationOfFilaur, 1);
				st.setCond(16);
				htmltext = "30535-03.htm";
				recommendationCount(st);
				st.setState(STARTED);
			}
			else if (cond > 15)
			{
				htmltext = "30535-04.htm";
			}
			break;
		case Croto:
			switch (cond)
			{
			case 2:
				htmltext = "30671-01.htm";
				break;
			case 3:
				htmltext = "30671-03.htm";
				break;
			case 5:
				st.takeItems(NecklaceOfKamuru, -1);
				st.takeItems(PaintOfKamuru, -1);
				st.giveItems(LetterOfSolderDetachment, 1);
				htmltext = "30671-04.htm";
				st.setCond(6);
				st.setState(STARTED);
				break;
			case 6:
				htmltext = "30671-05.htm";
				break;
			default:
				break;
			}
			break;
		default:
			if (npcId == Dubabah && cond == 3)
			{
				htmltext = "30672-01.htm";
			}
			else if (npcId == Kamur && cond == 3)
			{
				htmltext = "30675-01.htm";
				st.setCond(4);
				st.setState(STARTED);
			}
			else if (npcId == Toma)
			{
				switch (cond)
				{
				case 8:
					htmltext = "30556-01.htm";
					break;
				case 9:
					st.takeItems(BrokenTeleportDevice, -1);
					st.giveItems(TeleportDevice, 5);
					htmltext = "30556-06.htm";
					st.setCond(10);
					st.setState(STARTED);
					break;
				case 10:
					htmltext = "30556-07.htm";
					break;
				default:
					break;
				}
			}
			else if (npcId == Lorain)
			{
				switch (cond)
				{
				case 12:
					st.takeItems(ArchitectureOfCruma, -1);
					st.giveItems(IngredientsOfAntidote, 1);
					st.setCond(13);
					htmltext = "30673-01.htm";
					break;
				case 13:
					htmltext = "30673-02.htm";
					break;
				case 14:
					htmltext = "30673-03.htm";
					break;
				case 15:
					htmltext = "30673-05.htm";
					break;
				default:
					break;
				}
			}
			else if (npcId == Spiron && (cond == 1 || cond == 7 || cond == 11 || cond == 16))
			{
				htmltext = "30532-01.htm";
			}
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		for (int i = 0; i < DROPLIST_COND.length; i++)
		{
			if (cond == DROPLIST_COND[i][0] && npcId == DROPLIST_COND[i][2])
			{
				if (DROPLIST_COND[i][3] == 0 || st.getQuestItemsCount(DROPLIST_COND[i][3]) > 0)
				{
					if (DROPLIST_COND[i][5] == 0)
					{
						st.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][6]);
					}
					else if (st.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][7], DROPLIST_COND[i][5], DROPLIST_COND[i][6]))
					{
						if (DROPLIST_COND[i][1] != cond && DROPLIST_COND[i][1] != 0)
						{
							st.setCond(Integer.valueOf(DROPLIST_COND[i][1]));
							st.setState(STARTED);
						}
					}
				}
			}
		}
		if (cond == 13 && st.getQuestItemsCount(BloodOfLeech) >= 10 && st.getQuestItemsCount(StingerWaspNeedle) >= 10 && st.getQuestItemsCount(MarshSpidersWeb) >= 10)
		{
			st.setCond(14);
			st.setState(STARTED);
		}
		return null;
	}
}