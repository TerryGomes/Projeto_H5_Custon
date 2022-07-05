package quests;

import java.util.List;

import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.ai.CtrlIntention;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

/**
 * @author pchayka
 */

public class _454_CompletelyLost extends Quest implements ScriptFile
{
	private static final int WoundedSoldier = 32738;
	private static final int Ermian = 32736;
	private static final int[][] rewards =
	{
		{
			15792,
			1
		},
		{
			15798,
			1
		},
		{
			15795,
			1
		},
		{
			15801,
			1
		},
		{
			15808,
			1
		},
		{
			15804,
			1
		},
		{
			15809,
			1
		},
		{
			15810,
			1
		},
		{
			15811,
			1
		},
		{
			15660,
			3
		},
		{
			15666,
			3
		},
		{
			15663,
			3
		},
		{
			15667,
			3
		},
		{
			15669,
			3
		},
		{
			15668,
			3
		},
		{
			15769,
			3
		},
		{
			15770,
			3
		},
		{
			15771,
			3
		},
		{
			15805,
			1
		},
		{
			15796,
			1
		},
		{
			15793,
			1
		},
		{
			15799,
			1
		},
		{
			15802,
			1
		},
		{
			15809,
			1
		},
		{
			15810,
			1
		},
		{
			15811,
			1
		},
		{
			15672,
			3
		},
		{
			15664,
			3
		},
		{
			15661,
			3
		},
		{
			15670,
			3
		},
		{
			15671,
			3
		},
		{
			15769,
			3
		},
		{
			15770,
			3
		},
		{
			15771,
			3
		},
		{
			15800,
			1
		},
		{
			15803,
			1
		},
		{
			15806,
			1
		},
		{
			15807,
			1
		},
		{
			15797,
			1
		},
		{
			15794,
			1
		},
		{
			15809,
			1
		},
		{
			15810,
			1
		},
		{
			15811,
			1
		},
		{
			15673,
			3
		},
		{
			15674,
			3
		},
		{
			15675,
			3
		},
		{
			15691,
			3
		},
		{
			15665,
			3
		},
		{
			15662,
			3
		},
		{
			15769,
			3
		},
		{
			15770,
			3
		},
		{
			15771,
			3
		}
	};

	public _454_CompletelyLost()
	{
		super(PARTY_ALL);
		addStartNpc(WoundedSoldier);
		addTalkId(Ermian);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equalsIgnoreCase("wounded_soldier_q454_02.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("wounded_soldier_q454_03.htm"))
		{
			if (seeSoldier(npc, st.getPlayer()) == null)
			{
				npc.setFollowTarget(st.getPlayer());
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, st.getPlayer(), Config.FOLLOW_RANGE);
			}
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		if (npc.getNpcId() == WoundedSoldier)
		{
			switch (st.getState())
			{
			case CREATED:
				if (st.isNowAvailable())
				{
					if (st.getPlayer().getLevel() >= 84)
					{
						htmltext = "wounded_soldier_q454_01.htm";
					}
					else
					{
						htmltext = "wounded_soldier_q454_00.htm";
						st.exitCurrentQuest(true);
					}
				}
				else
				{
					htmltext = "wounded_soldier_q454_00a.htm";
				}
				break;
			case STARTED:
				if (st.getCond() == 1)
				{
					htmltext = "wounded_soldier_q454_04.htm";
					if (seeSoldier(npc, st.getPlayer()) == null)
					{
						npc.setFollowTarget(st.getPlayer());
						npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, st.getPlayer(), Config.FOLLOW_RANGE);
					}
				}
				break;
			}
		}
		else if (npc.getNpcId() == Ermian)
		{
			if (st.getCond() == 1)
			{
				if (seeSoldier(npc, st.getPlayer()) != null)
				{
					htmltext = "ermian_q454_01.htm";
					NpcInstance soldier = seeSoldier(npc, st.getPlayer());
					soldier.doDie(null);
					soldier.endDecayTask();
					giveReward(st);
					st.setState(COMPLETED);
					st.playSound(SOUND_FINISH);
					st.exitCurrentQuest(this);
				}
				else
				{
					htmltext = "ermian_q454_02.htm";
				}
			}
		}

		return htmltext;
	}

	private NpcInstance seeSoldier(NpcInstance npc, Player player)
	{
		List<NpcInstance> around = npc.getAroundNpc(Config.FOLLOW_RANGE * 2, 300);
		if (around != null && !around.isEmpty())
		{
			for (NpcInstance n : around)
			{
				if (n.getNpcId() == WoundedSoldier && n.getFollowTarget() != null)
				{
					if (n.getFollowTarget().getObjectId() == player.getObjectId())
					{
						return n;
					}
				}
			}
		}

		return null;
	}

	private void giveReward(QuestState st)
	{
		int row = Rnd.get(0, rewards.length - 1);
		int id = rewards[row][0];
		int count = rewards[row][1];
		st.giveItems(id, count);
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