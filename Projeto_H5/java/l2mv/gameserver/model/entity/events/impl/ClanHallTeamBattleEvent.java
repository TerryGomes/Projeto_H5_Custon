package l2mv.gameserver.model.entity.events.impl;

import java.util.List;

import l2mv.commons.collections.CollectionUtils;
import l2mv.commons.collections.MultiValueSet;
import l2mv.gameserver.dao.SiegeClanDAO;
import l2mv.gameserver.dao.SiegePlayerDAO;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.RestartType;
import l2mv.gameserver.model.entity.events.objects.CTBSiegeClanObject;
import l2mv.gameserver.model.entity.events.objects.CTBTeamObject;
import l2mv.gameserver.model.entity.events.objects.SiegeClanObject;
import l2mv.gameserver.model.entity.residence.ClanHall;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.network.serverpackets.PlaySound;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.tables.ClanTable;
import l2mv.gameserver.utils.Location;

public class ClanHallTeamBattleEvent extends SiegeEvent<ClanHall, CTBSiegeClanObject>
{
	public static final String TRYOUT_PART = "tryout_part";
	public static final String CHALLENGER_RESTART_POINTS = "challenger_restart_points";
	public static final String FIRST_DOORS = "first_doors";
	public static final String SECOND_DOORS = "second_doors";
	public static final String NEXT_STEP = "next_step";

	public ClanHallTeamBattleEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	@Override
	public void startEvent()
	{
		if (!_isInProgress.compareAndSet(false, true))
		{
			return;
		}
		_oldOwner = getResidence().getOwner();
		final List<CTBSiegeClanObject> attackers = getObjects(ATTACKERS);
		if (attackers.isEmpty())
		{
			if (_oldOwner == null)
			{
				broadcastInZone2(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST).addResidenceName(getResidence()));
			}
			else
			{
				broadcastInZone2(new SystemMessage2(SystemMsg.S1S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED).addResidenceName(getResidence()));
			}

			reCalcNextTime(false);
			return;
		}

		if (_oldOwner != null)
		{
			addObject(DEFENDERS, new SiegeClanObject(DEFENDERS, _oldOwner, 0));
			newSiegeClan(_oldOwner.getName(), _oldOwner.getClanId(), 0, System.currentTimeMillis());
		}
		final List<CTBTeamObject> teams = getObjects(TRYOUT_PART);
		for (int i = 0; i < 5; i++)
		{
			CTBTeamObject team = teams.get(i);

			team.setSiegeClan(CollectionUtils.safeGet(attackers, i));
		}

		broadcastTo(new SystemMessage2(SystemMsg.THE_SIEGE_TO_CONQUER_S1_HAS_BEGUN).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);
		broadcastTo(SystemMsg.THE_TRYOUTS_ARE_ABOUT_TO_BEGIN, ATTACKERS);

