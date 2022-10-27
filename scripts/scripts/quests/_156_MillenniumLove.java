package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _156_MillenniumLove extends Quest implements ScriptFile
{
	int LILITHS_LETTER = 1022;
	int THEONS_DIARY = 1023;
	int GR_COMP_PACKAGE_SS = 5250;
	int GR_COMP_PACKAGE_SPS = 5256;

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

	public _156_MillenniumLove()
	{
		super(false);

		addStartNpc(30368);

		addTalkId(30368);
		addTalkId(30368);
		addTalkId(30368);
		addTalkId(30369);

		addQuestItem(new int[]
		{
			LILITHS_LETTER,
			THEONS_DIARY
		});
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("30368-06.htm"))
		{
			st.giveItems(LILITHS_LETTER, 1);
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("156_1"))
		{
			st.takeItems(LILITHS_LETTER, -1);
			if (st.getQuestItemsCount(THEONS_DIARY) == 0)
			{
				st.giveItems(THEONS_DIARY, 1);
				st.setCond(2);
			}
			htmltext = "30369-03.htm";
		}
		else if (event.equals("156_2"))
		{
			st.takeItems(LILITHS_LETTER, -1);
			st.playSound(SOUND_FINISH);
			htmltext = "30369-04.htm";
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getCond();
		if (npcId == 30368)
		{
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() >= 15)
				{
					htmltext = "30368-02.htm";
				}
				else
				{
					htmltext = "30368-05.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 1 && st.getQuestItemsCount(LILITHS_LETTER) == 1)
			{
				htmltext = "30368-07.htm";
			}
			else if (cond == 2 && st.getQuestItemsCount(THEONS_DIARY) == 1)
			{
				st.takeItems(THEONS_DIARY, -1);
				if (st.getPlayer().getClassId().isMage())
				{
					st.giveItems(GR_COMP_PACKAGE_SPS, 1);
				}
				else
				{
					st.giveItems(GR_COMP_PACKAGE_SS, 1);
				}
				st.addExpAndSp(3000, 0);
				st.playSound(SOUND_FINISH);
				htmltext = "30368-08.htm";
				st.exitCurrentQuest(false);
			}
		}
		else if (npcId == 30369)
		{
			if (cond == 1 && st.getQuestItemsCount(LILITHS_LETTER) == 1)
			{
				htmltext = "30369-02.htm";
			}
			else if (cond == 2 && st.getQuestItemsCount(THEONS_DIARY) == 1)
			{
				htmltext = "30369-05.htm";
			}
		}
		return htmltext;
	}
}