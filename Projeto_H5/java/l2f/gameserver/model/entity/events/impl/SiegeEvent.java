package l2f.gameserver.model.entity.events.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import l2f.commons.collections.MultiValueSet;
import l2f.commons.dao.JdbcEntityState;
import l2f.commons.lang.reference.HardReference;
import l2f.commons.util.Rnd;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.dao.SiegeClanDAO;
import l2f.gameserver.data.xml.holder.ResidenceHolder;
import l2f.gameserver.instancemanager.ReflectionManager;
import l2f.gameserver.listener.actor.OnDeathListener;
import l2f.gameserver.listener.actor.OnKillListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.GameObject;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.base.RestartType;
import l2f.gameserver.model.entity.SevenSigns;
import l2f.gameserver.model.entity.events.GlobalEvent;
import l2f.gameserver.model.entity.events.objects.SiegeClanObject;
import l2f.gameserver.model.entity.events.objects.ZoneObject;
import l2f.gameserver.model.entity.residence.Residence;
import l2f.gameserver.model.instances.DoorInstance;
import l2f.gameserver.model.instances.SummonInstance;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.network.serverpackets.ExPVPMatchCCRetire;
import l2f.gameserver.network.serverpackets.L2GameServerPacket;
import l2f.gameserver.network.serverpackets.RelationChanged;
import l2f.gameserver.network.serverpackets.ShowSiegeKillResults;
import l2f.gameserver.network.serverpackets.components.IStaticPacket;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.tables.ClanTable;
import l2f.gameserver.templates.DoorTemplate;
import l2f.gameserver.utils.Location;
import l2f.gameserver.utils.TimeUtils;

