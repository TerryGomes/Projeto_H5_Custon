package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;

/**
 * Квест Path To Orc Shaman
 *
 * @author Sergey Ibryaev aka Artful
 */
public class _416_PathToOrcShaman extends Quest implements ScriptFile
{
	// NPC
	private static final int Hestui = 30585;
	private static final int HestuiTotemSpirit = 30592;
	private static final int SeerUmos = 30502;
	private static final int DudaMaraTotemSpirit = 30593;
	private static final int SeerMoira = 31979;
	private static final int GandiTotemSpirit = 32057;
	private static final int LeopardCarcass = 32090;
	// Quest Items
	private static final int FireCharm = 1616;
	private static final int KashaBearPelt = 1617;
	private static final int KashaBladeSpiderHusk = 1618;
	private static final int FieryEgg1st = 1619;
	private static final int HestuiMask = 1620;
	private static final int FieryEgg2nd = 1621;
	private static final int TotemSpiritClaw = 1622;
	private static final int TatarusLetterOfRecommendation = 1623;
	private static final int FlameCharm = 1624;
	private static final int GrizzlyBlood = 1625;
	private static final int BloodCauldron = 1626;
	private static final int SpiritNet = 1627;
	private static final int BoundDurkaSpirit = 1628;
	private static final int DurkaParasite = 1629;
	private static final int TotemSpiritBlood = 1630;
	// Items
	private static final int MaskOfMedium = 1631;
	// MOB
	private static final int KashaBear = 20479;
	private static final int KashaBladeSpider = 20478;
	private static final int ScarletSalamander = 20415;
	private static final int GrizzlyBear = 20335;
	private static final int VenomousSpider = 20038;
	private static final int ArachnidTracker = 20043;
	private static final int QuestMonsterDurkaSpirit = 27056;
	private static final int QuestBlackLeopard = 27319;

