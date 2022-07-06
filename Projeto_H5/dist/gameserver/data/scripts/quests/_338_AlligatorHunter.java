package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * Квест Alligator Hunter
 *
 * @author Sergey Ibryaev aka Artful
 */

public class _338_AlligatorHunter extends Quest implements ScriptFile
{
	// NPC
	private static final int Enverun = 30892;
	// QuestItems
	private static final int AlligatorLeather = 4337;
	// MOB
	private static final int CrokianLad = 20804;
	private static final int DailaonLad = 20805;
	private static final int CrokianLadWarrior = 20806;
	private static final int FarhiteLad = 20807;
	private static final int NosLad = 20808;
	private static final int SwampTribe = 20991;
	// Drop Cond
	// # [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	public final int[][] DROPLIST_COND =
	{
		{
			1,
			0,
			CrokianLad,
			0,
			AlligatorLeather,
			0,
			60,
			1
		},
		{
			1,
			0,
			DailaonLad,
			0,
			AlligatorLeather,
			0,
			60,
			1
		},
		{
			1,
			0,
			CrokianLadWarrior,
			0,
			AlligatorLeather,
			0,
			60,
			1
		},
		{
			1,
			0,
			FarhiteLad,
			0,
			AlligatorLeather,
			0,
			60,
			1
		},
		{
			1,
			0,
			NosLad,
			0,
			AlligatorLeather,
			0,
			60,
			1
		},
		{
			1,
			0,
			SwampTribe,
			0,
			AlligatorLeather,
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

	public _338_AlligatorHunter()
	{
		super(false);
		addStartNpc(Enverun);
		// Mob Drop
		for (int i = 0; i < DROPLIST_COND.length; i++)
		{
			addKillId(DROPLIST_COND[i][2]);
		}
		addQuestItem(AlligatorLeather);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("30892-02.htm"))
		{
			st.playSound(SOUND_ACCEPT);
			st.setCond(1);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30892-02-afmenu.htm"))
		{
			long AdenaCount = st.getQuestItemsCount(AlligatorLeather) * 40;
			st.takeItems(AlligatorLeather, -1);
			st.giveItems(ADENA_ID, AdenaCount);
		}
		else if (event.equalsIgnoreCase("quit"))
		{
			if (st.getQuestItemsCount(AlligatorLeather) >= 1)
			{
				long AdenaCount = st.getQuestItemsCount(AlligatorLeather) * 40;
				st.takeItems(AlligatorLeather, -1);
				st.giveItems(ADENA_ID, AdenaCount);
				htmltext = "30892-havequit.htm";
			}
			else
			{
				htmltext = "30892-havent.htm";
			}
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "<html><body>I have nothing to say you</body></html>";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == Enverun)
		{
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() >= 40)
				{
					htmltext = "30892-01.htm";
				}
				else
				{
					htmltext = "30892-00.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (st.getQuestItemsCount(AlligatorLeather) == 0)
			{
				htmltext = "30892-02-rep.htm";
			}
			else
			{
				htmltext = "30892-menu.htm";
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