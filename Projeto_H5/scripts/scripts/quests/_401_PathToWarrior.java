package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.items.Inventory;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _401_PathToWarrior extends Quest implements ScriptFile
{
	int AURON = 30010;
	int SIMPLON = 30253;

	int TRACKER_SKELETON = 20035;
	int POISON_SPIDER = 20038;
	int TRACKER_SKELETON_LD = 20042;
	int ARACHNID_TRACKER = 20043;

	int EINS_LETTER_ID = 1138;
	int WARRIOR_GUILD_MARK_ID = 1139;
	int RUSTED_BRONZE_SWORD1_ID = 1140;
	int RUSTED_BRONZE_SWORD2_ID = 1141;
	int SIMPLONS_LETTER_ID = 1143;
	int POISON_SPIDER_LEG2_ID = 1144;
	int MEDALLION_OF_WARRIOR_ID = 1145;
	int RUSTED_BRONZE_SWORD3_ID = 1142;

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

	public _401_PathToWarrior()
	{
		super(false);

		addStartNpc(AURON);

		addTalkId(SIMPLON);

		addKillId(TRACKER_SKELETON);
		addKillId(POISON_SPIDER);
		addKillId(TRACKER_SKELETON_LD);
		addKillId(ARACHNID_TRACKER);

		addQuestItem(new int[]
		{
			SIMPLONS_LETTER_ID,
			RUSTED_BRONZE_SWORD2_ID,
			EINS_LETTER_ID,
			WARRIOR_GUILD_MARK_ID,
			RUSTED_BRONZE_SWORD1_ID,
			POISON_SPIDER_LEG2_ID,
			RUSTED_BRONZE_SWORD3_ID
		});

	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("401_1"))
		{
			if (st.getPlayer().getClassId().getId() == 0x00)
			{
				if (st.getPlayer().getLevel() >= 18)
				{
					if (st.getQuestItemsCount(MEDALLION_OF_WARRIOR_ID) > 0)
					{
						htmltext = "ein_q0401_04.htm";
					}
					else
					{
						htmltext = "ein_q0401_05.htm";
					}
				}
				else
				{
					htmltext = "ein_q0401_02.htm";
				}
			}
			else if (st.getPlayer().getClassId().getId() == 0x01)
			{
				htmltext = "ein_q0401_02a.htm";
			}
			else
			{
				htmltext = "ein_q0401_03.htm";
			}
		}
		else if (event.equalsIgnoreCase("401_2"))
		{
			htmltext = "ein_q0401_10.htm";
		}
		else if (event.equalsIgnoreCase("401_3"))
		{
			htmltext = "ein_q0401_11.htm";
			st.takeItems(SIMPLONS_LETTER_ID, 1);
			st.takeItems(RUSTED_BRONZE_SWORD2_ID, 1);
			st.giveItems(RUSTED_BRONZE_SWORD3_ID, 1);
			st.setCond(5);
		}
		else if (event.equalsIgnoreCase("1"))
		{
			if (st.getQuestItemsCount(EINS_LETTER_ID) == 0)
			{
				st.setCond(1);
				st.setState(STARTED);
				st.playSound(SOUND_ACCEPT);
				st.giveItems(EINS_LETTER_ID, 1);
				htmltext = "ein_q0401_06.htm";
			}
		}
		else if (event.equalsIgnoreCase("30253_1"))
		{
			htmltext = "trader_simplon_q0401_02.htm";
			st.takeItems(EINS_LETTER_ID, 1);
			st.giveItems(WARRIOR_GUILD_MARK_ID, 1);
			st.setCond(2);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getCond();
		if (id == CREATED)
		{
			st.setState(STARTED);
			st.setCond(0);
		}
		if (npcId == AURON && cond == 0)
		{
			htmltext = "ein_q0401_01.htm";
		}
		else if (npcId == AURON && st.getQuestItemsCount(EINS_LETTER_ID) > 0)
		{
			htmltext = "ein_q0401_07.htm";
		}
		else if (npcId == AURON && st.getQuestItemsCount(WARRIOR_GUILD_MARK_ID) == 1)
		{
			htmltext = "ein_q0401_08.htm";
		}
		else if (npcId == SIMPLON && st.getQuestItemsCount(EINS_LETTER_ID) > 0)
		{
			htmltext = "trader_simplon_q0401_01.htm";
		}
		else if (npcId == SIMPLON && st.getQuestItemsCount(WARRIOR_GUILD_MARK_ID) > 0)
		{
			if (st.getQuestItemsCount(RUSTED_BRONZE_SWORD1_ID) < 1)
			{
				htmltext = "trader_simplon_q0401_03.htm";
			}
			else if (st.getQuestItemsCount(RUSTED_BRONZE_SWORD1_ID) < 10)
			{
				htmltext = "trader_simplon_q0401_04.htm";
			}
			else if (st.getQuestItemsCount(RUSTED_BRONZE_SWORD1_ID) >= 10)
			{
				st.takeItems(WARRIOR_GUILD_MARK_ID, -1);
				st.takeItems(RUSTED_BRONZE_SWORD1_ID, -1);
				st.giveItems(RUSTED_BRONZE_SWORD2_ID, 1);
				st.giveItems(SIMPLONS_LETTER_ID, 1);
				st.setCond(4);
				htmltext = "trader_simplon_q0401_05.htm";
			}
		}
		else if (npcId == SIMPLON && st.getQuestItemsCount(SIMPLONS_LETTER_ID) > 0)
		{
			htmltext = "trader_simplon_q0401_06.htm";
		}
		else if (npcId == AURON && st.getQuestItemsCount(SIMPLONS_LETTER_ID) > 0 && st.getQuestItemsCount(RUSTED_BRONZE_SWORD2_ID) > 0 && st.getQuestItemsCount(WARRIOR_GUILD_MARK_ID) == 0 && st.getQuestItemsCount(EINS_LETTER_ID) == 0)
		{
			htmltext = "ein_q0401_09.htm";
		}
		else if (npcId == AURON && st.getQuestItemsCount(RUSTED_BRONZE_SWORD3_ID) > 0 && st.getQuestItemsCount(WARRIOR_GUILD_MARK_ID) == 0 && st.getQuestItemsCount(EINS_LETTER_ID) == 0)
		{
			if (st.getQuestItemsCount(POISON_SPIDER_LEG2_ID) < 20)
			{
				htmltext = "ein_q0401_12.htm";
			}
			else if (st.getQuestItemsCount(POISON_SPIDER_LEG2_ID) > 19)
			{
				st.takeItems(POISON_SPIDER_LEG2_ID, -1);
				st.takeItems(RUSTED_BRONZE_SWORD3_ID, -1);
				if (st.getPlayer().getClassId().getLevel() == 1)
				{
					st.giveItems(MEDALLION_OF_WARRIOR_ID, 1);
					if (!st.getPlayer().getVarB("prof1"))
					{
						st.getPlayer().setVar("prof1", "1", -1);
						st.addExpAndSp(228064, 16455);
						// FIXME [G1ta0] дать адены, только если первый чар на акке
						st.giveItems(ADENA_ID, 81900);
					}
				}
				htmltext = "ein_q0401_13.htm";
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == TRACKER_SKELETON || npcId == TRACKER_SKELETON_LD)
		{
			if (cond == 2 && st.getQuestItemsCount(RUSTED_BRONZE_SWORD1_ID) < 10)
			{
				st.giveItems(RUSTED_BRONZE_SWORD1_ID, 1);
				if (st.getQuestItemsCount(RUSTED_BRONZE_SWORD1_ID) == 10)
				{
					st.playSound(SOUND_MIDDLE);
					st.setCond(3);
				}
				else
				{
					st.playSound(SOUND_ITEMGET);
				}
			}
		}
		else if (npcId == ARACHNID_TRACKER || npcId == POISON_SPIDER)
		{
			if (st.getQuestItemsCount(POISON_SPIDER_LEG2_ID) < 20 && st.getQuestItemsCount(RUSTED_BRONZE_SWORD3_ID) == 1 && st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == RUSTED_BRONZE_SWORD3_ID)
			{
				st.giveItems(POISON_SPIDER_LEG2_ID, 1);
				if (st.getQuestItemsCount(POISON_SPIDER_LEG2_ID) == 20)
				{
					st.playSound(SOUND_MIDDLE);
					st.setCond(6);
				}
				else
				{
					st.playSound(SOUND_ITEMGET);
				}
			}
		}
		return null;
	}
}