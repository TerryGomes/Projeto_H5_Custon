package l2f.gameserver.model.entity.events.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.napile.primitive.Containers;
import org.napile.primitive.sets.IntSet;

import l2f.commons.collections.MultiValueSet;
import l2f.commons.dao.JdbcEntityState;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.dao.CastleDamageZoneDAO;
import l2f.gameserver.dao.CastleDoorUpgradeDAO;
import l2f.gameserver.dao.CastleHiredGuardDAO;
import l2f.gameserver.dao.SiegeClanDAO;
import l2f.gameserver.data.xml.holder.EventHolder;
import l2f.gameserver.instancemanager.ReflectionManager;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.Spawner;
import l2f.gameserver.model.Zone;
import l2f.gameserver.model.base.RestartType;
import l2f.gameserver.model.entity.Hero;
import l2f.gameserver.model.entity.HeroDiary;
import l2f.gameserver.model.entity.SevenSigns;
import l2f.gameserver.model.entity.events.EventType;
import l2f.gameserver.model.entity.events.objects.DoorObject;
import l2f.gameserver.model.entity.events.objects.SiegeClanObject;
import l2f.gameserver.model.entity.events.objects.SiegeToggleNpcObject;
import l2f.gameserver.model.entity.events.objects.SpawnExObject;
import l2f.gameserver.model.entity.events.objects.SpawnSimpleObject;
import l2f.gameserver.model.entity.residence.Castle;
import l2f.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.model.pledge.UnitMember;
import l2f.gameserver.network.serverpackets.ExShowScreenMessage;
import l2f.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2f.gameserver.network.serverpackets.L2GameServerPacket;
import l2f.gameserver.network.serverpackets.PlaySound;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.templates.item.support.MerchantGuard;
import l2f.gameserver.utils.Location;

public class CastleSiegeEvent extends SiegeEvent<Castle, SiegeClanObject>
{
	public static final int MAX_SIEGE_CLANS = 200;
	public static final long DAY_IN_MILISECONDS = 86400000L;

	public static final String DEFENDERS_WAITING = "defenders_waiting";
	public static final String DEFENDERS_REFUSED = "defenders_refused";

	public static final String CONTROL_TOWERS = "control_towers";
	public static final String FLAME_TOWERS = "flame_towers";
	public static final String BOUGHT_ZONES = "bought_zones";
	public static final String GUARDS = "guards";
	public static final String HIRED_GUARDS = "hired_guards";

	private final IntSet _nextSiegeTimes = Containers.EMPTY_INT_SET;
	private boolean _firstStep = false;

	public CastleSiegeEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	// ========================================================================================================================================================================
	// Главные методы осады
	// ========================================================================================================================================================================
	@Override
	public void initEvent()
	{
		super.initEvent();

		List<DoorObject> doorObjects = getObjects(DOORS);

		addObjects(BOUGHT_ZONES, CastleDamageZoneDAO.getInstance().load(getResidence()));

		for (DoorObject doorObject : doorObjects)
		{
			doorObject.setUpgradeValue(this, CastleDoorUpgradeDAO.getInstance().load(doorObject.getUId()));
			doorObject.getDoor().addListener(_doorDeathListener);
		}
	}

