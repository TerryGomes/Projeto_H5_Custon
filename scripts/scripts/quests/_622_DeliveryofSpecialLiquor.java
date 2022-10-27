package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _622_DeliveryofSpecialLiquor extends Quest implements ScriptFile
{
	// NPCs
	private static int JEREMY = 31521;
	private static int LIETTA = 31267;
	private static int PULIN = 31543;
	private static int NAFF = 31544;
	private static int CROCUS = 31545;
	private static int KUBER = 31546;
	private static int BEOLIN = 31547;
	// Quest Items
	private static int SpecialDrink = 7207;
	private static int FeeOfSpecialDrink = 7198;
	// Items
	private static int RecipeSealedTateossianRing = 6849;
	private static int RecipeSealedTateossianEarring = 6847;
	private static int RecipeSealedTateossianNecklace = 6851;
	private static int HastePotion = 734;
	// Chances
	private static int Tateossian_CHANCE = 20;

	public _622_DeliveryofSpecialLiquor()
	{
		super(false);
		addStartNpc(JEREMY);
		addTalkId(LIETTA);
		addTalkId(PULIN);
		addTalkId(NAFF);
		addTalkId(CROCUS);
		addTalkId(KUBER);
		addTalkId(BEOLIN);
		addQuestItem(SpecialDrink);
		addQuestItem(FeeOfSpecialDrink);
	}

	private static void takeDrink(QuestState st, int setcond)
	{
		st.setCond(Integer.valueOf(setcond));
		st.takeItems(SpecialDrink, 1);
		st.giveItems(FeeOfSpecialDrink, 1);
		st.playSound(SOUND_MIDDLE);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int _state = st.getState();
		int cond = st.getCond();
		long SpecialDrink_count = st.getQuestItemsCount(SpecialDrink);

		if (event.equalsIgnoreCase("jeremy_q0622_0104.htm") && _state == CREATED)
		{
			st.setState(STARTED);
			st.setCond(1);
			st.takeItems(SpecialDrink, -1);
			st.takeItems(FeeOfSpecialDrink, -1);
			st.giveItems(SpecialDrink, 5);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("beolin_q0622_0201.htm") && cond == 1 && SpecialDrink_count > 0)
		{
			takeDrink(st, 2);
		}
		else if (event.equalsIgnoreCase("kuber_q0622_0301.htm") && cond == 2 && SpecialDrink_count > 0)
		{
			takeDrink(st, 3);
		}
		else if (event.equalsIgnoreCase("crocus_q0622_0401.htm") && cond == 3 && SpecialDrink_count > 0)
		{
			takeDrink(st, 4);
		}
		else if (event.equalsIgnoreCase("naff_q0622_0501.htm") && cond == 4 && SpecialDrink_count > 0)
		{
			takeDrink(st, 5);
		}
		else if (event.equalsIgnoreCase("pulin_q0622_0601.htm") && cond == 5 && SpecialDrink_count > 0)
		{
			takeDrink(st, 6);
		}
		else if (event.equalsIgnoreCase("jeremy_q0622_0701.htm") && cond == 6 && st.getQuestItemsCount(FeeOfSpecialDrink) >= 5)
		{
			st.setCond(7);
		}
		else if (event.equalsIgnoreCase("warehouse_keeper_lietta_q0622_0801.htm") && cond == 7 && st.getQuestItemsCount(FeeOfSpecialDrink) >= 5)
		{
			st.takeItems(SpecialDrink, -1);
			st.takeItems(FeeOfSpecialDrink, -1);
			if (Rnd.chance(Tateossian_CHANCE))
			{
				if (Rnd.chance(40))
				{
					st.giveItems(RecipeSealedTateossianRing, 1);
				}
				else if (Rnd.chance(40))
				{
					st.giveItems(RecipeSealedTateossianEarring, 1);
				}
				else
				{
					st.giveItems(RecipeSealedTateossianNecklace, 1);
				}
			}
			else
			{
				st.giveItems(ADENA_ID, 18800);
				st.giveItems(HastePotion, 1, true);
			}

			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}

		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		if (st.getState() == CREATED)
		{
			if (npcId != JEREMY)
			{
				return htmltext;
			}
			if (st.getPlayer().getLevel() >= 68)
			{
				st.setCond(0);
				return "jeremy_q0622_0101.htm";
			}
			st.exitCurrentQuest(true);
			return "jeremy_q0622_0103.htm";
		}

		int cond = st.getCond();
		long SpecialDrink_count = st.getQuestItemsCount(SpecialDrink);
		long FeeOfSpecialDrink_count = st.getQuestItemsCount(FeeOfSpecialDrink);

		if (cond == 1 && npcId == BEOLIN && SpecialDrink_count > 0)
		{
			htmltext = "beolin_q0622_0101.htm";
		}
		else if (cond == 2 && npcId == KUBER && SpecialDrink_count > 0)
		{
			htmltext = "kuber_q0622_0201.htm";
		}
		else if (cond == 3 && npcId == CROCUS && SpecialDrink_count > 0)
		{
			htmltext = "crocus_q0622_0301.htm";
		}
		else if (cond == 4 && npcId == NAFF && SpecialDrink_count > 0)
		{
			htmltext = "naff_q0622_0401.htm";
		}
		else if (cond == 5 && npcId == PULIN && SpecialDrink_count > 0)
		{
			htmltext = "pulin_q0622_0501.htm";
		}
		else if (cond == 6 && npcId == JEREMY && FeeOfSpecialDrink_count >= 5)
		{
			htmltext = "jeremy_q0622_0601.htm";
		}
		else if (cond == 7 && npcId == JEREMY && FeeOfSpecialDrink_count >= 5)
		{
			htmltext = "jeremy_q0622_0703.htm";
		}
		else if (cond == 7 && npcId == LIETTA && FeeOfSpecialDrink_count >= 5)
		{
			htmltext = "warehouse_keeper_lietta_q0622_0701.htm";
		}
		else if (cond > 0 && npcId == JEREMY && SpecialDrink_count > 0)
		{
			htmltext = "jeremy_q0622_0104.htm";
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