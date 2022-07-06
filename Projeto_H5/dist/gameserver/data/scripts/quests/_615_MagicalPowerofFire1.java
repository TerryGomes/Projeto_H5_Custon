package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.tables.SkillTable;

public class _615_MagicalPowerofFire1 extends Quest implements ScriptFile
{
	// NPC
	private final int NARAN = 31378;
	private final int UDAN = 31379;
	private final int BOX_OF_ASEFA = 31559;

	// Quest items
	private final int STOLEN_RED_TOTEM = 7242;
	private final int DIVINE_STONE_OF_WISDOM = 7081;
	private final int RED_TOTEM = 7243;

	// etc
	private final int MARK_OF_VARKA_ALLIANCE1 = 7221;
	private final int MARK_OF_VARKA_ALLIANCE2 = 7222;
	private final int MARK_OF_VARKA_ALLIANCE3 = 7223;
	private final int MARK_OF_VARKA_ALLIANCE4 = 7224;
	private final int MARK_OF_VARKA_ALLIANCE5 = 7225;
	private final int THIEF_KEY = 1661;

	private final int[] KETRA_NPC_LIST = new int[19];

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

	public _615_MagicalPowerofFire1()
	{
		super(false);

		addStartNpc(NARAN);

		addTalkId(NARAN);
		addTalkId(UDAN);
		addTalkId(BOX_OF_ASEFA);

		KETRA_NPC_LIST[0] = 21324;
		KETRA_NPC_LIST[1] = 21325;
		KETRA_NPC_LIST[2] = 21327;
		KETRA_NPC_LIST[3] = 21328;
		KETRA_NPC_LIST[4] = 21329;
		KETRA_NPC_LIST[5] = 21331;
		KETRA_NPC_LIST[6] = 21332;
		KETRA_NPC_LIST[7] = 21334;
		KETRA_NPC_LIST[8] = 21335;
		KETRA_NPC_LIST[9] = 21336;
		KETRA_NPC_LIST[10] = 21338;
		KETRA_NPC_LIST[11] = 21339;
		KETRA_NPC_LIST[12] = 21340;
		KETRA_NPC_LIST[13] = 21342;
		KETRA_NPC_LIST[14] = 21343;
		KETRA_NPC_LIST[15] = 21344;
		KETRA_NPC_LIST[16] = 21345;
		KETRA_NPC_LIST[17] = 21346;
		KETRA_NPC_LIST[18] = 21347;

		for (int npcId : KETRA_NPC_LIST)
		{
			addAttackId(npcId);
		}
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("quest_accept"))
		{
			htmltext = "herald_naran_q0615_02.htm";
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("615_1") && st.getCond() == 2)
		{
			if (st.getQuestItemsCount(THIEF_KEY) < 1)
			{
				htmltext = "asefas_box_q0615_02.htm";
			}
			else if (st.getInt("proval") == 1)
			{
				htmltext = "asefas_box_q0615_04.htm";
				st.takeItems(THIEF_KEY, 1);
			}
			else
			{
				st.takeItems(THIEF_KEY, 1);
				st.giveItems(STOLEN_RED_TOTEM, 1);
				htmltext = "asefas_box_q0615_03.htm";
				st.setCond(3);
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		int proval = st.getInt("proval");
		switch (npcId)
		{
		case NARAN:
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() >= 74)
				{
					if (st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE1) == 1 || st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE2) == 1 || st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE3) == 1 || st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE4) == 1 || st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE5) == 1)
					{
						if (st.getQuestItemsCount(DIVINE_STONE_OF_WISDOM) == 0)
						{
							htmltext = "herald_naran_q0615_01.htm";
						}
						else
						{
							htmltext = "completed";
							st.exitCurrentQuest(true);
						}
					}
					else
					{
						htmltext = "herald_naran_q0615_01a.htm";
						st.exitCurrentQuest(true);
					}
				}
				else
				{
					htmltext = "herald_naran_q0615_01b.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 1)
			{
				htmltext = "herald_naran_q0615_03.htm";
			}
			break;
		case UDAN:
			if (cond == 1)
			{
				htmltext = "shaman_udan_q0615_01.htm";
				st.setCond(2);
			}
			else if (cond == 2 && proval == 1)
			{
				htmltext = "shaman_udan_q0615_03.htm";
				npc.doCast(SkillTable.getInstance().getInfo(4548, 1), st.getPlayer(), true);
				st.set("proval", "0");
			}
			else if (cond == 3 && st.getQuestItemsCount(STOLEN_RED_TOTEM) >= 1)
			{
				htmltext = "shaman_udan_q0615_04.htm";
				st.takeItems(STOLEN_RED_TOTEM, st.getQuestItemsCount(STOLEN_RED_TOTEM));
				st.giveItems(RED_TOTEM, 1);
				st.giveItems(DIVINE_STONE_OF_WISDOM, 1);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
			}
			break;
		case BOX_OF_ASEFA:
			if (cond == 2)
			{
				htmltext = "asefas_box_q0615_01.htm";
			}
			break;
		}
		return htmltext;
	}

	@Override
	public String onAttack(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int proval = st.getInt("proval");
		if (cond == 2 && proval == 0)
		{
			npc.doCast(SkillTable.getInstance().getInfo(4547, 1), st.getPlayer(), true);
			st.set("proval", "1");
		}
		return null;
	}
}