package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _409_PathToOracle extends Quest implements ScriptFile
{
	// npc
	public final int MANUEL = 30293;
	public final int ALLANA = 30424;
	public final int PERRIN = 30428;
	// mobs
	public final int LIZARDMAN_WARRIOR = 27032;
	public final int LIZARDMAN_SCOUT = 27033;
	public final int LIZARDMAN = 27034;
	public final int TAMIL = 27035;
	// items
	public final int CRYSTAL_MEDALLION_ID = 1231;
	public final int MONEY_OF_SWINDLER_ID = 1232;
	public final int DAIRY_OF_ALLANA_ID = 1233;
	public final int LIZARD_CAPTAIN_ORDER_ID = 1234;
	public final int LEAF_OF_ORACLE_ID = 1235;
	public final int HALF_OF_DAIRY_ID = 1236;
	public final int TAMATOS_NECKLACE_ID = 1275;

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

	public _409_PathToOracle()
	{
		super(false);

		addStartNpc(MANUEL);

		addTalkId(ALLANA);
		addTalkId(PERRIN);

		addKillId(LIZARDMAN_WARRIOR);
		addKillId(LIZARDMAN_SCOUT);
		addKillId(LIZARDMAN);
		addKillId(TAMIL);

		addQuestItem(new int[]
		{
			MONEY_OF_SWINDLER_ID,
			DAIRY_OF_ALLANA_ID,
			LIZARD_CAPTAIN_ORDER_ID,
			CRYSTAL_MEDALLION_ID,
			HALF_OF_DAIRY_ID,
			TAMATOS_NECKLACE_ID
		});
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("1"))
		{
			if (event.equalsIgnoreCase("1"))
			{
				if (st.getPlayer().getClassId().getId() != 0x19)
				{
					if (st.getPlayer().getClassId().getId() == 0x1d)
					{
						htmltext = "father_manuell_q0409_02a.htm";
					}
					else
					{
						htmltext = "father_manuell_q0409_02.htm";
					}
				}
				else if (st.getPlayer().getLevel() < 18)
				{
					htmltext = "father_manuell_q0409_03.htm";
				}
				else if (st.getQuestItemsCount(LEAF_OF_ORACLE_ID) > 0)
				{
					htmltext = "father_manuell_q0409_04.htm";
				}
				else
				{
					st.setCond(1);
					st.setState(STARTED);
					st.playSound(SOUND_ACCEPT);
					st.giveItems(CRYSTAL_MEDALLION_ID, 1);
					htmltext = "father_manuell_q0409_05.htm";
				}
			}
		}
		else if (event.equalsIgnoreCase("allana_q0409_08.htm"))
		{
			st.addSpawn(LIZARDMAN_WARRIOR);
			st.addSpawn(LIZARDMAN_SCOUT);
			st.addSpawn(LIZARDMAN);
			st.setCond(2);
		}
		else if (event.equalsIgnoreCase("30424_1"))
		{
			htmltext = "";
		}
		else if (event.equalsIgnoreCase("30428_1"))
		{
			htmltext = "perrin_q0409_02.htm";
		}
		else if (event.equalsIgnoreCase("30428_2"))
		{
			htmltext = "perrin_q0409_03.htm";
		}
		else if (event.equalsIgnoreCase("30428_3"))
		{
			st.addSpawn(TAMIL);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch (npcId)
		{
		case MANUEL:
			if (cond < 1)
			{
				htmltext = "father_manuell_q0409_01.htm";
			}
			else if (st.getQuestItemsCount(CRYSTAL_MEDALLION_ID) > 0)
			{
				if (st.getQuestItemsCount(MONEY_OF_SWINDLER_ID) < 1 && st.getQuestItemsCount(DAIRY_OF_ALLANA_ID) < 1 && st.getQuestItemsCount(LIZARD_CAPTAIN_ORDER_ID) < 1 && st.getQuestItemsCount(HALF_OF_DAIRY_ID) < 1)
				{
					htmltext = "father_manuell_q0409_09.htm";
				}
				else if (st.getQuestItemsCount(MONEY_OF_SWINDLER_ID) > 0 && st.getQuestItemsCount(DAIRY_OF_ALLANA_ID) > 0 && st.getQuestItemsCount(LIZARD_CAPTAIN_ORDER_ID) > 0 && st.getQuestItemsCount(HALF_OF_DAIRY_ID) < 1)
				{
					htmltext = "father_manuell_q0409_08.htm";
					st.takeItems(MONEY_OF_SWINDLER_ID, 1);
					st.takeItems(DAIRY_OF_ALLANA_ID, -1);
					st.takeItems(LIZARD_CAPTAIN_ORDER_ID, -1);
					st.takeItems(CRYSTAL_MEDALLION_ID, -1);
					if (st.getPlayer().getClassId().getLevel() == 1)
					{
						st.giveItems(LEAF_OF_ORACLE_ID, 1);
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
				else
				{
					htmltext = "father_manuell_q0409_07.htm";
				}
			}
			break;
		case ALLANA:
			if (st.getQuestItemsCount(CRYSTAL_MEDALLION_ID) > 0)
			{
				if (st.getQuestItemsCount(MONEY_OF_SWINDLER_ID) < 1 && st.getQuestItemsCount(DAIRY_OF_ALLANA_ID) < 1 && st.getQuestItemsCount(LIZARD_CAPTAIN_ORDER_ID) < 1 && st.getQuestItemsCount(HALF_OF_DAIRY_ID) < 1)
				{
					if (cond > 2)
					{
						htmltext = "allana_q0409_05.htm";
					}
					else
					{
						htmltext = "allana_q0409_01.htm";
					}
				}
				else if (st.getQuestItemsCount(MONEY_OF_SWINDLER_ID) < 1 && st.getQuestItemsCount(DAIRY_OF_ALLANA_ID) < 1 && st.getQuestItemsCount(LIZARD_CAPTAIN_ORDER_ID) > 0 && st.getQuestItemsCount(HALF_OF_DAIRY_ID) < 1)
				{
					htmltext = "allana_q0409_02.htm";
					st.giveItems(HALF_OF_DAIRY_ID, 1);
					st.setCond(4);
				}
				else if (st.getQuestItemsCount(MONEY_OF_SWINDLER_ID) < 1 && st.getQuestItemsCount(DAIRY_OF_ALLANA_ID) < 1 && st.getQuestItemsCount(LIZARD_CAPTAIN_ORDER_ID) > 0 && st.getQuestItemsCount(HALF_OF_DAIRY_ID) > 0)
				{
					if (st.getQuestItemsCount(TAMATOS_NECKLACE_ID) < 1)
					{
						htmltext = "allana_q0409_06.htm";
					}
					else
					{
						htmltext = "allana_q0409_03.htm";
					}
				}
				else if (st.getQuestItemsCount(MONEY_OF_SWINDLER_ID) > 0 && st.getQuestItemsCount(DAIRY_OF_ALLANA_ID) < 1 && st.getQuestItemsCount(LIZARD_CAPTAIN_ORDER_ID) > 0 && st.getQuestItemsCount(HALF_OF_DAIRY_ID) > 0)
				{
					htmltext = "allana_q0409_04.htm";
					st.takeItems(HALF_OF_DAIRY_ID, -1);
					st.giveItems(DAIRY_OF_ALLANA_ID, 1);
					st.setCond(7);
				}
				else if (st.getQuestItemsCount(MONEY_OF_SWINDLER_ID) > 0 && st.getQuestItemsCount(LIZARD_CAPTAIN_ORDER_ID) > 0 && st.getQuestItemsCount(HALF_OF_DAIRY_ID) < 1 && st.getQuestItemsCount(DAIRY_OF_ALLANA_ID) > 0)
				{
					htmltext = "allana_q0409_05.htm";
				}
			}
			break;
		case PERRIN:
			if (st.getQuestItemsCount(CRYSTAL_MEDALLION_ID) > 0 && st.getQuestItemsCount(LIZARD_CAPTAIN_ORDER_ID) > 0)
			{
				if (st.getQuestItemsCount(TAMATOS_NECKLACE_ID) > 0)
				{
					htmltext = "perrin_q0409_04.htm";
					st.takeItems(TAMATOS_NECKLACE_ID, -1);
					st.giveItems(MONEY_OF_SWINDLER_ID, 1);
					st.setCond(6);
				}
				else if (st.getQuestItemsCount(MONEY_OF_SWINDLER_ID) > 0)
				{
					htmltext = "perrin_q0409_05.htm";
				}
				else if (cond > 4)
				{
					htmltext = "perrin_q0409_06.htm";
				}
				else
				{
					htmltext = "perrin_q0409_01.htm";
				}
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
		if (npcId == LIZARDMAN_WARRIOR | npcId == LIZARDMAN_SCOUT | npcId == LIZARDMAN)
		{
			if (cond == 2 && st.getQuestItemsCount(LIZARD_CAPTAIN_ORDER_ID) < 1)
			{
				st.giveItems(LIZARD_CAPTAIN_ORDER_ID, 1);
				st.playSound(SOUND_MIDDLE);
				st.setCond(3);
			}
		}
		else if (npcId == TAMIL)
		{
			if (cond == 4 && st.getQuestItemsCount(TAMATOS_NECKLACE_ID) < 1)
			{
				st.giveItems(TAMATOS_NECKLACE_ID, 1);
				st.playSound(SOUND_MIDDLE);
				st.setCond(5);
			}
		}
		return null;
	}
}