	// Drop Cond
	// # [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	private static final int[][] DROPLIST_COND =
	{
		{
			1,
			0,
			KashaBear,
			FireCharm,
			KashaBearPelt,
			1,
			70,
			1
		},
		{
			1,
			0,
			KashaBladeSpider,
			FireCharm,
			KashaBladeSpiderHusk,
			1,
			70,
			1
		},
		{
			1,
			0,
			ScarletSalamander,
			FireCharm,
			FieryEgg1st,
			1,
			70,
			1
		},
		{
			6,
			7,
			GrizzlyBear,
			FlameCharm,
			GrizzlyBlood,
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

	public _416_PathToOrcShaman()
	{
		super(false);

		addStartNpc(Hestui);

		addTalkId(HestuiTotemSpirit, SeerUmos, DudaMaraTotemSpirit, SeerMoira, GandiTotemSpirit, LeopardCarcass);

		// Mob Drop
		for (int i = 0; i < DROPLIST_COND.length; i++)
		{
			addKillId(DROPLIST_COND[i][2]);
			addQuestItem(DROPLIST_COND[i][4]);
		}

		addKillId(VenomousSpider, ArachnidTracker, QuestMonsterDurkaSpirit, QuestBlackLeopard);
		addQuestItem(FireCharm, HestuiMask, FieryEgg2nd, TotemSpiritClaw, TatarusLetterOfRecommendation, FlameCharm, BloodCauldron, SpiritNet, BoundDurkaSpirit, DurkaParasite, TotemSpiritBlood);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("tataru_zu_hestui_q0416_06.htm"))
		{
			st.giveItems(FireCharm, 1);
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("hestui_totem_spirit_q0416_03.htm"))
		{
			st.takeItems(HestuiMask, -1);
			st.takeItems(FieryEgg2nd, -1);
			st.giveItems(TotemSpiritClaw, 1);
			st.setCond(4);
		}
		else if (event.equalsIgnoreCase("tataru_zu_hestui_q0416_11.htm"))
		{
			st.takeItems(TotemSpiritClaw, -1);
			st.giveItems(TatarusLetterOfRecommendation, 1);
			st.setCond(5);
		}
		else if (event.equalsIgnoreCase("tataru_zu_hestui_q0416_11c.htm"))
		{
			st.takeItems(TotemSpiritClaw, -1);
			st.setCond(12);
		}
		else if (event.equalsIgnoreCase("dudamara_totem_spirit_q0416_03.htm"))
		{
			st.takeItems(BloodCauldron, -1);
			st.giveItems(SpiritNet, 1);
			st.setCond(9);
		}
		else if (event.equalsIgnoreCase("seer_umos_q0416_07.htm"))
		{
			st.takeItems(TotemSpiritBlood, -1);
			if (st.getPlayer().getClassId().getLevel() == 1)
			{
				st.giveItems(MaskOfMedium, 1);
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
		}
		else if (event.equalsIgnoreCase("totem_spirit_gandi_q0416_02.htm"))
		{
			st.setCond(14);
		}
		else if (event.equalsIgnoreCase("dead_leopard_q0416_04.htm"))
		{
			st.setCond(18);
		}
		else if (event.equalsIgnoreCase("totem_spirit_gandi_q0416_05.htm"))
		{
			st.setCond(21);
		}
		if (event.equalsIgnoreCase("QuestMonsterDurkaSpirit_Fail"))
		{
			for (NpcInstance n : GameObjectsStorage.getAllByNpcId(QuestMonsterDurkaSpirit, false))
			{
				n.deleteMe();
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getCond();
		if (npcId == Hestui)
		{
			if (st.getQuestItemsCount(MaskOfMedium) != 0)
			{
				htmltext = "seer_umos_q0416_04.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				switch (cond)
				{
				case 0:
					if (st.getPlayer().getClassId().getId() != 0x31)
					{
						if (st.getPlayer().getClassId().getId() == 0x32)
						{
							htmltext = "tataru_zu_hestui_q0416_02a.htm";
						}
						else
						{
							htmltext = "tataru_zu_hestui_q0416_02.htm";
						}
						st.exitCurrentQuest(true);
					}
					else if (st.getPlayer().getLevel() < 18)
					{
						htmltext = "tataru_zu_hestui_q0416_03.htm";
						st.exitCurrentQuest(true);
					}
					else
					{
						htmltext = "tataru_zu_hestui_q0416_01.htm";
					}
					break;
				case 1:
					htmltext = "tataru_zu_hestui_q0416_07.htm";
					break;
				case 2:
					htmltext = "tataru_zu_hestui_q0416_08.htm";
					st.takeItems(KashaBearPelt, -1);
					st.takeItems(KashaBladeSpiderHusk, -1);
					st.takeItems(FieryEgg1st, -1);
					st.takeItems(FireCharm, -1);
					st.giveItems(HestuiMask, 1);
					st.giveItems(FieryEgg2nd, 1);
					st.setCond(3);
					break;
				case 3:
					htmltext = "tataru_zu_hestui_q0416_09.htm";
					break;
				case 4:
					htmltext = "tataru_zu_hestui_q0416_10.htm";
					break;
				case 5:
					htmltext = "tataru_zu_hestui_q0416_12.htm";
					break;
				default:
					if (cond > 5)
					{
						htmltext = "tataru_zu_hestui_q0416_13.htm";
					}
					break;
				}
			}
		}
		else if (npcId == HestuiTotemSpirit)
		{
			if (cond == 3)
			{
				htmltext = "hestui_totem_spirit_q0416_01.htm";
			}
			else if (cond == 4)
			{
				htmltext = "hestui_totem_spirit_q0416_04.htm";
			}
		}
		else if (npcId == HestuiTotemSpirit && st.getCond() > 0 && (st.getQuestItemsCount(GrizzlyBlood) > 0 || st.getQuestItemsCount(FlameCharm) > 0 || st.getQuestItemsCount(BloodCauldron) > 0 || st.getQuestItemsCount(SpiritNet) > 0 || st.getQuestItemsCount(BoundDurkaSpirit) > 0 || st.getQuestItemsCount(TotemSpiritBlood) > 0 || st.getQuestItemsCount(TatarusLetterOfRecommendation) > 0))
		{
			htmltext = "hestui_totem_spirit_q0416_05.htm";
		}
		else
		{
			switch (npcId)
			{
			case SeerUmos:
				switch (cond)
				{
				case 5:
					st.takeItems(TatarusLetterOfRecommendation, -1);
					st.giveItems(FlameCharm, 1);
					htmltext = "seer_umos_q0416_01.htm";
					st.setCond(6);
					break;
				case 6:
					htmltext = "seer_umos_q0416_02.htm";
					break;
				case 7:
					st.takeItems(GrizzlyBlood, -1);
					st.takeItems(FlameCharm, -1);
					st.giveItems(BloodCauldron, 1);
					htmltext = "seer_umos_q0416_03.htm";
					st.setCond(8);
					break;
				case 8:
					htmltext = "seer_umos_q0416_04.htm";
					break;
				case 9:
				case 10:
					htmltext = "seer_umos_q0416_05.htm";
					break;
				case 11:
					htmltext = "seer_umos_q0416_06.htm";
					break;
				default:
					break;
				}
				break;
			case SeerMoira:
				if (cond == 12)
				{
					htmltext = "seer_moirase_q0416_01.htm";
					st.setCond(13);
				}
				else if (cond > 12 && cond < 21)
				{
					htmltext = "seer_moirase_q0416_02.htm";
				}
				else if (cond == 21)
				{
					htmltext = "seer_moirase_q0416_03.htm";
					if (st.getPlayer().getClassId().getLevel() == 1)
					{
						st.giveItems(MaskOfMedium, 1);
						if (!st.getPlayer().getVarB("prof1"))
						{
							st.getPlayer().setVar("prof1", "1", -1);
							st.addExpAndSp(295862, 18194);
							// TODO [G1ta0] дать адены, если первый чар на акке 81900 х Config.RATE_QUESTS_OCCUPATION_CHANGE
							// st.giveItems(ADENA_ID, 81900);
						}
					}
					st.playSound(SOUND_FINISH);
					st.exitCurrentQuest(true);
				}
				break;
			case GandiTotemSpirit:
				if (cond == 13)
				{
					htmltext = "totem_spirit_gandi_q0416_01.htm";
				}
				else if (cond > 13 && cond < 20)
				{
					htmltext = "totem_spirit_gandi_q0416_03.htm";
				}
				else if (cond == 20)
				{
					htmltext = "totem_spirit_gandi_q0416_04.htm";
				}
				break;
			case LeopardCarcass:
				if (cond <= 14)
				{
					htmltext = "dead_leopard_q0416_01a.htm";
				}
				else
				{
					switch (cond)
					{
					case 15:
						htmltext = "dead_leopard_q0416_01.htm";
						st.setCond(16);
						break;
					case 16:
						htmltext = "dead_leopard_q0416_01.htm";
						break;
					case 17:
						htmltext = "dead_leopard_q0416_02.htm";
						break;
					case 18:
						htmltext = "dead_leopard_q0416_05.htm";
						break;
					case 19:
						htmltext = "dead_leopard_q0416_06.htm";
						st.setCond(20);
						break;
					default:
						htmltext = "dead_leopard_q0416_06.htm";
						break;
					}
				}
				break;
			case DudaMaraTotemSpirit:
				switch (cond)
				{
				case 8:
					htmltext = "dudamara_totem_spirit_q0416_01.htm";
					break;
				case 9:
					htmltext = "dudamara_totem_spirit_q0416_04.htm";
					break;
				case 10:
					st.takeItems(BoundDurkaSpirit, -1);
					st.giveItems(TotemSpiritBlood, 1);
					htmltext = "dudamara_totem_spirit_q0416_05.htm";
					st.setCond(11);
					break;
				case 11:
					htmltext = "dudamara_totem_spirit_q0416_06.htm";
					break;
				default:
					break;
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
							st.setCond(DROPLIST_COND[i][1]);
						}
					}
				}
			}
		}
		if (st.getQuestItemsCount(KashaBearPelt) != 0 && st.getQuestItemsCount(KashaBladeSpiderHusk) != 0 && st.getQuestItemsCount(FieryEgg1st) != 0)
		{
			st.setCond(2);
		}
		else if (cond == 9 && (npcId == VenomousSpider || npcId == ArachnidTracker))
		{
			if (st.getQuestItemsCount(DurkaParasite) < 8)
			{
				st.giveItems(DurkaParasite, 1);
				st.playSound(SOUND_ITEMGET);
			}
			if (st.getQuestItemsCount(DurkaParasite) == 8 || st.getQuestItemsCount(DurkaParasite) >= 5 && Rnd.chance(st.getQuestItemsCount(DurkaParasite) * 10))
			{
				if (GameObjectsStorage.getByNpcId(QuestMonsterDurkaSpirit) == null)
				{
					st.takeItems(DurkaParasite, -1);
					st.addSpawn(QuestMonsterDurkaSpirit);
					st.startQuestTimer("QuestMonsterDurkaSpirit_Fail", 300000);
				}
			}
		}
		else if (npcId == QuestMonsterDurkaSpirit)
		{
			st.cancelQuestTimer("QuestMonsterDurkaSpirit_Fail");

			for (NpcInstance qnpc : GameObjectsStorage.getAllByNpcId(QuestMonsterDurkaSpirit, false))
			{
				qnpc.deleteMe();
			}
			if (cond == 9)
			{
				st.takeItems(SpiritNet, -1);
				st.takeItems(DurkaParasite, -1);
				st.giveItems(BoundDurkaSpirit, 1);
				st.playSound(SOUND_MIDDLE);
				st.setCond(10);
			}
		}
		else if (npcId == QuestBlackLeopard)
		{
			if (cond == 14 && Rnd.chance(50))
			{
				Functions.npcSayCustomMessage(GameObjectsStorage.getByNpcId(LeopardCarcass), new CustomMessage("quests._416_PathToOrcShaman.LeopardCarcass", st.getPlayer()).toString(), st.getPlayer());
				st.setCond(15);
			}
			else if (cond == 16 && Rnd.chance(50))
			{
				st.setCond(17);
			}
			else if (cond == 18 && Rnd.chance(50))
			{
				st.setCond(19);
			}
		}
		return null;
	}
}