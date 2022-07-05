package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _377_GiantsExploration2 extends Quest implements ScriptFile
{
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

	// Titan Ancient Books drop rate in %
	private static final int DROP_RATE = 20;

	// Quest items
	private static final int ANC_BOOK = 14847;
	private static final int DICT2 = 5892;

	// Quest collections
	private static final int[][] EXCHANGE =
	{
		{
			5945,
			5946,
			5947,
			5948,
			5949
		},
		// science basis
		{
			5950,
			5951,
			5952,
			5953,
			5954
		}
		// culture
	};

	// NPCs
	private static final int HR_SOBLING = 31147;

	// Mobs
	private static final int[] MOBS =
	{
		// список мобов для квеста
		22661,
		// Lesser Giant Soldier L81
		22662,
		// Lesser Giant Shooter L82
		22663,
		// Lesser Giant Scout L82
		22664,
		// Lesser Giant Mage L82
		22665,
		// Lesser Giant Elder L82
		22666,
		// Barif L82
		22667,
		// Barif's Pet L81
		22668,
		// Gamlin L81
		22669,
		// Leogul L82
	};

	public _377_GiantsExploration2()
	{
		super(true);
		addStartNpc(HR_SOBLING);
		addKillId(MOBS);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("yes"))
		{
			htmltext = "Starting.htm";
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("0"))
		{
			htmltext = "ext_msg.htm";
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		else if (event.equalsIgnoreCase("show"))
		{
			htmltext = "no_items.htm";
			for (int[] i : EXCHANGE)
			{
				long count = Long.MAX_VALUE;
				for (int j : i)
				{
					count = Math.min(count, st.getQuestItemsCount(j));
				}
				if (count > 0)
				{
					htmltext = "tnx4items.htm";
					for (int j : i)
					{
						st.takeItems(j, count);
					}
					for (int n = 0; n < count; n++)
					{
						int luck = Rnd.get(100);
						int item = 0;
						if (luck > 75)
						{
							item = 5420; // nightmare leather 60%
						}
						else if (luck > 50)
						{
							item = 5422; // majestic plate 60%
						}
						else if (luck > 25)
						{
							item = 5336; // nightmare armor 60%
						}
						else
						{
							item = 5338; // majestic leather 60%
						}
						st.giveItems(item, 1);
					}
				}
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int id = st.getState();
		if (st.getQuestItemsCount(DICT2) == 0)
		{
			st.exitCurrentQuest(true);
		}
		else if (id == CREATED)
		{
			htmltext = "start.htm";
			if (st.getPlayer().getLevel() < 75)
			{
				st.exitCurrentQuest(true);
				htmltext = "error_1.htm";
			}
		}
		else if (id == STARTED)
		{
			if (st.getQuestItemsCount(ANC_BOOK) != 0)
			{
				htmltext = "checkout.htm";
			}
			else
			{
				htmltext = "checkout2.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getCond() == 1)
		{
			st.rollAndGive(ANC_BOOK, 1, DROP_RATE);
		}
		return null;
	}
}