package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * Квест Path To Orc Monk
 *
 * @author Sergey Ibryaev aka Artful
 */

public class _415_PathToOrcMonk extends Quest implements ScriptFile
{
	// NPC
	private static final int Urutu = 30587;
	private static final int Rosheek = 30590;
	private static final int Kasman = 30501;
	private static final int Toruku = 30591;
	// Quest Items
	private static final int Pomegranate = 1593;
	private static final int KashaBearClaw = 1600;
	private static final int KashaBladeSpiderTalon = 1601;
	private static final int ScarletSalamanderScale = 1602;
	private static final int LeatherPouch1st = 1594;
	private static final int LeatherPouchFull1st = 1597;
	private static final int LeatherPouch2st = 1595;
	private static final int LeatherPouchFull2st = 1598;
	private static final int LeatherPouch3st = 1596;
	private static final int LeatherPouchFull3st = 1599;
	private static final int LeatherPouch4st = 1607;
	private static final int LeatherPouchFull4st = 1608;
	private static final int FierySpiritScroll = 1603;
	private static final int RosheeksLetter = 1604;
	private static final int GantakisLetterOfRecommendation = 1605;
	private static final int Fig = 1606;
	private static final int VukuOrcTusk = 1609;
	private static final int RatmanFang = 1610;
	private static final int LangkLizardmanTooth = 1611;
	private static final int FelimLizardmanTooth = 1612;
	private static final int IronWillScroll = 1613;
	private static final int TorukusLetter = 1614;
	// Items
	private static final int KhavatariTotem = 1615;
	// MOB
	private static final int KashaBear = 20479;
	private static final int KashaBladeSpider = 20478;
	private static final int ScarletSalamander = 20415;
	private static final int VukuOrcFighter = 20017;
	private static final int RatmanWarrior = 20359;
	private static final int LangkLizardmanWarrior = 20024;
	private static final int FelimLizardmanWarrior = 20014;
	// Drop Cond
	// # [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	private static final int[][] DROPLIST_COND =
	{
		{
			2,
			3,
			KashaBear,
			LeatherPouch1st,
			KashaBearClaw,
			5,
			70,
			1
		},
		{
			4,
			5,
			KashaBladeSpider,
			LeatherPouch2st,
			KashaBladeSpiderTalon,
			5,
			70,
			1
		},
		{
			6,
			7,
			ScarletSalamander,
			LeatherPouch3st,
			ScarletSalamanderScale,
			5,
			70,
			1
		},
		{
			11,
			0,
			VukuOrcFighter,
			LeatherPouch4st,
			VukuOrcTusk,
			3,
			70,
			1
		},
		{
			11,
			0,
			RatmanWarrior,
			LeatherPouch4st,
			RatmanFang,
			3,
			70,
			1
		},
		{
			11,
			0,
			LangkLizardmanWarrior,
			LeatherPouch4st,
			LangkLizardmanTooth,
			3,
			70,
			1
		},
		{
			11,
			0,
			FelimLizardmanWarrior,
			LeatherPouch4st,
			FelimLizardmanTooth,
			3,
			70,
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

	public _415_PathToOrcMonk()
	{
		super(false);

		addStartNpc(Urutu);

		addTalkId(Rosheek);
		addTalkId(Kasman);
		addTalkId(Toruku);

		// Mob Drop
		for (int i = 0; i < DROPLIST_COND.length; i++)
		{
			addKillId(DROPLIST_COND[i][2]);
			addQuestItem(DROPLIST_COND[i][4]);
		}
		addQuestItem(new int[]
		{
			Pomegranate,
			LeatherPouch1st,
			LeatherPouchFull1st,
			LeatherPouch2st,
			LeatherPouchFull2st,
			LeatherPouch3st,
			LeatherPouchFull3st,
			Fig,
			FierySpiritScroll,
			RosheeksLetter,
			GantakisLetterOfRecommendation,
			LeatherPouch4st,
			LeatherPouchFull4st,
			IronWillScroll,
			TorukusLetter
		});
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("gantaki_zu_urutu_q0415_06.htm"))
		{
			st.giveItems(Pomegranate, 1);
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
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
		case Urutu:
			if (st.getQuestItemsCount(KhavatariTotem) != 0)
			{
				htmltext = "gantaki_zu_urutu_q0415_04.htm";
				st.exitCurrentQuest(true);
			}
			else if (cond == 0)
			{
				if (st.getPlayer().getClassId().getId() != 0x2c)
				{
					if (st.getPlayer().getClassId().getId() == 0x2f)
					{
						htmltext = "gantaki_zu_urutu_q0415_02a.htm";
					}
					else
					{
						htmltext = "gantaki_zu_urutu_q0415_02.htm";
					}
					st.exitCurrentQuest(true);
				}
				else if (st.getPlayer().getLevel() < 18)
				{
					htmltext = "gantaki_zu_urutu_q0415_03.htm";
					st.exitCurrentQuest(true);
				}
				else
				{
					htmltext = "gantaki_zu_urutu_q0415_01.htm";
				}
			}
			else if (cond == 1)
			{
				htmltext = "gantaki_zu_urutu_q0415_07.htm";
			}
			else if (cond >= 2 && cond <= 7)
			{
				htmltext = "gantaki_zu_urutu_q0415_08.htm";
			}
			else if (cond == 8)
			{
				st.takeItems(RosheeksLetter, 1);
				st.giveItems(GantakisLetterOfRecommendation, 1);
				htmltext = "gantaki_zu_urutu_q0415_09.htm";
				st.setCond(9);
				st.setState(STARTED);
			}
			else if (cond == 9)
			{
				htmltext = "gantaki_zu_urutu_q0415_10.htm";
			}
			else if (cond >= 10)
			{
				htmltext = "gantaki_zu_urutu_q0415_11.htm";
			}
			break;
		case Rosheek:
			switch (cond)
			{
			case 1:
				st.takeItems(Pomegranate, -1);
				st.giveItems(LeatherPouch1st, 1);
				htmltext = "khavatari_rosheek_q0415_01.htm";
				st.setCond(2);
				st.setState(STARTED);
				break;
			case 2:
				htmltext = "khavatari_rosheek_q0415_02.htm";
				break;
			case 3:
				htmltext = "khavatari_rosheek_q0415_03.htm";
				st.takeItems(LeatherPouchFull1st, -1);
				st.giveItems(LeatherPouch2st, 1);
				st.setCond(4);
				st.setState(STARTED);
				break;
			case 4:
				htmltext = "khavatari_rosheek_q0415_04.htm";
				break;
			case 5:
				st.takeItems(LeatherPouchFull2st, -1);
				st.giveItems(LeatherPouch3st, 1);
				htmltext = "khavatari_rosheek_q0415_05.htm";
				st.setCond(6);
				st.setState(STARTED);
				break;
			case 6:
				htmltext = "khavatari_rosheek_q0415_06.htm";
				break;
			case 7:
				st.takeItems(LeatherPouchFull3st, -1);
				st.giveItems(FierySpiritScroll, 1);
				st.giveItems(RosheeksLetter, 1);
				htmltext = "khavatari_rosheek_q0415_07.htm";
				st.setCond(8);
				st.setState(STARTED);
				break;
			case 8:
				htmltext = "khavatari_rosheek_q0415_08.htm";
				break;
			case 9:
				htmltext = "khavatari_rosheek_q0415_09.htm";
				break;
			default:
				break;
			}
			break;
		case Kasman:
			switch (cond)
			{
			case 9:
				st.takeItems(GantakisLetterOfRecommendation, -1);
				st.giveItems(Fig, 1);
				htmltext = "prefect_kasman_q0415_01.htm";
				st.setCond(10);
				st.setState(STARTED);
				break;
			case 10:
				htmltext = "prefect_kasman_q0415_02.htm";
				break;
			case 11:
			case 12:
				htmltext = "prefect_kasman_q0415_03.htm";
				break;
			case 13:
				st.takeItems(FierySpiritScroll, -1);
				st.takeItems(IronWillScroll, -1);
				st.takeItems(TorukusLetter, -1);
				htmltext = "prefect_kasman_q0415_04.htm";
				if (st.getPlayer().getClassId().getLevel() == 1)
				{
					st.giveItems(KhavatariTotem, 1);
					if (!st.getPlayer().getVarB("prof1"))
					{
						st.getPlayer().setVar("prof1", "1", -1);
						st.addExpAndSp(228064, 16455);
						// FIXME [G1ta0] дать адены, только если первый чар на акке
						st.giveItems(ADENA_ID, 81900);
					}
				}
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
				break;
			default:
				break;
			}
			break;
		case Toruku:
			switch (cond)
			{
			case 10:
				st.takeItems(Fig, -1);
				st.giveItems(LeatherPouch4st, 1);
				htmltext = "khavatari_toruku_q0415_01.htm";
				st.setCond(11);
				st.setState(STARTED);
				break;
			case 11:
				htmltext = "khavatari_toruku_q0415_02.htm";
				break;
			case 12:
				st.takeItems(LeatherPouchFull4st, -1);
				st.giveItems(IronWillScroll, 1);
				st.giveItems(TorukusLetter, 1);
				htmltext = "khavatari_toruku_q0415_03.htm";
				st.setCond(13);
				st.setState(STARTED);
				break;
			case 13:
				htmltext = "khavatari_toruku_q0415_04.htm";
				break;
			default:
				break;
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
		if (cond == 3 && st.getQuestItemsCount(LeatherPouchFull1st) == 0)
		{
			st.takeItems(KashaBearClaw, -1);
			st.takeItems(LeatherPouch1st, -1);
			st.giveItems(LeatherPouchFull1st, 1);
		}
		else if (cond == 5 && st.getQuestItemsCount(LeatherPouchFull2st) == 0)
		{
			st.takeItems(KashaBladeSpiderTalon, -1);
			st.takeItems(LeatherPouch2st, -1);
			st.giveItems(LeatherPouchFull2st, 1);
		}
		else if (cond == 7 && st.getQuestItemsCount(LeatherPouchFull3st) == 0)
		{
			st.takeItems(ScarletSalamanderScale, -1);
			st.takeItems(LeatherPouch3st, -1);
			st.giveItems(LeatherPouchFull3st, 1);
		}
		else if (cond == 11 && st.getQuestItemsCount(RatmanFang) >= 3 && st.getQuestItemsCount(LangkLizardmanTooth) >= 3 && st.getQuestItemsCount(FelimLizardmanTooth) >= 3 && st.getQuestItemsCount(VukuOrcTusk) >= 3)
		{
			st.takeItems(VukuOrcTusk, -1);
			st.takeItems(RatmanFang, -1);
			st.takeItems(LangkLizardmanTooth, -1);
			st.takeItems(FelimLizardmanTooth, -1);
			st.takeItems(LeatherPouch4st, -1);
			st.giveItems(LeatherPouchFull4st, 1);
			st.setCond(12);
			st.setState(STARTED);
		}
		return null;
	}
}