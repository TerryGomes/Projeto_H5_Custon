package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _038_DragonFangs extends Quest implements ScriptFile
{
	// NPC
	public final int ROHMER = 30344;
	public final int LUIS = 30386;
	public final int IRIS = 30034;

	// QUEST ITEM
	public final int FEATHER_ORNAMENT = 7173;
	public final int TOOTH_OF_TOTEM = 7174;
	public final int LETTER_OF_IRIS = 7176;
	public final int LETTER_OF_ROHMER = 7177;
	public final int TOOTH_OF_DRAGON = 7175;

	// MOBS
	public final int LANGK_LIZARDMAN_LIEUTENANT = 20357;
	public final int LANGK_LIZARDMAN_SENTINEL = 21100;
	public final int LANGK_LIZARDMAN_LEADER = 20356;
	public final int LANGK_LIZARDMAN_SHAMAN = 21101;

	// CHANCE FOR DROP
	public final int CHANCE_FOR_QUEST_ITEMS = 100; // 100%???

	// REWARD
	public final int BONE_HELMET = 45;
	public final int ASSAULT_BOOTS = 1125;
	public final int BLUE_BUCKSKIN_BOOTS = 1123;

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

	public _038_DragonFangs()
	{
		super(false);

		addStartNpc(LUIS);

		addTalkId(IRIS);
		addTalkId(ROHMER);

		addKillId(LANGK_LIZARDMAN_LEADER);
		addKillId(LANGK_LIZARDMAN_SHAMAN);
		addKillId(LANGK_LIZARDMAN_SENTINEL);
		addKillId(LANGK_LIZARDMAN_LIEUTENANT);

		addQuestItem(TOOTH_OF_TOTEM, LETTER_OF_IRIS, LETTER_OF_ROHMER, TOOTH_OF_DRAGON, FEATHER_ORNAMENT);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		int cond = st.getCond();
		if (event.equals("guard_luis_q0038_0104.htm"))
		{
			if (cond == 0)
			{
				st.setState(STARTED);
				st.setCond(1);
				st.playSound(SOUND_ACCEPT);
			}
		}
		if (event.equals("guard_luis_q0038_0201.htm"))
		{
			if (cond == 2)
			{
				st.setCond(3);
				st.takeItems(FEATHER_ORNAMENT, 100);
				st.giveItems(TOOTH_OF_TOTEM, 1);
				st.playSound(SOUND_MIDDLE);
			}
		}
		if (event.equals("iris_q0038_0301.htm"))
		{
			if (cond == 3)
			{
				st.setCond(4);
				st.takeItems(TOOTH_OF_TOTEM, 1);
				st.giveItems(LETTER_OF_IRIS, 1);
				st.playSound(SOUND_MIDDLE);
			}
		}
		if (event.equals("magister_roh_q0038_0401.htm"))
		{
			if (cond == 4)
			{
				st.setCond(5);
				st.takeItems(LETTER_OF_IRIS, 1);
				st.giveItems(LETTER_OF_ROHMER, 1);
				st.playSound(SOUND_MIDDLE);
			}
		}
		if (event.equals("iris_q0038_0501.htm"))
		{
			if (cond == 5)
			{
				st.setCond(6);
				st.takeItems(LETTER_OF_ROHMER, 1);
				st.playSound(SOUND_MIDDLE);
			}
		}
		if (event.equals("iris_q0038_0601.htm"))
		{
			if (cond == 7)
			{
				st.takeItems(TOOTH_OF_DRAGON, 50);
				int luck = Rnd.get(3);
				if (luck == 0)
				{
					st.giveItems(BLUE_BUCKSKIN_BOOTS, 1);
					st.giveItems(ADENA_ID, 1500);
				}
				if (luck == 1)
				{
					st.giveItems(BONE_HELMET, 1);
					st.giveItems(ADENA_ID, 5200);
				}
				if (luck == 2)
				{
					st.giveItems(ASSAULT_BOOTS, 1);
					st.giveItems(ADENA_ID, 1500);
				}
				st.addExpAndSp(435117, 23977);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
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
		if (npcId == LUIS && cond == 0)
		{
			if (st.getPlayer().getLevel() < 19)
			{
				htmltext = "guard_luis_q0038_0102.htm";
				st.exitCurrentQuest(true);
			}
			else if (st.getPlayer().getLevel() >= 19)
			{
				htmltext = "guard_luis_q0038_0101.htm";
			}
		}
		if (npcId == LUIS && cond == 1)
		{
			htmltext = "guard_luis_q0038_0202.htm";
		}
		if (npcId == LUIS && cond == 2 && st.getQuestItemsCount(FEATHER_ORNAMENT) == 100)
		{
			htmltext = "guard_luis_q0038_0105.htm";
		}
		if (npcId == LUIS && cond == 3)
		{
			htmltext = "guard_luis_q0038_0203.htm";
		}
		if (npcId == IRIS && cond == 3 && st.getQuestItemsCount(TOOTH_OF_TOTEM) == 1)
		{
			htmltext = "iris_q0038_0201.htm";
		}
		if (npcId == IRIS && cond == 4)
		{
			htmltext = "iris_q0038_0303.htm";
		}
		if (npcId == IRIS && cond == 5 && st.getQuestItemsCount(LETTER_OF_ROHMER) == 1)
		{
			htmltext = "iris_q0038_0401.htm";
		}
		if (npcId == IRIS && cond == 6)
		{
			htmltext = "iris_q0038_0602.htm";
		}
		if (npcId == IRIS && cond == 7 && st.getQuestItemsCount(TOOTH_OF_DRAGON) == 50)
		{
			htmltext = "iris_q0038_0503.htm";
		}
		if (npcId == ROHMER && cond == 4 && st.getQuestItemsCount(LETTER_OF_IRIS) == 1)
		{
			htmltext = "magister_roh_q0038_0301.htm";
		}
		if (npcId == ROHMER && cond == 5)
		{
			htmltext = "magister_roh_q0038_0403.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		boolean chance = Rnd.chance(CHANCE_FOR_QUEST_ITEMS);
		int cond = st.getCond();
		if (npcId == 20357 || npcId == 21100)
		{
			if (cond == 1 && chance && st.getQuestItemsCount(FEATHER_ORNAMENT) < 100)
			{
				st.giveItems(FEATHER_ORNAMENT, 1);
				if (st.getQuestItemsCount(FEATHER_ORNAMENT) == 100)
				{
					st.playSound(SOUND_MIDDLE);
					st.setCond(2);
				}
				else
				{
					st.playSound(SOUND_ITEMGET);
				}
			}
		}
		if (npcId == 20356 || npcId == 21101)
		{
			if (cond == 6 && chance && st.getQuestItemsCount(TOOTH_OF_DRAGON) < 50)
			{
				st.giveItems(TOOTH_OF_DRAGON, 1);
				if (st.getQuestItemsCount(TOOTH_OF_DRAGON) == 50)
				{
					st.playSound(SOUND_MIDDLE);
					st.setCond(7);
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