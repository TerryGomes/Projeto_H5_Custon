package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * Квест Muertos Feather
 *
 * @author Sergey Ibryaev aka Artful
 */

public class _284_MuertosFeather extends Quest implements ScriptFile
{
	// NPC
	private static final int Trevor = 32166;
	// Quest Item
	private static final int MuertosFeather = 9748;
	// MOBs
	private static final int MuertosGuard = 22239;
	private static final int MuertosScout = 22240;
	private static final int MuertosWarrior = 22242;
	private static final int MuertosCaptain = 22243;
	private static final int MuertosLieutenant = 22245;
	private static final int MuertosCommander = 22246;
	// Drop Cond
	// # [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	private static final int[][] DROPLIST_COND =
	{
		{
			1,
			0,
			MuertosGuard,
			0,
			MuertosFeather,
			0,
			44,
			1
		},
		{
			1,
			0,
			MuertosScout,
			0,
			MuertosFeather,
			0,
			48,
			1
		},
		{
			1,
			0,
			MuertosWarrior,
			0,
			MuertosFeather,
			0,
			56,
			1
		},
		{
			1,
			0,
			MuertosCaptain,
			0,
			MuertosFeather,
			0,
			60,
			1
		},
		{
			1,
			0,
			MuertosLieutenant,
			0,
			MuertosFeather,
			0,
			64,
			1
		},
		{
			1,
			0,
			MuertosCommander,
			0,
			MuertosFeather,
			0,
			69,
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

	public _284_MuertosFeather()
	{
		super(false);

		addStartNpc(Trevor);

		addTalkId(Trevor);
		// Mob Drop
		for (int i = 0; i < DROPLIST_COND.length; i++)
		{
			addKillId(DROPLIST_COND[i][2]);
		}
		addQuestItem(MuertosFeather);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("trader_treauvi_q0284_0103.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("trader_treauvi_q0284_0203.htm"))
		{
			long counts = st.getQuestItemsCount(MuertosFeather) * 45;
			st.takeItems(MuertosFeather, -1);
			st.giveItems(ADENA_ID, counts);
		}
		else if (event.equalsIgnoreCase("trader_treauvi_q0284_0204.htm"))
		{
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getCond();
		if (npcId == Trevor)
		{
			if (st.getPlayer().getLevel() < 11)
			{
				htmltext = "trader_treauvi_q0284_0102.htm";
				st.exitCurrentQuest(true);
			}
			else if (cond == 0)
			{
				htmltext = "trader_treauvi_q0284_0101.htm";
			}
			else if (cond == 1 && st.getQuestItemsCount(MuertosFeather) == 0)
			{
				htmltext = "trader_treauvi_q0284_0103.htm";
			}
			else
			{
				htmltext = "trader_treauvi_q0284_0105.htm";
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
		return null;
	}
}