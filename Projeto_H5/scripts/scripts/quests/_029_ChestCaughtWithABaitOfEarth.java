package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _029_ChestCaughtWithABaitOfEarth extends Quest implements ScriptFile
{
	int Willie = 31574;
	int Anabel = 30909;

	int SmallPurpleTreasureChest = 6507;
	int SmallGlassBox = 7627;
	int PlatedLeatherGloves = 2455;

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

	public _029_ChestCaughtWithABaitOfEarth()
	{
		super(false);
		addStartNpc(Willie);
		addTalkId(Anabel);
		addQuestItem(SmallGlassBox);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("fisher_willeri_q0029_0104.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("fisher_willeri_q0029_0201.htm"))
		{
			if (st.getQuestItemsCount(SmallPurpleTreasureChest) > 0)
			{
				st.setCond(2);
				st.playSound(SOUND_MIDDLE);
				st.takeItems(SmallPurpleTreasureChest, 1);
				st.giveItems(SmallGlassBox, 1);
			}
			else
			{
				htmltext = "fisher_willeri_q0029_0202.htm";
			}
		}
		else if (event.equals("29_GiveGlassBox"))
		{
			if (st.getQuestItemsCount(SmallGlassBox) == 1)
			{
				htmltext = "magister_anabel_q0029_0301.htm";
				st.takeItems(SmallGlassBox, -1);
				st.giveItems(PlatedLeatherGloves, 1);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
			}
			else
			{
				htmltext = "magister_anabel_q0029_0302.htm";
				st.exitCurrentQuest(true);
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int id = st.getState();
		int cond = st.getCond();
		if (npcId == Willie)
		{
			if (id == CREATED)
			{
				if (st.getPlayer().getLevel() < 48)
				{
					htmltext = "fisher_willeri_q0029_0102.htm";
					st.exitCurrentQuest(true);
				}
				else
				{
					QuestState WilliesSpecialBait = st.getPlayer().getQuestState(_052_WilliesSpecialBait.class);
					if (WilliesSpecialBait != null)
					{
						if (WilliesSpecialBait.isCompleted())
						{
							htmltext = "fisher_willeri_q0029_0101.htm";
						}
						else
						{
							htmltext = "fisher_willeri_q0029_0102.htm";
							st.exitCurrentQuest(true);
						}
					}
					else
					{
						htmltext = "fisher_willeri_q0029_0103.htm";
						st.exitCurrentQuest(true);
					}
				}
			}
			else if (cond == 1)
			{
				htmltext = "fisher_willeri_q0029_0105.htm";
				if (st.getQuestItemsCount(SmallPurpleTreasureChest) == 0)
				{
					htmltext = "fisher_willeri_q0029_0106.htm";
				}
			}
			else if (cond == 2)
			{
				htmltext = "fisher_willeri_q0029_0203.htm";
			}
		}
		else if (npcId == Anabel)
		{
			if (cond == 2)
			{
				htmltext = "magister_anabel_q0029_0201.htm";
			}
			else
			{
				htmltext = "magister_anabel_q0029_0302.htm";
			}
		}
		return htmltext;
	}
}
