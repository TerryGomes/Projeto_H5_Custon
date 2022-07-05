package quests;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

/**
 * @author pchayka
 *         Repeatable
 */
public class _904_DragonTrophyAntharas extends Quest implements ScriptFile
{
	private static final int Theodric = 30755;
	private static final int AntharasMax = 29068;
	private static final int MedalofGlory = 21874;

	public _904_DragonTrophyAntharas()
	{
		super(PARTY_ALL);
		addStartNpc(Theodric);
		addKillId(AntharasMax);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("theodric_q904_04.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("theodric_q904_07.htm"))
		{
			st.giveItems(MedalofGlory, 30);
			st.setState(COMPLETED);
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
		if (npc.getNpcId() == Theodric)
		{
			switch (st.getState())
			{
			case CREATED:
				if (st.getPlayer().getLevel() >= 84)
				{
					if (st.getQuestItemsCount(3865) > 0)
					{
						htmltext = "theodric_q904_01.htm";
					}
					else
					{
						htmltext = "theodric_q904_00b.htm";
					}
				}
				else
				{
					htmltext = "theodric_q904_00.htm";
					st.exitCurrentQuest(true);
				}
				break;
			case STARTED:
				if (cond == 1)
				{
					htmltext = "theodric_q904_05.htm";
				}
				else if (cond == 2)
				{
					htmltext = "theodric_q904_06.htm";
				}
				break;
			}
		}

		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if (cond == 1)
		{
			if (npc.getNpcId() == AntharasMax)
			{
				st.setCond(2);
			}
		}
		return null;
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