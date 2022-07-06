package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _027_ChestCaughtWithABaitOfWind extends Quest implements ScriptFile
{
	// NPC List
	private static final int Lanosco = 31570;
	private static final int Shaling = 31434;
	// Quest Items
	private static final int StrangeGolemBlueprint = 7625;
	// Items
	private static final int BigBlueTreasureChest = 6500;
	private static final int BlackPearlRing = 880;

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

	public _027_ChestCaughtWithABaitOfWind()
	{
		super(false);

		addStartNpc(Lanosco);
		addTalkId(Shaling);
		addQuestItem(StrangeGolemBlueprint);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("fisher_lanosco_q0027_0104.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("fisher_lanosco_q0027_0201.htm"))
		{
			if (st.getQuestItemsCount(BigBlueTreasureChest) > 0)
			{
				st.takeItems(BigBlueTreasureChest, 1);
				st.giveItems(StrangeGolemBlueprint, 1);
				st.setCond(2);
				st.playSound(SOUND_MIDDLE);
			}
			else
			{
				htmltext = "fisher_lanosco_q0027_0202.htm";
			}
		}
		else if (event.equals("blueprint_seller_shaling_q0027_0301.htm"))
		{
			if (st.getQuestItemsCount(StrangeGolemBlueprint) == 1)
			{
				st.takeItems(StrangeGolemBlueprint, -1);
				st.giveItems(BlackPearlRing, 1);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
			}
			else
			{
				htmltext = "blueprint_seller_shaling_q0027_0302.htm";
				st.exitCurrentQuest(true);
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		int id = st.getState();
		if (npcId == Lanosco)
		{
			if (id == CREATED)
			{
				if (st.getPlayer().getLevel() < 27)
				{
					htmltext = "fisher_lanosco_q0027_0101.htm";
					st.exitCurrentQuest(true);
				}
				else
				{
					QuestState LanoscosSpecialBait = st.getPlayer().getQuestState(_050_LanoscosSpecialBait.class);
					if (LanoscosSpecialBait != null)
					{
						if (LanoscosSpecialBait.isCompleted())
						{
							htmltext = "fisher_lanosco_q0027_0101.htm";
						}
						else
						{
							htmltext = "fisher_lanosco_q0027_0102.htm";
							st.exitCurrentQuest(true);
						}
					}
					else
					{
						htmltext = "fisher_lanosco_q0027_0103.htm";
						st.exitCurrentQuest(true);
					}
				}
			}
			else if (cond == 1)
			{
				htmltext = "fisher_lanosco_q0027_0105.htm";
				if (st.getQuestItemsCount(BigBlueTreasureChest) == 0)
				{
					htmltext = "fisher_lanosco_q0027_0106.htm";
				}
			}
			else if (cond == 2)
			{
				htmltext = "fisher_lanosco_q0027_0203.htm";
			}
		}
		else if (npcId == Shaling)
		{
			if (cond == 2)
			{
				htmltext = "blueprint_seller_shaling_q0027_0201.htm";
			}
			else
			{
				htmltext = "blueprint_seller_shaling_q0027_0302.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		return null;
	}
}