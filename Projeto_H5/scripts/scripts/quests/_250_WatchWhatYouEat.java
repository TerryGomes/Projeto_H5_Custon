package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _250_WatchWhatYouEat extends Quest implements ScriptFile
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

	// NPCs
	private static final int SALLY = 32743;
	// Mobs - Items
	private static final int[][] MOBS =
	{
		{
			18864,
			15493
		},
		{
			18865,
			15494
		},
		{
			18868,
			15495
		}
	};

	public _250_WatchWhatYouEat()
	{
		super(false);

		addStartNpc(SALLY);
		addTalkId(SALLY);
		for (int i[] : MOBS)
		{
			addKillId(i[0]);
		}
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if (npc.getNpcId() == SALLY)
		{
			if (event.equalsIgnoreCase("32743-03.htm"))
			{
				st.setState(STARTED);
				st.setCond(1);
				st.playSound(SOUND_ACCEPT);
			}
			else if (event.equalsIgnoreCase("32743-end.htm"))
			{
				st.unset("cond");
				st.giveItems(57, 135661, true);
				st.addExpAndSp(698334, 76369);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		if (npc.getNpcId() == SALLY)
		{
			switch (st.getState())
			{
			case CREATED:
				if (st.getPlayer().getLevel() >= 82)
				{
					htmltext = "32743-01.htm";
				}
				else
				{
					htmltext = "32743-00.htm";
				}
				break;
			case STARTED:
				if (cond == 1)
				{
					htmltext = "32743-04.htm";
				}
				else if (cond == 2)
				{
					if (st.getQuestItemsCount(MOBS[0][1]) > 0 && st.getQuestItemsCount(MOBS[1][1]) > 0 && st.getQuestItemsCount(MOBS[2][1]) > 0)
					{
						htmltext = "32743-05.htm";
						for (int items[] : MOBS)
						{
							st.takeItems(items[1], -1);
						}
					}
					else
					{
						htmltext = "32743-06.htm";
					}
				}
				break;
			case COMPLETED:
				htmltext = "32743-done.htm";
				break;
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getState() == STARTED && st.getCond() == 1)
		{
			for (int mob[] : MOBS)
			{
				if (npc.getNpcId() == mob[0])
				{
					if (st.getQuestItemsCount(mob[1]) == 0)
					{
						st.giveItems(mob[1], 1);
						st.playSound(SOUND_ITEMGET);
					}
				}
			}
			if (st.getQuestItemsCount(MOBS[0][1]) > 0 && st.getQuestItemsCount(MOBS[1][1]) > 0 && st.getQuestItemsCount(MOBS[2][1]) > 0)
			{
				st.setCond(2);
				st.playSound(SOUND_MIDDLE);
			}
		}
		return null;
	}
}
