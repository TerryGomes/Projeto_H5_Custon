package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _017_LightAndDarkness extends Quest implements ScriptFile
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

	public _017_LightAndDarkness()
	{
		super(false);

		addStartNpc(31517);

		addTalkId(31508);
		addTalkId(31509);
		addTalkId(31510);
		addTalkId(31511);

		addQuestItem(7168);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("dark_presbyter_q0017_04.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.giveItems(7168, 4);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("blessed_altar1_q0017_02.htm"))
		{
			st.takeItems(7168, 1);
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equals("blessed_altar2_q0017_02.htm"))
		{
			st.takeItems(7168, 1);
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equals("blessed_altar3_q0017_02.htm"))
		{
			st.takeItems(7168, 1);
			st.setCond(4);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equals("blessed_altar4_q0017_02.htm"))
		{
			st.takeItems(7168, 1);
			st.setCond(5);
			st.playSound(SOUND_MIDDLE);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch (npcId)
		{
		case 31517:
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() >= 61)
				{
					htmltext = "dark_presbyter_q0017_01.htm";
				}
				else
				{
					htmltext = "dark_presbyter_q0017_03.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond > 0 && cond < 5 && st.getQuestItemsCount(7168) > 0)
			{
				htmltext = "dark_presbyter_q0017_05.htm";
			}
			else if (cond > 0 && cond < 5 && st.getQuestItemsCount(7168) == 0)
			{
				htmltext = "dark_presbyter_q0017_06.htm";
				st.setCond(0);
				st.exitCurrentQuest(false);
			}
			else if (cond == 5 && st.getQuestItemsCount(7168) == 0)
			{
				htmltext = "dark_presbyter_q0017_07.htm";
				st.addExpAndSp(697040, 54887);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
			}
			break;
		case 31508:
			if (cond == 1)
			{
				if (st.getQuestItemsCount(7168) != 0)
				{
					htmltext = "blessed_altar1_q0017_01.htm";
				}
				else
				{
					htmltext = "blessed_altar1_q0017_03.htm";
				}
			}
			else if (cond == 2)
			{
				htmltext = "blessed_altar1_q0017_05.htm";
			}
			break;
		case 31509:
			if (cond == 2)
			{
				if (st.getQuestItemsCount(7168) != 0)
				{
					htmltext = "blessed_altar2_q0017_01.htm";
				}
				else
				{
					htmltext = "blessed_altar2_q0017_03.htm";
				}
			}
			else if (cond == 3)
			{
				htmltext = "blessed_altar2_q0017_05.htm";
			}
			break;
		case 31510:
			if (cond == 3)
			{
				if (st.getQuestItemsCount(7168) != 0)
				{
					htmltext = "blessed_altar3_q0017_01.htm";
				}
				else
				{
					htmltext = "blessed_altar3_q0017_03.htm";
				}
			}
			else if (cond == 4)
			{
				htmltext = "blessed_altar3_q0017_05.htm";
			}
			break;
		case 31511:
			if (cond == 4)
			{
				if (st.getQuestItemsCount(7168) != 0)
				{
					htmltext = "blessed_altar4_q0017_01.htm";
				}
				else
				{
					htmltext = "blessed_altar4_q0017_03.htm";
				}
			}
			else if (cond == 5)
			{
				htmltext = "blessed_altar4_q0017_05.htm";
			}
			break;
		default:
			break;
		}
		return htmltext;
	}
}