	@Override
	public void processStep(Clan newOwnerClan)
	{
		final Clan oldOwnerClan = getResidence().getOwner();
		getResidence().changeOwner(newOwnerClan);

		// если есть овнер в резиденции, делаем его аттакером
		if (oldOwnerClan != null)
		{
			final SiegeClanObject ownerSiegeClan = getSiegeClan(DEFENDERS, oldOwnerClan);
			removeObject(DEFENDERS, ownerSiegeClan);

			ownerSiegeClan.setType(ATTACKERS);
			addObject(ATTACKERS, ownerSiegeClan);
		}
		else
		{
			// Если атакуется замок, принадлежащий NPC, и только 1 атакующий - закончить осаду
			if (getObjects(ATTACKERS).size() == 1)
			{
				stopEvent();
				return;
			}

			// Если атакуется замок, принадлежащий NPC, и все атакующие в одном альянсе - закончить осаду
			final int allianceObjectId = newOwnerClan.getAllyId();
			if (allianceObjectId > 0)
			{
				final List<SiegeClanObject> attackers = getObjects(ATTACKERS);
				boolean sameAlliance = true;
				for (SiegeClanObject sc : attackers)
				{
					if ((sc != null) && (sc.getClan().getAllyId() != allianceObjectId))
					{
						sameAlliance = false;
					}
				}
				if (sameAlliance)
				{
					stopEvent();
					return;
				}
			}
		}

		// ставим нового овнера защитником
		final SiegeClanObject newOwnerSiegeClan = getSiegeClan(ATTACKERS, newOwnerClan);
		newOwnerSiegeClan.deleteFlag();
		newOwnerSiegeClan.setType(DEFENDERS);

		removeObject(ATTACKERS, newOwnerSiegeClan);

		// у нас защитник ток овнер
		final List<SiegeClanObject> defenders = removeObjects(DEFENDERS);
		for (SiegeClanObject siegeClan : defenders)
		{
			siegeClan.setType(ATTACKERS);
		}

		// новый овнер это защитник
		addObject(DEFENDERS, newOwnerSiegeClan);

		// все дефендеры, стают аттакующими
		addObjects(ATTACKERS, defenders);

		updateParticles(true, ATTACKERS, DEFENDERS);

		teleportPlayers(ATTACKERS);
		teleportPlayers(SPECTATORS);

		// ток при первом захвате обнуляем мерчант гвардов и убираем апгрейд дверей
		if (!_firstStep)
		{
			_firstStep = true;

			broadcastTo(SystemMsg.THE_TEMPORARY_ALLIANCE_OF_THE_CASTLE_ATTACKER_TEAM_HAS_BEEN_DISSOLVED, ATTACKERS, DEFENDERS);

			if (_oldOwner != null)
			{
				spawnAction(HIRED_GUARDS, false);
				damageZoneAction(false);
				removeObjects(HIRED_GUARDS);
				removeObjects(BOUGHT_ZONES);

				CastleDamageZoneDAO.getInstance().delete(getResidence());
			}
			else
			{
				spawnAction(GUARDS, false);
			}

			List<DoorObject> doorObjects = getObjects(DOORS);
			for (DoorObject doorObject : doorObjects)
			{
				doorObject.setWeak(true);
				doorObject.setUpgradeValue(this, 0);

				CastleDoorUpgradeDAO.getInstance().delete(doorObject.getUId());
			}
		}

		spawnAction(DOORS, true);
		despawnSiegeSummons();
	}

	@Override
	public void startEvent()
	{
		if (!_isInProgress.compareAndSet(false, true))
		{
			return;
		}
		_oldOwner = getResidence().getOwner();
		if (_oldOwner != null)
		{
			addObject(DEFENDERS, new SiegeClanObject(DEFENDERS, _oldOwner, 0));

			if (getResidence().getSpawnMerchantTickets().size() > 0)
			{
				for (ItemInstance item : getResidence().getSpawnMerchantTickets())
				{
					final MerchantGuard guard = getResidence().getMerchantGuard(item.getItemId());
					addObject(HIRED_GUARDS, new SpawnSimpleObject(guard.getNpcId(), item.getLoc()));

					item.deleteMe();
				}

				CastleHiredGuardDAO.getInstance().delete(getResidence());

				spawnAction(HIRED_GUARDS, true);
			}
		}

		List<SiegeClanObject> attackers = getObjects(ATTACKERS);
		if (attackers.isEmpty())
		{
			if (_oldOwner == null)
			{
				broadcastToWorld(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST).addResidenceName(getResidence()));
			}
			else
			{
				broadcastToWorld(new SystemMessage2(SystemMsg.S1S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED).addResidenceName(getResidence()));
				getResidence().getOwnDate().setTimeInMillis(System.currentTimeMillis());
				getResidence().getLastSiegeDate().setTimeInMillis(getResidence().getSiegeDate().getTimeInMillis());
				getResidence().update();
			}

			reCalcNextTime(false);
			SiegeClanDAO.getInstance().delete(getResidence());
			return;
		}
		updateParticles(true, ATTACKERS, DEFENDERS);

