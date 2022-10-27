package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * Квест Bring Out The Flavor Of Ingredients
 *
 * @author Sergey Ibryaev aka Artful
 */

public class _380_BringOutTheFlavorOfIngredients extends Quest implements ScriptFile
{
	// NPCs
	private static final int Rollant = 30069;
	// Quest Items
	private static final int RitronsFruit = 5895;
	private static final int MoonFaceFlower = 5896;
	private static final int LeechFluids = 5897;
	// Items
	private static final int Antidote = 1831;
	private static final int RitronsDessertRecipe = 5959;
	private static final int RitronJelly = 5960;
	// Chances
	private static final int RitronsDessertRecipeChance = 55;
	// Mobs
	private static final int DireWolf = 20205;
	private static final int KadifWerewolf = 20206;
	private static final int GiantMistLeech = 20225;
	// Drop Cond
	// # [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	private static final int[][] DROPLIST_COND =
	{
		{
			1,
			0,
			DireWolf,
			0,
			RitronsFruit,
			4,
			10,
			1
		},
		{
			1,
			0,
			KadifWerewolf,
			0,
			MoonFaceFlower,
			20,
			50,
			1
		},
		{
			1,
			0,
			GiantMistLeech,
			0,
			LeechFluids,
			10,
			50,
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

	public _380_BringOutTheFlavorOfIngredients()
	{
		super(false);
		addStartNpc(Rollant);

		for (int i = 0; i < DROPLIST_COND.length; i++)
		{
			addKillId(DROPLIST_COND[i][2]);
			addQuestItem(DROPLIST_COND[i][4]);
		}
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equalsIgnoreCase("rollant_q0380_05.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("rollant_q0380_12.htm"))
		{
			st.giveItems(RitronsDessertRecipe, 1);
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
		if (npcId == Rollant)
		{
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() >= 24)
				{
					htmltext = "rollant_q0380_02.htm";
				}
				else
				{
					htmltext = "rollant_q0380_01.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 1)
			{
				htmltext = "rollant_q0380_06.htm";
			}
			else if (cond == 2 && st.getQuestItemsCount(Antidote) >= 2)
			{
				st.takeItems(Antidote, 2);
				st.takeItems(RitronsFruit, -1);
				st.takeItems(MoonFaceFlower, -1);
				st.takeItems(LeechFluids, -1);
				htmltext = "rollant_q0380_07.htm";
				st.setCond(3);
				st.setState(STARTED);
			}
			else
			{
				switch (cond)
				{
				case 2:
					htmltext = "rollant_q0380_06.htm";
					break;
				case 3:
					htmltext = "rollant_q0380_08.htm";
					st.setCond(4);
					break;
				case 4:
					htmltext = "rollant_q0380_09.htm";
					st.setCond(5);
					break;
				default:
					break;
				}
			}
			if (cond == 5)
			{
				htmltext = "rollant_q0380_10.htm";
				st.setCond(6);
			}
			if (cond == 6)
			{
				st.giveItems(RitronJelly, 1);
				if (Rnd.chance(RitronsDessertRecipeChance))
				{
					htmltext = "rollant_q0380_11.htm";
				}
				else
				{
					htmltext = "rollant_q0380_14.htm";
					st.playSound(SOUND_FINISH);
					st.exitCurrentQuest(true);
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
		if (cond == 1 && st.getQuestItemsCount(RitronsFruit) >= 4 && st.getQuestItemsCount(MoonFaceFlower) >= 20 && st.getQuestItemsCount(LeechFluids) >= 10)
		{
			st.setCond(2);
			st.setState(STARTED);
		}
		return null;
	}
}