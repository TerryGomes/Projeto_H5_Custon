package l2f.gameserver.model.entity.events.impl;

import java.util.Iterator;
import java.util.List;

import l2f.commons.collections.CollectionUtils;
import l2f.commons.collections.JoinedIterator;
import l2f.commons.collections.MultiValueSet;
import l2f.gameserver.ai.CtrlEvent;
import l2f.gameserver.data.xml.holder.InstantZoneHolder;
import l2f.gameserver.model.GameObject;
import l2f.gameserver.model.Party;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Request;
import l2f.gameserver.model.World;
import l2f.gameserver.model.base.TeamType;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.model.entity.events.objects.DuelSnapshotObject;
import l2f.gameserver.network.serverpackets.ExDuelAskStart;
import l2f.gameserver.network.serverpackets.ExDuelEnd;
import l2f.gameserver.network.serverpackets.ExDuelReady;
import l2f.gameserver.network.serverpackets.SocialAction;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.IStaticPacket;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.templates.InstantZone;

public class PartyVsPartyDuelEvent extends DuelEvent
{
	public PartyVsPartyDuelEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	protected PartyVsPartyDuelEvent(int id, String name)
	{
		super(id, name);
	}

	@Override
	public void stopEvent()
	{
		clearActions();

		updatePlayers(false, false);

		for (DuelSnapshotObject d : this)
		{
			d.getPlayer().sendPacket(new ExDuelEnd(this));
			GameObject target = d.getPlayer().getTarget();
			if (target != null)
			{
				d.getPlayer().getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, target);
			}
		}

		switch (_winner)
		{
		case NONE:
			sendPacket(SystemMsg.THE_DUEL_HAS_ENDED_IN_A_TIE);
			break;
		case RED:
		case BLUE:
			List<DuelSnapshotObject> winners = getObjects(_winner.name());
			List<DuelSnapshotObject> lossers = getObjects(_winner.revert().name());

			DuelSnapshotObject winner = CollectionUtils.safeGet(winners, 0);
			if (winner != null)
			{
				sendPacket(new SystemMessage2(SystemMsg.C1S_PARTY_HAS_WON_THE_DUEL).addName(winners.get(0).getPlayer()));

				for (DuelSnapshotObject d : lossers)
				{
					d.getPlayer().broadcastPacket(new SocialAction(d.getPlayer().getObjectId(), SocialAction.BOW));
				}

				// Synerge - Add to the stats the won and lost duel for each part
//					for (DuelSnapshotObject d : winners)
//						d.getPlayer().addPlayerStats(Ranking.STAT_TOP_DUELS_WIN);
//					for (DuelSnapshotObject d : lossers)
//						d.getPlayer().addPlayerStats(Ranking.STAT_TOP_DUELS_LOST);
			}
			else
			{
				sendPacket(SystemMsg.THE_DUEL_HAS_ENDED_IN_A_TIE);
			}
			break;
		}

