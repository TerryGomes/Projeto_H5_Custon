package quests;

import org.apache.commons.lang3.ArrayUtils;

import l2f.gameserver.data.xml.holder.EventHolder;
import l2f.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2f.gameserver.listener.event.OnStartStopListener;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.actor.listener.PlayerListenerList;
import l2f.gameserver.model.entity.events.EventType;
import l2f.gameserver.model.entity.events.GlobalEvent;
import l2f.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2f.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;

/**
 * @author pchayka
 */
public abstract class Dominion_ForTheSakeOfTerritory extends Quest
{
	private class OnPlayerEnterListenerImpl implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			DominionSiegeEvent siegeEvent = player.getEvent(DominionSiegeEvent.class);
			if (siegeEvent == null || siegeEvent.getId() != getDominionId())
			{
				return;
			}

			QuestState questState = player.getQuestState(Dominion_ForTheSakeOfTerritory.this.getClass());
			if (player.getLevel() > 61 && questState == null)
			{
				questState = newQuestState(player, Quest.CREATED);
				questState.setState(Quest.STARTED);
				questState.setCond(1);
			}
		}
	}

	public class OnStartStopListenerImpl implements OnStartStopListener
	{
		@Override
		public void onStart(GlobalEvent event)
		{
			PlayerListenerList.addGlobal(_onPlayerEnterListener);
		}

		@Override
		public void onStop(GlobalEvent event)
		{
			PlayerListenerList.removeGlobal(_onPlayerEnterListener);
		}
	}

	private final int[] supplyBoxes =
	{
		36591,
		36592,
		36593,
		36594,
		36595,
		36596,
		36597,
		36598,
		36599
	};
	private final int[] catapultas =
	{
		36499,
		36500,
		36501,
		36502,
		36503,
		36504,
		36505,
		36506,
		36507
	};
	private final int[] militaryUnitLeaders =
	{
		36508,
		36514,
		36520,
		36526,
		36532,
		36538,
		36544,
		36550,
		36556
	};
	private final int[] religionUnitLeaders =
	{
		36510,
		36516,
		36522,
		36528,
		36534,
		36540,
		36546,
		36552,
		36558
	};
	private final int[] economicUnitLeaders =
	{
		36513,
		36519,
		36525,
		36531,
		36537,
		36543,
		36549,
		36555,
		36561
	};

	private OnPlayerEnterListener _onPlayerEnterListener = new OnPlayerEnterListenerImpl();

	public Dominion_ForTheSakeOfTerritory()
	{
		super(PARTY_ALL);
		DominionSiegeEvent siegeEvent = EventHolder.getInstance().getEvent(EventType.SIEGE_EVENT, getDominionId());
		siegeEvent.setForSakeQuest(this);
		siegeEvent.addListener(new OnStartStopListenerImpl());

		DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
		runnerEvent.addBreakQuest(this);

		addKillId(supplyBoxes);
		addKillId(catapultas);
		addKillId(militaryUnitLeaders);
		addKillId(religionUnitLeaders);
		addKillId(economicUnitLeaders);
	}

	public abstract int getDominionId();

	private boolean isValidNpcKill(Player killer, NpcInstance npc)
	{
		DominionSiegeEvent npcSiegeEvent = npc.getEvent(DominionSiegeEvent.class);
		DominionSiegeEvent killerSiegeEvent = killer.getEvent(DominionSiegeEvent.class);

		if (npcSiegeEvent == null || killerSiegeEvent == null || (npcSiegeEvent == killerSiegeEvent))
		{
			return false;
		}
		return true;
	}

	private void handleReward(QuestState st)
	{
		Player player = st.getPlayer();
		if (player == null)
		{
			return;
		}

		DominionSiegeEvent siegeEvent = player.getEvent(DominionSiegeEvent.class);
		if (siegeEvent != null)
		{
			siegeEvent.addReward(player, DominionSiegeEvent.STATIC_BADGES, 10);
		}
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (!isValidNpcKill(st.getPlayer(), npc))
		{
			return null;
		}

		if (st.getCond() == 1)
		{
			if (ArrayUtils.contains(catapultas, npc.getNpcId()))
			{
				st.setCond(2);
			}
			else if (ArrayUtils.contains(supplyBoxes, npc.getNpcId()))
			{
				st.setCond(3);
			}
			else if (ArrayUtils.contains(militaryUnitLeaders, npc.getNpcId()) || ArrayUtils.contains(religionUnitLeaders, npc.getNpcId()) || ArrayUtils.contains(economicUnitLeaders, npc.getNpcId()))
			{
				st.setCond(4);
			}
		}
		else if (st.getCond() == 2)
		{
			if (ArrayUtils.contains(supplyBoxes, npc.getNpcId()))
			{
				st.setCond(5);
			}
			else if (ArrayUtils.contains(militaryUnitLeaders, npc.getNpcId()) || ArrayUtils.contains(religionUnitLeaders, npc.getNpcId()) || ArrayUtils.contains(economicUnitLeaders, npc.getNpcId()))
			{
				st.setCond(6);
			}
		}
		else if (st.getCond() == 3)
		{
			if (ArrayUtils.contains(catapultas, npc.getNpcId()))
			{
				st.setCond(7);
			}
			else if (ArrayUtils.contains(militaryUnitLeaders, npc.getNpcId()) || ArrayUtils.contains(religionUnitLeaders, npc.getNpcId()) || ArrayUtils.contains(economicUnitLeaders, npc.getNpcId()))
			{
				st.setCond(8);
			}
		}
		else if (st.getCond() == 4)
		{
			if (ArrayUtils.contains(catapultas, npc.getNpcId()))
			{
				st.setCond(9);
			}
			else if (ArrayUtils.contains(supplyBoxes, npc.getNpcId()))
			{
				st.setCond(10);
			}
		}
		else if (st.getCond() == 5)
		{
			if (ArrayUtils.contains(militaryUnitLeaders, npc.getNpcId()) || ArrayUtils.contains(religionUnitLeaders, npc.getNpcId()) || ArrayUtils.contains(economicUnitLeaders, npc.getNpcId()))
			{
				st.setCond(11);
				handleReward(st);
			}
		}
		else if (st.getCond() == 6)
		{
			if (ArrayUtils.contains(supplyBoxes, npc.getNpcId()))
			{
				st.setCond(11);
				handleReward(st);
			}
		}
		else if (st.getCond() == 7)
		{
			if (ArrayUtils.contains(militaryUnitLeaders, npc.getNpcId()) || ArrayUtils.contains(religionUnitLeaders, npc.getNpcId()) || ArrayUtils.contains(economicUnitLeaders, npc.getNpcId()))
			{
				st.setCond(11);
				handleReward(st);
			}
		}
		else if (st.getCond() == 8)
		{
			if (ArrayUtils.contains(catapultas, npc.getNpcId()))
			{
				st.setCond(11);
				handleReward(st);
			}
		}
		else if (st.getCond() == 9)
		{
			if (ArrayUtils.contains(supplyBoxes, npc.getNpcId()))
			{
				st.setCond(11);
				handleReward(st);
			}
		}
		else if (st.getCond() == 10)
		{
			if (ArrayUtils.contains(catapultas, npc.getNpcId()))
			{
				st.setCond(11);
				handleReward(st);
			}
		}

		return null;
	}

	@Override
	public boolean canAbortByPacket()
	{
		return false;
	}
}
