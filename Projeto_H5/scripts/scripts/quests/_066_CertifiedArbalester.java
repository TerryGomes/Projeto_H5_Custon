package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * Квест на вторую профессию Certified Arbalester
 *
 * @author Sergey Ibryaev aka Artful
 */
public class _066_CertifiedArbalester extends Quest implements ScriptFile
{
	// NPC
	private static final int Rindy = 32201;
	private static final int Clayton = 30464;
	private static final int Poitan = 30458;
	private static final int Holvas = 30058;
	private static final int Meldina = 32214;
	private static final int Selsia = 32220;
	private static final int Gaius = 30171;
	private static final int Gauen = 30717;
	private static final int Kaiena = 30720;
	// Mobs
	private static final int WatchmanofthePlains = 21102;
	private static final int RoughlyHewnRockGolem = 21103;
	private static final int DeluLizardmanSupplier = 21104;
	private static final int DeluLizardmanSpecialAgent = 21105;
	private static final int CursedSeer = 21106;
	private static final int DeluLizardmanCommander = 21107;
	private static final int DeluLizardmanShaman = 20781;
	private static final int AmberBasilisk = 20199;
	private static final int Strain = 20200;
	private static final int Ghoul = 20201;
	private static final int GraniteGolem = 20083;
	private static final int DeadSeeker = 20202;
	private static final int Grandis = 20554;
	private static final int ManashenGargoyle = 20563;
	private static final int TimakOrcArcher = 20584;
	private static final int TimakOrcSoldier = 20585;
	private static final int CrimsonLady = 27336;

	// Quest Items
	private static final int EnmityCrystal = 9773;
	private static final int EnmityCrystalCore = 9774;
	private static final int ManuscriptPage = 9775;
	private static final int KamaelInquisitorTraineeMark = 9777;
	private static final int FragmentofAttackOrders = 9778;
	private static final int ManashenTalisman = 9780;
	private static final int ResearchOnTheGiantsAndTheAncientRace = 9781;

