package quests;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _10274_CollectingInTheAir extends Quest implements ScriptFile
{
	private final static int Lekon = 32557;

	private final static int StarStoneExtractionScroll = 13844;
	private final static int ExpertTextStarStoneExtractionSkillLevel1 = 13728;
	private final static int ExtractedCoarseRedStarStone = 13858;
	private final static int ExtractedCoarseBlueStarStone = 13859;
	private final static int ExtractedCoarseGreenStarStone = 13860;

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

	public _10274_CollectingInTheAir()
	{
		super(false);

		addStartNpc(Lekon);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equalsIgnoreCase("32557-03.htm"))
		{
			st.setCond(1);
			st.giveItems(StarStoneExtractionScroll, 8);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int id = st.getState();
		if (id == COMPLETED)
		{
			htmltext = "32557-0a.htm";
		}
		else if (id == CREATED)
		{
			QuestState qs = st.getPlayer().getQuestState(_10273_GoodDayToFly.class);
			if (qs != null && qs.isCompleted() && st.getPlayer().getLevel() >= 75)
			{
				htmltext = "32557-01.htm";
			}
			else
			{
				htmltext = "32557-00.htm";
			}
		}
		else if (st.getQuestItemsCount(ExtractedCoarseRedStarStone) + st.getQuestItemsCount(ExtractedCoarseBlueStarStone) + st.getQuestItemsCount(ExtractedCoarseGreenStarStone) >= 8)
		{
			htmltext = "32557-05.htm";
			st.takeAllItems(ExtractedCoarseRedStarStone, ExtractedCoarseBlueStarStone, ExtractedCoarseGreenStarStone);
			st.giveItems(ExpertTextStarStoneExtractionSkillLevel1, 1);
			st.addExpAndSp(25160, 2525);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		else
		{
			htmltext = "32557-04.htm";
		}
		return htmltext;
	}
}