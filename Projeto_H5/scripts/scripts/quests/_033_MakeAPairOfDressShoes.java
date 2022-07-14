package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _033_MakeAPairOfDressShoes extends Quest implements ScriptFile
{
	int LEATHER = 1882;
	int THREAD = 1868;
	int DRESS_SHOES_BOX = 7113;

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

	public _033_MakeAPairOfDressShoes()
	{
		super(false);

		addStartNpc(30838);
		addTalkId(30838);
		addTalkId(30838);
		addTalkId(30164);
		addTalkId(31520);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("30838-1.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("31520-1.htm"))
		{
			st.setCond(2);
		}
		else if (event.equals("30838-3.htm"))
		{
			st.setCond(3);
		}
		else if (event.equals("30838-5.htm"))
		{
			if (st.getQuestItemsCount(LEATHER) >= 200 && st.getQuestItemsCount(THREAD) >= 600 && st.getQuestItemsCount(ADENA_ID) >= 200000)
			{
				st.takeItems(LEATHER, 200);
				st.takeItems(THREAD, 600);
				st.takeItems(ADENA_ID, 200000);
				st.setCond(4);
			}
			else
			{
				htmltext = "You don't have enough materials";
			}
		}
		else if (event.equals("30164-1.htm"))
		{
			if (st.getQuestItemsCount(ADENA_ID) >= 300000)
			{
				st.takeItems(ADENA_ID, 300000);
				st.setCond(5);
			}
			else
			{
				htmltext = "30164-havent.htm";
			}
		}
		else if (event.equals("30838-7.htm"))
		{
			st.giveItems(DRESS_SHOES_BOX, 1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
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
		case 30838:
			if (cond == 0 && st.getQuestItemsCount(DRESS_SHOES_BOX) == 0)
			{
				if (st.getPlayer().getLevel() >= 60)
				{
					QuestState fwear = st.getPlayer().getQuestState(_037_PleaseMakeMeFormalWear.class);
					if (fwear != null && fwear.getCond() == 7)
					{
						htmltext = "30838-0.htm";
					}
					else
					{
						st.exitCurrentQuest(true);
					}
				}
				else
				{
					htmltext = "30838-00.htm";
				}
			}
			else if (cond == 1)
			{
				htmltext = "30838-1.htm";
			}
			else if (cond == 2)
			{
				htmltext = "30838-2.htm";
			}
			else if (cond == 3 && st.getQuestItemsCount(LEATHER) >= 200 && st.getQuestItemsCount(THREAD) >= 600 && st.getQuestItemsCount(ADENA_ID) >= 200000)
			{
				htmltext = "30838-4.htm";
			}
			else if (cond == 3 && (st.getQuestItemsCount(LEATHER) < 200 || st.getQuestItemsCount(THREAD) < 600 || st.getQuestItemsCount(ADENA_ID) < 200000))
			{
				htmltext = "30838-4r.htm";
			}
			else if (cond == 4)
			{
				htmltext = "30838-5r.htm";
			}
			else if (cond == 5)
			{
				htmltext = "30838-6.htm";
			}
			break;
		case 31520:
			if (cond == 1)
			{
				htmltext = "31520-0.htm";
			}
			else if (cond == 2)
			{
				htmltext = "31520-1r.htm";
			}
			break;
		case 30164:
			if (cond == 4)
			{
				htmltext = "30164-0.htm";
			}
			else if (cond == 5)
			{
				htmltext = "30164-2.htm";
			}
			break;
		default:
			break;
		}
		return htmltext;
	}
}