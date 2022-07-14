package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _030_ChestCaughtWithABaitOfFire extends Quest implements ScriptFile
{
	int Linnaeus = 31577;
	int Rukal = 30629;

	int RedTreasureChest = 6511;
	int RukalsMusicalScore = 7628;
	int NecklaceOfProtection = 916;

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

	public _030_ChestCaughtWithABaitOfFire()
	{
		super(false);
		addStartNpc(Linnaeus);
		addTalkId(Rukal);
		addQuestItem(RukalsMusicalScore);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("fisher_linneaus_q0030_0104.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("fisher_linneaus_q0030_0201.htm"))
		{
			if (st.getQuestItemsCount(RedTreasureChest) > 0)
			{
				st.takeItems(RedTreasureChest, 1);
				st.giveItems(RukalsMusicalScore, 1);
				st.setCond(2);
				st.playSound(SOUND_MIDDLE);
			}
			else
			{
				htmltext = "fisher_linneaus_q0030_0202.htm";
			}
		}
		else if (event.equals("bard_rukal_q0030_0301.htm"))
		{
			if (st.getQuestItemsCount(RukalsMusicalScore) == 1)
			{
				st.takeItems(RukalsMusicalScore, -1);
				st.giveItems(NecklaceOfProtection, 1);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
			}
			else
			{
				htmltext = "bard_rukal_q0030_0302.htm";
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
		id = st.getState();
		int cond = st.getCond();
		if (npcId == Linnaeus)
		{
			if (id == CREATED)
			{
				if (st.getPlayer().getLevel() < 60)
				{
					htmltext = "fisher_linneaus_q0030_0102.htm";
					st.exitCurrentQuest(true);
				}
				else
				{
					QuestState LinnaeusSpecialBait = st.getPlayer().getQuestState(_053_LinnaeusSpecialBait.class);
					if (LinnaeusSpecialBait != null)
					{
						if (LinnaeusSpecialBait.isCompleted())
						{
							htmltext = "fisher_linneaus_q0030_0101.htm";
						}
						else
						{
							htmltext = "fisher_linneaus_q0030_0102.htm";
							st.exitCurrentQuest(true);
						}
					}
					else
					{
						htmltext = "fisher_linneaus_q0030_0103.htm";
						st.exitCurrentQuest(true);
					}
				}
			}
			else if (cond == 1)
			{
				htmltext = "fisher_linneaus_q0030_0105.htm";
				if (st.getQuestItemsCount(RedTreasureChest) == 0)
				{
					htmltext = "fisher_linneaus_q0030_0106.htm";
				}
			}
			else if (cond == 2)
			{
				htmltext = "fisher_linneaus_q0030_0203.htm";
			}
		}
		else if (npcId == Rukal)
		{
			if (cond == 2)
			{
				htmltext = "bard_rukal_q0030_0201.htm";
			}
			else
			{
				htmltext = "bard_rukal_q0030_0302.htm";
			}
		}
		return htmltext;
	}
}