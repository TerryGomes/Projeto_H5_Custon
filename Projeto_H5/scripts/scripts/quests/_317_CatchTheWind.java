package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * Квест Catch The Wind
 *
 * @author Sergey Ibryaev aka Artful
 */

public class _317_CatchTheWind extends Quest implements ScriptFile
{
	// NPCs
	private static int Rizraell = 30361;
	// Quest Items
	private static int WindShard = 1078;
	// Mobs
	private static int Lirein = 20036;
	private static int LireinElder = 20044;
	// Drop Cond
	// # [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	public final int[][] DROPLIST_COND =
	{
		{
			1,
			0,
			Lirein,
			0,
			WindShard,
			0,
			60,
			1
		},
		{
			1,
			0,
			LireinElder,
			0,
			WindShard,
			0,
			60,
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

	public _317_CatchTheWind()
	{
		super(false);
		addStartNpc(Rizraell);
		// Mob Drop
		for (int i = 0; i < DROPLIST_COND.length; i++)
		{
			addKillId(DROPLIST_COND[i][2]);
		}
		addQuestItem(WindShard);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equalsIgnoreCase("rizraell_q0317_04.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("rizraell_q0317_08.htm"))
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getCond();
		if (npcId == Rizraell)
		{
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() >= 18)
				{
					htmltext = "rizraell_q0317_03.htm";
				}
				else
				{
					htmltext = "rizraell_q0317_02.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 1)
			{
				long count = st.getQuestItemsCount(WindShard);
				if (count > 0)
				{
					st.takeItems(WindShard, -1);
					st.giveItems(ADENA_ID, 40 * count);
					htmltext = "rizraell_q0317_07.htm";
				}
				else
				{
					htmltext = "rizraell_q0317_05.htm";
				}
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