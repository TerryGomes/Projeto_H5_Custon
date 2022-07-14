package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _10282_ToTheSeedOfAnnihilation extends Quest implements ScriptFile
{
	private final static int KBALDIR = 32733;
	private final static int KLEMIS = 32734;

	private final static int SOA_ORDERS = 15512;

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

	public _10282_ToTheSeedOfAnnihilation()
	{
		super(false);

		addStartNpc(KBALDIR);
		addTalkId(KBALDIR);
		addTalkId(KLEMIS);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equalsIgnoreCase("32733-07.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.giveItems(SOA_ORDERS, 1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("32734-02.htm"))
		{
			st.unset("cond");
			st.addExpAndSp(1148480, 99110);
			st.takeItems(SOA_ORDERS, -1);
			st.exitCurrentQuest(false);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int id = st.getState();
		int npcId = npc.getNpcId();
		if (id == COMPLETED)
		{
			if (npcId == KBALDIR)
			{
				htmltext = "32733-09.htm";
			}
			else if (npcId == KLEMIS)
			{
				htmltext = "32734-03.htm";
			}
		}
		else if (id == CREATED)
		{
			if (st.getPlayer().getLevel() >= 84)
			{
				htmltext = "32733-01.htm";
			}
			else
			{
				htmltext = "32733-00.htm";
			}
		}
		else if (st.getCond() == 1)
		{
			if (npcId == KBALDIR)
			{
				htmltext = "32733-08.htm";
			}
			else if (npcId == KLEMIS)
			{
				htmltext = "32734-01.htm";
			}
		}
		return htmltext;
	}
}