package quests;

import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * Квест на вторую профессию Testimony Of Prosperity
 *
 * @author Sergey Ibryaev aka Artful
 */
public class _221_TestimonyOfProsperity extends Quest implements ScriptFile
{
	// NPC
	private static final int Parman = 30104;
	private static final int Bright = 30466;
	private static final int Emily = 30620;
	private static final int Piotur = 30597;
	private static final int Wilford = 30005;
	private static final int Lilith = 30368;
	private static final int Lockirin = 30531;
	private static final int Spiron = 30532;
	private static final int Shari = 30517;
	private static final int Balanki = 30533;
	private static final int Mion = 30519;
	private static final int Redbonnet = 30553;
	private static final int Keef = 30534;
	private static final int Torocco = 30555;
	private static final int Filaur = 30535;
	private static final int Bolter = 30554;
	private static final int Arin = 30536;
	private static final int Toma = 30556;
	private static final int Nikola = 30621;
	private static final int BoxOfTitan = 30622;
	// Quest Item
	private static final int RingOfTestimony1st = 3239;
	private static final int BrightsList = 3264;
	private static final int MandragoraPetal = 3265;
	private static final int CrimsonMoss = 3266;
	private static final int MandragoraBouquet = 3267;
	private static final int EmilysRecipe = 3243;
	private static final int BlessedSeed = 3242;
	private static final int CrystalBrooch = 3428;
	private static final int LilithsElvenWafer = 3244;
	private static final int CollectionLicense = 3246;
	private static final int Lockirins1stNotice = 3247;
	private static final int ContributionOfShari = 3252;
	private static final int ReceiptOfContribution1st = 3258;
	private static final int Lockirins2stNotice = 3248;
	private static final int ContributionOfMion = 3253;
	private static final int MarysesRequest = 3255;
	private static final int ContributionOfMaryse = 3254;
	private static final int ReceiptOfContribution2st = 3259;
	private static final int Lockirins3stNotice = 3249;
	private static final int ProcurationOfTorocco = 3263;
	private static final int ReceiptOfContribution3st = 3260;
	private static final int Lockirins4stNotice = 3250;
	private static final int ReceiptOfBolter = 3257;
	private static final int ReceiptOfContribution4st = 3261;
	private static final int Lockirins5stNotice = 3251;
	private static final int ContributionOfToma = 3256;
	private static final int ReceiptOfContribution5st = 3262;
	private static final int OldAccountBook = 3241;
	private static final int ParmansInstructions = 3268;
	private static final int ParmansLetter = 3269;
	private static final int RingOfTestimony2st = 3240;
	private static final int ClayDough = 3270;
	private static final int PatternOfKeyhole = 3271;
	private static final int NikolasList = 3272;
	private static final int RecipeTitanKey = 3023;
	private static final int MaphrTabletFragment = 3245;
	private static final int StakatoShell = 3273;
	private static final int ToadLordSac = 3274;
	private static final int SpiderThorn = 3275;
	private static final int KeyOfTitan = 3030;
	// Item
	private static final int MarkOfProsperity = 3238;
	private static final int AnimalSkin = 1867;
	// MOB
	private static final int MandragoraSprout = 20154;
	private static final int MandragoraSapling = 20155;
	private static final int MandragoraBlossom = 20156;
	private static final int MandragoraSprout2 = 20223;
	private static final int GiantCrimsonAnt = 20228;
	private static final int MarshStakato = 20157;
	private static final int MarshStakatoWorker = 20230;
	private static final int MarshStakatoSoldier = 20232;
	private static final int MarshStakatoDrone = 20234;
	private static final int ToadLord = 20231;
	private static final int MarshSpider = 20233;