		super.startEvent();
	}

	public void nextStep()
	{
		broadcastTo(SystemMsg.THE_TRYOUTS_HAVE_BEGUN, ATTACKERS, DEFENDERS);

		updateParticles(true, ATTACKERS, DEFENDERS);
	}

	public void processStep(CTBTeamObject team)
	{
		if (team.getSiegeClan() != null)
		{
			final CTBSiegeClanObject object = team.getSiegeClan();
			object.setEvent(false, this);

			teleportPlayers(SPECTATORS);
		}

		team.despawnObject(this);

		List<CTBTeamObject> teams = getObjects(TRYOUT_PART);

		boolean hasWinner = false;
		CTBTeamObject winnerTeam = null;

		for (CTBTeamObject t : teams)
		{
			if (t.isParticle())
			{
				hasWinner = winnerTeam == null; // если зайдет второй раз то скажет что нету виннера

				winnerTeam = t;
			}
		}

		if (!hasWinner)
		{
			return;
		}

		SiegeClanObject clan = winnerTeam.getSiegeClan();
		if (clan != null)
		{
			getResidence().changeOwner(clan.getClan());
		}

		stopEvent(true);
	}

	@Override
	public void announce(int val)
	{
		final int minute = val / 60;
		if (minute > 0)
		{
			broadcastTo(new SystemMessage2(SystemMsg.THE_CONTEST_WILL_BEGIN_IN_S1_MINUTES).addInteger(minute), ATTACKERS, DEFENDERS);
		}
		else
		{
			broadcastTo(new SystemMessage2(SystemMsg.THE_PRELIMINARY_MATCH_WILL_BEGIN_IN_S1_SECONDS).addInteger(val), ATTACKERS, DEFENDERS);
		}
	}

	@Override
	public void stopEvent(boolean step)
	{
		if (!_isInProgress.compareAndSet(true, false))
		{
			return;
		}
		final Clan newOwner = getResidence().getOwner();
		if (newOwner != null)
		{
			if (_oldOwner != newOwner)
			{
				newOwner.broadcastToOnlineMembers(PlaySound.SIEGE_VICTORY);

				newOwner.incReputation(1700, false, toString());
			}

			broadcastTo(new SystemMessage2(SystemMsg.S1_CLAN_HAS_DEFEATED_S2).addString(newOwner.getName()).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);
			broadcastTo(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);
		}
		else
		{
			broadcastTo(new SystemMessage2(SystemMsg.THE_PRELIMINARY_MATCH_OF_S1_HAS_ENDED_IN_A_DRAW).addResidenceName(getResidence()), ATTACKERS);
		}

		updateParticles(false, ATTACKERS, DEFENDERS);
		SiegeClanDAO.getInstance().delete(getResidence());
		SiegePlayerDAO.getInstance().delete(getResidence());
		removeObjects(DEFENDERS);
		removeObjects(ATTACKERS);

		super.stopEvent(step);

		_oldOwner = null;
	}

	@Override
	public void loadSiegeClans()
	{
		final List<SiegeClanObject> siegeClanObjectList = SiegeClanDAO.getInstance().load(getResidence(), ATTACKERS);
		addObjects(ATTACKERS, siegeClanObjectList);
		final List<CTBSiegeClanObject> objects = getObjects(ATTACKERS);
		for (CTBSiegeClanObject clan : objects)
		{
			clan.select(getResidence());
		}
	}

	@Override
	public CTBSiegeClanObject newSiegeClan(String type, int clanId, long i, long date)
	{
		final Clan clan = ClanTable.getInstance().getClan(clanId);
		return clan == null ? null : new CTBSiegeClanObject(type, clan, i, date);
	}

	@Override
	public boolean isParticle(Player player)
	{
		if (!isInProgress() || player.getClan() == null)
		{
			return false;
		}
		final CTBSiegeClanObject object = getSiegeClan(ATTACKERS, player.getClan());
		return object != null && object.getPlayers().contains(player.getObjectId());
	}

	@Override
	public Location getRestartLoc(Player player, RestartType type)
	{
		if (!checkIfInZone(player))
		{
			return null;
		}

		SiegeClanObject attackerClan = getSiegeClan(ATTACKERS, player.getClan());

		Location loc = null;
		switch (type)
		{
		case TO_VILLAGE:
			if (attackerClan != null && checkIfInZone(player))
			{
				final List<SiegeClanObject> objectList = getObjects(ATTACKERS);
				final List<Location> teleportList = getObjects(CHALLENGER_RESTART_POINTS);
				final int index = objectList.indexOf(attackerClan);
				loc = teleportList.get(index);
			}
			break;
		}
		return loc;
	}

	@Override
	public void action(String name, boolean start)
	{
		if (name.equalsIgnoreCase(NEXT_STEP))
		{
			nextStep();
		}
		else
		{
			super.action(name, start);
		}
	}

	@Override
	public int getUserRelation(Player thisPlayer, int result)
	{
		return result;
	}

	@Override
	public int getRelation(Player thisPlayer, Player targetPlayer, int result)
	{
		return result;
	}
}
