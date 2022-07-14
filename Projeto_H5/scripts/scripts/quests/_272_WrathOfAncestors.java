package quests;

import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * Квест Wrath Of Ancestors
 *
 * @author Sergey Ibryaev aka Artful
 */

public class _272_WrathOfAncestors extends Quest implements ScriptFile
{
	// NPC
	private static final int Livina = 30572;
	// Quest Item
	private static final int GraveRobbersHead = 1474;
	// MOB
	private static final int GoblinGraveRobber = 20319;
	private static final int GoblinTombRaiderLeader = 20320;
	// Drop Cond
	// # [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	private static final int[][] DROPLIST_COND =
	{
		{
			1,
			2,
			GoblinGraveRobber,
			0,
			GraveRobbersHead,
			50,
			100,
			1
		},
		{
			1,
			2,
			GoblinTombRaiderLeader,
			0,
			GraveRobbersHead,
			50,
			100,
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

	public _272_WrathOfAncestors()
	{
		super(false);
		addStartNpc(Livina);
		// Mob Drop
		for (int i = 0; i < DROPLIST_COND.length; i++)
		{
			addKillId(DROPLIST_COND[i][2]);
		}
		addQuestItem(GraveRobbersHead);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("1"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			htmltext = "seer_livina_q0272_03.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getCond();
		if (npcId == Livina)
		{
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getRace() != Race.orc)
				{
					htmltext = "seer_livina_q0272_00.htm";
					st.exitCurrentQuest(true);
				}
				else if (st.getPlayer().getLevel() < 5)
				{
					htmltext = "seer_livina_q0272_01.htm";
					st.exitCurrentQuest(true);
				}
				else
				{
					htmltext = "seer_livina_q0272_02.htm";
					return htmltext;
				}
				break;
			case 1:
				htmltext = "seer_livina_q0272_04.htm";
				break;
			case 2:
				st.takeItems(GraveRobbersHead, -1);
				st.giveItems(ADENA_ID, 1500);
				htmltext = "seer_livina_q0272_05.htm";
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
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