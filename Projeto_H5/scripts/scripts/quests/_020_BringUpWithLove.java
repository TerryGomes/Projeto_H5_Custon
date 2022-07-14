package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _020_BringUpWithLove extends Quest implements ScriptFile
{
	private static final int TUNATUN = 31537;
	// Item
	private static final int BEAST_WHIP = 15473;
	private static final int CRYSTAL = 9553;
	private static final int JEWEL = 7185;

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

	public _020_BringUpWithLove()
	{
		super(false);

		addStartNpc(TUNATUN);
		addTalkId(TUNATUN);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (npc.getNpcId() == TUNATUN)
		{
			if (event.equalsIgnoreCase("31537-12.htm"))
			{
				st.setState(STARTED);
				st.setCond(1);
				st.playSound(SOUND_ACCEPT);
			}
			else if (event.equalsIgnoreCase("31537-03.htm"))
			{
				if (st.getQuestItemsCount(BEAST_WHIP) > 0)
				{
					return "31537-03a.htm";
				}
				else
				{
					st.giveItems(BEAST_WHIP, 1);
				}
			}
			else if (event.equalsIgnoreCase("31537-15.htm"))
			{
				st.unset("cond");
				st.takeItems(JEWEL, -1);
				st.giveItems(CRYSTAL, 1);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
			}
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmtext = "noquest";
		if (npc.getNpcId() == TUNATUN)
		{
			switch (st.getState())
			{
			case CREATED:
				if (st.getPlayer().getLevel() >= 82)
				{
					htmtext = "31537-01.htm";
				}
				else
				{
					htmtext = "31537-00.htm";
				}
				break;
			case STARTED:
				if (st.getCond() == 1)
				{
					htmtext = "31537-13.htm";
				}
				else if (st.getCond() == 2)
				{
					htmtext = "31537-14.htm";
				}
				break;
			}
		}
		return htmtext;
	}
}