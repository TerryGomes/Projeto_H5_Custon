package quests;

import l2f.gameserver.model.base.Race;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _160_NerupasFavor extends Quest implements ScriptFile
{
	private static int SILVERY_SPIDERSILK = 1026;
	private static int UNOS_RECEIPT = 1027;
	private static int CELS_TICKET = 1028;
	private static int NIGHTSHADE_LEAF = 1029;
	private static int LESSER_HEALING_POTION = 1060;

	private static int NERUPA = 30370;
	private static int UNOREN = 30147;
	private static int CREAMEES = 30149;
	private static int JULIA = 30152;

	/**
	 * Delivery of Goods
	 * Trader Unoren asked Nerupa to collect silvery spidersilks for him.
	 * Norupa doesn't want to enter the village and asks you to deliver the silvery spidersilks to Trader Unoren in the weapons shop and bring back a nightshade leaf.	 *
	 */
	private static int COND1 = 1;

	/**
	 * Nightshade Leaf
	 * Nightshade leaves are very rare. Fortunately, Trader Creamees of the magic shop has obtained a few of them. Go see him with Unoren's receipt.
	 */
	private static int COND2 = 2;

	/**
	 * Go to the Warehouse
	 * Since nightshade leaf is so rare it has been stored in the warehouse. Take Creamees' ticket to Warehouse Keeper Julia.
	 */
	private static int COND3 = 3;

	/**
	 * Goods to be Delivered to Nerupa
	 * You've obtained the nightshade leaf that Creamees stored in the warehouse. Deliver it to Nerupa.
	 */
	private static int COND4 = 4;

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

	public _160_NerupasFavor()
	{
		super(false);

		addStartNpc(NERUPA);

		addTalkId(UNOREN, CREAMEES, JULIA);

		addQuestItem(SILVERY_SPIDERSILK, UNOS_RECEIPT, CELS_TICKET, NIGHTSHADE_LEAF);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("30370-04.htm"))
		{
			st.setCond(COND1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(SILVERY_SPIDERSILK, 1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getCond();
		if (npcId == NERUPA)
		{
			if (st.getState() == CREATED)
			{
				if (st.getPlayer().getRace() != Race.elf)
				{
					htmltext = "30370-00.htm";
				}
				else if (st.getPlayer().getLevel() < 3)
				{
					htmltext = "30370-02.htm";
					st.exitCurrentQuest(true);
				}
				else
				{
					htmltext = "30370-03.htm";
				}
			}
			else if (cond == COND1)
			{
				htmltext = "30370-04.htm";
			}
			else if (cond == COND4 && st.getQuestItemsCount(NIGHTSHADE_LEAF) > 0)
			{
				st.takeItems(NIGHTSHADE_LEAF, -1);
				st.giveItems(LESSER_HEALING_POTION, 5);
				st.addExpAndSp(1000, 0);
				st.playSound(SOUND_FINISH);
				htmltext = "30370-06.htm";
				st.exitCurrentQuest(false);
			}
			else
			{
				htmltext = "30370-05.htm";
			}
		}
		else if (npcId == UNOREN)
		{
			if (cond == COND1)
			{
				st.takeItems(SILVERY_SPIDERSILK, -1);
				st.giveItems(UNOS_RECEIPT, 1);
				st.setCond(COND2);
				htmltext = "30147-01.htm";
			}
			else if (cond == COND2 || cond == COND3)
			{
				htmltext = "30147-02.htm";
			}
			else if (cond == COND4)
			{
				htmltext = "30147-03.htm";
			}
		}
		else if (npcId == CREAMEES)
		{
			if (cond == COND2)
			{
				st.takeItems(UNOS_RECEIPT, -1);
				st.giveItems(CELS_TICKET, 1);
				st.setCond(COND3);
				htmltext = "30149-01.htm";
			}
			else if (cond == COND3)
			{
				htmltext = "30149-02.htm";
			}
			else if (cond == COND4)
			{
				htmltext = "30149-03.htm";
			}
		}
		else if (npcId == JULIA)
		{
			if (cond == COND3)
			{
				st.takeItems(CELS_TICKET, -1);
				st.giveItems(NIGHTSHADE_LEAF, 1);
				htmltext = "30152-01.htm";
				st.setCond(COND4);
			}
			else if (cond == COND4)
			{
				htmltext = "30152-02.htm";
			}
		}
		return htmltext;
	}
}