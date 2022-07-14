package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _249_PoisonedPlainsOfTheLizardmen extends Quest implements ScriptFile
{
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

	private static final int MOUEN = 30196;
	private static final int JOHNNY = 32744;

	public _249_PoisonedPlainsOfTheLizardmen()
	{
		super(false);

		addStartNpc(MOUEN);
		addTalkId(MOUEN);
		addTalkId(JOHNNY);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if (npc.getNpcId() == MOUEN)
		{
			if (event.equalsIgnoreCase("30196-03.htm"))
			{
				st.setState(STARTED);
				st.setCond(1);
				st.playSound(SOUND_ACCEPT);
			}
		}
		else if (npc.getNpcId() == JOHNNY && event.equalsIgnoreCase("32744-03.htm"))
		{
			st.unset("cond");
			st.giveItems(57, 83056);
			st.addExpAndSp(477496, 58743);
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
		if (npcId == MOUEN)
		{
			switch (st.getState())
			{
			case CREATED:
				if (st.getPlayer().getLevel() >= 82)
				{
					htmltext = "30196-01.htm";
				}
				else
				{
					htmltext = "30196-00.htm";
				}
				break;
			case STARTED:
				if (cond == 1)
				{
					htmltext = "30196-04.htm";
				}
				break;
			case COMPLETED:
				htmltext = "30196-05.htm";
				break;
			}
		}
		else if (npcId == JOHNNY)
		{
			if (cond == 1)
			{
				htmltext = "32744-01.htm";
			}
			else if (st.isCompleted())
			{
				htmltext = "32744-04.htm";
			}
		}
		return htmltext;
	}
}