		updatePlayers(false, true);
		removeObjects(RED_TEAM);
		removeObjects(BLUE_TEAM);
	}

	@Override
	public void teleportPlayers(String name)
	{
		InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(1);

		Reflection reflection = new Reflection();
		reflection.init(instantZone);

		List<DuelSnapshotObject> team = getObjects(BLUE_TEAM);

		for (int i = 0; i < team.size(); i++)
		{
			DuelSnapshotObject $member = team.get(i);

			$member.getPlayer().addEvent(this);
			$member.getPlayer()._stablePoint = $member.getLoc();
			$member.getPlayer().teleToLocation(instantZone.getTeleportCoords().get(i), reflection);
		}

		team = getObjects(RED_TEAM);

		for (int i = 0; i < team.size(); i++)
		{
			DuelSnapshotObject $member = team.get(i);

			$member.getPlayer().addEvent(this);
			$member.getPlayer()._stablePoint = $member.getLoc();
			$member.getPlayer().teleToLocation(instantZone.getTeleportCoords().get(9 + i), reflection);
		}
	}

	@Override
	public boolean canDuel(Player player, Player target, boolean first)
	{
		if (player.getParty() == null)
		{
			player.sendPacket(SystemMsg.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME);
			return false;
		}

		if (target.getParty() == null)
		{
			player.sendPacket(SystemMsg.SINCE_THE_PERSON_YOU_CHALLENGED_IS_NOT_CURRENTLY_IN_A_PARTY_THEY_CANNOT_DUEL_AGAINST_YOUR_PARTY);
			return false;
		}

		Party party1 = player.getParty();
		Party party2 = target.getParty();
		if (player != party1.getLeader() || target != party2.getLeader())
		{
			player.sendPacket(SystemMsg.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME);
			return false;
		}

		Iterator<Player> iterator = new JoinedIterator<Player>(party1.iterator(), party2.iterator());
		while (iterator.hasNext())
		{
			Player $member = iterator.next();

			IStaticPacket packet = null;
			if ((packet = canDuel0(player, $member)) != null)
			{
				player.sendPacket(packet);
				target.sendPacket(packet);
				return false;
			}
		}
		return true;
	}

	@Override
	public void askDuel(Player player, Player target)
	{
		Request request = new Request(Request.L2RequestType.DUEL, player, target).setTimeout(10000L);
		request.set("duelType", 1);
		player.setRequest(request);
		target.setRequest(request);

		player.sendPacket(new SystemMessage2(SystemMsg.C1S_PARTY_HAS_BEEN_CHALLENGED_TO_A_DUEL).addName(target));
		target.sendPacket(new SystemMessage2(SystemMsg.C1S_PARTY_HAS_CHALLENGED_YOUR_PARTY_TO_A_DUEL).addName(player), new ExDuelAskStart(player.getName(), 1));
	}

	@Override
	public void createDuel(Player player, Player target)
	{
		PartyVsPartyDuelEvent duelEvent = new PartyVsPartyDuelEvent(getDuelType(), player.getObjectId() + "_" + target.getObjectId() + "_duel");
		cloneTo(duelEvent);

		for (Player $member : player.getParty())
		{
			duelEvent.addObject(BLUE_TEAM, new DuelSnapshotObject($member, TeamType.BLUE));
		}

		for (Player $member : target.getParty())
		{
			duelEvent.addObject(RED_TEAM, new DuelSnapshotObject($member, TeamType.RED));
		}

		duelEvent.sendPacket(new ExDuelReady(this));
		duelEvent.reCalcNextTime(false);
	}

	@Override
	public void playerExit(Player player)
	{
		for (DuelSnapshotObject $snapshot : this)
		{
			if ($snapshot.getPlayer() == player)
			{
				removeObject($snapshot.getTeam().name(), $snapshot);
			}

			List<DuelSnapshotObject> objects = getObjects($snapshot.getTeam().name());
			if (objects.isEmpty())
			{
				_winner = $snapshot.getTeam().revert();
				stopEvent();
			}
		}
	}

	@Override
	public void packetSurrender(Player player)
	{
		//
	}

	@Override
	public void onDie(Player player)
	{
		TeamType team = player.getTeam();
		if (team == TeamType.NONE || _aborted)
		{
			return;
		}

		sendPacket(SystemMsg.THE_OTHER_PARTY_IS_FROZEN, team.revert().name());

		player.stopAttackStanceTask();
		player.startFrozen();
		player.setTeam(TeamType.NONE);

		for (Player $player : World.getAroundPlayers(player))
		{
			$player.getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, player);
			if (player.getPet() != null)
			{
				$player.getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, player.getPet());
			}
		}
		player.sendChanges();

		boolean allDead = true;
		List<DuelSnapshotObject> objs = getObjects(team.name());
		for (DuelSnapshotObject obj : objs)
		{
			if (obj.getPlayer() == player)
			{
				obj.setDead();
			}

			if (!obj.isDead())
			{
				allDead = false;
			}
		}

		if (allDead)
		{
			_winner = team.revert();

			stopEvent();
		}
	}

	@Override
	public int getDuelType()
	{
		return 1;
	}

	@Override
	protected long startTimeMillis()
	{
		return System.currentTimeMillis() + 30000L;
	}
}
