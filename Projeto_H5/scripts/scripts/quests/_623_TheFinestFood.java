package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _623_TheFinestFood extends Quest implements ScriptFile
{
	public final int JEREMY = 31521;

	public static final int HOT_SPRINGS_BUFFALO = 21315;
	public static final int HOT_SPRINGS_FLAVA = 21316;
	public static final int HOT_SPRINGS_ANTELOPE = 21318;

	public static final int LEAF_OF_FLAVA = 7199;
	public static final int BUFFALO_MEAT = 7200;
	public static final int ANTELOPE_HORN = 7201;

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

	public _623_TheFinestFood()
	{
		super(true);

		addStartNpc(JEREMY);

		addTalkId(JEREMY);

		addKillId(HOT_SPRINGS_BUFFALO);
		addKillId(HOT_SPRINGS_FLAVA);
		addKillId(HOT_SPRINGS_ANTELOPE);

		addQuestItem(BUFFALO_MEAT);
		addQuestItem(LEAF_OF_FLAVA);
		addQuestItem(ANTELOPE_HORN);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("quest_accept"))
		{
			htmltext = "jeremy_q0623_0104.htm";
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("623_3"))
		{
			htmltext = "jeremy_q0623_0201.htm";
			st.takeItems(LEAF_OF_FLAVA, -1);
			st.takeItems(BUFFALO_MEAT, -1);
			st.takeItems(ANTELOPE_HORN, -1);
			st.giveItems(ADENA_ID, 73000);
			st.addExpAndSp(230000, 18250);
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
		int id = st.getState();
		if (id == CREATED)
		{
			st.setCond(0);
		}
		// На случай любых ошибок, если предметы есть - квест все равно пройдется.
		if (summ(st) >= 300)
		{
			st.setCond(2);
		}
		int cond = st.getCond();
		if (npcId == JEREMY)
		{
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() >= 71)
				{
					htmltext = "jeremy_q0623_0101.htm";
				}
				else
				{
					htmltext = "jeremy_q0623_0103.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 1 && summ(st) < 300)
			{
				htmltext = "jeremy_q0623_0106.htm";
			}
			else if (cond == 2 && summ(st) >= 300)
			{
				htmltext = "jeremy_q0623_0105.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		if (cond == 1) // Like off C4 PTS AI (убрали && Rnd.chance(50))
		{
			switch (npcId)
			{
			case HOT_SPRINGS_BUFFALO:
				if (st.getQuestItemsCount(BUFFALO_MEAT) < 100)
				{
					st.giveItems(BUFFALO_MEAT, 1);
					if (st.getQuestItemsCount(BUFFALO_MEAT) == 100)
					{
						if (summ(st) >= 300)
						{
							st.setCond(2);
						}
						st.playSound(SOUND_MIDDLE);
					}
					else
					{
						st.playSound(SOUND_ITEMGET);
					}
				}
				break;
			case HOT_SPRINGS_FLAVA:
				if (st.getQuestItemsCount(LEAF_OF_FLAVA) < 100)
				{
					st.giveItems(LEAF_OF_FLAVA, 1);
					if (st.getQuestItemsCount(LEAF_OF_FLAVA) == 100)
					{
						if (summ(st) >= 300)
						{
							st.setCond(2);
						}
						st.playSound(SOUND_MIDDLE);
					}
					else
					{
						st.playSound(SOUND_ITEMGET);
					}
				}
				break;
			case HOT_SPRINGS_ANTELOPE:
				if (st.getQuestItemsCount(ANTELOPE_HORN) < 100)
				{
					st.giveItems(ANTELOPE_HORN, 1);
					if (st.getQuestItemsCount(ANTELOPE_HORN) == 100)
					{
						if (summ(st) >= 300)
						{
							st.setCond(2);
						}
						st.playSound(SOUND_MIDDLE);
					}
					else
					{
						st.playSound(SOUND_ITEMGET);
					}
				}
				break;
			default:
				break;
			}
		}
		return null;
	}

	private long summ(QuestState st)
	{
		return st.getQuestItemsCount(LEAF_OF_FLAVA) + st.getQuestItemsCount(BUFFALO_MEAT) + st.getQuestItemsCount(ANTELOPE_HORN);
	}
}