package quests;

import l2f.gameserver.model.base.Race;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _009_IntoTheCityOfHumans extends Quest implements ScriptFile
{
	// NPC
	public final int PETUKAI = 30583;
	public final int TANAPI = 30571;
	public final int TAMIL = 30576;
	// Items
	public final int SCROLL_OF_ESCAPE_GIRAN = 7126;
	// Quest Item
	public final int MARK_OF_TRAVELER = 7570;

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

	public _009_IntoTheCityOfHumans()
	{
		super(false);

		addStartNpc(PETUKAI);

		addTalkId(PETUKAI);
		addTalkId(TANAPI);
		addTalkId(TAMIL);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("centurion_petukai_q0009_0104.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("seer_tanapi_q0009_0201.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("gatekeeper_tamil_q0009_0301.htm"))
		{
			st.giveItems(SCROLL_OF_ESCAPE_GIRAN, 1);
			st.giveItems(MARK_OF_TRAVELER, 1);
			st.unset("cond");
			st.playSound(SOUND_FINISH);
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
		switch (npcId)
		{
		case PETUKAI:
			if (cond == 0)
			{
				if (st.getPlayer().getRace() == Race.orc && st.getPlayer().getLevel() >= 3)
				{
					htmltext = "centurion_petukai_q0009_0101.htm";
				}
				else
				{
					htmltext = "centurion_petukai_q0009_0102.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 1)
			{
				htmltext = "centurion_petukai_q0009_0105.htm";
			}
			break;
		case TANAPI:
			if (cond == 1)
			{
				htmltext = "seer_tanapi_q0009_0101.htm";
			}
			else if (cond == 2)
			{
				htmltext = "seer_tanapi_q0009_0202.htm";
			}
			break;
		case TAMIL:
			if (cond == 2)
			{
				htmltext = "gatekeeper_tamil_q0009_0201.htm";
			}
			break;
		default:
			break;
		}
		return htmltext;
	}
}