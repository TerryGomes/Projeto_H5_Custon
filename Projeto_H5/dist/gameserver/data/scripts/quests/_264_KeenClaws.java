package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * Квест Keen Claws
 *
 * @author Sergey Ibryaev aka Artful
 */

public class _264_KeenClaws extends Quest implements ScriptFile
{
	// NPC
	private static final int Payne = 30136;
	// Quest Items
	private static final int WolfClaw = 1367;
	// Items
	private static final int LeatherSandals = 36;
	private static final int WoodenHelmet = 43;
	private static final int Stockings = 462;
	private static final int HealingPotion = 1061;
	private static final int ShortGloves = 48;
	private static final int ClothShoes = 35;
	// MOB
	private static final int Goblin = 20003;
	private static final int AshenWolf = 20456;
	// Drop Cond
	// # [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	private static final int[][] DROPLIST_COND =
	{
		{
			1,
			2,
			Goblin,
			0,
			WolfClaw,
			50,
			50,
			2
		},
		{
			1,
			2,
			AshenWolf,
			0,
			WolfClaw,
			50,
			50,
			2
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

	public _264_KeenClaws()
	{
		super(false);

		addStartNpc(Payne);

		addKillId(Goblin);
		addKillId(AshenWolf);

		addQuestItem(WolfClaw);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("paint_q0264_03.htm"))
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
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == Payne)
		{
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getLevel() >= 3)
				{
					htmltext = "paint_q0264_02.htm";
				}
				else
				{
					st.exitCurrentQuest(true);
					return "paint_q0264_01.htm";
				}
				break;
			case 1:
				htmltext = "paint_q0264_04.htm";
				break;
			case 2:
			{
				st.takeItems(WolfClaw, -1);
				int n = Rnd.get(17);
				if (n == 0)
				{
					st.giveItems(WoodenHelmet, 1);
					st.playSound(SOUND_JACKPOT);
				}
				else if (n < 2)
				{
					st.giveItems(ADENA_ID, 1000);
				}
				else if (n < 5)
				{
					st.giveItems(LeatherSandals, 1);
				}
				else if (n < 8)
				{
					st.giveItems(Stockings, 1);
					st.giveItems(ADENA_ID, 50);
				}
				else if (n < 11)
				{
					st.giveItems(HealingPotion, 1);
				}
				else if (n < 14)
				{
					st.giveItems(ShortGloves, 1);
				}
				else
				{
					st.giveItems(ClothShoes, 1);
				}
				htmltext = "paint_q0264_05.htm";
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
				break;
			}
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