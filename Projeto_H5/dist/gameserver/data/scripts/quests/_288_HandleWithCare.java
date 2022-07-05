package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

/**
 * @author pchayka
 */

public class _288_HandleWithCare extends Quest implements ScriptFile
{
	private static final int Ankumi = 32741;
	private static final int MiddleGradeLizardScale = 15498;
	private static final int HighestGradeLizardScale = 15497;

	public _288_HandleWithCare()
	{
		super(true);
		addStartNpc(Ankumi);
		addQuestItem(MiddleGradeLizardScale, HighestGradeLizardScale);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("ankumi_q288_03.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("request_reward"))
		{
			if (st.getCond() == 2 && st.getQuestItemsCount(MiddleGradeLizardScale) >= 1)
			{
				st.takeAllItems(MiddleGradeLizardScale);
				switch (Rnd.get(1, 6))
				{
				case 1:
					st.giveItems(959, 1);
					break;
				case 2:
					st.giveItems(960, 1);
					break;
				case 3:
					st.giveItems(960, 2);
					break;
				case 4:
					st.giveItems(960, 3);
					break;
				case 5:
					st.giveItems(9557, 1);
					break;
				case 6:
					st.giveItems(9557, 2);
					break;
				}
				htmltext = "ankumi_q288_06.htm";
				st.exitCurrentQuest(true);
			}
			else if (st.getCond() == 3 && st.getQuestItemsCount(HighestGradeLizardScale) >= 1)
			{
				st.takeAllItems(HighestGradeLizardScale);
				switch (Rnd.get(1, 4))
				{
				case 1:
					st.giveItems(959, 1);
					st.giveItems(9557, 1);
					break;
				case 2:
					st.giveItems(960, 1);
					st.giveItems(9557, 1);
					break;
				case 3:
					st.giveItems(960, 2);
					st.giveItems(9557, 1);
					break;
				case 4:
					st.giveItems(960, 3);
					st.giveItems(9557, 1);
					break;
				}
				htmltext = "ankumi_q288_06.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "ankumi_q288_07.htm";
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
		if (npcId == Ankumi)
		{
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getLevel() >= 82)
				{
					htmltext = "ankumi_q288_01.htm";
				}
				else
				{
					htmltext = "ankumi_q288_00.htm";
					st.exitCurrentQuest(true);
				}
				break;
			case 1:
				htmltext = "ankumi_q288_04.htm";
				break;
			case 2:
			case 3:
				htmltext = "ankumi_q288_05.htm";
				break;
			default:
				break;
			}
		}
		return htmltext;
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