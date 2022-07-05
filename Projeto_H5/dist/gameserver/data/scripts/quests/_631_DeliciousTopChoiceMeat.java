package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _631_DeliciousTopChoiceMeat extends Quest implements ScriptFile
{
	// NPC
	private final int TUNATUN = 31537;
	// MOBS
	private final int MOB_LIST[] =
	{
		18878,
		18879,
		18885,
		18886,
		18892,
		18893,
		18899,
		18900
	}; // Full Grown Kookabura/Cougar/Buffalo/Grendel
		// ITEMS
	private final int PRIME_MEAT = 15534;
	// CHANCE
	private final int MEAT_DROP_CHANCE = 100;
	// REWARDS
	private final int[][] REWARDS =
	{
		{
			10373,
			1,
			1
		},
		{
			10374,
			1,
			1
		},
		{
			10375,
			1,
			1
		},
		{
			10376,
			1,
			1
		},
		{
			10377,
			1,
			1
		},
		{
			10378,
			1,
			1
		},
		{
			10379,
			1,
			1
		},
		{
			10380,
			1,
			1
		},
		{
			10381,
			1,
			1
		},
		{
			10397,
			1,
			9
		},
		{
			10398,
			1,
			9
		},
		{
			10399,
			1,
			9
		},
		{
			10400,
			1,
			9
		},
		{
			10401,
			1,
			9
		},
		{
			10402,
			1,
			9
		},
		{
			10403,
			1,
			9
		},
		{
			10404,
			1,
			9
		},
		{
			10405,
			1,
			9
		},
		{
			15482,
			1,
			2
		},
		{
			15483,
			1,
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

	public _631_DeliciousTopChoiceMeat()
	{
		super(false);

		addStartNpc(TUNATUN);

		addTalkId(TUNATUN);

		for (int i : MOB_LIST)
		{
			addKillId(i);
		}

		addQuestItem(PRIME_MEAT);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("beast_herder_tunatun_q0631_0104.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("beast_herder_tunatun_q0631_0201.htm"))
		{
			if (st.getQuestItemsCount(PRIME_MEAT) >= 120)
			{
				st.takeItems(PRIME_MEAT, -1);
				int[] reward = REWARDS[Rnd.get(0, REWARDS.length - 1)];
				int count = Rnd.get(reward[1], reward[2]);
				st.giveItems(reward[0], Math.round(count * st.getRateQuestsReward()));
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "beast_herder_tunatun_q0631_0202.htm";
				st.setCond(1);
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		if (cond < 1)
		{
			if (st.getPlayer().getLevel() < 82)
			{
				htmltext = "beast_herder_tunatun_q0631_0103.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "beast_herder_tunatun_q0631_0101.htm";
			}
		}
		else if (cond == 1)
		{
			htmltext = "beast_herder_tunatun_q0631_0106.htm";
		}
		else if (cond == 2)
		{
			if (st.getQuestItemsCount(PRIME_MEAT) < 120)
			{
				htmltext = "beast_herder_tunatun_q0631_0106.htm";
				st.setCond(1);
			}
			else
			{
				htmltext = "beast_herder_tunatun_q0631_0105.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getCond() == 1 && Rnd.chance(MEAT_DROP_CHANCE))
		{
			st.giveItems(PRIME_MEAT, 1, true);
			if (st.getQuestItemsCount(PRIME_MEAT) < 120)
			{
				st.playSound(SOUND_ITEMGET);
			}
			else
			{
				st.playSound(SOUND_MIDDLE);
				st.setCond(2);
			}
		}
		return null;
	}
}