public abstract class SiegeEvent<R extends Residence, S extends SiegeClanObject> extends GlobalEvent
{
	public class DoorDeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature actor, Creature killer)
		{
			if (!isInProgress())
			{
				return;
			}

			DoorInstance door = (DoorInstance) actor;
			if (door.getDoorType() == DoorTemplate.DoorType.WALL)
			{
				return;
			}

			broadcastTo(SystemMsg.THE_CASTLE_GATE_HAS_BEEN_DESTROYED, SiegeEvent.ATTACKERS, SiegeEvent.DEFENDERS);
		}
	}

	public class KillListener implements OnKillListener
	{
		@Override
		public void onKill(Creature actor, Creature victim)
		{
			Player winner = actor.getPlayer();

			if (winner == null || !victim.isPlayer() || winner.getLevel() < 40 || winner == victim || victim.getEvent(SiegeEvent.this.getClass()) != SiegeEvent.this || !checkIfInZone(actor)
						|| !checkIfInZone(victim))
			{
				return;
			}

			Player killed = victim.getPlayer();
			if ((killed == null) || (killed.getVar("DisabledSiegeFame") != null))
			{
				return;
			}

			killed.setVar("DisabledSiegeFame", "true", System.currentTimeMillis() + 300000L);

			if ((winner.getPlayerGroup() == killed.getPlayerGroup()) || winner.isDualbox(killed) || winner.isInSameClan(killed))
			{
				return;
			}
			if (winner.getParty() == null)
			{
				winner.setFame(winner.getFame() + Rnd.get(10, 20), SiegeEvent.this.toString());
			}
			else
			{
				for (Player member : winner.getParty().getMembers())
				{
					member.setFame(member.getFame() + Rnd.get(10, 20), SiegeEvent.this.toString());
				}
			}

			if (SiegeEvent.this instanceof CastleSiegeEvent)
			{
				winner.getCounters().playersKilledInSiege++;
			}
			if (SiegeEvent.this instanceof DominionSiegeEvent)
			{
				winner.getCounters().playersKilledInDominion++;
			}
		}

		@Override
		public boolean ignorePetOrSummon()
		{
			return true;
		}
	}

	public static final String OWNER = "owner";
	public static final String OLD_OWNER = "old_owner";

	public static final String ATTACKERS = "attackers";
	public static final String DEFENDERS = "defenders";
	public static final String SPECTATORS = "spectators";

	public static final String SIEGE_ZONES = "siege_zones";
	public static final String FLAG_ZONES = "flag_zones";

	public static final String DAY_OF_WEEK = "day_of_week";
	public static final String HOUR_OF_DAY = "hour_of_day";

	public static final String REGISTRATION = "registration";

	public static final String DOORS = "doors";

	protected R _residence;
	protected AtomicBoolean _isInProgress = new AtomicBoolean(false);
	private boolean _isRegistrationOver;

	protected int _dayOfWeek;
	protected int _hourOfDay;

	protected Clan _oldOwner;
	protected ScheduledFuture<?> _siegeStartTask;
	protected OnKillListener _killListener = new KillListener();
	protected OnDeathListener _doorDeathListener = new DoorDeathListener();
	protected List<HardReference<SummonInstance>> _siegeSummons = new ArrayList<HardReference<SummonInstance>>();

	public SiegeEvent(MultiValueSet<String> set)
	{
		super(set);
		_dayOfWeek = set.getInteger(DAY_OF_WEEK, 0);
		_hourOfDay = set.getInteger(HOUR_OF_DAY, 0);
	}

	// ========================================================================================================================================================================
	// Start / Stop Siege
	// ========================================================================================================================================================================

	@Override
	public void startEvent()
	{
		setInProgress(true);

		super.startEvent();
	}

	@Override
	public final void stopEvent()
	{
		stopEvent(false);
	}

	public void stopEvent(boolean step)
	{
		despawnSiegeSummons();
		setInProgress(false);
		reCalcNextTime(false);

		super.stopEvent();
	}

	public void processStep(Clan clan)
	{
		//
	}

	@Override
	public void reCalcNextTime(boolean isServerStarted)
	{
		clearActions();

		final Calendar startSiegeDate = getResidence().getSiegeDate();
		if (isServerStarted)
		{
			if (startSiegeDate.getTimeInMillis() <= System.currentTimeMillis())
			{
				startSiegeDate.set(Calendar.DAY_OF_WEEK, _dayOfWeek);
				startSiegeDate.set(Calendar.HOUR_OF_DAY, _hourOfDay);

				validateSiegeDate(startSiegeDate, 1);
				getResidence().setJdbcState(JdbcEntityState.UPDATED);
			}
		}
		else
		{
			startSiegeDate.add(Calendar.WEEK_OF_YEAR, 1);
			getResidence().setJdbcState(JdbcEntityState.UPDATED);
		}

		registerActions();

		getResidence().update();
	}

	protected void validateSiegeDate(Calendar calendar, int add)
	{
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		while (calendar.getTimeInMillis() < System.currentTimeMillis())
		{
			calendar.add(Calendar.WEEK_OF_YEAR, add);
		}
	}

	@Override
	protected long startTimeMillis()
	{
		return getResidence().getSiegeDate().getTimeInMillis();
	}

	// ========================================================================================================================================================================
	// Zones
	// ========================================================================================================================================================================

	@Override
	public void teleportPlayers(String t)
	{
		List<Player> players = new ArrayList<Player>();
		final Clan ownerClan = getResidence().getOwner();
		if (t.equalsIgnoreCase(OWNER))
		{
			if (ownerClan != null)
			{
				for (Player player : getPlayersInZone())
				{
					if (player.getClan() == ownerClan)
					{
						players.add(player);
					}
				}
			}
		}
		else if (t.equalsIgnoreCase(ATTACKERS))
		{
			for (Player player : getPlayersInZone())
			{
				final S siegeClan = getSiegeClan(ATTACKERS, player.getClan());
				if (siegeClan != null && siegeClan.isParticle(player))
				{
					players.add(player);
				}
			}
		}
		else if (t.equalsIgnoreCase(DEFENDERS))
		{
			for (Player player : getPlayersInZone())
			{
				if (ownerClan != null && player.getClan() != null && player.getClan() == ownerClan)
				{
					continue;
				}

				S siegeClan = getSiegeClan(DEFENDERS, player.getClan());
				if (siegeClan != null && siegeClan.isParticle(player))
				{
					players.add(player);
				}
			}
		}
		else if (t.equalsIgnoreCase(SPECTATORS))
		{
			for (Player player : getPlayersInZone())
			{
				if (ownerClan != null && player.getClan() != null && player.getClan() == ownerClan)
				{
					continue;
				}

				if (player.getClan() == null || getSiegeClan(ATTACKERS, player.getClan()) == null && getSiegeClan(DEFENDERS, player.getClan()) == null)
				{
					players.add(player);
				}
			}
		}
		else
		{
			players = getPlayersInZone();
		}

		for (Player player : players)
		{
			Location loc;
			if (t.equalsIgnoreCase(OWNER) || t.equalsIgnoreCase(DEFENDERS))
			{
				loc = getResidence().getOwnerRestartPoint();
			}
			else
			{
				loc = getResidence().getNotOwnerRestartPoint(player);
			}

			player.teleToLocation(loc, ReflectionManager.DEFAULT);
		}
	}

	public List<Player> getPlayersInZone()
	{
		final List<ZoneObject> zones = getObjects(SIEGE_ZONES);
		final List<Player> result = new ArrayList<Player>();
		for (ZoneObject zone : zones)
		{
			result.addAll(zone.getInsidePlayers());
		}
		return result;
	}

	public void broadcastInZone(L2GameServerPacket... packet)
	{
		for (Player player : getPlayersInZone())
		{
			player.sendPacket(packet);
		}
	}

	public void broadcastInZone(IStaticPacket... packet)
	{
		for (Player player : getPlayersInZone())
		{
			player.sendPacket(packet);
		}
	}

	public boolean checkIfInZone(Creature character)
	{
		final List<ZoneObject> zones = getObjects(SIEGE_ZONES);
		for (ZoneObject zone : zones)
		{
			if (zone.checkIfInZone(character))
			{
				return true;
			}
		}
		return false;
	}

	public void broadcastInZone2(IStaticPacket... packet)
	{
		for (Player player : getResidence().getZone().getInsidePlayers())
		{
			player.sendPacket(packet);
		}
	}

	public void broadcastInZone2(L2GameServerPacket... packet)
	{
		for (Player player : getResidence().getZone().getInsidePlayers())
		{
			player.sendPacket(packet);
		}
	}

	// ========================================================================================================================================================================
	// Siege Clans
	// ========================================================================================================================================================================
	public void loadSiegeClans()
	{
		addObjects(ATTACKERS, SiegeClanDAO.getInstance().load(getResidence(), ATTACKERS));
		addObjects(DEFENDERS, SiegeClanDAO.getInstance().load(getResidence(), DEFENDERS));
	}

	@SuppressWarnings("unchecked")
	public S newSiegeClan(String type, int clanId, long param, long date)
	{
		final Clan clan = ClanTable.getInstance().getClan(clanId);
		return clan == null ? null : (S) new SiegeClanObject(type, clan, param, date);
	}

	public void updateParticles(boolean start, String... arg)
	{
		for (String a : arg)
		{
			final List<SiegeClanObject> siegeClans = getObjects(a);
			for (SiegeClanObject s : siegeClans)
			{
				s.setEvent(start, this);
			}
		}
	}

	public S getSiegeClan(String name, Clan clan)
	{
		if (clan == null)
		{
			return null;
		}
		return getSiegeClan(name, clan.getClanId());
	}

	@SuppressWarnings("unchecked")
	public S getSiegeClan(String name, int objectId)
	{
		final List<SiegeClanObject> siegeClanList = getObjects(name);
		if (siegeClanList.isEmpty())
		{
			return null;
		}
		for (SiegeClanObject siegeClan : siegeClanList)
		{
			if (siegeClan.getObjectId() == objectId)
			{
				return (S) siegeClan;
			}
		}
		return null;
	}

	public void broadcastTo(IStaticPacket packet, String... types)
	{
		for (String type : types)
		{
			final List<SiegeClanObject> siegeClans = getObjects(type);
			for (SiegeClanObject siegeClan : siegeClans)
			{
				siegeClan.broadcast(packet);
			}
		}
	}

	public void broadcastTo(L2GameServerPacket packet, String... types)
	{
		for (String type : types)
		{
			final List<SiegeClanObject> siegeClans = getObjects(type);
			for (SiegeClanObject siegeClan : siegeClans)
			{
				siegeClan.broadcast(packet);
			}
		}
	}

	// ========================================================================================================================================================================
	// Override GlobalEvent
	// ========================================================================================================================================================================

	@Override
	@SuppressWarnings("unchecked")
	public void initEvent()
	{
		_residence = (R) ResidenceHolder.getInstance().getResidence(getId());

		loadSiegeClans();

		clearActions();

		super.initEvent();
	}

	@Override
	protected void printInfo()
	{
		final long startSiegeMillis = startTimeMillis();

		if (startSiegeMillis == 0)
		{
			info(getName() + " time - undefined");
		}
		else
		{
			info(getName() + " time - " + TimeUtils.toSimpleFormat(startSiegeMillis));
		}
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

		return false;
	}

	@Override
	public boolean isParticle(Player player)
	{
		if (!isInProgress() || player.getClan() == null)
		{
			return false;
		}
		return getSiegeClan(ATTACKERS, player.getClan()) != null || getSiegeClan(DEFENDERS, player.getClan()) != null;
	}

	@Override
	public void checkRestartLocs(Player player, Map<RestartType, Boolean> r)
	{
		if (getObjects(FLAG_ZONES).isEmpty())
		{
			return;
		}

		S clan = getSiegeClan(ATTACKERS, player.getClan());
		if (clan != null)
		{
			if (clan.getFlag() != null)
			{
				r.put(RestartType.TO_FLAG, Boolean.TRUE);
			}
		}
	}

	@Override
	public Location getRestartLoc(Player player, RestartType type)
	{
		final S attackerClan = getSiegeClan(ATTACKERS, player.getClan());
		Location loc = null;
		switch (type)
		{
		case TO_FLAG:
			if (!getObjects(FLAG_ZONES).isEmpty() && attackerClan != null && attackerClan.getFlag() != null)
			{
				loc = Location.findPointToStay(attackerClan.getFlag(), 50, 75);
			}
			else
			{
				player.sendPacket(SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
			}
			break;
		case TO_VILLAGE:
			// If the Lords of Dawn's own seal (Dawn), and in the siege of the city is, then teleport in the 2nd in a row the city.
			if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DAWN)
			{
				loc = _residence.getNotOwnerRestartPoint(player);
			}
			break;
		}

		return loc;
	}

	@Override
	public int getRelation(Player thisPlayer, Player targetPlayer, int result)
	{
		final Clan clan1 = thisPlayer.getClan();
		final Clan clan2 = targetPlayer.getClan();
		if (clan1 == null || clan2 == null)
		{
			return result;
		}

		SiegeEvent<?, ?> siegeEvent2 = targetPlayer.getEvent(SiegeEvent.class);
		if (this == siegeEvent2)
		{
			result |= RelationChanged.RELATION_INSIEGE;
			final SiegeClanObject siegeClan1 = getSiegeClan(SiegeEvent.ATTACKERS, clan1);
			final SiegeClanObject siegeClan2 = getSiegeClan(SiegeEvent.ATTACKERS, clan2);
			if (siegeClan1 == null && siegeClan2 == null || siegeClan1 != null && siegeClan2 != null && isAttackersInAlly())
			{
				result |= RelationChanged.RELATION_ALLY;
			}
			else
			{
				result |= RelationChanged.RELATION_ENEMY;
			}
			if (siegeClan1 != null)
			{
				result |= RelationChanged.RELATION_ATTACKER;
			}
		}

		return result;
	}

	@Override
	public int getUserRelation(Player thisPlayer, int oldRelation)
	{
		oldRelation |= RelationChanged.USER_RELATION_IN_SIEGE;
		final SiegeClanObject siegeClan = getSiegeClan(SiegeEvent.ATTACKERS, thisPlayer.getClan());
		if (siegeClan != null)
		{
			oldRelation |= RelationChanged.USER_RELATION_ATTACKER;
		}
		return oldRelation;
	}

	@Override
	public Boolean canAttack(Creature target, Creature attacker, Skill skill, boolean force)
	{
		return null;
	}

	@Override
	public SystemMsg checkForAttack(Creature target, Creature attacker, Skill skill, boolean force)
	{
		final SiegeEvent siegeEvent = target.getEvent(SiegeEvent.class);
		// или вообще не учасник, или учасники разных осад
		if ((this != siegeEvent) || !checkIfInZone(target) || !checkIfInZone(attacker))
		{
			return null;
		}
		final Player player = target.getPlayer();
		if (player == null)
		{
			return null;
		}
		final SiegeClanObject siegeClan1 = getSiegeClan(SiegeEvent.ATTACKERS, player.getClan());
		if (siegeClan1 == null && attacker.isSiegeGuard())
		{
			return SystemMsg.INVALID_TARGET;
		}
		final Player playerAttacker = attacker.getPlayer();
		if (playerAttacker == null)
		{
			return SystemMsg.INVALID_TARGET;
		}
		final SiegeClanObject siegeClan2 = getSiegeClan(SiegeEvent.ATTACKERS, playerAttacker.getClan());
		// если оба аттакеры, и в осаде, аттакеры в Алли, невозможно бить
		if (siegeClan1 != null && siegeClan2 != null && isAttackersInAlly())
		{
			return SystemMsg.FORCE_ATTACK_IS_IMPOSSIBLE_AGAINST_A_TEMPORARY_ALLIED_MEMBER_DURING_A_SIEGE;
		}
		// если нету как Аттакры, это дефендеры, то невозможно бить
		if (siegeClan1 == null && siegeClan2 == null)
		{
			return SystemMsg.INVALID_TARGET;
		}
		return null;
	}

	@Override
	public boolean isInProgress()
	{
		return _isInProgress.get();
	}

	@Override
	public void action(String name, boolean start)
	{
		if (name.equalsIgnoreCase(REGISTRATION))
		{
			setRegistrationOver(!start);
		}
		else
		{
			super.action(name, start);
		}
	}

	public boolean isAttackersInAlly()
	{
		return false;
	}

	@Override
	public void onAddEvent(GameObject object)
	{
		if (_killListener == null)
		{
			return;
		}

		if (object.isPlayer())
		{
			((Player) object).addListener(_killListener);
		}
	}

	@Override
	public void onRemoveEvent(GameObject object)
	{
		if (_killListener == null)
		{
			return;
		}

		if (object.isPlayer())
		{
			((Player) object).removeListener(_killListener);
		}
	}

	@Override
	public List<Player> broadcastPlayers(int range)
	{
		return itemObtainPlayers();
	}

	@Override
	public List<Player> itemObtainPlayers()
	{
		final List<Player> playersInZone = getPlayersInZone();
		final List<Player> list = new ArrayList<Player>(playersInZone.size());
		for (Player player : getPlayersInZone())
		{
			if (player.getEvent(getClass()) == this)
			{
				list.add(player);
			}
		}
		return list;
	}

	public Location getEnterLoc(Player player)
	{
		final S siegeClan = getSiegeClan(ATTACKERS, player.getClan());
		if (siegeClan != null)
		{
			if (siegeClan.getFlag() != null)
			{
				return Location.findAroundPosition(siegeClan.getFlag(), 50, 75);
			}
			else
			{
				return getResidence().getNotOwnerRestartPoint(player);
			}
		}
		else
		{
			return getResidence().getOwnerRestartPoint();
		}
	}

	// ========================================================================================================================================================================
	// Getters & Setters
	// ========================================================================================================================================================================
	public R getResidence()
	{
		return _residence;
	}

	public void setInProgress(boolean b)
	{
		_isInProgress.set(b);
	}

	public boolean isRegistrationOver()
	{
		return _isRegistrationOver;
	}

	public void setRegistrationOver(boolean b)
	{
		_isRegistrationOver = b;
	}

	// ========================================================================================================================================================================
	public void addSiegeSummon(SummonInstance summon)
	{
		_siegeSummons.add(summon.getRef());
	}

	public boolean containsSiegeSummon(SummonInstance cha)
	{
		return _siegeSummons.contains(cha.getRef());
	}

	public void despawnSiegeSummons()
	{
		for (HardReference<SummonInstance> ref : _siegeSummons)
		{
			final SummonInstance summon = ref.get();
			if (summon != null)
			{
				summon.unSummon();
			}
		}
		_siegeSummons.clear();
	}

	private static ScheduledFuture<?> _resultsThread = null;

	protected static void showResults()
	{
		if (_resultsThread != null)
		{
			return;
		}
		final Clan[] clans = ClanTable.getInstance().getClans();
		Arrays.sort(clans, new ClanKillsComparator());
		final Clan[] result = new Clan[Math.min(25, clans.length)];
		for (int i = 0; i < result.length; i++)
		{
			if (clans[i] == null || clans[i].getSiegeKills() == 0)
			{
				break;
			}
			result[i] = clans[i];
		}

		ShowSiegeKillResults results = new ShowSiegeKillResults(result);
		broadcastToWorld(results);

		_resultsThread = ThreadPoolManager.getInstance().schedule(() ->
		{
			broadcastToWorld(ExPVPMatchCCRetire.STATIC);

			for (Clan clan : ClanTable.getInstance().getClans())
			{
				clan.setSiegeKills(0);
			}
			_resultsThread = null;
		}, 5 * 60000L);
	}

	protected static class ClanKillsComparator implements Comparator<Clan>, Serializable
	{
		@Override
		public int compare(Clan o1, Clan o2)
		{
			return Integer.compare(o2.getSiegeKills(), o1.getSiegeKills());
		}
	}
}