	// Drop Cond
	// # [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	private static final int[][] DROPLIST_COND =
	{
		{
			1,
			0,
			MandragoraSprout,
			BrightsList,
			MandragoraPetal,
			20,
			60,
			1
		},
		{
			1,
			0,
			MandragoraSapling,
			BrightsList,
			MandragoraPetal,
			20,
			80,
			1
		},
		{
			1,
			0,
			MandragoraBlossom,
			BrightsList,
			MandragoraPetal,
			20,
			100,
			1
		},
		{
			1,
			0,
			MandragoraSprout2,
			BrightsList,
			MandragoraPetal,
			20,
			30,
			1
		},
		{
			1,
			0,
			GiantCrimsonAnt,
			BrightsList,
			CrimsonMoss,
			10,
			100,
			1
		},
		{
			7,
			0,
			MarshStakato,
			0,
			StakatoShell,
			20,
			100,
			1
		},
		{
			7,
			0,
			MarshStakatoWorker,
			0,
			StakatoShell,
			20,
			100,
			1
		},
		{
			7,
			0,
			MarshStakatoSoldier,
			0,
			StakatoShell,
			20,
			100,
			1
		},
		{
			7,
			0,
			MarshStakatoDrone,
			0,
			StakatoShell,
			20,
			100,
			1
		},
		{
			7,
			0,
			ToadLord,
			0,
			ToadLordSac,
			10,
			100,
			1
		},
		{
			7,
			0,
			MarshSpider,
			0,
			SpiderThorn,
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

	public _221_TestimonyOfProsperity()
	{
		super(false);

		addStartNpc(Parman);

		addTalkId(Bright);
		addTalkId(Emily);
		addTalkId(Piotur);
		addTalkId(Wilford);
		addTalkId(Lilith);
		addTalkId(Shari);
		addTalkId(Mion);
		addTalkId(Lockirin);
		addTalkId(Spiron);
		addTalkId(Balanki);
		addTalkId(Keef);
		addTalkId(Filaur);
		addTalkId(Arin);
		addTalkId(Redbonnet);
		addTalkId(Bolter);
		addTalkId(Torocco);
		addTalkId(Toma);
		addTalkId(Nikola);
		addTalkId(BoxOfTitan);

		for (int i = 0; i < DROPLIST_COND.length; i++)
		{
			addKillId(DROPLIST_COND[i][2]);
		}

		addQuestItem(new int[]
		{
			RingOfTestimony1st,
			BrightsList,
			MandragoraBouquet,
			BlessedSeed,
			EmilysRecipe,
			CrystalBrooch,
			LilithsElvenWafer,
			CollectionLicense,
			Lockirins1stNotice,
			Lockirins2stNotice,
			Lockirins3stNotice,
			Lockirins4stNotice,
			Lockirins5stNotice,
			ReceiptOfContribution1st,
			ReceiptOfContribution2st,
			ReceiptOfContribution3st,
			ReceiptOfContribution4st,
			ReceiptOfContribution5st,
			OldAccountBook,
			ContributionOfShari,
			ContributionOfMaryse,
			ContributionOfMion,
			ProcurationOfTorocco,
			ReceiptOfBolter,
			ContributionOfToma,
			MarysesRequest,
			ParmansInstructions,
			RingOfTestimony2st,
			MaphrTabletFragment,
			PatternOfKeyhole,
			ClayDough,
			NikolasList,
			MandragoraPetal,
			CrimsonMoss,
			StakatoShell,
			ToadLordSac,
			SpiderThorn
		});
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("30104-04.htm"))
		{
			if (!st.getPlayer().getVarB("dd2"))
			{
				st.giveItems(7562, 50);
				st.getPlayer().setVar("dd2", "1", -1);
			}
			st.playSound(SOUND_ACCEPT);
			st.giveItems(RingOfTestimony1st, 1);
			st.setCond(1);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30466-03.htm"))
		{
			st.giveItems(BrightsList, 1);
		}
		else if (event.equalsIgnoreCase("30620-03.htm"))
		{
			st.takeItems(MandragoraBouquet, -1);
			st.giveItems(EmilysRecipe, 1);
			htmltext = "30620-03.htm";
			if (st.getQuestItemsCount(OldAccountBook) > 0 && st.getQuestItemsCount(BlessedSeed) > 0 && st.getQuestItemsCount(EmilysRecipe) > 0 && st.getQuestItemsCount(LilithsElvenWafer) > 0)
			{
				st.setCond(2);
				st.setState(STARTED);
			}
		}
		else if (event.equalsIgnoreCase("30597-02.htm"))
		{
			st.giveItems(BlessedSeed, 1);
			if (st.getQuestItemsCount(OldAccountBook) > 0 && st.getQuestItemsCount(BlessedSeed) > 0 && st.getQuestItemsCount(EmilysRecipe) > 0 && st.getQuestItemsCount(LilithsElvenWafer) > 0)
			{
				st.setCond(2);
				st.setState(STARTED);
			}
		}
		else if (event.equalsIgnoreCase("30005-04.htm"))
		{
			st.giveItems(CrystalBrooch, 1);
		}
		else if (event.equalsIgnoreCase("30368-03.htm"))
		{
			st.takeItems(CrystalBrooch, -1);
			st.giveItems(LilithsElvenWafer, 1);
			if (st.getQuestItemsCount(OldAccountBook) > 0 && st.getQuestItemsCount(BlessedSeed) > 0 && st.getQuestItemsCount(EmilysRecipe) > 0 && st.getQuestItemsCount(LilithsElvenWafer) > 0)
			{
				st.setCond(2);
				st.setState(STARTED);
			}
		}
		else if (event.equalsIgnoreCase("30531-03.htm"))
		{
			st.giveItems(CollectionLicense, 1);
			st.giveItems(Lockirins1stNotice, 1);
			st.giveItems(Lockirins2stNotice, 1);
			st.giveItems(Lockirins3stNotice, 1);
			st.giveItems(Lockirins4stNotice, 1);
			st.giveItems(Lockirins5stNotice, 1);
		}
		else if (event.equalsIgnoreCase("30555-02.htm"))
		{
			st.giveItems(ProcurationOfTorocco, 1);
		}
		else if (event.equalsIgnoreCase("30534-03a.htm") && st.getQuestItemsCount(ADENA_ID) >= 5000)
		{
			htmltext = "30534-03b.htm";
			st.takeItems(ADENA_ID, 5000);
			st.takeItems(ProcurationOfTorocco, -1);
			st.giveItems(ReceiptOfContribution3st, 1);
		}
		else if (event.equalsIgnoreCase("30104-07.htm"))
		{
			st.takeItems(RingOfTestimony1st, -1);
			st.takeItems(OldAccountBook, -1);
			st.takeItems(BlessedSeed, -1);
			st.takeItems(EmilysRecipe, -1);
			st.takeItems(LilithsElvenWafer, -1);
			if (st.getPlayer().getLevel() < 38)
			{
				st.giveItems(ParmansInstructions, 1);
				st.setCond(3);
				st.setState(STARTED);
			}
			else
			{
				st.giveItems(ParmansLetter, 1);
				st.giveItems(RingOfTestimony2st, 1);
				htmltext = "30104-08.htm";
				st.setCond(4);
				st.setState(STARTED);
			}
		}
		else if (event.equalsIgnoreCase("30621-04.htm"))
		{
			st.giveItems(ClayDough, 1);
			st.setCond(5);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30622-02.htm"))
		{
			st.takeItems(ClayDough, -1);
			st.giveItems(PatternOfKeyhole, 1);
			st.setCond(6);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30622-04.htm"))
		{
			st.takeItems(NikolasList, -1);
			st.takeItems(KeyOfTitan, 1);
			st.giveItems(MaphrTabletFragment, 1);
			st.setCond(9);
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
		if (npcId == Parman)
		{
			if (st.getQuestItemsCount(MarkOfProsperity) != 0)
			{
				htmltext = "completed";
				st.exitCurrentQuest(true);
			}
			else
			{
				switch (cond)
				{
				case 0:
					if (st.getPlayer().getRace() == Race.dwarf)
					{
						if (st.getPlayer().getLevel() >= 37)
						{
							htmltext = "30104-03.htm";
						}
						else
						{
							htmltext = "30104-02.htm";
							st.exitCurrentQuest(true);
						}
					}
					else
					{
						htmltext = "30104-01.htm";
						st.exitCurrentQuest(true);
					}
					break;
				case 1:
					htmltext = "30104-05.htm";
					break;
				case 2:
					if (st.getQuestItemsCount(OldAccountBook) > 0 && st.getQuestItemsCount(BlessedSeed) > 0 && st.getQuestItemsCount(EmilysRecipe) > 0 && st.getQuestItemsCount(LilithsElvenWafer) > 0)
					{
						htmltext = "30104-06.htm";
					}
					break;
				default:
					if (cond == 3 && st.getQuestItemsCount(ParmansInstructions) > 0)
					{
						if (st.getPlayer().getLevel() < 38)
						{
							htmltext = "30104-09.htm";
						}
						else
						{
							htmltext = "30104-10.htm";
							st.takeItems(ParmansInstructions, -1);
							st.giveItems(RingOfTestimony2st, 1);
							st.giveItems(ParmansLetter, 1);
							st.setCond(4);
							st.setState(STARTED);
						}
					}
					else if (cond == 4 && st.getQuestItemsCount(RingOfTestimony2st) > 0 && st.getQuestItemsCount(ParmansLetter) > 0 && st.getQuestItemsCount(MaphrTabletFragment) == 0)
					{
						htmltext = "30104-11.htm";
					}
					else if (cond >= 5 && cond <= 7)
					{
						htmltext = "30104-12.htm";
					}
					else if (cond == 9)
					{
						if (!st.getPlayer().getVarB("prof2.2"))
						{
							st.addExpAndSp(599979, 40040);
							st.giveItems(57, 108841);
							st.getPlayer().setVar("prof2.2", "1", -1);
						}
						st.takeItems(RingOfTestimony2st, -1);
						st.takeItems(MaphrTabletFragment, -1);
						st.giveItems(MarkOfProsperity, 1);
						htmltext = "30104-13.htm";
						st.playSound(SOUND_FINISH);
						st.exitCurrentQuest(true);
					}
					break;
				}
			}
		}
		else if (npcId == Lockirin)
		{
			if (st.getQuestItemsCount(CollectionLicense) == 0)
			{
				htmltext = "30531-01.htm";
			}
			else if (st.getQuestItemsCount(CollectionLicense) > 0)
			{
				if (st.getQuestItemsCount(ReceiptOfContribution1st) > 0 && st.getQuestItemsCount(ReceiptOfContribution2st) > 0 && st.getQuestItemsCount(ReceiptOfContribution3st) > 0 && st.getQuestItemsCount(ReceiptOfContribution4st) > 0 && st.getQuestItemsCount(ReceiptOfContribution5st) > 0)
				{
					htmltext = "30531-05.htm";
					st.takeItems(CollectionLicense, -1);
					st.takeItems(ReceiptOfContribution1st, -1);
					st.takeItems(ReceiptOfContribution2st, -1);
					st.takeItems(ReceiptOfContribution3st, -1);
					st.takeItems(ReceiptOfContribution4st, -1);
					st.takeItems(ReceiptOfContribution5st, -1);
					st.giveItems(OldAccountBook, 1);
					if (st.getQuestItemsCount(OldAccountBook) > 0 && st.getQuestItemsCount(BlessedSeed) > 0 && st.getQuestItemsCount(EmilysRecipe) > 0 && st.getQuestItemsCount(LilithsElvenWafer) > 0)
					{
						st.setCond(2);
					}
				}
				else
				{
					htmltext = "30531-04.htm";
				}
			}
			else if (cond >= 1 && st.getQuestItemsCount(RingOfTestimony1st) > 0 && st.getQuestItemsCount(OldAccountBook) > 0 && st.getQuestItemsCount(CollectionLicense) == 0)
			{
				htmltext = "30531-06.htm";
			}
			else if (cond >= 1 && st.getQuestItemsCount(RingOfTestimony2st) > 0)
			{
				htmltext = "30531-07.htm";
			}
		}
		else if (npcId == Spiron && cond == 1 && st.getQuestItemsCount(CollectionLicense) > 0)
		{
			if (st.getQuestItemsCount(Lockirins1stNotice) > 0)
			{
				htmltext = "30532-01.htm";
				st.takeItems(Lockirins1stNotice, -1);
			}
			else if (st.getQuestItemsCount(ReceiptOfContribution1st) == 0 && st.getQuestItemsCount(ContributionOfShari) == 0)
			{
				htmltext = "30532-02.htm";
			}
			else if (st.getQuestItemsCount(ContributionOfShari) > 0)
			{
				st.takeItems(ContributionOfShari, -1);
				st.giveItems(ReceiptOfContribution1st, 1);
				htmltext = "30532-03.htm";
			}
			else if (st.getQuestItemsCount(ReceiptOfContribution1st) > 0)
			{
				htmltext = "30532-04.htm";
			}
		}
		else if (npcId == Shari && cond == 1 && st.getQuestItemsCount(CollectionLicense) > 0 && st.getQuestItemsCount(Lockirins1stNotice) == 0 && st.getQuestItemsCount(ReceiptOfContribution1st) == 0)
		{
			if (st.getQuestItemsCount(ContributionOfShari) == 0)
			{
				st.giveItems(ContributionOfShari, 1);
				htmltext = "30517-01.htm";
			}
			else
			{
				htmltext = "30517-02.htm";
			}
		}
		else if (npcId == Balanki && cond == 1 && st.getQuestItemsCount(CollectionLicense) > 0)
		{
			if (st.getQuestItemsCount(Lockirins2stNotice) > 0)
			{
				htmltext = "30533-01.htm";
				st.takeItems(Lockirins2stNotice, -1);
			}
			else if (st.getQuestItemsCount(ReceiptOfContribution2st) == 0 && (st.getQuestItemsCount(ContributionOfMion) == 0 || st.getQuestItemsCount(ContributionOfMaryse) == 0))
			{
				htmltext = "30533-02.htm";
			}
			else if (st.getQuestItemsCount(ContributionOfMion) != 0 && st.getQuestItemsCount(ContributionOfMaryse) != 0)
			{
				htmltext = "30533-03.htm";
				st.takeItems(ContributionOfMaryse, -1);
				st.takeItems(ContributionOfMion, -1);
				st.giveItems(ReceiptOfContribution2st, 1);
			}
			else if (st.getQuestItemsCount(ReceiptOfContribution2st) > 0)
			{
				htmltext = "30533-04.htm";
			}
		}
		else if (npcId == Mion && cond == 1 && st.getQuestItemsCount(CollectionLicense) > 0 && st.getQuestItemsCount(Lockirins2stNotice) == 0 && st.getQuestItemsCount(ReceiptOfContribution2st) == 0)
		{
			if (st.getQuestItemsCount(ContributionOfMion) == 0)
			{
				htmltext = "30519-01.htm";
				st.giveItems(ContributionOfMion, 1);
			}
			else
			{
				htmltext = "30519-02.htm";
			}
		}
		else if (npcId == Redbonnet && cond == 1 && st.getQuestItemsCount(CollectionLicense) > 0 && st.getQuestItemsCount(Lockirins2stNotice) == 0 && st.getQuestItemsCount(ReceiptOfContribution2st) == 0)
		{
			if (st.getQuestItemsCount(MarysesRequest) == 0 && st.getQuestItemsCount(ContributionOfMaryse) == 0)
			{
				htmltext = "30553-01.htm";
				st.giveItems(MarysesRequest, 1);
			}
			else if (st.getQuestItemsCount(MarysesRequest) > 0 && st.getQuestItemsCount(ContributionOfMaryse) == 0)
			{
				if (st.getQuestItemsCount(AnimalSkin) < 100)
				{
					htmltext = "30553-02.htm";
				}
				else
				{
					htmltext = "30553-03.htm";
					st.takeItems(AnimalSkin, 100);
					st.takeItems(MarysesRequest, -1);
					st.giveItems(ContributionOfMaryse, 1);
				}
			}
			else if (st.getQuestItemsCount(ContributionOfMaryse) > 0)
			{
				htmltext = "30553-04.htm";
			}
		}
		else if (npcId == Keef && cond == 1 && st.getQuestItemsCount(CollectionLicense) > 0)
		{
			if (st.getQuestItemsCount(Lockirins3stNotice) > 0)
			{
				htmltext = "30534-01.htm";
				st.takeItems(Lockirins3stNotice, -1);
			}
			else if (st.getQuestItemsCount(ReceiptOfContribution3st) == 0 && st.getQuestItemsCount(ProcurationOfTorocco) == 0)
			{
				htmltext = "30534-02.htm";
			}
			else if (st.getQuestItemsCount(ReceiptOfContribution3st) == 0 && st.getQuestItemsCount(ProcurationOfTorocco) > 0)
			{
				htmltext = "30534-03.htm";
			}
			else if (st.getQuestItemsCount(ReceiptOfContribution3st) > 0)
			{
				htmltext = "30534-04.htm";
			}
		}
		else if (npcId == Torocco && cond == 1 && st.getQuestItemsCount(CollectionLicense) > 0 && st.getQuestItemsCount(Lockirins3stNotice) == 0 && st.getQuestItemsCount(ReceiptOfContribution3st) == 0)
		{
			if (st.getQuestItemsCount(ProcurationOfTorocco) == 0)
			{
				htmltext = "30555-01.htm";
			}
			else if (st.getQuestItemsCount(ProcurationOfTorocco) > 0)
			{
				htmltext = "30555-03.htm";
			}
		}
		else if (npcId == Filaur && cond == 1 && st.getQuestItemsCount(CollectionLicense) > 0)
		{
			if (st.getQuestItemsCount(Lockirins4stNotice) > 0)
			{
				htmltext = "30535-01.htm";
				st.takeItems(Lockirins4stNotice, -1);
			}
			else if (st.getQuestItemsCount(ReceiptOfContribution4st) == 0 && st.getQuestItemsCount(ReceiptOfBolter) == 0)
			{
				htmltext = "30535-02.htm";
			}
			else if (st.getQuestItemsCount(ReceiptOfBolter) > 0 && st.getQuestItemsCount(ReceiptOfContribution4st) == 0)
			{
				htmltext = "30535-03.htm";
				st.takeItems(ReceiptOfBolter, -1);
				st.giveItems(ReceiptOfContribution4st, 1);
			}
			else if (st.getQuestItemsCount(CollectionLicense) > 0 && st.getQuestItemsCount(ReceiptOfContribution4st) > 0 && st.getQuestItemsCount(ReceiptOfBolter) == 0 && st.getQuestItemsCount(Lockirins4stNotice) == 0)
			{
				htmltext = "30535-04.htm";
			}
		}
		else if (npcId == Bolter && cond == 1 && st.getQuestItemsCount(CollectionLicense) > 0 && st.getQuestItemsCount(Lockirins4stNotice) == 0 && st.getQuestItemsCount(ReceiptOfContribution4st) == 0)
		{
			if (st.getQuestItemsCount(ReceiptOfBolter) == 0)
			{
				htmltext = "30554-01.htm";
				st.giveItems(ReceiptOfBolter, 1);
			}
			else
			{
				htmltext = "30554-02.htm";
			}
		}
		else if (npcId == Arin && cond == 1 && st.getQuestItemsCount(CollectionLicense) > 0)
		{
			if (st.getQuestItemsCount(Lockirins5stNotice) > 0)
			{
				htmltext = "30536-01.htm";
				st.takeItems(Lockirins5stNotice, -1);
			}
			else if (st.getQuestItemsCount(ReceiptOfContribution5st) == 0 && st.getQuestItemsCount(ContributionOfToma) == 0)
			{
				htmltext = "30536-02.htm";
			}
			else if (st.getQuestItemsCount(ReceiptOfContribution5st) == 0 && st.getQuestItemsCount(ContributionOfToma) > 0)
			{
				htmltext = "30536-03.htm";
				st.takeItems(ContributionOfToma, -1);
				st.giveItems(ReceiptOfContribution5st, 1);
			}
			else
			{
				htmltext = "30536-04.htm";
			}
		}
		else if (npcId == Toma && cond == 1 && st.getQuestItemsCount(CollectionLicense) > 0 && st.getQuestItemsCount(Lockirins5stNotice) == 0 && st.getQuestItemsCount(ReceiptOfContribution5st) == 0)
		{
			if (st.getQuestItemsCount(ContributionOfToma) == 0)
			{
				htmltext = "30556-01.htm";
				st.giveItems(ContributionOfToma, 1);
			}
			else
			{
				htmltext = "30556-02.htm";
			}
		}
		else
		{
			switch (npcId)
			{
			case Piotur:
				if (cond == 1)
				{
					if (st.getQuestItemsCount(BlessedSeed) == 0)
					{
						htmltext = "30597-01.htm";
					}
					else
					{
						htmltext = "30597-03.htm";
					}
				}
				else if (st.getQuestItemsCount(RingOfTestimony2st) > 0)
				{
					htmltext = "30597-04.htm";
				}
				break;
			case Wilford:
				if (cond == 1)
				{
					if (st.getQuestItemsCount(LilithsElvenWafer) == 0 && st.getQuestItemsCount(CrystalBrooch) == 0)
					{
						htmltext = "30005-01.htm";
					}
					else if (st.getQuestItemsCount(LilithsElvenWafer) == 0 && st.getQuestItemsCount(CrystalBrooch) > 0)
					{
						htmltext = "30005-05.htm";
					}
					else if (st.getQuestItemsCount(LilithsElvenWafer) > 0)
					{
						htmltext = "30005-06.htm";
					}
				}
				else if (st.getQuestItemsCount(RingOfTestimony2st) > 0)
				{
					htmltext = "30005-07.htm";
				}
				break;
			case Lilith:
				if (cond == 1)
				{
					if (st.getQuestItemsCount(CrystalBrooch) > 0 && st.getQuestItemsCount(LilithsElvenWafer) == 0)
					{
						htmltext = "30368-01.htm";
					}
					else if (st.getQuestItemsCount(LilithsElvenWafer) > 0 && st.getQuestItemsCount(CrystalBrooch) == 0)
					{
						htmltext = "30368-04.htm";
					}
				}
				else if (st.getQuestItemsCount(RingOfTestimony2st) > 0)
				{
					htmltext = "30368-05.htm";
				}
				break;
			case Bright:
				if (cond == 1)
				{
					if (st.getQuestItemsCount(BrightsList) == 0 && st.getQuestItemsCount(EmilysRecipe) == 0 && st.getQuestItemsCount(MandragoraBouquet) == 0)
					{
						htmltext = "30466-01.htm";
					}
					else if (st.getQuestItemsCount(MandragoraPetal) < 20 || st.getQuestItemsCount(CrimsonMoss) < 10)
					{
						htmltext = "30466-04.htm";
					}
					else if (st.getQuestItemsCount(MandragoraPetal) >= 20 || st.getQuestItemsCount(CrimsonMoss) >= 10)
					{
						st.takeItems(BrightsList, -1);
						st.takeItems(MandragoraPetal, -1);
						st.takeItems(CrimsonMoss, -1);
						st.giveItems(MandragoraBouquet, 1);
						htmltext = "30466-05.htm";
					}
					else if (st.getQuestItemsCount(MandragoraBouquet) > 0 && st.getQuestItemsCount(EmilysRecipe) == 0)
					{
						htmltext = "30466-06.htm";
					}
					else if (st.getQuestItemsCount(EmilysRecipe) > 0)
					{
						htmltext = "30466-07.htm";
					}

				}
				else if (st.getQuestItemsCount(RingOfTestimony2st) > 0)
				{
					htmltext = "30466-08.htm";
				}
				break;
			case Emily:
				if (cond == 1)
				{
					if (st.getQuestItemsCount(MandragoraBouquet) != 0)
					{
						htmltext = "30620-01.htm";
					}
					else
					{
						htmltext = "30620-04.htm";
					}
				}
				else if (st.getQuestItemsCount(RingOfTestimony2st) > 0)
				{
					htmltext = "30620-05.htm";
				}
				break;
			case Nikola:
				switch (cond)
				{
				case 4:
					htmltext = "30621-01.htm";
					break;
				case 5:
					htmltext = "30621-05.htm";
					break;
				case 6:
					st.takeItems(PatternOfKeyhole, -1);
					st.giveItems(NikolasList, 1);
					st.giveItems(RecipeTitanKey, 1);
					htmltext = "30621-06.htm";
					st.setCond(7);
					st.setState(STARTED);
					break;
				case 7:
					htmltext = "30621-07.htm";
					break;
				default:
					if (cond == 8 && st.getQuestItemsCount(KeyOfTitan) > 0)
					{
						htmltext = "30621-08.htm";
					}
					else if (cond == 9)
					{
						htmltext = "30621-09.htm";
					}
					break;
				}
				break;
			case BoxOfTitan:
				if (cond == 5)
				{
					htmltext = "30622-01.htm";
				}
				else if (cond == 8 && st.getQuestItemsCount(KeyOfTitan) > 0)
				{
					htmltext = "30622-03.htm";
				}
				break;
			default:
				break;
			}
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
		if (cond == 7 && st.getQuestItemsCount(StakatoShell) >= 20 && st.getQuestItemsCount(ToadLordSac) >= 10 && st.getQuestItemsCount(SpiderThorn) >= 10)
		{
			st.setCond(8);
			st.setState(STARTED);
		}
		return null;
	}
}