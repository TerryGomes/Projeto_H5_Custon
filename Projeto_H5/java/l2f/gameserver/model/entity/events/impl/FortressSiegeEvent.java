package l2f.gameserver.model.entity.events.impl;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Future;

import l2f.commons.collections.MultiValueSet;
import l2f.commons.dao.JdbcEntityState;
import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.dao.SiegeClanDAO;
import l2f.gameserver.dao.SiegePlayerDAO;
import l2f.gameserver.data.xml.holder.EventHolder;
import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.data.xml.holder.ResidenceHolder;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.Zone;
import l2f.gameserver.model.entity.events.EventType;
import l2f.gameserver.model.entity.events.objects.DoorObject;
import l2f.gameserver.model.entity.events.objects.SiegeClanObject;
import l2f.gameserver.model.entity.events.objects.SpawnExObject;
import l2f.gameserver.model.entity.events.objects.SpawnableObject;
import l2f.gameserver.model.entity.events.objects.StaticObjectObject;
import l2f.gameserver.model.entity.residence.Castle;
import l2f.gameserver.model.entity.residence.Fortress;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.model.pledge.Privilege;
import l2f.gameserver.network.serverpackets.PlaySound;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.NpcString;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.templates.item.ItemTemplate;
import l2f.gameserver.utils.Log;
import l2f.gameserver.utils.TimeUtils;

/**
 * @author VISTALL
 * @date 15:13/14.02.2011
 * Barracks:
 * 0 - Archer Captain
 * 1 - Guard Captain
 * 2 - Support Unit Captain
 * 3 - Control Room
 * 4 - General
 */
