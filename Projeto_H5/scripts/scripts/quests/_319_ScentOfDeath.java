package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * Квест Scent Of Death
 *
 * @author Sergey Ibryaev aka Artful
 */

public class _319_ScentOfDeath extends Quest implements ScriptFile
{
	// NPC
	private static final int MINALESS = 30138;
	// Item
	private static final int HealingPotion = 1060;
	// Quest Item
	private static final int ZombieSkin = 1045;

	// Drop Cond
	// # [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	private static final int[][] DROPLIST_COND =
	{
		{
			1,
			2,
			20015,
			0,
			ZombieSkin,
			5,
			20,
			1
		},
		{
			1,
			2,
			20020,
			0,
			ZombieSkin,
			5,
			25,
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

	public _319_ScentOfDeath()
	{
		super(false);

		addStartNpc(MINALESS);
		addTalkId(MINALESS);
		// Mob Drop
		for (int i = 0; i < DROPLIST_COND.length; i++)
		{
			addKillId(DROPLIST_COND[i][2]);
		}

		addQuestItem(ZombieSkin);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("mina_q0319_04.htm"))
		{
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
		int id = st.getState();
		int cond = 0;
		if (id != CREATED)
		{
			cond = st.getCond();
		}
		if (npcId == MINALESS)
		{
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() < 11)
				{
					htmltext = "mina_q0319_02.htm";
					st.exitCurrentQuest(true);
				}
				else
				{
					htmltext = "mina_q0319_03.htm";
				}
			}
			else if (cond == 1)
			{
				htmltext = "mina_q0319_05.htm";
			}
			else if (cond == 2 && st.getQuestItemsCount(ZombieSkin) >= 5)
			{
				htmltext = "mina_q0319_06.htm";
				st.takeItems(ZombieSkin, -1);
				st.giveItems(ADENA_ID, 3350);
				st.giveItems(HealingPotion, 1);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "mina_q0319_05.htm";
				st.setCond(1);
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
