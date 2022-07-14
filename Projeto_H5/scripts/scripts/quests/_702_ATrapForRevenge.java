package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * @author: pchayka
 * @date: 08.06.2010
 */
public class _702_ATrapForRevenge extends Quest implements ScriptFile
{
	// NPC's
	private static int PLENOS = 32563;
	private static int TENIUS = 32555;
	// ITEMS
	private static int DRAKES_FLESH = 13877;
	private static int LEONARD = 9628;
	private static int ADAMANTINE = 9629;
	private static int ORICHALCUM = 9630;
	// MOB's
	private static int DRAK = 22612;
	private static int MUTATED_DRAKE_WING = 22611;

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

	public _702_ATrapForRevenge()
	{
		super(true);

		addStartNpc(PLENOS);
		addTalkId(PLENOS);
		addTalkId(TENIUS);
		addKillId(DRAK);
		addKillId(MUTATED_DRAKE_WING);
		addQuestItem(DRAKES_FLESH);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int cond = st.getCond();
		String htmltext = event;

		if (event.equals("take") && cond == 0)
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			htmltext = "plenos_q702_2.htm";
		}
		else if (event.equals("took_mission") && cond == 1)
		{
			st.setCond(2);
			htmltext = "tenius_q702_3.htm";
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equals("hand_over") && cond == 2)
		{
			int rand = Rnd.get(1, 3);
			htmltext = "tenius_q702_6.htm";
			st.takeItems(DRAKES_FLESH, -1);
			switch (rand)
			{
			case 1:
				st.giveItems(LEONARD, 3);
				break;
			case 2:
				st.giveItems(ADAMANTINE, 3);
				break;
			case 3:
				st.giveItems(ORICHALCUM, 3);
				break;
			default:
				break;
			}

			st.giveItems(ADENA_ID, 157200);
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

		QuestState GoodDayToFly = st.getPlayer().getQuestState(_10273_GoodDayToFly.class);
		if (npcId == PLENOS)
		{
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() >= 78)
				{
					if (GoodDayToFly != null && GoodDayToFly.isCompleted())
					{
						htmltext = "plenos_q702_1.htm";
					}
					else
					{
						htmltext = "plenos_q702_1a.htm";
					}
				}
				else
				{
					htmltext = "plenos_q702_1b.htm";
					st.exitCurrentQuest(true);
				}
			}
			else
			{
				htmltext = "plenos_q702_1c.htm";
			}

		}
		else if (npcId == TENIUS)
		{
			if (cond == 1)
			{
				htmltext = "tenius_q702_1.htm";
			}
			else if (cond == 2 && st.getQuestItemsCount(DRAKES_FLESH) < 100)
			{
				htmltext = "tenius_q702_4.htm";
			}
			else if (cond == 2 && st.getQuestItemsCount(DRAKES_FLESH) >= 100)
			{
				htmltext = "tenius_q702_5.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (cond == 2 && (npcId == DRAK || npcId == MUTATED_DRAKE_WING) && st.getQuestItemsCount(DRAKES_FLESH) <= 100)
		{
			st.giveItems(DRAKES_FLESH, 1);
			st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}