		broadcastTo(SystemMsg.THE_TEMPORARY_ALLIANCE_OF_THE_CASTLE_ATTACKER_TEAM_IS_IN_EFFECT, ATTACKERS);
		broadcastTo(new SystemMessage2(SystemMsg.YOU_ARE_PARTICIPATING_IN_THE_SIEGE_OF_S1_THIS_SIEGE_IS_SCHEDULED_FOR_2_HOURS).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);
		broadcastToWorld(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_STARTED).addResidenceName(getResidence()));
		super.startEvent();

		if (_oldOwner == null)
		{
			initControlTowers();
		}
		else
		{
			damageZoneAction(true);
		}
	}

	@Override
	public void stopEvent(boolean step)
	{
		if (!_isInProgress.compareAndSet(true, false))
		{
			return;
		}
		final List<DoorObject> doorObjects = getObjects(DOORS);
		for (DoorObject doorObject : doorObjects)
		{
			doorObject.setWeak(false);
		}

		damageZoneAction(false);

		updateParticles(false, ATTACKERS, DEFENDERS);

		List<SiegeClanObject> attackers = removeObjects(ATTACKERS);
		for (SiegeClanObject siegeClan : attackers)
		{
			siegeClan.deleteFlag();
		}

		broadcastToWorld(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED).addResidenceName(getResidence()));

		removeObjects(DEFENDERS);
		removeObjects(DEFENDERS_WAITING);
		removeObjects(DEFENDERS_REFUSED);

		Clan ownerClan = getResidence().getOwner();
		if (ownerClan != null)
		{
			if (_oldOwner == ownerClan)
			{
				getResidence().setRewardCount(getResidence().getRewardCount() + 1);
				ownerClan.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.SINCE_YOUR_CLAN_EMERGED_VICTORIOUS_FROM_THE_SIEGE_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLANS_REPUTATION_SCORE)
							.addInteger(ownerClan.incReputation(1500, false, toString())));

				// Synerge - Give the winner clan a reputation reward. Half reward if the clan keeps the castle
				if (Config.SIEGE_WINNER_REPUTATION_REWARD > 0)
				{
					ownerClan.incReputation(Config.SIEGE_WINNER_REPUTATION_REWARD / 2, false, "SiegeWinnerCustomReward");
				}
			}
			else
			{
				L2GameServerPacket packet = new Say2(0, ChatType.CRITICAL_ANNOUNCE, getResidence().getName() + " Castle",
							"Clan " + ownerClan.getName() + " is victorious over " + getResidence().getName() + "'s castle siege!");
				for (Player player : GameObjectsStorage.getAllPlayersForIterate())
				{
					player.sendPacket(packet);
				}

				ownerClan.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.SINCE_YOUR_CLAN_EMERGED_VICTORIOUS_FROM_THE_SIEGE_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLANS_REPUTATION_SCORE)
							.addInteger(ownerClan.incReputation(3000, false, toString())));

				if (_oldOwner != null)
				{
					_oldOwner.incReputation(-3000, false, toString());
					_oldOwner.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.YOUR_CLAN_HAS_FAILED_TO_DEFEND_THE_CASTLE));
				}

				for (UnitMember member : ownerClan)
				{
					final Player player = member.getPlayer();
					if (player != null)
					{

						player.getPlayer().getCounters().castleSiegesWon++;

						player.sendPacket(PlaySound.SIEGE_VICTORY);
						if (player.isOnline() && player.isNoble())
						{
							Hero.getInstance().addHeroDiary(player.getObjectId(), HeroDiary.ACTION_CASTLE_TAKEN, getResidence().getId());
						}
					}
				}
			}

			getResidence().getOwnDate().setTimeInMillis(System.currentTimeMillis());
			getResidence().getLastSiegeDate().setTimeInMillis(getResidence().getSiegeDate().getTimeInMillis());

			DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
			runnerEvent.registerDominion(getResidence().getDominion());
			int id = getResidence().getId();
			if (id == 3 || id == 5 || id == 8)
			{
				// ownerClan.incReputation(20000);
				ownerClan.incReputation(Config.SIEGE_WINNER_REPUTATION_REWARD, false, "SiegeWinnerCustomReward");
				Player leader = ownerClan.getLeader().getPlayer();
				if (leader != null && leader.isOnline())
				{
					leader.getInventory().addItem(24003, 1, "SiegeEvent");
				}

				String msg = "20.000 Clan Reputation Points has been added to " + ownerClan.getName() + " clan for capturing " + getResidence().getName() + " of castle!";
				L2GameServerPacket packet = new Say2(0, ChatType.CRITICAL_ANNOUNCE, getResidence().getName() + " Castle", msg);
				for (Player player : GameObjectsStorage.getAllPlayersForIterate())
				{
					player.sendPacket(packet);
					player.sendPacket(new ExShowScreenMessage(msg, 3000, ScreenMessageAlign.TOP_CENTER, false));
				}
			}
		}
		else
		{
			broadcastToWorld(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addResidenceName(getResidence()));

			getResidence().getOwnDate().setTimeInMillis(0);
			getResidence().getLastSiegeDate().setTimeInMillis(0);
			final DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
			runnerEvent.unRegisterDominion(getResidence().getDominion());
		}
		SiegeClanDAO.getInstance().delete(getResidence());
		if (_siegeStartTask != null)
		{
			_siegeStartTask.cancel(false);
			_siegeStartTask = null;
		}
		despawnSiegeSummons();

		if (_oldOwner != null)
		{
			spawnAction(HIRED_GUARDS, false);
			removeObjects(HIRED_GUARDS);
		}

		showResults();

		super.stopEvent(step);
	}

	// ========================================================================================================================================================================

	@Override
	public void reCalcNextTime(boolean onInit)
	{
		if (_siegeStartTask != null)
		{
			return;
		}
		clearActions();

		broadcastToWorld(new SystemMessage2(SystemMsg.S1_HAS_ANNOUNCED_THE_NEXT_CASTLE_SIEGE_TIME).addResidenceName(getResidence()));
		correctSiegeDateTime();
		final Calendar startSiegeDate = getResidence().getSiegeDate();
		if (startSiegeDate.getTimeInMillis() + 120 * 60000 >= System.currentTimeMillis())
		{
			while (startSiegeDate.getTimeInMillis() <= System.currentTimeMillis())
			{
				// After rr siege will continue after 10 minutes.
				startSiegeDate.add(Calendar.MINUTE, 10);
				startSiegeDate.set(Calendar.SECOND, 0);
			}
		}
		registerActions(); // It will start event
		_siegeStartTask = ThreadPoolManager.getInstance().schedule(new SiegeStartTask(this), 1000);
	}

	static final class SiegeStartTask implements Runnable
	{
		private final CastleSiegeEvent _castle;

		public SiegeStartTask(CastleSiegeEvent castleSiegeEvent)
		{
			_castle = castleSiegeEvent;
		}

		@Override
		public void run()
		{
			final long timeRemaining = _castle.getResidence().getSiegeDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
			if (timeRemaining > 86400000)
			{
				ThreadPoolManager.getInstance().schedule(new SiegeStartTask(_castle), timeRemaining - 86400000); // Prepare task for 24 before siege start to end registration
			}
			else if (timeRemaining <= 86400000 && timeRemaining > 3600000)
			{
				ThreadPoolManager.getInstance().schedule(new SiegeStartTask(_castle), timeRemaining - 3600000); // Prepare task for 1 hr left before siege start.
			}
			else if (timeRemaining <= 3600000 && timeRemaining > 600000)
			{
				broadcastToWorld(new SystemMessage(Math.round(timeRemaining / 60000) + " minute(s) until " + _castle.getResidence().getName() + " siege begin."));
				ThreadPoolManager.getInstance().schedule(new SiegeStartTask(_castle), timeRemaining - 600000); // Prepare task for 10 minute left.
			}
			else if (timeRemaining <= 600000 && timeRemaining > 300000)
			{
				broadcastToWorld(new SystemMessage(Math.round(timeRemaining / 60000) + " minute(s) until " + _castle.getResidence().getName() + " siege begin."));
				ThreadPoolManager.getInstance().schedule(new SiegeStartTask(_castle), timeRemaining - 300000); // Prepare task for 5 minute left.
			}
			else if (timeRemaining <= 300000 && timeRemaining > 10000)
			{
				broadcastToWorld(new SystemMessage(Math.round(timeRemaining / 60000) + " minute(s) until " + _castle.getResidence().getName() + " siege begin."));
				ThreadPoolManager.getInstance().schedule(new SiegeStartTask(_castle), timeRemaining - 10000); // Prepare task for 10 seconds count down
			}
			else if (timeRemaining <= 10000 && timeRemaining > 0)
			{
				broadcastToWorld(new SystemMessage(_castle.getResidence().getName() + " siege " + Math.round(timeRemaining / 1000) + " second(s) to start!"));
				ThreadPoolManager.getInstance().schedule(new SiegeStartTask(_castle), timeRemaining); // Prepare task for second count down
			}
		}
	}

	private void correctSiegeDateTime()
	{
		boolean corrected = false;
		if (getResidence().getSiegeDate().getTimeInMillis() == 0)
		{
			corrected = true;
			setNextSiegeDate(1); // first sieges are scheduled for the first week
		}
		else if (getResidence().getSiegeDate().getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
		{
			corrected = true;
			setNextSiegeDate(Config.PERIOD_CASTLE_SIEGE);
		}
		if (getResidence().getSiegeDate().get(Calendar.DAY_OF_WEEK) != _dayOfWeek)
		{
			corrected = true;
			getResidence().getSiegeDate().set(Calendar.DAY_OF_WEEK, _dayOfWeek);
		}
		if (getResidence().getSiegeDate().get(Calendar.HOUR_OF_DAY) != _hourOfDay)
		{
			corrected = true;
			getResidence().getSiegeDate().set(Calendar.HOUR_OF_DAY, _hourOfDay);
		}
		getResidence().getSiegeDate().set(Calendar.MINUTE, 0);
		if (corrected)
		{
			getResidence().setJdbcState(JdbcEntityState.UPDATED);
			getResidence().update();
		}
	}

	private void setNextSiegeDate(int week)
	{
		if (getResidence().getSiegeDate().getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
		{
			// Set next siege date if siege has passed
			getResidence().getSiegeDate().add(Calendar.WEEK_OF_YEAR, week);
			if (getResidence().getSiegeDate().getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
			{
				setNextSiegeDate(week); // Re-run again if still in the pass
			}
		}
	}

	@Override
	public void loadSiegeClans()
	{
		super.loadSiegeClans();

		addObjects(DEFENDERS_WAITING, SiegeClanDAO.getInstance().load(getResidence(), DEFENDERS_WAITING));
		addObjects(DEFENDERS_REFUSED, SiegeClanDAO.getInstance().load(getResidence(), DEFENDERS_REFUSED));
	}

	@Override
	public void setRegistrationOver(boolean b)
	{
		if (b)
		{
			broadcastToWorld(new SystemMessage2(SystemMsg.THE_DEADLINE_TO_REGISTER_FOR_THE_SIEGE_OF_S1_HAS_PASSED).addResidenceName(getResidence()));
		}

		super.setRegistrationOver(b);
	}

	@Override
	public void announce(int val)
	{
		SystemMessage2 msg;
		final int min = val / 60;
		final int hour = min / 60;
		if (hour > 0)
		{
			msg = new SystemMessage2(SystemMsg.S1_HOURS_UNTIL_CASTLE_SIEGE_CONCLUSION).addInteger(hour);
		}
		else if (min > 0)
		{
			msg = new SystemMessage2(SystemMsg.S1_MINUTES_UNTIL_CASTLE_SIEGE_CONCLUSION).addInteger(min);
		}
		else
		{
			msg = new SystemMessage2(SystemMsg.THIS_CASTLE_SIEGE_WILL_END_IN_S1_SECONDS).addInteger(val);
		}

		broadcastTo(msg, ATTACKERS, DEFENDERS);
	}

	// ========================================================================================================================================================================
	// Control Tower Support
	// ========================================================================================================================================================================
	private void initControlTowers()
	{
		final List<SpawnExObject> objects = getObjects(GUARDS);
		final List<Spawner> spawns = new ArrayList<Spawner>();
		for (SpawnExObject o : objects)
		{
			spawns.addAll(o.getSpawns());
		}

		List<SiegeToggleNpcObject> ct = getObjects(CONTROL_TOWERS);

		SiegeToggleNpcInstance closestCt;
		double distance, distanceClosest;

		for (Spawner spawn : spawns)
		{
			final Location spawnLoc = spawn.getCurrentSpawnRange().getRandomLoc(ReflectionManager.DEFAULT.getGeoIndex());
			closestCt = null;
			distanceClosest = 0;

			for (SiegeToggleNpcObject c : ct)
			{
				final SiegeToggleNpcInstance npcTower = c.getToggleNpc();
				distance = npcTower.getDistance(spawnLoc);
				if (closestCt == null || distance < distanceClosest)
				{
					closestCt = npcTower;
					distanceClosest = distance;
				}

				closestCt.register(spawn);
			}
		}
	}

	// ========================================================================================================================================================================
	// Damage Zone Actions
	// ========================================================================================================================================================================
	private void damageZoneAction(boolean active)
	{
		zoneAction(BOUGHT_ZONES, active);
	}

	// ========================================================================================================================================================================
	// Суппорт Методы для установки времени осады
	// ========================================================================================================================================================================
	@Override
	public boolean isAttackersInAlly()
	{
		return !_firstStep;
	}

	public int[] getNextSiegeTimes()
	{
		return _nextSiegeTimes.toArray();
	}

	@Override
	public SystemMsg checkForAttack(Creature target, Creature attacker, Skill skill, boolean force)
	{
		try
		{
			if ((target == null) || (attacker.isPlayer() && target.isPlayer() && attacker.getPlayer().getPlayerGroup() == target.getPlayer().getPlayerGroup())) // Party and
																																								// CommandChannel check.
			{
				return SystemMsg.INVALID_TARGET;
			}
			if (attacker.isPlayer() && target.isPlayer() && attacker.getPlayer().getClan() != null && target.getPlayer().getClan() != null && attacker.getPlayer().getClan() == target.getPlayer().getClan())
			{
				return SystemMsg.INVALID_TARGET;
			}
			if (attacker.isPlayer() && target.isPlayer() && attacker.getPlayer().getClan() != null && attacker.getPlayer().getClan().getAlliance() != null && target.getPlayer().getClan() != null
						&& target.getPlayer().getClan().getAlliance() != null && attacker.getPlayer().getClan().getAlliance() == target.getPlayer().getClan().getAlliance())
			{
				return SystemMsg.INVALID_TARGET;
			}
			final Player targetPlayer = target.getPlayer();
			final Player attackerPlayer = attacker.getPlayer();
			if (targetPlayer == null)
			{
				return SystemMsg.INVALID_TARGET;
			}
			final CastleSiegeEvent siegeEvent = target.getEvent(CastleSiegeEvent.class);
			final CastleSiegeEvent siegeEventatt = attacker.getEvent(CastleSiegeEvent.class);
			// if(!targetPlayer.isOnSiegeField())
			// return null;
			if ((siegeEvent == null) || (siegeEventatt == null) || !target.isPlayer() || (targetPlayer.getClan() == null))
			{
				return null;
			}
			if ((attacker.getClan() == null) || (siegeEvent != this))
			{
				return null;
			}
			SiegeClanObject targetSiegeClan = siegeEvent.getSiegeClan(ATTACKERS, targetPlayer.getClan());
			if (targetSiegeClan == null)
			{
				targetSiegeClan = siegeEvent.getSiegeClan(DEFENDERS, targetPlayer.getClan());
			}
			if (targetSiegeClan == null)
			{
				return null;
			}
			if (targetSiegeClan.getType().equals(ATTACKERS))
			{
				if (targetPlayer.getClan() == attackerPlayer.getClan())
				{
					return SystemMsg.INVALID_TARGET;
				}
				if (targetPlayer.getClan() != attackerPlayer.getClan())
				{
					if (attackerPlayer.getAlliance() != null && attackerPlayer.getAlliance() == targetPlayer.getAlliance())
					{
						return SystemMsg.INVALID_TARGET;
					}
				}
			}
			else
			{
				return null;
			}
		}
		catch (final Exception e)
		{
			return null;
		}
		return null;
	}

	@Override
	public Boolean canAttack(Creature target, Creature attacker, Skill skill, boolean force)
	{
		try
		{
			if (attacker == null || target == null)
			{
				return null;
			}

			final Player targetPlayer = target.getPlayer();
			if (targetPlayer == null)
			{
				return null;
			}
			final CastleSiegeEvent siegeEvent = target.getEvent(CastleSiegeEvent.class);
			final CastleSiegeEvent siegeEventatt = attacker.getEvent(CastleSiegeEvent.class);
			if ((siegeEvent == null) || (siegeEventatt == null) || !target.isPlayer() || (targetPlayer.getClan() == null))
			{
				return null;
			}
			if ((attacker.getClan() == null) || (siegeEvent != this))
			{
				return null;
			}
			SiegeClanObject targetSiegeClan = siegeEvent.getSiegeClan(ATTACKERS, targetPlayer.getClan());
			if (targetSiegeClan == null)
			{
				targetSiegeClan = siegeEvent.getSiegeClan(DEFENDERS, targetPlayer.getClan());
			}
			if (targetSiegeClan == null)
			{
				return null;
			}

			if (attacker.isPlayer() && target.isPlayer() && attacker.getPlayer().getPlayerGroup() == target.getPlayer().getPlayerGroup()) // Party and CommandChannel check.
			{
				return false;
			}
			if (attacker.isPlayer() && target.isPlayer() && attacker.getPlayer().getClan() != null && target.getPlayer().getClan() != null && attacker.getPlayer().getClan() == target.getPlayer().getClan())
			{
				return false;
			}
			if (attacker.isPlayer() && target.isPlayer() && attacker.getPlayer().getClan() != null && attacker.getPlayer().getClan().getAlliance() != null && target.getPlayer().getClan() != null
						&& target.getPlayer().getClan().getAlliance() != null && attacker.getPlayer().getClan().getAlliance() == target.getPlayer().getClan().getAlliance())
			{
				return false;
			}

			if (targetSiegeClan.getType().equals(ATTACKERS))
			{
				if (targetPlayer.getClan() == attacker.getPlayer().getClan())
				{
					attacker.sendPacket(new SystemMessage2(SystemMsg.INVALID_TARGET));
					return false;
				}
				if (targetPlayer.getClan() != attacker.getPlayer().getClan())
				{
					if (attacker.getPlayer().getAlliance() != null && attacker.getPlayer().getAlliance() == targetPlayer.getAlliance())
					{
						attacker.sendPacket(new SystemMessage2(SystemMsg.INVALID_TARGET));
						return false;
					}
				}
			}
			else
			{
				return true;
			}
		}
		catch (Exception e)
		{
			return null;
		}
		return true;
	}

	@Override
	public boolean canRessurect(Player resurrectPlayer, Creature target, boolean force)
	{
		boolean playerInZone = resurrectPlayer.isInZone(Zone.ZoneType.SIEGE);
		boolean targetInZone = target.isInZone(Zone.ZoneType.SIEGE);
		// если оба вне зоны - рес разрешен
		if (!playerInZone && !targetInZone)
		{
			return true;
		}
		// если таргет вне осадный зоны - рес разрешен
		if (!targetInZone)
		{
			return false;
		}

		Player targetPlayer = target.getPlayer();
		// если таргет не с нашей осады(или вообще нету осады) - рес запрещен
		CastleSiegeEvent activeCharSiegeEvent = resurrectPlayer.getEvent(CastleSiegeEvent.class);
		CastleSiegeEvent targetSiegeEvent = target.getEvent(CastleSiegeEvent.class);
		if (activeCharSiegeEvent != this || targetSiegeEvent != this)
		{
			targetPlayer.sendPacket(SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
			resurrectPlayer.sendPacket(SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
			return false;
		}

		SiegeClanObject targetSiegeClan = targetSiegeEvent.getSiegeClan(ATTACKERS, targetPlayer.getClan());
		if (targetSiegeClan == null)
		{
			targetSiegeClan = targetSiegeEvent.getSiegeClan(DEFENDERS, targetPlayer.getClan());
		}

		if (targetSiegeClan.getType() == ATTACKERS)
		{
			// если нету флага - рес запрещен
			if (targetSiegeClan.getFlag() == null)
			{
				targetPlayer.sendPacket(SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
				resurrectPlayer.sendPacket(SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
				return false;
			}
		}
		else
		{
			final List<SiegeToggleNpcObject> towers = getObjects(CONTROL_TOWERS);

			int deadTowers = 0;
			for (SiegeToggleNpcObject t : towers)
			{
				if (!t.isAlive())
				{
					deadTowers++;
				}
			}

			// Prims - If two or more of the towers have been destroyed: Neither the resurrection spell nor the scroll may be used
			if (deadTowers >= 2)
			{
				targetPlayer.sendPacket(SystemMsg.THE_GUARDIAN_TOWER_HAS_BEEN_DESTROYED_AND_RESURRECTION_IS_NOT_POSSIBLE);
				resurrectPlayer.sendPacket(SystemMsg.THE_GUARDIAN_TOWER_HAS_BEEN_DESTROYED_AND_RESURRECTION_IS_NOT_POSSIBLE);
				return false;
			}
		}

		return true;
	}

	@Override
	public Location getRestartLoc(Player player, RestartType type)
	{
		final SiegeClanObject attackerClan = getSiegeClan(ATTACKERS, player.getClan());
		Location loc = null;
		switch (type)
		{
		case TO_VILLAGE:
			// Если печатью владеют лорды Рассвета (Dawn), и в данном городе идет осада, то телепортирует во 2-й по счету город.
			if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DAWN)
			{
				loc = _residence.getNotOwnerRestartPoint(player);
			}
			break;
		case TO_FLAG:
			if (!getObjects(FLAG_ZONES).isEmpty() && (attackerClan != null) && (attackerClan.getFlag() != null))
			{
				loc = Location.findPointToStay(attackerClan.getFlag(), 50, 75);
			}
			else
			{
				player.sendPacket(SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
			}
			break;
		}
		return loc;
	}

	public void setNextSiegeTime(int time)
	{
		broadcastToWorld(new SystemMessage2(SystemMsg.S1_HAS_ANNOUNCED_THE_NEXT_CASTLE_SIEGE_TIME).addResidenceName(getResidence()));
		clearActions();
		getResidence().getSiegeDate().setTimeInMillis(time * 1000);
		getResidence().setJdbcState(JdbcEntityState.UPDATED);
		getResidence().update();

		registerActions();
	}
}