public class FortressSiegeEvent extends SiegeEvent<Fortress, SiegeClanObject>
{
	private class EnvoyDespawn extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			despawnEnvoy();
		}
	}

	private class MerchantSpawnTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			setRegistrationOver(false);
			spawnAction(MERCHANT, true);
			_merchantSpawnTask = null;
		}
	}

	private class SpawnCommanderTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			/*
			 * SpawnExObject spawnExObject = getFirstObject(FortressSiegeEvent.SIEGE_COMMANDERS);
			 * List<Spawner> spawnerList = spawnExObject.getSpawns();
			 * for(int i = 0; i < spawnerList.size(); i++)
			 * {
			 * if(i == 3) // main machine не востанавливается
			 * continue;
			 * Spawner spawner = spawnerList.get(i);
			 * // убит кеп
			 * if(spawner.getAllSpawned().isEmpty())
			 * spawner.doSpawn(true);
			 * else
			 * {
			 * NpcInstance npc = spawner.getAllSpawned().get(0);
			 * npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
			 * }
			 * }
			 */
			// Synerge - To avoid problems of unsync with the normal respawn and task respawn, we just unspawn every commander and the spawn them again. Except for main machine
			final List<Serializable> objects = getObjects(SIEGE_COMMANDERS);
			for (int i = 0; i < objects.size(); i++)
			{
				if (i == 3) // We must keep the main machine or it will cause problems
				{
					continue;
				}

				Object object = objects.get(i);
				if (object instanceof SpawnableObject)
				{
					((SpawnableObject) object).despawnObject(FortressSiegeEvent.this);
					((SpawnableObject) object).spawnObject(FortressSiegeEvent.this);
				}
			}

			broadcastTo(SystemMsg.THE_BARRACKS_FUNCTION_HAS_BEEN_RESTORED, FortressSiegeEvent.ATTACKERS, FortressSiegeEvent.DEFENDERS);

			stopCommanderSpawnTask();
		}
	}

	public static final String FLAG_POLE = "flag_pole";
	public static final String COMBAT_FLAGS = "combat_flags";
	public static final String SIEGE_COMMANDERS = "siege_commanders";
	public static final String PEACE_COMMANDERS = "peace_commanders";
	public static final String UPGRADEABLE_DOORS = "upgradeable_doors";
	public static final String COMMANDER_DOORS = "commander_doors";
	public static final String ENTER_DOORS = "enter_doors";
	public static final String MACHINE_DOORS = "machine_doors";
	public static final String OUT_POWER_UNITS = "out_power_units";
	public static final String IN_POWER_UNITS = "in_power_units";
	public static final String GUARDS_LIVE_WITH_C_CENTER = "guards_live_with_c_center";
	public static final String ENVOY = "envoy";
	public static final String MERCENARY_POINTS = "mercenary_points";
	public static final String MERCENARY = "mercenary";
	public static final String MERCHANT = "merchant";
	public static final long SIEGE_WAIT_PERIOD = 1 * 60 * 60 * 1000L;
	public static final long COMMANDER_RESPAWN = 10 * 60 * 1000L;

	private final SpawnCommanderTask _commanderSpawnRunnable = new SpawnCommanderTask();

	private Future<?> _envoyTask;
	private Future<?> _merchantSpawnTask;
	private Future<?> _commanderSpawnTask;
	private boolean[] _barrackStatus;

	public FortressSiegeEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	@Override
	public void processStep(Clan newOwnerClan)
	{
		if (newOwnerClan.getCastle() > 0)
		{
			getResidence().changeOwner(null);
		}
		else
		{
			getResidence().changeOwner(newOwnerClan);

			stopEvent(true);
		}
	}

	@Override
	public void initEvent()
	{
		super.initEvent();

		SpawnExObject exObject = getFirstObject(SIEGE_COMMANDERS);
		_barrackStatus = new boolean[exObject.getSpawns().size()];

		int lvl = getResidence().getFacilityLevel(Fortress.DOOR_UPGRADE);
		List<DoorObject> doorObjects = getObjects(UPGRADEABLE_DOORS);
		for (DoorObject d : doorObjects)
		{
			d.setUpgradeValue(this, d.getDoor().getMaxHp() * lvl);
			d.getDoor().addListener(_doorDeathListener);
		}

		flagPoleUpdate(false);
		if (getResidence().getOwnerId() > 0)
		{
			spawnEnvoy();
		}

		spawnMerchant();
	}

	@Override
	public void startEvent()
	{
		if (!_isInProgress.compareAndSet(false, true))
		{
			return;
		}
		// принудительный старт осады
		if (_merchantSpawnTask != null)
		{
			_merchantSpawnTask.cancel(false);
			_merchantSpawnTask = null;
		}

		stopCommanderSpawnTask();

		_oldOwner = getResidence().getOwner();

		if (_oldOwner != null)
		{
			addObject(DEFENDERS, new SiegeClanObject(DEFENDERS, _oldOwner, 0));
		}
		flagPoleUpdate(true);
		updateParticles(true, ATTACKERS, DEFENDERS);

		broadcastTo(new SystemMessage2(SystemMsg.THE_FORTRESS_BATTLE_S1_HAS_BEGUN).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);

		super.startEvent();
	}

	@Override
	public void stopEvent(boolean step)
	{
		if (!_isInProgress.compareAndSet(true, false))
		{
			return;
		}
		stopCommanderSpawnTask();

		spawnAction(COMBAT_FLAGS, false);
		updateParticles(false, ATTACKERS, DEFENDERS);

		broadcastTo(new SystemMessage2(SystemMsg.THE_FORTRESS_BATTLE_OF_S1_HAS_FINISHED).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);

		Clan ownerClan = getResidence().getOwner();
		if (ownerClan != null)
		{
			if (_oldOwner != ownerClan)
			{
				ownerClan.broadcastToOnlineMembers(PlaySound.SIEGE_VICTORY);

				ownerClan.incReputation(1700, false, toString());
				broadcastTo(new SystemMessage2(SystemMsg.S1_IS_VICTORIOUS_IN_THE_FORTRESS_BATTLE_OF_S2).addString(ownerClan.getName()).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);

				getResidence().getOwnDate().setTimeInMillis(System.currentTimeMillis());

				getResidence().startCycleTask();
				spawnEnvoy();
			}
		}
		else
		{
			getResidence().getOwnDate().setTimeInMillis(0);
		}

		getResidence().getLastSiegeDate().setTimeInMillis(System.currentTimeMillis());

		List<SiegeClanObject> attackers = removeObjects(ATTACKERS);
		for (SiegeClanObject siegeClan : attackers)
		{
			siegeClan.deleteFlag();
		}

		removeObjects(DEFENDERS);
		SiegeClanDAO.getInstance().delete(getResidence());
		flagPoleUpdate(false);

		super.stopEvent(step);

		spawnMerchant();
	}

	@Override
	public synchronized void reCalcNextTime(boolean onStart)
	{
		final int attackersSize = getObjects(ATTACKERS).size();
		final Calendar startSiegeDate = getResidence().getSiegeDate();
		final Calendar lastSiegeDate = getResidence().getLastSiegeDate();
		final long currentTimeMillis = System.currentTimeMillis();

		if (startSiegeDate.getTimeInMillis() > currentTimeMillis)
		{
			if (attackersSize > 0)
			{
				if (onStart)
				{
					registerActions();
				}
				return;
			}
		}

		clearActions();

		if (attackersSize > 0)
		{
			if (currentTimeMillis - lastSiegeDate.getTimeInMillis() > SIEGE_WAIT_PERIOD)
			{
				startSiegeDate.setTimeInMillis(currentTimeMillis);
				startSiegeDate.add(Calendar.HOUR_OF_DAY, 1);
			}
			else
			{
				startSiegeDate.setTimeInMillis(lastSiegeDate.getTimeInMillis());
				startSiegeDate.add(Calendar.HOUR_OF_DAY, 5);
			}

			/*
			 * if (startSiegeDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
			 * {
			 * if (startSiegeDate.get(Calendar.HOUR_OF_DAY) >= 19 && startSiegeDate.get(Calendar.HOUR_OF_DAY) < 22)
			 * {
			 * startSiegeDate.add(Calendar.HOUR_OF_DAY, 22-startSiegeDate.get(Calendar.HOUR_OF_DAY));
			 * }
			 * }
			 */

			registerActions();
		}
		else
		{
			startSiegeDate.setTimeInMillis(0);
		}

		getResidence().setJdbcState(JdbcEntityState.UPDATED);
		getResidence().update();
	}

	@Override
	public void announce(int val)
	{
		SystemMessage2 msg;
		final int min = val / 60;
		if (min > 0)
		{
			msg = new SystemMessage2(SystemMsg.S1_MINUTES_UNTIL_THE_FORTRESS_BATTLE_STARTS).addInteger(min);
		}
		else
		{
			msg = new SystemMessage2(SystemMsg.S1_SECONDS_UNTIL_THE_FORTRESS_BATTLE_STARTS).addInteger(val);
		}

		broadcastTo(msg, ATTACKERS, DEFENDERS);
	}

	public void spawnEnvoy()
	{
		final long endTime = getResidence().getOwnDate().getTimeInMillis() + 60 * 60 * 1000L;
		final long diff = endTime - System.currentTimeMillis();
		if (diff > 0 && getResidence().getContractState() == Fortress.NOT_DECIDED)
		{
			// FIXME [VISTALL] debug
			final SpawnExObject exObject = getFirstObject(ENVOY);
			if (exObject.isSpawned())
			{
				info("Last siege: " + TimeUtils.toSimpleFormat(getResidence().getLastSiegeDate()) + ", own date: " + TimeUtils.toSimpleFormat(getResidence().getOwnDate()) + ", siege date: " + TimeUtils.toSimpleFormat(getResidence().getSiegeDate()));
			}

			spawnAction(ENVOY, true);
			_envoyTask = ThreadPoolManager.getInstance().schedule(new EnvoyDespawn(), diff);
		}
		else if (getResidence().getContractState() == Fortress.NOT_DECIDED)
		{
			getResidence().setFortState(Fortress.INDEPENDENT, 0);
			getResidence().setJdbcState(JdbcEntityState.UPDATED);
			getResidence().update();
		}
	}

	private void spawnMerchant()
	{
		// DS: если до осады осталось меньше 10 минут то не спавним мерчанта
		// функция должна вызываться после установки времени осады
		final long siegeTime = getResidence().getSiegeDate().getTimeInMillis();
		if (siegeTime > 0 && siegeTime - System.currentTimeMillis() < 600000L)
		{
			return;
		}

		if (_merchantSpawnTask != null)
		{
			_merchantSpawnTask.cancel(false);
			_merchantSpawnTask = null;
		}

		SpawnExObject object = getFirstObject(MERCHANT);
		if (object.isSpawned())
		{
			Log.debug(toString() + ": merchant already spawned.", new Exception());
			return;
		}

		long needDate = getResidence().getLastSiegeDate().getTimeInMillis() + SIEGE_WAIT_PERIOD;
		long diff = needDate - System.currentTimeMillis();
		if (diff > 0)
		{
			_merchantSpawnTask = ThreadPoolManager.getInstance().schedule(new MerchantSpawnTask(), diff);
		}
		else
		{
			setRegistrationOver(false);
			spawnAction(MERCHANT, true);
		}
	}

	public void despawnEnvoy()
	{
		_envoyTask.cancel(false);
		_envoyTask = null;

		spawnAction(ENVOY, false);
		if (getResidence().getContractState() == Fortress.NOT_DECIDED)
		{
			getResidence().setFortState(Fortress.INDEPENDENT, 0);
			getResidence().setJdbcState(JdbcEntityState.UPDATED);
			getResidence().update();
		}
	}

	public void flagPoleUpdate(boolean dis)
	{
		final StaticObjectObject object = getFirstObject(FLAG_POLE);
		if (object != null)
		{
			object.setMeshIndex(dis ? 0 : getResidence().getOwner() != null ? 1 : 0);
		}
	}

	public synchronized void barrackAction(int id, boolean val)
	{
		_barrackStatus[id] = val;
	}

	public synchronized void checkBarracks()
	{
		if (_commanderSpawnTask == null)
		{
			startCommanderSpawnTask();
		}

		boolean allDead = true;
		for (boolean b : getBarrackStatus())
		{
			if (!b)
			{
				allDead = false;
			}
		}

		if (allDead)
		{
			if (_oldOwner != null)
			{
				// TODO: Infern0 if there is bug with taking for while defender kill all guards, disable this...
				final SpawnExObject spawn = getFirstObject(FortressSiegeEvent.MERCENARY);
				final NpcInstance npc = spawn.getFirstSpawned();
				if (npc == null || npc.isDead())
				{
					return;
				}

				Functions.npcShout(npc, NpcString.THE_COMMAND_GATE_HAS_OPENED_CAPTURE_THE_FLAG_QUICKLY_AND_RAISE_IT_HIGH_TO_PROCLAIM_OUR_VICTORY);

				spawnFlags();
			}
			else
			{
				spawnFlags();
			}
		}
	}

	public void spawnFlags()
	{
		doorAction(FortressSiegeEvent.COMMANDER_DOORS, true);
		spawnAction(FortressSiegeEvent.SIEGE_COMMANDERS, false);
		spawnAction(FortressSiegeEvent.COMBAT_FLAGS, true);

		if (_oldOwner != null)
		{
			spawnAction(FortressSiegeEvent.MERCENARY, false);
		}

		spawnAction(FortressSiegeEvent.GUARDS_LIVE_WITH_C_CENTER, false);

		broadcastTo(SystemMsg.ALL_BARRACKS_ARE_OCCUPIED, ATTACKERS, DEFENDERS);

		stopCommanderSpawnTask();
	}

	@Override
	public boolean ifVar(String name)
	{
		if (name.equals(OWNER))
		{
			return getResidence().getOwner() != null;
		}
		if (name.equals(OLD_OWNER))
		{
			return _oldOwner != null;
		}
		if (name.equalsIgnoreCase("reinforce_1"))
		{
			return getResidence().getFacilityLevel(Fortress.REINFORCE) == 1;
		}
		if (name.equalsIgnoreCase("reinforce_2"))
		{
			return getResidence().getFacilityLevel(Fortress.REINFORCE) == 2;
		}
		if (name.equalsIgnoreCase("dwarvens"))
		{
			return getResidence().getFacilityLevel(Fortress.DWARVENS) == 1;
		}
		return false;
	}

	public boolean[] getBarrackStatus()
	{
		return _barrackStatus;
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
			if (attacker.isPlayer() && target.isPlayer() && attacker.getPlayer().getClan() != null && attacker.getPlayer().getClan().getAlliance() != null && target.getPlayer().getClan() != null && target.getPlayer().getClan().getAlliance() != null && attacker.getPlayer().getClan().getAlliance() == target.getPlayer().getClan().getAlliance())
			{
				return SystemMsg.INVALID_TARGET;
			}
			final Player targetPlayer = target.getPlayer();
			final Player attackerPlayer = attacker.getPlayer();
			if (attackerPlayer == null)
			{
				return SystemMsg.INVALID_TARGET;
			}
			final FortressSiegeEvent siegeEvent = target.getEvent(FortressSiegeEvent.class);
			final FortressSiegeEvent siegeEventatt = attacker.getEvent(FortressSiegeEvent.class);
			if ((siegeEvent == null) || (siegeEventatt == null) || !target.isPlayer() || !targetPlayer.isOnSiegeField())
			{
				return null;
			}
			if ((targetPlayer.getClan() == null) || (attacker.getClan() == null) || (siegeEvent != this))
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
			if (attacker.isPlayer() && target.isPlayer() && attacker.getPlayer().getPlayerGroup() == target.getPlayer().getPlayerGroup()) // Party and CommandChannel check.
			{
				return null;
			}
			if (attacker.isPlayer() && target.isPlayer() && attacker.getPlayer().getClan() != null && target.getPlayer().getClan() != null && attacker.getPlayer().getClan() == target.getPlayer().getClan())
			{
				return null;
			}
			if (attacker.isPlayer() && target.isPlayer() && attacker.getPlayer().getClan() != null && attacker.getPlayer().getClan().getAlliance() != null && target.getPlayer().getClan() != null && target.getPlayer().getClan().getAlliance() != null && attacker.getPlayer().getClan().getAlliance() == target.getPlayer().getAlliance())
			{
				return null;
			}

			final Player targetPlayer = target.getPlayer();
			final Player attackerPlayer = attacker.getPlayer();
			if (targetPlayer == null)
			{
				attacker.sendPacket(new SystemMessage2(SystemMsg.INVALID_TARGET));
				return null;
			}
			final FortressSiegeEvent siegeEvent = target.getEvent(FortressSiegeEvent.class);
			final FortressSiegeEvent siegeEventatt = attacker.getEvent(FortressSiegeEvent.class);
			// TODO: check this..
			// if(targetPlayer != null && attackerPlayer != null && attacker.isPlayer() && !targetPlayer.isOnSiegeField() && !attackerPlayer.isOnSiegeField())
			// return true;
			if ((siegeEvent == null) || (siegeEventatt == null) || !target.isPlayer() || !targetPlayer.isOnSiegeField())
			{
				return null;
			}
			if ((targetPlayer.getClan() == null) || (attacker.getClan() == null) || (siegeEvent != this))
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
					attacker.sendPacket(new SystemMessage2(SystemMsg.INVALID_TARGET));
					return false;
				}
				if (targetPlayer.getClan() != attackerPlayer.getClan())
				{
					if (attackerPlayer.getAlliance() != null && attackerPlayer.getAlliance() == targetPlayer.getAlliance())
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
		final boolean playerInZone = resurrectPlayer.isInZone(Zone.ZoneType.SIEGE);
		final boolean targetInZone = target.isInZone(Zone.ZoneType.SIEGE);
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
		final FortressSiegeEvent siegeEvent = target.getEvent(FortressSiegeEvent.class);
		if (siegeEvent != this)
		{
			if (force)
			{
				targetPlayer.sendPacket(SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
			}
			resurrectPlayer.sendPacket(force ? SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE : SystemMsg.INVALID_TARGET);
			return false;
		}

		SiegeClanObject targetSiegeClan = siegeEvent.getSiegeClan(ATTACKERS, targetPlayer.getClan());
		// если нету флага - рес запрещен
		if (targetSiegeClan == null || targetSiegeClan.getFlag() == null)
		{
			if (force)
			{
				targetPlayer.sendPacket(SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
			}
			resurrectPlayer.sendPacket(force ? SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE : SystemMsg.INVALID_TARGET);
			return false;
		}

		if (force)
		{
			return true;
		}
		else
		{
			resurrectPlayer.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}
	}

	public void startCommanderSpawnTask()
	{
		_commanderSpawnTask = ThreadPoolManager.getInstance().schedule(_commanderSpawnRunnable, COMMANDER_RESPAWN);
	}

	public void stopCommanderSpawnTask()
	{
		if (_commanderSpawnTask != null)
		{
			_commanderSpawnTask.cancel(false);
			_commanderSpawnTask = null;
		}
	}

	@Override
	public void setRegistrationOver(boolean b)
	{
		super.setRegistrationOver(b);
		if (b)
		{
			getResidence().getLastSiegeDate().setTimeInMillis(getResidence().getSiegeDate().getTimeInMillis());
			getResidence().setJdbcState(JdbcEntityState.UPDATED);
			getResidence().update();

			if (getResidence().getOwner() != null)
			{
				getResidence().getOwner().broadcastToOnlineMembers(SystemMsg.ENEMY_BLOOD_PLEDGES_HAVE_INTRUDED_INTO_THE_FORTRESS);
			}
		}
	}

	public static boolean hasSignClanPrivilege(Player player)
	{
		return player.getClan() != null && (player.getClan().getLeaderId() == player.getObjectId() || player.hasPrivilege(Privilege.CS_FS_SIEGE_WAR));
	}

	public boolean tryToRegisterClan(Player player, boolean sendMessage, boolean sendHtml, NpcInstance npc, boolean signOnSuccess)
	{
		final Clan clan = player.getClan();
		if (clan == null)
		{
			if (sendHtml)
			{
				npc.showChatWindow(player, "residence2/fortress/fortress_ordery002.htm");
			}
			if (sendMessage)
			{
				player.sendMessage("You need to have a Clan first!");
			}
			return false;
		}
		if (clan.getHasFortress() == getResidence().getId())
		{
			if (sendHtml)
			{
				npc.showChatWindow(player, "residence2/fortress/fortress_ordery014.htm", "%clan_name%", clan.getName());
			}
			if (sendMessage)
			{
				player.sendMessage("You cannot sign for your own Fortress!");
			}
			return false;
		}
		if (!player.hasPrivilege(Privilege.CS_FS_SIEGE_WAR))
		{
			if (sendHtml)
			{
				npc.showChatWindow(player, "residence2/fortress/fortress_ordery012.htm");
			}
			if (sendMessage)
			{
				player.sendMessage("You don't have a privilege to register for a Siege!");
			}
			return false;
		}
		if (clan.getCastle() > 0)
		{
			Castle relatedCastle = null;
			for (Castle castle : getResidence().getRelatedCastles())
			{
				if (castle.getId() == clan.getCastle())
				{
					relatedCastle = castle;
				}
			}
			if (relatedCastle == null)
			{
				if (sendHtml)
				{
					npc.showChatWindow(player, "residence2/fortress/fortress_ordery021.htm");
				}
				if (sendMessage)
				{
					player.sendMessage("You cannot register to the Siege while having Castle in different region!");
				}
				return false;
			}
			if (getResidence().getContractState() == 2)
			{
				if (sendHtml)
				{
					npc.showChatWindow(player, "residence2/fortress/fortress_ordery022.htm");
				}
				if (sendMessage)
				{
					player.sendMessage("You cannot attack the Fortress that has contract with the Castle!");
				}
				return false;
			}
			if (relatedCastle.getSiegeEvent().isRegistrationOver())
			{
				if (sendHtml)
				{
					npc.showChatWindow(player, "residence2/fortress/fortress_ordery022.htm");
				}
				if (sendMessage)
				{
					player.sendMessage("It's too late to sign for this Fortress Siege!");
				}
				return false;
			}
		}
		if (System.currentTimeMillis() - _residence.getLastSiegeDate().getTimeInMillis() < SIEGE_WAIT_PERIOD)
		{
			if (sendMessage)
			{
				player.sendMessage("This Fortress cannot be attacked so often!");
			}
			return false;
		}
		if (getSiegeClan("attackers", clan) != null)
		{
			if (sendHtml)
			{
				npc.showChatWindow(player, "residence2/fortress/fortress_ordery007.htm");
			}
			if (sendMessage)
			{
				player.sendMessage("You are already registered for the Siege!");
			}
			return false;
		}
		for (Fortress anyFort : ResidenceHolder.getInstance().getResidenceList(Fortress.class))
		{
			if (anyFort.getSiegeEvent().getSiegeClan("attackers", clan) != null)
			{
				if (sendHtml)
				{
					npc.showChatWindow(player, "residence2/fortress/fortress_ordery006.htm");
				}
				if (sendMessage)
				{
					player.sendMessage("You are already registered for the different Fortress Siege!");
				}
				return false;
			}
		}
		if (clan.getHasFortress() > 0 && getResidence().getSiegeDate().getTimeInMillis() > 0L)
		{
			if (sendHtml)
			{
				npc.showChatWindow(player, "residence2/fortress/fortress_ordery006.htm");
			}
			if (sendMessage)
			{
				player.sendMessage("Sorry but you can register only for 1 battle at the time!");
			}
			return false;
		}
		final DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
		if (runnerEvent.isRegistrationOver())
		{
			if (sendHtml)
			{
				npc.showChatWindow(player, "residence2/fortress/fortress_ordery006.htm");
			}
			if (sendMessage)
			{
				player.sendMessage("You may not register to the Siege while registration for Territory War is over!");
			}
			return false;
		}
		if (Config.ALLOW_START_FORTRESS_SIEGE_FEE && getObjects("attackers").isEmpty() && getObjects("attacker_players").isEmpty())
		{
			final ItemTemplate priceTemplate = ItemHolder.getInstance().getTemplate(Config.START_FORTRESS_SIEGE_PRICE_ID);
			if (signOnSuccess)
			{
				if (!player.consumeItem(priceTemplate.getItemId(), Config.START_FORTRESS_SIEGE_PRICE_AMOUNT))
				{
					if (sendHtml)
					{
						npc.showChatWindow(player, "residence2/fortress/fortress_ordery003.htm");
					}
					if (sendMessage)
					{
						player.sendMessage("You don't have enough " + priceTemplate.getName() + " to start the Siege!");
					}
					return false;
				}
			}
			else if (player.getInventory().getCountOf(priceTemplate.getItemId()) < Config.START_FORTRESS_SIEGE_PRICE_AMOUNT)
			{
				if (sendHtml)
				{
					npc.showChatWindow(player, "residence2/fortress/fortress_ordery003.htm");
				}
				if (sendMessage)
				{
					player.sendMessage("You don't have enough " + priceTemplate.getName() + " to start the Siege!");
				}
				return false;
			}
		}
		if (signOnSuccess)
		{
			final SiegeClanObject siegeClan = new SiegeClanObject("attackers", clan, 0L);
			addObject("attackers", siegeClan);
			SiegeClanDAO.getInstance().insert(getResidence(), siegeClan);
			reCalcNextTime(false);
			if (sendHtml)
			{
				npc.showChatWindow(player, "residence2/fortress/fortress_ordery005.htm");
			}
			player.sendMessage("Your clan has been registered for " + getResidence().getName() + " Siege!");
			final List<Integer> singleAttackers = getObjects("attacker_players");
			for (Integer singlePlayerId : singleAttackers)
			{
				if (clan.isAnyMember(singlePlayerId))
				{
					singleAttackers.remove(singlePlayerId);
				}
			}
		}
		return true;
	}

	public boolean tryToCancelRegisterClan(Player player, boolean sendMessage, boolean unsignOnSuccess)
	{
		return this.tryToCancelRegisterClan(player, sendMessage, false, null, unsignOnSuccess);
	}

	public boolean tryToCancelRegisterClan(Player player, boolean sendMessage, boolean sendHtml, NpcInstance npc, boolean unsignOnSuccess)
	{
		final Clan clan = player.getClan();
		if (clan == null || !player.hasPrivilege(Privilege.CS_FS_SIEGE_WAR))
		{
			if (sendHtml)
			{
				npc.showChatWindow(player, "residence2/fortress/fortress_ordery010.htm");
			}
			if (sendMessage)
			{
				player.sendMessage("You don't have privilege to cancel registration!");
			}
			return false;
		}
		final SiegeClanObject siegeClan = getSiegeClan("attackers", clan);
		if (siegeClan != null)
		{
			if (unsignOnSuccess)
			{
				removeObject("attackers", siegeClan);
				SiegeClanDAO.getInstance().delete(getResidence(), siegeClan);
				reCalcNextTime(false);
			}
			if (sendHtml)
			{
				npc.showChatWindow(player, "residence2/fortress/fortress_ordery009.htm");
			}
			if (sendMessage)
			{
				player.sendMessage("You have been successfully signed off!");
			}
			return true;
		}
		if (sendHtml)
		{
			npc.showChatWindow(player, "residence2/fortress/fortress_ordery011.htm");
		}
		if (sendMessage)
		{
			player.sendMessage("You cannot cancel the registration because you are not signed yet!");
		}
		return false;
	}

	public boolean tryToRegisterSingle(Player player, boolean sendMessage, boolean signOnSuccess)
	{
		if (!Config.FORTRESS_SIEGE_ALLOW_SINGLE_PLAYERS)
		{
			if (sendMessage)
			{
				player.sendMessage("Single Players cannot register for Fortress Sieges!");
			}
			return false;
		}
		final Clan clan = player.getClan();
		if (clan != null)
		{
			if (getSiegeClan("attackers", clan) != null)
			{
				if (sendMessage)
				{
					player.sendMessage("Your Clan is already registered for the Siege!");
				}
				return false;
			}
			for (Fortress anyFort : ResidenceHolder.getInstance().getResidenceList(Fortress.class))
			{
				if (anyFort.getSiegeEvent().getSiegeClan("attackers", clan) != null)
				{
					if (sendMessage)
					{
						player.sendMessage("Your clan is already registered for the different Fortress Siege!");
					}
					return false;
				}
			}
			if (clan.getHasFortress() > 0 && getResidence().getSiegeDate().getTimeInMillis() > 0L)
			{
				if (sendMessage)
				{
					player.sendMessage("Sorry but you can register only for 1 battle at the time!");
				}
				return false;
			}
			if (clan.getHasFortress() == getResidence().getId())
			{
				if (sendMessage)
				{
					player.sendMessage("You cannot sign for your own Fortress!");
				}
				return false;
			}
			if (clan.getCastle() > 0)
			{
				Castle relatedCastle = null;
				for (Castle castle : getResidence().getRelatedCastles())
				{
					if (castle.getId() == clan.getCastle())
					{
						relatedCastle = castle;
					}
				}
				if (relatedCastle == null)
				{
					if (sendMessage)
					{
						player.sendMessage("You cannot register to the Siege while having Castle in different region!");
					}
					return false;
				}
				if (getResidence().getContractState() == 2)
				{
					if (sendMessage)
					{
						player.sendMessage("You cannot attack the Fortress that has contract with the Castle!");
					}
					return false;
				}
				if (relatedCastle.getSiegeEvent().isRegistrationOver())
				{
					if (sendMessage)
					{
						player.sendMessage("It's too late to sign for this Fortress Siege!");
					}
					return false;
				}
			}
		}
		for (Fortress anyFort : ResidenceHolder.getInstance().getResidenceList(Fortress.class))
		{
			if (anyFort.getSiegeEvent().getObjects("attacker_players").contains(player.getObjectId()))
			{
				if (sendMessage)
				{
					player.sendMessage("You are already registered for the Siege!");
				}
				return false;
			}
		}

		if (System.currentTimeMillis() - _residence.getLastSiegeDate().getTimeInMillis() < SIEGE_WAIT_PERIOD)
		{
			if (sendMessage)
			{
				player.sendMessage("This Fortress cannot be attacked so often!");
			}
			return false;
		}

		final DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
		if (runnerEvent.isRegistrationOver())
		{
			if (sendMessage)
			{
				player.sendMessage("You may not register to the Siege while registration for Territory War is over!");
			}
			return false;
		}
		if (Config.ALLOW_START_FORTRESS_SIEGE_FEE && getObjects("attackers").isEmpty() && getObjects("attacker_players").isEmpty())
		{
			final ItemTemplate priceTemplate = ItemHolder.getInstance().getTemplate(Config.START_FORTRESS_SIEGE_PRICE_ID);
			if (signOnSuccess)
			{
				if (!player.consumeItem(priceTemplate.getItemId(), Config.START_FORTRESS_SIEGE_PRICE_AMOUNT))
				{
					if (sendMessage)
					{
						player.sendMessage("You don't have enough " + priceTemplate.getName() + " to start the Siege!");
					}
					return false;
				}
			}
			else if (player.getInventory().getCountOf(priceTemplate.getItemId()) < Config.START_FORTRESS_SIEGE_PRICE_AMOUNT)
			{
				if (sendMessage)
				{
					player.sendMessage("You don't have enough " + priceTemplate.getName() + " to start the Siege!");
				}
				return false;
			}
		}
		if (signOnSuccess)
		{
			addObject("attacker_players", player.getObjectId());
			SiegePlayerDAO.insert(getResidence(), 0, player.getObjectId());
			reCalcNextTime(false);
			player.sendMessage("You have been has been registered for " + getResidence().getName() + " Siege as Single Player!");
		}
		return true;
	}

	public boolean tryToCancelRegisterSingle(Player player, boolean sendMessage, boolean unsignOnSuccess)
	{
		final boolean registered = getObjects("attacker_players").contains(player.getObjectId());
		if (registered)
		{
			if (unsignOnSuccess)
			{
				removeObject("attacker_players", player.getObjectId());
				SiegePlayerDAO.delete(getResidence(), 0, player.getObjectId());
				reCalcNextTime(false);
			}
			if (sendMessage)
			{
				player.sendMessage("You have been successfully signed off!");
			}
			return true;
		}
		if (sendMessage)
		{
			player.sendMessage("You cannot cancel the registration because you are not signed yet!");
		}
		return false;
	}
}
