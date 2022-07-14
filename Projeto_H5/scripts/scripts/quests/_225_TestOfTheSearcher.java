package quests;

import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * Квест на вторую профессию Test Of The Searcher
 *
 * @author Sergey Ibryaev aka Artful
 */
public class _225_TestOfTheSearcher extends Quest implements ScriptFile
{
	// NPC
	private static final int Luther = 30690;
	private static final int Alex = 30291;
	private static final int Tyra = 30420;
	private static final int Chest = 30628;
	private static final int Leirynn = 30728;
	private static final int Borys = 30729;
	private static final int Jax = 30730;
	private static final int Tree = 30627;
	// Quest Items
	private static final int LuthersLetter = 2784;
	private static final int AlexsWarrant = 2785;
	private static final int Leirynns1stOrder = 2786;
	private static final int DeluTotem = 2787;
	private static final int Leirynns2ndOrder = 2788;
	private static final int ChiefKalkisFang = 2789;
	private static final int AlexsRecommend = 2808;
	private static final int LambertsMap = 2792;
	private static final int LeirynnsReport = 2790;
	private static final int AlexsLetter = 2793;
	private static final int StrangeMap = 2791;
	private static final int AlexsOrder = 2794;
	private static final int CombinedMap = 2805;
	private static final int GoldBar = 2807;
	private static final int WineCatalog = 2795;
	private static final int OldOrder = 2799;
	private static final int MalrukianWine = 2798;
	private static final int TyrasContract = 2796;
	private static final int RedSporeDust = 2797;
	private static final int JaxsDiary = 2800;
	private static final int SoltsMap = 2803;
	private static final int MakelsMap = 2804;
	private static final int RustedKey = 2806;
	private static final int TornMapPiece1st = 2801;
	private static final int TornMapPiece2st = 2802;
	// Items
	private static final int MarkOfSearcher = 2809;
	// MOB
	private static final int DeluLizardmanShaman = 20781;
	private static final int DeluLizardmanAssassin = 27094;
	private static final int DeluChiefKalkis = 27093;
	private static final int GiantFungus = 20555;
	private static final int RoadScavenger = 20551;
	private static final int HangmanTree = 20144;
	// Drop Cond
	// # [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	private static final int[][] DROPLIST_COND =
	{
		{
			3,
			4,
			DeluLizardmanShaman,
			0,
			DeluTotem,
			10,
			100,
			1
		},
		{
			3,
			4,
			DeluLizardmanAssassin,
			0,
			DeluTotem,
			10,
			100,
			1
		},
		{
			10,
			11,
			GiantFungus,
			0,
			RedSporeDust,
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

	public _225_TestOfTheSearcher()
	{
		super(false);
		addStartNpc(Luther);
		addTalkId(Alex);
		addTalkId(Leirynn);
		addTalkId(Borys);
		addTalkId(Tyra);
		addTalkId(Jax);
		addTalkId(Tree);
		addTalkId(Chest);
		// Mob Drop
		addKillId(DeluChiefKalkis);
		addKillId(RoadScavenger);
		addKillId(HangmanTree);
		for (int i = 0; i < DROPLIST_COND.length; i++)
		{
			addKillId(DROPLIST_COND[i][2]);
		}
		addQuestItem(new int[]
		{
			DeluTotem,
			RedSporeDust,
			LuthersLetter,
			AlexsWarrant,
			Leirynns1stOrder,
			Leirynns2ndOrder,
			LeirynnsReport,
			ChiefKalkisFang,
			StrangeMap,
			LambertsMap,
			AlexsLetter,
			AlexsOrder,
			WineCatalog,
			TyrasContract,
			OldOrder,
			MalrukianWine,
			JaxsDiary,
			TornMapPiece1st,
			TornMapPiece2st,
			SoltsMap,
			MakelsMap,
			RustedKey,
			CombinedMap
		});
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("30690-05.htm"))
		{
			st.giveItems(LuthersLetter, 1);
			st.setCond(1);
			st.setState(STARTED);
			if (!st.getPlayer().getVarB("dd3"))
			{
				st.giveItems(7562, 82);
				st.getPlayer().setVar("dd3", "1", -1);
			}
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30291-07.htm"))
		{
			st.takeItems(LeirynnsReport, -1);
			st.takeItems(StrangeMap, -1);
			st.giveItems(LambertsMap, 1);
			st.giveItems(AlexsLetter, 1);
			st.giveItems(AlexsOrder, 1);
			st.setCond(8);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30420-01a.htm"))
		{
			st.takeItems(WineCatalog, -1);
			st.giveItems(TyrasContract, 1);
			st.setCond(10);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30730-01d.htm"))
		{
			st.takeItems(OldOrder, -1);
			st.giveItems(JaxsDiary, 1);
			st.setCond(14);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30627-01a.htm"))
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(Chest);
			if (isQuest == null)
			{
				if (st.getQuestItemsCount(RustedKey) == 0)
				{
					st.giveItems(RustedKey, 1);
				}
				st.addSpawn(Chest);
				st.startQuestTimer("Chest", 300000);
				st.setCond(17);
				st.setState(STARTED);
			}
			else
			{
				if (!st.isRunningQuestTimer("Wait1"))
				{
					st.startQuestTimer("Wait1", 300000);
				}
				htmltext = "<html><head><body>Please wait 5 minutes</body></html>";
			}
		}
		else if (event.equalsIgnoreCase("30628-01a.htm"))
		{
			st.takeItems(RustedKey, -1);
			st.giveItems(GoldBar, 20);
			st.setCond(18);
		}
		else if (event.equalsIgnoreCase("Wait1") || event.equalsIgnoreCase("Chest"))
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(Chest);
			if (isQuest != null)
			{
				isQuest.deleteMe();
			}
			st.cancelQuestTimer("Wait1");
			st.cancelQuestTimer("Chest");
			if (st.getCond() == 17)
			{
				st.setCond(16);
			}
			return null;
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
		case Luther:
			if (st.getQuestItemsCount(MarkOfSearcher) != 0)
			{
				htmltext = "completed";
				st.exitCurrentQuest(true);
			}
			else if (cond == 0)
			{
				if (st.getPlayer().getClassId().getId() == 0x07 || st.getPlayer().getClassId().getId() == 0x16 || st.getPlayer().getClassId().getId() == 0x23 || st.getPlayer().getClassId().getId() == 0x36)
				{
					if (st.getPlayer().getLevel() >= 39)
					{
						if (st.getPlayer().getClassId().getId() == 0x36)
						{
							htmltext = "30690-04.htm";
						}
						else
						{
							htmltext = "30690-03.htm";
						}
					}
					else
					{
						htmltext = "30690-02.htm";
						st.exitCurrentQuest(true);
					}
				}
				else
				{
					htmltext = "30690-01.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 1)
			{
				htmltext = "30690-06.htm";
			}
			else if (cond > 1 && cond < 16)
			{
				htmltext = "30623-17.htm";
			}
			else if (cond == 19)
			{
				htmltext = "30690-08.htm";
				if (!st.getPlayer().getVarB("prof2.3"))
				{
					st.addExpAndSp(447444, 30704);
					st.giveItems(ADENA_ID, 80093);
					st.getPlayer().setVar("prof2.3", "1", -1);
				}
				st.takeItems(AlexsRecommend, -1);
				st.giveItems(MarkOfSearcher, 1);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
			}
			break;
		case Alex:
			if (cond == 1)
			{
				htmltext = "30291-01.htm";
				st.takeItems(LuthersLetter, -1);
				st.giveItems(AlexsWarrant, 1);
				st.setCond(2);
				st.setState(STARTED);
			}
			else if (cond == 2)
			{
				htmltext = "30291-02.htm";
			}
			else if (cond > 2 && cond < 7)
			{
				htmltext = "30291-03.htm";
			}
			else
			{
				switch (cond)
				{
				case 7:
					htmltext = "30291-04.htm";
					break;
				case 8:
					htmltext = "30291-08.htm";
					break;
				case 13:
				case 14:
					htmltext = "30291-09.htm";
					break;
				case 18:
					st.takeItems(AlexsOrder, -1);
					st.takeItems(CombinedMap, -1);
					st.takeItems(GoldBar, -1);
					st.giveItems(AlexsRecommend, 1);
					htmltext = "30291-11.htm";
					st.setCond(19);
					st.setState(STARTED);
					break;
				case 19:
					htmltext = "30291-12.htm";
					break;
				default:
					break;
				}
			}
			break;
		case Leirynn:
			switch (cond)
			{
			case 2:
				htmltext = "30728-01.htm";
				st.takeItems(AlexsWarrant, -1);
				st.giveItems(Leirynns1stOrder, 1);
				st.setCond(3);
				st.setState(STARTED);
				break;
			case 3:
				htmltext = "30728-02.htm";
				break;
			case 4:
				htmltext = "30728-03.htm";
				st.takeItems(DeluTotem, -1);
				st.takeItems(Leirynns1stOrder, -1);
				st.giveItems(Leirynns2ndOrder, 1);
				st.setCond(5);
				st.setState(STARTED);
				break;
			case 5:
				htmltext = "30728-04.htm";
				break;
			case 6:
				st.takeItems(ChiefKalkisFang, -1);
				st.takeItems(Leirynns2ndOrder, -1);
				st.giveItems(LeirynnsReport, 1);
				htmltext = "30728-05.htm";
				st.setCond(7);
				st.setState(STARTED);
				break;
			case 7:
				htmltext = "30728-06.htm";
				break;
			case 8:
				htmltext = "30728-07.htm";
				break;
			default:
				break;
			}
			break;
		case Borys:
			switch (cond)
			{
			case 8:
				st.takeItems(AlexsLetter, -1);
				st.giveItems(WineCatalog, 1);
				htmltext = "30729-01.htm";
				st.setCond(9);
				st.setState(STARTED);
				break;
			case 9:
				htmltext = "30729-02.htm";
				break;
			case 12:
				st.takeItems(WineCatalog, -1);
				st.takeItems(MalrukianWine, -1);
				st.giveItems(OldOrder, 1);
				htmltext = "30729-03.htm";
				st.setCond(13);
				st.setState(STARTED);
				break;
			case 13:
				htmltext = "30729-04.htm";
				break;
			default:
				if (cond >= 8 && cond <= 14)
				{
					htmltext = "30729-05.htm";
				}
				break;
			}
			break;
		case Tyra:
			switch (cond)
			{
			case 9:
				htmltext = "30420-01.htm";
				break;
			case 10:
				htmltext = "30420-02.htm";
				break;
			case 11:
				st.takeItems(TyrasContract, -1);
				st.takeItems(RedSporeDust, -1);
				st.giveItems(MalrukianWine, 1);
				htmltext = "30420-03.htm";
				st.setCond(12);
				st.setState(STARTED);
				break;
			case 12:
			case 13:
				htmltext = "30420-04.htm";
				break;
			default:
				break;
			}
			break;
		case Jax:
			switch (cond)
			{
			case 13:
				htmltext = "30730-01.htm";
				break;
			case 14:
				htmltext = "30730-02.htm";
				break;
			case 15:
				st.takeItems(SoltsMap, -1);
				st.takeItems(MakelsMap, -1);
				st.takeItems(LambertsMap, -1);
				st.takeItems(JaxsDiary, -1);
				st.giveItems(CombinedMap, 1);
				htmltext = "30730-03.htm";
				st.setCond(16);
				st.setState(STARTED);
				break;
			case 16:
				htmltext = "30730-04.htm";
				break;
			default:
				break;
			}
			break;
		case Tree:
			if (cond == 16 || cond == 17)
			{
				htmltext = "30627-01.htm";
			}
			break;
		case Chest:
			if (cond == 17)
			{
				htmltext = "30628-01.htm";
			}
			else
			{
				htmltext = "<html><head><body>You haven't got a Key for this Chest.</body></html>";
			}
			break;
		default:
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
		if (cond == 5 && npcId == DeluChiefKalkis)
		{
			if (st.getQuestItemsCount(StrangeMap) == 0)
			{
				st.giveItems(StrangeMap, 1);
			}
			if (st.getQuestItemsCount(ChiefKalkisFang) == 0)
			{
				st.giveItems(ChiefKalkisFang, 1);
			}
			st.playSound(SOUND_MIDDLE);
			st.setCond(6);
			st.setState(STARTED);
		}
		else if (cond == 14)
		{
			if (npcId == RoadScavenger && st.getQuestItemsCount(SoltsMap) == 0)
			{
				st.giveItems(TornMapPiece1st, 1);
				if (st.getQuestItemsCount(TornMapPiece1st) >= 4)
				{
					st.takeItems(TornMapPiece1st, -1);
					st.giveItems(SoltsMap, 1);
				}
			}
			else if (npcId == HangmanTree && st.getQuestItemsCount(MakelsMap) == 0)
			{
				st.giveItems(TornMapPiece2st, 1);
				if (st.getQuestItemsCount(TornMapPiece2st) >= 4)
				{
					st.takeItems(TornMapPiece2st, -1);
					st.giveItems(MakelsMap, 1);
				}
			}
			if (st.getQuestItemsCount(SoltsMap) != 0 && st.getQuestItemsCount(MakelsMap) != 0)
			{
				st.setCond(15);
				st.setState(STARTED);
			}
		}
		return null;
	}
}