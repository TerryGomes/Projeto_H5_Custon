package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _614_SlayTheEnemyCommander extends Quest implements ScriptFile
{
	// NPC
	private static final int DURAI = 31377;
	private static final int KETRAS_COMMANDER_TAYR = 25302;

	// etc
	@SuppressWarnings("unused")
	private static final int MARK_OF_VARKA_ALLIANCE1 = 7221;
	@SuppressWarnings("unused")
	private static final int MARK_OF_VARKA_ALLIANCE2 = 7222;
	@SuppressWarnings("unused")
	private static final int MARK_OF_VARKA_ALLIANCE3 = 7223;
	private static final int MARK_OF_VARKA_ALLIANCE4 = 7224;
	private static final int MARK_OF_VARKA_ALLIANCE5 = 7225;
	private static final int HEAD_OF_TAYR = 7241;
	private static final int FEATHER_OF_WISDOM = 7230;

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

	public _614_SlayTheEnemyCommander()
	{
		super(true);
		addStartNpc(DURAI);
		addKillId(KETRAS_COMMANDER_TAYR);
		addQuestItem(HEAD_OF_TAYR);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("quest_accept"))
		{
			htmltext = "elder_ashas_barka_durai_q0614_0104.htm";
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("614_3"))
		{
			if (st.getQuestItemsCount(HEAD_OF_TAYR) >= 1)
			{
				htmltext = "elder_ashas_barka_durai_q0614_0201.htm";
				st.takeItems(HEAD_OF_TAYR, -1);
				st.giveItems(FEATHER_OF_WISDOM, 1);
				st.addExpAndSp(0, 10000);
				st.unset("cond");
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "elder_ashas_barka_durai_q0614_0106.htm";
			}
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
			if (st.getPlayer().getLevel() >= 75)
			{
				if (st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE4) == 1 || st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE5) == 1)
				{
					htmltext = "elder_ashas_barka_durai_q0614_0101.htm";
				}
				else
				{
					htmltext = "elder_ashas_barka_durai_q0614_0102.htm";
					st.exitCurrentQuest(true);
				}
			}
			else
			{
				htmltext = "elder_ashas_barka_durai_q0614_0103.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if (cond == 1 && st.getQuestItemsCount(HEAD_OF_TAYR) == 0)
		{
			htmltext = "elder_ashas_barka_durai_q0614_0106.htm";
		}
		else if (cond == 2 && st.getQuestItemsCount(HEAD_OF_TAYR) >= 1)
		{
			htmltext = "elder_ashas_barka_durai_q0614_0105.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getCond() == 1)
		{
			st.giveItems(HEAD_OF_TAYR, 1);
			st.setCond(2);
			st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}