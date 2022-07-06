package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _298_LizardmensConspiracy extends Quest implements ScriptFile
{
	// npc
	public final int PRAGA = 30333;
	public final int ROHMER = 30344;
	// mobs
	public final int MAILLE_LIZARDMAN_WARRIOR = 20922;
	public final int MAILLE_LIZARDMAN_SHAMAN = 20923;
	public final int MAILLE_LIZARDMAN_MATRIARCH = 20924;
	// public final int GIANT_ARANEID = 20925; //в клиенте о нем ни слова
	public final int POISON_ARANEID = 20926;
	public final int KING_OF_THE_ARANEID = 20927;
	// items
	public final int REPORT = 7182;
	public final int SHINING_GEM = 7183;
	public final int SHINING_RED_GEM = 7184;
	// MobsTable {MOB_ID, ITEM_ID}
	public final int[][] MobsTable =
	{
		{
			MAILLE_LIZARDMAN_WARRIOR,
			SHINING_GEM
		},
		{
			MAILLE_LIZARDMAN_SHAMAN,
			SHINING_GEM
		},
		{
			MAILLE_LIZARDMAN_MATRIARCH,
			SHINING_GEM
		},
		// { GIANT_ARANEID, SHINING_RED_GEM },
		{
			POISON_ARANEID,
			SHINING_RED_GEM
		},
		{
			KING_OF_THE_ARANEID,
			SHINING_RED_GEM
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

	public _298_LizardmensConspiracy()
	{
		super(false);

		addStartNpc(PRAGA);

		addTalkId(PRAGA);
		addTalkId(ROHMER);

		for (int[] element : MobsTable)
		{
			addKillId(element[0]);
		}

		addQuestItem(new int[]
		{
			REPORT,
			SHINING_GEM,
			SHINING_RED_GEM
		});
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("guard_praga_q0298_0104.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.giveItems(REPORT, 1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("magister_rohmer_q0298_0201.htm"))
		{
			st.takeItems(REPORT, -1);
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("magister_rohmer_q0298_0301.htm") && st.getQuestItemsCount(SHINING_GEM) + st.getQuestItemsCount(SHINING_RED_GEM) > 99)
		{
			st.takeItems(SHINING_GEM, -1);
			st.takeItems(SHINING_RED_GEM, -1);
			st.addExpAndSp(0, 42000);
			st.exitCurrentQuest(true);
			st.playSound(SOUND_FINISH);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == PRAGA)
		{
			if (cond < 1)
			{
				if (st.getPlayer().getLevel() < 25)
				{
					htmltext = "guard_praga_q0298_0102.htm";
					st.exitCurrentQuest(true);
				}
				else
				{
					htmltext = "guard_praga_q0298_0101.htm";
				}
			}
			if (cond == 1)
			{
				htmltext = "guard_praga_q0298_0105.htm";
			}
		}
		else if (npcId == ROHMER)
		{
			if (cond < 1)
			{
				htmltext = "magister_rohmer_q0298_0202.htm";
			}
			else if (cond == 1)
			{
				htmltext = "magister_rohmer_q0298_0101.htm";
			}
			else if (cond == 2 | st.getQuestItemsCount(SHINING_GEM) + st.getQuestItemsCount(SHINING_RED_GEM) < 100)
			{
				htmltext = "magister_rohmer_q0298_0204.htm";
				st.setCond(2);
			}
			else if (cond == 3 && st.getQuestItemsCount(SHINING_GEM) + st.getQuestItemsCount(SHINING_RED_GEM) > 99)
			{
				htmltext = "magister_rohmer_q0298_0203.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int rand = Rnd.get(10);
		if (st.getCond() == 2)
		{
			for (int[] element : MobsTable)
			{
				if (npcId == element[0])
				{
					if (rand < 6 && st.getQuestItemsCount(element[1]) < 50)
					{
						if (rand < 2 && element[1] == SHINING_GEM)
						{
							st.giveItems(element[1], 2);
						}
						else
						{
							st.giveItems(element[1], 1);
						}
						if (st.getQuestItemsCount(SHINING_GEM) + st.getQuestItemsCount(SHINING_RED_GEM) > 99)
						{
							st.setCond(3);
							st.playSound(SOUND_MIDDLE);
						}
						else
						{
							st.playSound(SOUND_ITEMGET);
						}
					}
				}
			}
		}
		return null;
	}
}