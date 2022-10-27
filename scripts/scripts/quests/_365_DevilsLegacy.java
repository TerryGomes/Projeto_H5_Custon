package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _365_DevilsLegacy extends Quest implements ScriptFile
{
	// NPC
	private static final int RANDOLF = 30095;

	// MOBS
	int[] MOBS = new int[]
	{
		20836,
		29027,
		20845,
		21629,
		21630,
		29026
	};

	// VARIABLES
	private static final int CHANCE_OF_DROP = 25;
	private static final int REWARD_PER_ONE = 5070;

	// ITEMS
	private static final int TREASURE_CHEST = 5873;

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

	public _365_DevilsLegacy()
	{
		super(false);
		addStartNpc(RANDOLF);
		addKillId(MOBS);
		addQuestItem(TREASURE_CHEST);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("30095-1.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30095-5.htm"))
		{
			long count = st.getQuestItemsCount(TREASURE_CHEST);
			if (count > 0)
			{
				long reward = count * REWARD_PER_ONE;
				st.takeItems(TREASURE_CHEST, -1);
				st.giveItems(ADENA_ID, reward);
			}
			else
			{
				htmltext = "You don't have required items";
			}
		}
		else if (event.equalsIgnoreCase("30095-6.htm"))
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		if (cond == 0)
		{
			if (st.getPlayer().getLevel() >= 39)
			{
				htmltext = "30095-0.htm";
			}
			else
			{
				htmltext = "30095-0a.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if (cond == 1)
		{
			if (st.getQuestItemsCount(TREASURE_CHEST) == 0)
			{
				htmltext = "30095-2.htm";
			}
			else
			{
				htmltext = "30095-4.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (Rnd.chance(CHANCE_OF_DROP))
		{
			st.giveItems(TREASURE_CHEST, 1);
			st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}