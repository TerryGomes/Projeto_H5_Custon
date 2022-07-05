package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.base.Race;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _165_ShilensHunt extends Quest implements ScriptFile
{
	private static final int DARK_BEZOAR = 1160;
	private static final int LESSER_HEALING_POTION = 1060;

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

	public _165_ShilensHunt()
	{
		super(false);

		addStartNpc(30348);

		addTalkId(30348);

		addKillId(20456);
		addKillId(20529);
		addKillId(20532);
		addKillId(20536);

		addQuestItem(DARK_BEZOAR);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("1"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			htmltext = "30348-03.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();

		if (cond == 0)
		{
			if (st.getPlayer().getRace() != Race.darkelf)
			{
				htmltext = "30348-00.htm";
			}
			else if (st.getPlayer().getLevel() >= 3)
			{
				htmltext = "30348-02.htm";
				return htmltext;
			}
			else
			{
				htmltext = "30348-01.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if (cond == 1 || st.getQuestItemsCount(DARK_BEZOAR) < 13)
		{
			htmltext = "30348-04.htm";
		}
		else if (cond == 2)
		{
			htmltext = "30348-05.htm";
			st.takeItems(DARK_BEZOAR, -1);
			st.giveItems(LESSER_HEALING_POTION, 5);
			st.addExpAndSp(1000, 0);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if (cond == 1 && st.getQuestItemsCount(DARK_BEZOAR) < 13 && Rnd.chance(90))
		{
			st.giveItems(DARK_BEZOAR, 1);
			if (st.getQuestItemsCount(DARK_BEZOAR) == 13)
			{
				st.setCond(2);
				st.playSound(SOUND_MIDDLE);
			}
			else
			{
				st.playSound(SOUND_ITEMGET);
			}
		}
		return null;
	}
}