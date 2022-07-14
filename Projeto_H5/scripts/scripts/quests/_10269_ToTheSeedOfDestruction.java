package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _10269_ToTheSeedOfDestruction extends Quest implements ScriptFile
{
	private final static int Keucereus = 32548;
	private final static int Allenos = 32526;

	private final static int Introduction = 13812;

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

	public _10269_ToTheSeedOfDestruction()
	{
		super(false);

		addStartNpc(Keucereus);

		addTalkId(Allenos);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equalsIgnoreCase("32548-05.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(Introduction, 1);
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
			if (npcId == Allenos)
			{
				htmltext = "32526-02.htm";
			}
			else
			{
				htmltext = "32548-0a.htm";
			}
		}
		else if (id == CREATED && npcId == Keucereus)
		{
			if (st.getPlayer().getLevel() < 75)
			{
				htmltext = "32548-00.htm";
			}
			else
			{
				htmltext = "32548-01.htm";
			}
		}
		else if (id == STARTED && npcId == Keucereus)
		{
			htmltext = "32548-06.htm";
		}
		else if (id == STARTED && npcId == Allenos)
		{
			htmltext = "32526-01.htm";
			st.giveItems(ADENA_ID, 29174);
			st.addExpAndSp(176121, 17671);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		return htmltext;
	}
}