package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _431_WeddingMarch extends Quest implements ScriptFile
{
	private static int MELODY_MAESTRO_KANTABILON = 31042;
	private static int SILVER_CRYSTAL = 7540;
	private static int WEDDING_ECHO_CRYSTAL = 7062;

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

	public _431_WeddingMarch()
	{
		super(false);

		addStartNpc(MELODY_MAESTRO_KANTABILON);

		addKillId(20786);
		addKillId(20787);

		addQuestItem(SILVER_CRYSTAL);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("quest_accept"))
		{
			htmltext = "muzyk_q0431_0104.htm";
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("431_3"))
		{
			if (st.getQuestItemsCount(SILVER_CRYSTAL) == 50)
			{
				htmltext = "muzyk_q0431_0201.htm";
				st.takeItems(SILVER_CRYSTAL, -1);
				st.giveItems(WEDDING_ECHO_CRYSTAL, 25);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "muzyk_q0431_0202.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int condition = st.getCond();
		int npcId = npc.getNpcId();
		int id = st.getState();
		if (npcId == MELODY_MAESTRO_KANTABILON)
		{
			if (id != STARTED)
			{
				if (st.getPlayer().getLevel() < 38)
				{
					htmltext = "muzyk_q0431_0103.htm";
					st.exitCurrentQuest(true);
				}
				else
				{
					htmltext = "muzyk_q0431_0101.htm";
				}
			}
			else if (condition == 1)
			{
				htmltext = "muzyk_q0431_0106.htm";
			}
			else if (condition == 2 && st.getQuestItemsCount(SILVER_CRYSTAL) == 50)
			{
				htmltext = "muzyk_q0431_0105.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getState() != STARTED)
		{
			return null;
		}
		int npcId = npc.getNpcId();

		if (npcId == 20786 || npcId == 20787)
		{
			if (st.getCond() == 1 && st.getQuestItemsCount(SILVER_CRYSTAL) < 50)
			{
				st.giveItems(SILVER_CRYSTAL, 1);

				if (st.getQuestItemsCount(SILVER_CRYSTAL) == 50)
				{
					st.playSound(SOUND_MIDDLE);
					st.setCond(2);
				}
				else
				{
					st.playSound(SOUND_ITEMGET);
				}
			}
		}
		return null;
	}
}