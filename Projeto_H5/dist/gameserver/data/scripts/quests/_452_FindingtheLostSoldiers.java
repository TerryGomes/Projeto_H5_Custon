package quests;

import org.apache.commons.lang3.ArrayUtils;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _452_FindingtheLostSoldiers extends Quest implements ScriptFile
{
	private static final int JAKAN = 32773;
	private static final int TAG_ID = 15513;
	private static final int[] SOLDIER_CORPSES =
	{
		32769,
		32770,
		32771,
		32772
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

	public _452_FindingtheLostSoldiers()
	{
		super(false);

		addStartNpc(JAKAN);
		addTalkId(JAKAN);
		addTalkId(SOLDIER_CORPSES);
		addQuestItem(TAG_ID);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (npc == null)
		{
			return event;
		}

		if (npc.getNpcId() == JAKAN)
		{
			if (event.equalsIgnoreCase("32773-3.htm"))
			{
				st.setState(STARTED);
				st.setCond(1);
				st.playSound(SOUND_ACCEPT);
			}
		}
		else if (ArrayUtils.contains(SOLDIER_CORPSES, npc.getNpcId()) && st.getCond() == 1)
		{
			st.giveItems(TAG_ID, 1);
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
			npc.deleteMe();
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		if (npc == null)
		{
			return htmltext;
		}

		if (npc.getNpcId() == JAKAN)
		{
			switch (st.getState())
			{
			case CREATED:
				if (st.getPlayer().getLevel() >= 84)
				{
					if (st.isNowAvailable())
					{
						htmltext = "32773-1.htm";
					}
					else
					{
						htmltext = "32773-6.htm";
					}
				}
				else
				{
					htmltext = "32773-0.htm";
				}
				break;
			case STARTED:
				if (st.getCond() == 1)
				{
					htmltext = "32773-4.htm";
				}
				else if (st.getCond() == 2)
				{
					htmltext = "32773-5.htm";
					st.unset("cond");
					st.takeItems(TAG_ID, 1);
					st.giveItems(57, 95200);
					st.addExpAndSp(435024, 50366);
					st.playSound(SOUND_FINISH);
					st.exitCurrentQuest(this);
				}
				break;
			}
		}
		else if (ArrayUtils.contains(SOLDIER_CORPSES, npc.getNpcId()))
		{
			if (st.getCond() == 1)
			{
				htmltext = "corpse-1.htm";
			}
		}

		return htmltext;
	}
}