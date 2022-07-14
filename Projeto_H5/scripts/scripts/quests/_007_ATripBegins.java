package quests;

import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _007_ATripBegins extends Quest implements ScriptFile
{
	int MIRABEL = 30146;
	int ARIEL = 30148;
	int ASTERIOS = 30154;

	int ARIELS_RECOMMENDATION = 7572;

	int SCROLL_OF_ESCAPE_GIRAN = 7126;
	int MARK_OF_TRAVELER = 7570;

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

	public _007_ATripBegins()
	{
		super(false);

		addStartNpc(MIRABEL);

		addTalkId(MIRABEL);
		addTalkId(ARIEL);
		addTalkId(ASTERIOS);

		addQuestItem(ARIELS_RECOMMENDATION);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("mint_q0007_0104.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("ariel_q0007_0201.htm"))
		{
			st.giveItems(ARIELS_RECOMMENDATION, 1);
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("ozzy_q0007_0301.htm"))
		{
			st.takeItems(ARIELS_RECOMMENDATION, -1);
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("mint_q0007_0401.htm"))
		{
			st.giveItems(SCROLL_OF_ESCAPE_GIRAN, 1);
			st.giveItems(MARK_OF_TRAVELER, 1);
			st.setCond(0);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == MIRABEL)
		{
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getRace() == Race.elf && st.getPlayer().getLevel() >= 3)
				{
					htmltext = "mint_q0007_0101.htm";
				}
				else
				{
					htmltext = "mint_q0007_0102.htm";
					st.exitCurrentQuest(true);
				}
				break;
			case 1:
				htmltext = "mint_q0007_0105.htm";
				break;
			case 3:
				htmltext = "mint_q0007_0301.htm";
				break;
			default:
				break;
			}
		}
		else if (npcId == ARIEL)
		{
			if (cond == 1 && st.getQuestItemsCount(ARIELS_RECOMMENDATION) == 0)
			{
				htmltext = "ariel_q0007_0101.htm";
			}
			else if (cond == 2)
			{
				htmltext = "ariel_q0007_0202.htm";
			}
		}
		else if (npcId == ASTERIOS)
		{
			if (cond == 2 && st.getQuestItemsCount(ARIELS_RECOMMENDATION) > 0)
			{
				htmltext = "ozzy_q0007_0201.htm";
			}
			else if (cond == 2 && st.getQuestItemsCount(ARIELS_RECOMMENDATION) == 0)
			{
				htmltext = "ozzy_q0007_0302.htm";
			}
			else if (cond == 3)
			{
				htmltext = "ozzy_q0007_0303.htm";
			}
		}
		return htmltext;
	}
}