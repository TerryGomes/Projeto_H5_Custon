package ai.residences.dominion;

import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

import ai.residences.SiegeGuardFighter;
import l2f.gameserver.Config;
import l2f.gameserver.instancemanager.QuestManager;
import l2f.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.actor.listener.PlayerListenerList;
import l2f.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.network.serverpackets.ExShowScreenMessage;
import l2f.gameserver.network.serverpackets.components.NpcString;
import quests._731_ProtectTheMilitaryAssociationLeader;

/**
 * @author VISTALL
 * @date 4:32/23.06.2011
 */
public class MilitaryAssociationLeader extends SiegeGuardFighter
{
	private static final IntObjectMap<NpcString[]> MESSAGES = new HashIntObjectMap<NpcString[]>(9);

	static
	{
		MESSAGES.put(81, new NpcString[]
		{
			NpcString.PROTECT_THE_MILITARY_ASSOCIATION_LEADER_OF_GLUDIO,
			NpcString.THE_MILITARY_ASSOCIATION_LEADER_OF_GLUDIO_IS_DEAD
		});
		MESSAGES.put(82, new NpcString[]
		{
			NpcString.PROTECT_THE_MILITARY_ASSOCIATION_LEADER_OF_DION,
			NpcString.THE_MILITARY_ASSOCIATION_LEADER_OF_DION_IS_DEAD
		});
		MESSAGES.put(83, new NpcString[]
		{
			NpcString.PROTECT_THE_MILITARY_ASSOCIATION_LEADER_OF_GIRAN,
			NpcString.THE_MILITARY_ASSOCIATION_LEADER_OF_GIRAN_IS_DEAD
		});
		MESSAGES.put(84, new NpcString[]
		{
			NpcString.PROTECT_THE_MILITARY_ASSOCIATION_LEADER_OF_OREN,
			NpcString.THE_MILITARY_ASSOCIATION_LEADER_OF_OREN_IS_DEAD
		});
		MESSAGES.put(85, new NpcString[]
		{
			NpcString.PROTECT_THE_MILITARY_ASSOCIATION_LEADER_OF_ADEN,
			NpcString.THE_MILITARY_ASSOCIATION_LEADER_OF_ADEN_IS_DEAD
		});
		MESSAGES.put(86, new NpcString[]
		{
			NpcString.PROTECT_THE_MILITARY_ASSOCIATION_LEADER_OF_INNADRIL,
			NpcString.THE_MILITARY_ASSOCIATION_LEADER_OF_INNADRIL_IS_DEAD
		});
		MESSAGES.put(87, new NpcString[]
		{
			NpcString.PROTECT_THE_MILITARY_ASSOCIATION_LEADER_OF_GODDARD,
			NpcString.THE_MILITARY_ASSOCIATION_LEADER_OF_GODDARD_IS_DEAD
		});
		MESSAGES.put(88, new NpcString[]
		{
			NpcString.PROTECT_THE_MILITARY_ASSOCIATION_LEADER_OF_RUNE,
			NpcString.THE_MILITARY_ASSOCIATION_LEADER_OF_RUNE_IS_DEAD
		});
		MESSAGES.put(89, new NpcString[]
		{
			NpcString.PROTECT_THE_MILITARY_ASSOCIATION_LEADER_OF_SCHUTTGART,
			NpcString.THE_MILITARY_ASSOCIATION_LEADER_OF_SCHUTTGART_IS_DEAD
		});
	}

	private class OnPlayerEnterListenerImpl implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			NpcInstance actor = getActor();
			DominionSiegeEvent siegeEvent = actor.getEvent(DominionSiegeEvent.class);
			if ((siegeEvent == null) || (player.getEvent(DominionSiegeEvent.class) != siegeEvent))
			{
				return;
			}

			Quest q = QuestManager.getQuest(_731_ProtectTheMilitaryAssociationLeader.class);

			QuestState questState = q.newQuestStateAndNotSave(player, Quest.CREATED);
			questState.setCond(1, false);
			questState.setStateAndNotSave(Quest.STARTED);
		}
	}

	private final OnPlayerEnterListener _listener = new OnPlayerEnterListenerImpl();

	public MilitaryAssociationLeader(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public void onEvtAttacked(Creature attacker, int dam)
	{
		super.onEvtAttacked(attacker, dam);

		NpcInstance actor = getActor();

		DominionSiegeEvent siegeEvent = actor.getEvent(DominionSiegeEvent.class);
		if (siegeEvent == null)
		{
			return;
		}

		boolean first = actor.getParameter("dominion_first_attack", true);
		if (first)
		{
			actor.setParameter("dominion_first_attack", false);
			NpcString msg = MESSAGES.get(siegeEvent.getId())[0];
			Quest q = QuestManager.getQuest(_731_ProtectTheMilitaryAssociationLeader.class);
			for (Player player : GameObjectsStorage.getAllPlayersForIterate())
			{
				if (player.getEvent(DominionSiegeEvent.class) == siegeEvent)
				{
					player.sendPacket(new ExShowScreenMessage(msg, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));

					QuestState questState = q.newQuestStateAndNotSave(player, Quest.CREATED);
					questState.setCond(1, false);
					questState.setStateAndNotSave(Quest.STARTED);
				}
			}
			PlayerListenerList.addGlobal(_listener);
		}
	}

	@Override
	public void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);

		NpcInstance actor = getActor();

		DominionSiegeEvent siegeEvent = actor.getEvent(DominionSiegeEvent.class);
		if (siegeEvent == null)
		{
			return;
		}

		NpcString msg = MESSAGES.get(siegeEvent.getId())[1];
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player.getEvent(DominionSiegeEvent.class) == siegeEvent)
			{
				player.sendPacket(new ExShowScreenMessage(msg, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));

				QuestState questState = player.getQuestState(_731_ProtectTheMilitaryAssociationLeader.class);
				if (questState != null)
				{
					questState.abortQuest();
				}
			}
		}

		Player player = killer.getPlayer();
		if (player == null)
		{
			return;
		}

		if (player.getParty() == null)
		{
			DominionSiegeEvent siegeEvent2 = player.getEvent(DominionSiegeEvent.class);
			if (siegeEvent2 == null || siegeEvent2 == siegeEvent)
			{
				return;
			}
			siegeEvent2.addReward(player, DominionSiegeEvent.STATIC_BADGES, 5);
		}
		else
		{
			for (Player $member : player.getParty())
			{
				if ($member.isInRange(player, Config.ALT_PARTY_DISTRIBUTION_RANGE))
				{
					DominionSiegeEvent siegeEvent2 = $member.getEvent(DominionSiegeEvent.class);
					if (siegeEvent2 == null || siegeEvent2 == siegeEvent)
					{
						continue;
					}
					siegeEvent2.addReward($member, DominionSiegeEvent.STATIC_BADGES, 5);
				}
			}
		}
	}

	@Override
	public void onEvtDeSpawn()
	{
		super.onEvtDeSpawn();

		PlayerListenerList.removeGlobal(_listener);
	}
}
