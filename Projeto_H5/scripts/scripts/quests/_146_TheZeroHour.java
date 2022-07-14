package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * @author: n0nam3
 * @date: 30.05.2010
 */
public class _146_TheZeroHour extends Quest implements ScriptFile
{
	// NPC's
	private static int KAHMAN = 31554;
	// ITEMS
	private static int STAKATO_QUEENS_FANG = 14859;
	private static int KAHMANS_SUPPLY_BOX = 14849;
	// MOB's
	private static int QUEEN_SHYEED_ID = 25671;

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

	public _146_TheZeroHour()
	{
		super(true);

		addStartNpc(KAHMAN);
		addTalkId(KAHMAN);
		addKillId(QUEEN_SHYEED_ID);
		addQuestItem(STAKATO_QUEENS_FANG);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int cond = st.getCond();
		String htmltext = event;

		if (event.equals("merc_kahmun_q0146_0103.htm") && cond == 0)
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}

		if (event.equals("reward") && cond == 2)
		{
			htmltext = "merc_kahmun_q0146_0107.htm";
			st.takeItems(STAKATO_QUEENS_FANG, -1);
			st.giveItems(KAHMANS_SUPPLY_BOX, 1);
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

		QuestState InSearchOfTheNest = st.getPlayer().getQuestState(_109_InSearchOfTheNest.class);
		if (npcId == KAHMAN)
		{
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() >= 81)
				{
					if (InSearchOfTheNest != null && InSearchOfTheNest.isCompleted())
					{
						htmltext = "merc_kahmun_q0146_0101.htm";
					}
					else
					{
						htmltext = "merc_kahmun_q0146_0104.htm";
					}
				}
				else
				{
					htmltext = "merc_kahmun_q0146_0102.htm";
				}
			}
			else if (cond == 1 && st.getQuestItemsCount(STAKATO_QUEENS_FANG) < 1)
			{
				htmltext = "merc_kahmun_q0146_0105.htm";
			}
			else if (cond == 2)
			{
				htmltext = "merc_kahmun_q0146_0106.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getState() == STARTED)
		{
			st.setCond(2);
			st.giveItems(STAKATO_QUEENS_FANG, 1);
		}
		return null;
	}
}