	// Items
	private static final int KamaelInquisitorMark = 9782;
	// Drop Cond
	// # [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	private static final int[][] DROPLIST_COND =
	{
		{
			3,
			4,
			WatchmanofthePlains,
			0,
			EnmityCrystal,
			30,
			25,
			1
		},
		{
			3,
			4,
			RoughlyHewnRockGolem,
			0,
			EnmityCrystal,
			30,
			25,
			1
		},
		{
			3,
			4,
			DeluLizardmanSupplier,
			0,
			EnmityCrystal,
			30,
			25,
			1
		},
		{
			3,
			4,
			DeluLizardmanSpecialAgent,
			0,
			EnmityCrystal,
			30,
			25,
			1
		},
		{
			3,
			4,
			CursedSeer,
			0,
			EnmityCrystal,
			30,
			25,
			1
		},
		{
			3,
			4,
			DeluLizardmanCommander,
			0,
			EnmityCrystal,
			30,
			25,
			1
		},
		{
			3,
			4,
			DeluLizardmanShaman,
			0,
			EnmityCrystal,
			30,
			25,
			1
		},
		{
			7,
			8,
			AmberBasilisk,
			0,
			ManuscriptPage,
			30,
			25,
			1
		},
		{
			7,
			8,
			Strain,
			0,
			ManuscriptPage,
			30,
			25,
			1
		},
		{
			7,
			8,
			Ghoul,
			0,
			ManuscriptPage,
			30,
			25,
			1
		},
		{
			7,
			8,
			GraniteGolem,
			0,
			ManuscriptPage,
			30,
			25,
			1
		},
		{
			7,
			8,
			DeadSeeker,
			0,
			ManuscriptPage,
			30,
			25,
			1
		},
		{
			11,
			12,
			Grandis,
			0,
			FragmentofAttackOrders,
			10,
			20,
			1
		},
		{
			15,
			16,
			ManashenGargoyle,
			0,
			ManashenTalisman,
			10,
			20,
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

	public _066_CertifiedArbalester()
	{
		super(false);

		addStartNpc(Rindy);

		addTalkId(Clayton);
		addTalkId(Poitan);
		addTalkId(Holvas);
		addTalkId(Meldina);
		addTalkId(Selsia);
		addTalkId(Gaius);
		addTalkId(Gauen);
		addTalkId(Kaiena);

		addQuestItem(new int[]
		{
			EnmityCrystalCore,
			KamaelInquisitorTraineeMark,
			ResearchOnTheGiantsAndTheAncientRace,
			EnmityCrystal,
			ManuscriptPage,
			FragmentofAttackOrders,
			ManashenTalisman
		});

		for (int i = 0; i < DROPLIST_COND.length; i++)
		{
			addKillId(DROPLIST_COND[i][2]);
		}

		addKillId(TimakOrcArcher);
		addKillId(TimakOrcSoldier);
		addKillId(CrimsonLady);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("32201-05.htm"))
		{
			st.setCond(2);
			st.set("id", "0");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			if (!st.getPlayer().getVarB("dd1"))
			{
				st.giveItems(7562, 64);
				st.getPlayer().setVar("dd1", "1", -1);
			}
		}
		else if (event.equalsIgnoreCase("30464-05.htm"))
		{
			st.setCond(3);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30464-09.htm"))
		{
			st.set("id", "0");
			st.takeItems(EnmityCrystalCore, 1);
			st.setCond(5);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30458-07.htm"))
		{
			st.takeItems(EnmityCrystalCore, -1);
			st.setCond(6);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30058-04.htm"))
		{
			st.setCond(7);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30058-07.htm"))
		{
			st.takeItems(ManuscriptPage, -1);
			st.setCond(9);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("32214-03.htm"))
		{
			st.giveItems(KamaelInquisitorTraineeMark, 1);
			st.setCond(10);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("32220-08.htm"))
		{
			st.setCond(11);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30171-05.htm"))
		{
			st.takeItems(FragmentofAttackOrders, -1);
			st.takeItems(KamaelInquisitorTraineeMark, -1); // возможно не тут забирают
			st.setCond(15);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30717-07.htm"))
		{
			st.takeItems(ManashenTalisman, -1);
			st.setCond(17);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30720-03.htm"))
		{
			st.setCond(18);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("32220-17.htm"))
		{
			st.setCond(19);
			st.setState(STARTED);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int id = st.getState();
		int cond = 0;
		if (id != CREATED)
		{
			cond = st.getCond();
		}
		switch (npcId)
		{
		case Rindy:
			if (st.getQuestItemsCount(KamaelInquisitorMark) > 0)
			{
				htmltext = "32201-00.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				switch (cond)
				{
				case 0:
					if (st.getPlayer().getClassId().getId() == 0x7e)
					{
						if (st.getPlayer().getLevel() >= 39)
						{
							htmltext = "32201-03.htm";
							st.setCond(1);
						}
						else
						{
							htmltext = "32201-02.htm";
							st.exitCurrentQuest(true);
						}
					}
					else
					{
						htmltext = "32201-01.htm";
						st.exitCurrentQuest(true);
					}
					break;
				case 1:
					htmltext = "32201-04.htm";
					break;
				case 2:
					htmltext = "32201-06.htm";
					break;
				default:
					break;
				}
			}
			break;
		case Clayton:
			switch (cond)
			{
			case 2:
				htmltext = "30464-01.htm";
				break;
			case 3:
				htmltext = "30464-06.htm";
				break;
			case 4:
				if (st.getInt("id") == 0 && st.getQuestItemsCount(EnmityCrystal) == 30)
				{
					htmltext = "30464-07.htm";
					st.takeItems(EnmityCrystal, -1);
					st.set("id", "1");
				}
				else if (st.getInt("id") == 1)
				{
					htmltext = "30464-08.htm";
				}
				else if (st.getInt("id") == 0 && st.getQuestItemsCount(EnmityCrystal) < 30)
				{
					htmltext = "30464-06.htm";
					st.setCond(2);
				}
				break;
			case 5:
				htmltext = "30464-10.htm";
				break;
			default:
				break;
			}
			break;
		case Poitan:
			if (cond == 5)
			{
				htmltext = "30458-01.htm";
			}
			else if (cond == 6)
			{
				htmltext = "30458-08.htm";
			}
			break;
		case Holvas:
			switch (cond)
			{
			case 6:
				htmltext = "30058-01.htm";
				break;
			case 7:
				htmltext = "30058-05.htm";
				break;
			case 8:
				if (st.getQuestItemsCount(ManuscriptPage) == 30)
				{
					htmltext = "30058-06.htm";
				}
				else
				{
					htmltext = "30058-05.htm";
					st.setCond(7);
				}
				break;
			case 9:
				htmltext = "30058-08.htm";
				break;
			default:
				break;
			}
			break;
		case Meldina:
			if (cond == 9)
			{
				htmltext = "32214-01.htm";
			}
			else if (cond == 10)
			{
				htmltext = "32214-04.htm";
			}
			break;
		case Selsia:
			switch (cond)
			{
			case 10:
				htmltext = "32220-01.htm";
				break;
			case 11:
				htmltext = "32220-09.htm";
				break;
			case 18:
				htmltext = "32220-10.htm";
				break;
			case 19:
				htmltext = "32220-18.htm";
				break;
			case 20:
				st.takeItems(ResearchOnTheGiantsAndTheAncientRace, -1);
				if (!st.getPlayer().getVarB("prof2.1"))
				{
					st.addExpAndSp(214773, 14738);
					st.giveItems(ADENA_ID, 38833);
					st.getPlayer().setVar("prof2.1", "1", -1);
				}
				st.giveItems(KamaelInquisitorMark, 1);
				htmltext = "32220-19.htm";
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
				break;
			default:
				break;
			}
			break;
		case Gaius:
			switch (cond)
			{
			case 12:
				htmltext = "30171-01.htm";
				break;
			case 15:
				htmltext = "30171-06.htm";
				break;
			case 16:
				if (st.getQuestItemsCount(ManashenTalisman) == 10)
				{
					htmltext = "30171-07.htm";
				}
				else
				{
					htmltext = "30171-06.htm";
					st.setCond(15);
				}
				break;
			default:
				break;
			}
			break;
		case Gauen:
			if (cond == 16)
			{
				if (st.getQuestItemsCount(ManashenTalisman) == 10)
				{
					htmltext = "30717-01.htm";
				}
				else
				{
					st.setCond(15);
				}
			}
			break;
		case Kaiena:
			if (cond == 17)
			{
				htmltext = "30720-01.htm";
			}
			else if (cond == 18)
			{
				htmltext = "30720-04.htm";
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
		if (cond == 19 && (npcId == TimakOrcArcher || npcId == TimakOrcSoldier))
		{
			if (st.getInt("id") < 20)
			{
				st.set("id", String.valueOf(st.getInt("id") + 1));
			}
			else if (Rnd.chance(25))
			{
				st.set("id", "0");
				st.addSpawn(CrimsonLady);
			}
		}
		else if (cond == 19 && npcId == CrimsonLady)
		{
			st.giveItems(ResearchOnTheGiantsAndTheAncientRace, 1);
			st.setCond(20);
			st.setState(STARTED);
		}
		return null;
	}
}
