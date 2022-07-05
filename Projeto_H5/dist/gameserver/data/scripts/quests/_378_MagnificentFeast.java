package quests;

import java.util.HashMap;
import java.util.Map;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _378_MagnificentFeast extends Quest implements ScriptFile
{
	// NPCs
	private static int RANSPO = 30594;
	// Items
	private static int WINE_15 = 5956;
	private static int WINE_30 = 5957;
	private static int WINE_60 = 5958;
	private static int Musical_Score__Theme_of_the_Feast = 4421;
	private static int Ritrons_Dessert_Recipe = 5959;
	private static int Jonass_Salad_Recipe = 1455;
	private static int Jonass_Sauce_Recipe = 1456;
	private static int Jonass_Steak_Recipe = 1457;

	private Map<Integer, int[]> rewards = new HashMap<Integer, int[]>();

	public _378_MagnificentFeast()
	{
		super(false);
		addStartNpc(RANSPO);

		rewards.put(9, new int[]
		{
			847,
			1,
			5700
		});
		rewards.put(10, new int[]
		{
			846,
			2,
			0
		});
		rewards.put(12, new int[]
		{
			909,
			1,
			25400
		});
		rewards.put(17, new int[]
		{
			846,
			2,
			1200
		});
		rewards.put(18, new int[]
		{
			879,
			1,
			6900
		});
		rewards.put(20, new int[]
		{
			890,
			2,
			8500
		});
		rewards.put(33, new int[]
		{
			879,
			1,
			8100
		});
		rewards.put(34, new int[]
		{
			910,
			1,
			0
		});
		rewards.put(36, new int[]
		{
			910,
			1,
			0
		});
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		int _state = st.getState();
		int cond = st.getCond();
		int score = st.getInt("score");

		if (event.equalsIgnoreCase("quest_accept") && _state == CREATED)
		{
			htmltext = "warehouse_chief_ranspo_q0378_03.htm";
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("378_1") && _state == STARTED)
		{
			if (cond == 1 && st.getQuestItemsCount(WINE_15) > 0)
			{
				htmltext = "warehouse_chief_ranspo_q0378_05.htm";
				st.takeItems(WINE_15, 1);
				st.setCond(2);
				st.set("score", String.valueOf(score + 1));
			}
			else
			{
				htmltext = "warehouse_chief_ranspo_q0378_08.htm";
			}
		}
		else if (event.equalsIgnoreCase("378_2") && _state == STARTED)
		{
			if (cond == 1 && st.getQuestItemsCount(WINE_30) > 0)
			{
				htmltext = "warehouse_chief_ranspo_q0378_06.htm";
				st.takeItems(WINE_30, 1);
				st.setCond(2);
				st.set("score", String.valueOf(score + 2));
			}
			else
			{
				htmltext = "warehouse_chief_ranspo_q0378_08.htm";
			}
		}
		else if (event.equalsIgnoreCase("378_3") && _state == STARTED)
		{
			if (cond == 1 && st.getQuestItemsCount(WINE_60) > 0)
			{
				htmltext = "warehouse_chief_ranspo_q0378_07.htm";
				st.takeItems(WINE_60, 1);
				st.setCond(2);
				st.set("score", String.valueOf(score + 4));
			}
			else
			{
				htmltext = "warehouse_chief_ranspo_q0378_08.htm";
			}
		}
		else if (event.equalsIgnoreCase("378_5") && _state == STARTED)
		{
			if (cond == 2 && st.getQuestItemsCount(Musical_Score__Theme_of_the_Feast) > 0)
			{
				htmltext = "warehouse_chief_ranspo_q0378_12.htm";
				st.takeItems(Musical_Score__Theme_of_the_Feast, 1);
				st.setCond(3);
			}
			else
			{
				htmltext = "warehouse_chief_ranspo_q0378_10.htm";
			}
		}
		else if (event.equalsIgnoreCase("378_6") && _state == STARTED)
		{
			if (cond == 3 && st.getQuestItemsCount(Jonass_Salad_Recipe) > 0)
			{
				htmltext = "warehouse_chief_ranspo_q0378_14.htm";
				st.takeItems(Jonass_Salad_Recipe, 1);
				st.setCond(4);
				st.set("score", String.valueOf(score + 8));
			}
			else
			{
				htmltext = "warehouse_chief_ranspo_q0378_17.htm";
			}
		}
		else if (event.equalsIgnoreCase("378_7") && _state == STARTED)
		{
			if (cond == 3 && st.getQuestItemsCount(Jonass_Sauce_Recipe) > 0)
			{
				htmltext = "warehouse_chief_ranspo_q0378_15.htm";
				st.takeItems(Jonass_Sauce_Recipe, 1);
				st.setCond(4);
				st.set("score", String.valueOf(score + 16));
			}
			else
			{
				htmltext = "warehouse_chief_ranspo_q0378_17.htm";
			}
		}
		else if (event.equalsIgnoreCase("378_8") && _state == STARTED)
		{
			if (cond == 3 && st.getQuestItemsCount(Jonass_Steak_Recipe) > 0)
			{
				htmltext = "warehouse_chief_ranspo_q0378_16.htm";
				st.takeItems(Jonass_Steak_Recipe, 1);
				st.setCond(4);
				st.set("score", String.valueOf(score + 32));
			}
			else
			{
				htmltext = "warehouse_chief_ranspo_q0378_17.htm";
			}
		}

		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		if (npc.getNpcId() != RANSPO)
		{
			return htmltext;
		}
		int _state = st.getState();
		int cond = st.getCond();

		if (_state == CREATED)
		{
			if (st.getPlayer().getLevel() < 20)
			{
				htmltext = "warehouse_chief_ranspo_q0378_01.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "warehouse_chief_ranspo_q0378_02.htm";
				st.setCond(0);
			}
		}
		else if (cond == 1 && _state == STARTED)
		{
			htmltext = "warehouse_chief_ranspo_q0378_04.htm";
		}
		else if (cond == 2 && _state == STARTED)
		{
			htmltext = st.getQuestItemsCount(Musical_Score__Theme_of_the_Feast) > 0 ? "warehouse_chief_ranspo_q0378_11.htm" : "warehouse_chief_ranspo_q0378_10.htm";
		}
		else if (cond == 3 && _state == STARTED)
		{
			htmltext = "warehouse_chief_ranspo_q0378_13.htm";
		}
		else if (cond == 4 && _state == STARTED)
		{
			int[] reward = rewards.get(st.getInt("score"));
			if (st.getQuestItemsCount(Ritrons_Dessert_Recipe) > 0 && reward != null)
			{
				htmltext = "warehouse_chief_ranspo_q0378_20.htm";
				st.takeItems(Ritrons_Dessert_Recipe, 1);
				st.giveItems(reward[0], reward[1]);
				if (reward[2] > 0)
				{
					st.giveItems(ADENA_ID, reward[2]);
				}
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "warehouse_chief_ranspo_q0378_19.htm";
			}
		}

		return htmltext;
	}

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
}