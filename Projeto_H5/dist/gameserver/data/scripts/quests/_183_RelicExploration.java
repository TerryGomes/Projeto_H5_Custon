package quests;

import l2mv.gameserver.instancemanager.QuestManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _183_RelicExploration extends Quest implements ScriptFile
{
	private static final int Kusto = 30512;
	private static final int Lorain = 30673;
	private static final int Nikola = 30621;

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

	public _183_RelicExploration()
	{
		super(false);

		addStartNpc(Kusto);
		addStartNpc(Nikola);
		addTalkId(Lorain);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();
		if (event.equalsIgnoreCase("30512-03.htm"))
		{
			st.playSound(SOUND_ACCEPT);
			st.setCond(1);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30673-04.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("Contract"))
		{
			Quest q1 = QuestManager.getQuest(_184_NikolasCooperationContract.class);
			if (q1 != null)
			{
				st.giveItems(ADENA_ID, 18100);
				st.addExpAndSp(60000, 3000);
				QuestState qs1 = q1.newQuestState(player, STARTED);
				q1.notifyEvent("30621-01.htm", qs1, npc);
				st.playSound(SOUND_MIDDLE);
				st.exitCurrentQuest(false);
			}
			return null;
		}
		else if (event.equalsIgnoreCase("Consideration"))
		{
			Quest q2 = QuestManager.getQuest(_185_NikolasCooperationConsideration.class);
			if (q2 != null)
			{
				st.giveItems(ADENA_ID, 18100);
				QuestState qs2 = q2.newQuestState(st.getPlayer(), STARTED);
				q2.notifyEvent("30621-01.htm", qs2, npc);
				st.playSound(SOUND_MIDDLE);
				st.exitCurrentQuest(false);
			}
			return null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == Kusto)
		{
			if (st.getState() == CREATED)
			{
				if (st.getPlayer().getLevel() < 40)
				{
					htmltext = "30512-00.htm";
				}
				else
				{
					htmltext = "30512-01.htm";
				}
			}
			else
			{
				htmltext = "30512-04.htm";
			}
		}
		else if (npcId == Lorain)
		{
			if (cond == 1)
			{
				htmltext = "30673-01.htm";
			}
			else
			{
				htmltext = "30673-05.htm";
			}
		}
		else if (npcId == Nikola && cond == 2)
		{
			htmltext = "30621-01.htm";
		}
		return htmltext;
	}
}