package quests;

import l2mv.gameserver.data.xml.holder.EventHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.events.EventType;
import l2mv.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2mv.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * @author pchayka
 */
public class _176_StepsForHonor extends Quest implements ScriptFile
{
	private static int RAPIDUS = 36479;

	public _176_StepsForHonor()
	{
		super(PARTY_ALL);
		addStartNpc(RAPIDUS);
	}

	@Override
	public String onKill(Player killed, QuestState st)
	{
		int cond = st.getCond();
		if (!isValidKill(killed, st.getPlayer()))
		{
			return null;
		}
		if (cond == 1 || cond == 3 || cond == 5 || cond == 7)
		{
			st.set("kill", st.getInt("kill") + 1);
			if (st.getInt("kill") >= calculatePlayersToKill(cond))
			{
				st.setCond(cond + 1);
			}
		}
		return null;
	}

	private static int calculatePlayersToKill(int cond)
	{
		switch (cond)
		{
		case 1:
			return 9;
		case 3:
			return 9 + 18;
		case 5:
			return 9 + 18 + 27;
		case 7:
			return 9 + 18 + 27 + 36;
		default:
			return 0;
		}
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("rapidus_q176_04.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
		if (runnerEvent.isInProgress())
		{
			htmltext = "rapidus_q176_05.htm";
		}
		else
		{
			switch (cond)
			{
			default:
				if (st.getPlayer().getLevel() >= 80)
				{
					htmltext = "rapidus_q176_03.htm";
				}
				else
				{
					htmltext = "rapidus_q176_02.htm";
					st.exitCurrentQuest(true);
				}
				break;
			case 1:
				htmltext = "rapidus_q176_06.htm";
				break;
			case 2:
				htmltext = "rapidus_q176_07.htm";
				st.setCond(3);
				st.playSound(SOUND_MIDDLE);
				break;
			case 3:
				htmltext = "rapidus_q176_08.htm";
				break;
			case 4:
				htmltext = "rapidus_q176_09.htm";
				st.setCond(5);
				st.playSound(SOUND_MIDDLE);
				break;
			case 5:
				htmltext = "rapidus_q176_10.htm";
				break;
			case 6:
				htmltext = "rapidus_q176_11.htm";
				st.setCond(7);
				st.playSound(SOUND_MIDDLE);
				break;
			case 7:
				htmltext = "rapidus_q176_12.htm";
				break;
			case 8:
				htmltext = "rapidus_q176_13.htm";
				st.giveItems(14603, 1);
				st.exitCurrentQuest(false);
				st.playSound(SOUND_FINISH);
				break;
			}
		}
		return htmltext;
	}

	private boolean isValidKill(Player killed, Player killer)
	{
		DominionSiegeEvent killedSiegeEvent = killed.getEvent(DominionSiegeEvent.class);
		DominionSiegeEvent killerSiegeEvent = killer.getEvent(DominionSiegeEvent.class);

		if (killedSiegeEvent == null || killerSiegeEvent == null || (killedSiegeEvent == killerSiegeEvent) || (killed.getLevel() < 61))
		{
			return false;
		}
		return true;
	}

	@Override
	public void onCreate(QuestState qs)
	{
		super.onCreate(qs);
		qs.addPlayerOnKillListener();
	}

	@Override
	public void onAbort(QuestState qs)
	{
		qs.removePlayerOnKillListener();
		super.onAbort(qs);
	}

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

}