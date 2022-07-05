package l2f.gameserver.model.entity.events.impl;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;
import l2f.commons.collections.MultiValueSet;
import l2f.commons.lang.ArrayUtils;
import l2f.commons.time.cron.SchedulingPattern;
import l2f.commons.util.Rnd;
import l2f.gameserver.data.xml.holder.EventHolder;
import l2f.gameserver.instancemanager.ReflectionManager;
import l2f.gameserver.listener.actor.OnKillListener;
import l2f.gameserver.listener.actor.player.OnPlayerExitListener;
import l2f.gameserver.listener.actor.player.OnTeleportListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.GameObject;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.base.RestartType;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.model.entity.events.EventType;
import l2f.gameserver.model.entity.events.GlobalEvent;
import l2f.gameserver.model.entity.events.objects.KrateisCubePlayerObject;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.ExPVPMatchCCMyRecord;
import l2f.gameserver.network.serverpackets.ExPVPMatchCCRecord;
import l2f.gameserver.network.serverpackets.ExPVPMatchCCRetire;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.IStaticPacket;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.tables.SkillTable;
import l2f.gameserver.utils.Location;

public class KrateisCubeEvent extends GlobalEvent
{
	private class Listeners implements OnKillListener, OnPlayerExitListener, OnTeleportListener
	{
		@Override
		public void onKill(Creature actor, Creature victim)
		{
			if (!victim.isPlayer())
			{
				return;
			}

			KrateisCubeEvent cubeEvent2 = victim.getEvent(KrateisCubeEvent.class);
			if (cubeEvent2 != KrateisCubeEvent.this)
			{
				return;
			}

			KrateisCubePlayerObject winnerPlayer = getParticlePlayer((Player) actor);

			winnerPlayer.setPoints(winnerPlayer.getPoints() + 5);
			updatePoints(winnerPlayer);

			KrateisCubePlayerObject looserPlayer = getParticlePlayer((Player) victim);

			looserPlayer.startRessurectTask();
		}

		@Override
		public boolean ignorePetOrSummon()
		{
			return true;
		}

		@Override
		public void onPlayerExit(Player player)
		{
			exitCube(player, false);
		}

		@Override
		public void onTeleport(Player player, int x, int y, int z, Reflection reflection)
		{
			List<Location> waitLocs = getObjects(WAIT_LOCS);
			for (Location loc : waitLocs)
			{
				if (loc.x == x && loc.y == y)
				{
					return;
				}
			}

			waitLocs = getObjects(TELEPORT_LOCS);

			for (Location loc : waitLocs)
			{
				if (loc.x == x && loc.y == y)
				{
					return;
				}
			}

			exitCube(player, false);
		}
	}

	private static final SchedulingPattern DATE_PATTERN = new SchedulingPattern("0,30 * * * *");
	private static final Location RETURN_LOC = new Location(-70381, -70937, -1428);
	private static final int[] SKILL_IDS =
	{
		1086,
		1204,
		1059,
		1085,
		1078,
		1068,
		1240,
		1077,
		1242,
		1062,
		5739
	};
	private static final int[] SKILL_LEVEL =
	{
		2,
		2,
		3,
		3,
		6,
		3,
		3,
		3,
		3,
		2,
		1
	};

	public static final String PARTICLE_PLAYERS = "particle_players";
	public static final String REGISTERED_PLAYERS = "registered_players";
	public static final String WAIT_LOCS = "wait_locs";
	public static final String TELEPORT_LOCS = "teleport_locs";
	public static final String PREPARE = "prepare";

	private final int _minLevel;
	private final int _maxLevel;

	private Calendar _calendar = Calendar.getInstance();

	private KrateisCubeRunnerEvent _runnerEvent;
	private Listeners _listeners = new Listeners();

	public KrateisCubeEvent(MultiValueSet<String> set)
	{
		super(set);
		_minLevel = set.getInteger("min_level");
		_maxLevel = set.getInteger("max_level");
	}

	@Override
	public void initEvent()
	{
		_runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 2);

		super.initEvent();
	}

	public void prepare()
	{
		NpcInstance npc = _runnerEvent.getNpc();
		List<KrateisCubePlayerObject> registeredPlayers = removeObjects(REGISTERED_PLAYERS);
		List<Location> waitLocs = getObjects(WAIT_LOCS);
		for (KrateisCubePlayerObject k : registeredPlayers)
		{
			if (npc.getDistance(k.getPlayer()) > 800)
			{
				continue;
			}

			addObject(PARTICLE_PLAYERS, k);

			Player player = k.getPlayer();

			player.teleToLocation(Rnd.get(waitLocs), ReflectionManager.DEFAULT);
		}
	}

	@Override
	public void startEvent()
	{
		super.startEvent();

		List<KrateisCubePlayerObject> players = getObjects(PARTICLE_PLAYERS);
		List<Location> teleportLocs = getObjects(TELEPORT_LOCS);

		for (int i = 0; i < players.size(); i++)
		{
			KrateisCubePlayerObject k = players.get(i);
			Player player = k.getPlayer();

			player.getEffectList().stopAllEffects();

			giveEffects(player);

			player.teleToLocation(teleportLocs.get(i));
			player.addEvent(this);

			player.sendPacket(new ExPVPMatchCCMyRecord(k), SystemMsg.THE_MATCH_HAS_STARTED);
		}
	}

	@Override
	public void stopEvent()
	{
		super.stopEvent();
		reCalcNextTime(false);

		double dif = 0.05;
		int pos = 0;

		List<KrateisCubePlayerObject> players = removeObjects(PARTICLE_PLAYERS);
		for (KrateisCubePlayerObject krateisPlayer : players)
		{
			Player player = krateisPlayer.getPlayer();
			pos++;
			if (krateisPlayer.getPoints() >= 10)
			{
				int count = (int) (krateisPlayer.getPoints() * dif * (1.0 + players.size() / pos * 0.04));
				dif -= 0.0016;
				if (count > 0)
				{
					Functions.addItem(player, 13067, count, "Kratei Reward");

					int exp = count * 2880;
					int sp = count * 288;
					player.addExpAndSp(exp, sp);
				}
			}

			player.removeEvent(this);

			player.sendPacket(ExPVPMatchCCRetire.STATIC, SystemMsg.END_MATCH);
			player.teleToLocation(RETURN_LOC);
		}
	}

	private void giveEffects(Player player)
	{
		player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
		player.setCurrentCp(player.getMaxCp());

		for (int j = 0; j < SKILL_IDS.length; j++)
		{
			Skill skill = SkillTable.getInstance().getInfo(SKILL_IDS[j], SKILL_LEVEL[j]);
			if (skill != null)
			{
				skill.getEffects(player, player, false, false);
			}
		}
	}

	@Override
	public void reCalcNextTime(boolean onInit)
	{
		clearActions();

		_calendar.setTimeInMillis(DATE_PATTERN.next(System.currentTimeMillis()));

		registerActions();
	}

	@Override
	protected long startTimeMillis()
	{
		return _calendar.getTimeInMillis();
	}

	@Override
	public boolean canRessurect(Player resurrectPlayer, Creature creature, boolean force)
	{
		resurrectPlayer.sendPacket(SystemMsg.INVALID_TARGET);
		return false;
	}

	public KrateisCubePlayerObject getRegisteredPlayer(Player player)
	{
		List<KrateisCubePlayerObject> registeredPlayers = getObjects(REGISTERED_PLAYERS);
		for (KrateisCubePlayerObject p : registeredPlayers)
		{
			if (p.getPlayer() == player)
			{
				return p;
			}
		}
		return null;
	}

	public KrateisCubePlayerObject getParticlePlayer(Player player)
	{
		List<KrateisCubePlayerObject> registeredPlayers = getObjects(PARTICLE_PLAYERS);
		for (KrateisCubePlayerObject p : registeredPlayers)
		{
			if (p.getPlayer() == player)
			{
				return p;
			}
		}
		return null;
	}

	public void showRank(Player player)
	{
		KrateisCubePlayerObject particlePlayer = getParticlePlayer(player);
		if (particlePlayer == null || particlePlayer.isShowRank())
		{
			return;
		}

		particlePlayer.setShowRank(true);

		Map<String, Integer> scores = new FastMap<>();
		for (KrateisCubePlayerObject p : getSortedPlayers())
		{
			scores.put(p.getName(), p.getPoints());
		}

		player.sendPacket(new ExPVPMatchCCRecord(scores));
	}

	public void closeRank(Player player)
	{
		KrateisCubePlayerObject particlePlayer = getParticlePlayer(player);
		if (particlePlayer == null || !particlePlayer.isShowRank())
		{
			return;
		}

		particlePlayer.setShowRank(false);
	}

	public void updatePoints(KrateisCubePlayerObject k)
	{
		k.getPlayer().sendPacket(new ExPVPMatchCCMyRecord(k));

		Map<String, Integer> scores = new FastMap<>();
		for (KrateisCubePlayerObject p : getSortedPlayers())
		{
			scores.put(p.getName(), p.getPoints());
		}

		final ExPVPMatchCCRecord p = new ExPVPMatchCCRecord(scores);

		List<KrateisCubePlayerObject> players = getObjects(PARTICLE_PLAYERS);
		for (KrateisCubePlayerObject $player : players)
		{
			if ($player.isShowRank())
			{
				$player.getPlayer().sendPacket(p);
			}
		}
	}

	public KrateisCubePlayerObject[] getSortedPlayers()
	{
		List<KrateisCubePlayerObject> players = getObjects(PARTICLE_PLAYERS);
		KrateisCubePlayerObject[] array = players.toArray(new KrateisCubePlayerObject[players.size()]);
		ArrayUtils.eqSort(array);
		return array;
	}

	public void exitCube(Player player, boolean teleport)
	{
		KrateisCubePlayerObject krateisCubePlayer = getParticlePlayer(player);
		krateisCubePlayer.stopRessurectTask();

		getObjects(PARTICLE_PLAYERS).remove(krateisCubePlayer);

		player.sendPacket(ExPVPMatchCCRetire.STATIC);
		player.removeEvent(this);

		if (teleport)
		{
			player.teleToLocation(RETURN_LOC);
		}
	}

	@Override
	public void announce(int a)
	{
		IStaticPacket p = null;
		if (a > 0)
		{
			p = new SystemMessage2(SystemMsg.S1_SECONDS_TO_GAME_END).addInteger(a);
		}
		else
		{
			p = new SystemMessage2(SystemMsg.THE_MATCH_WILL_START_IN_S1_SECONDS).addInteger(-a);
		}

		List<KrateisCubePlayerObject> players = getObjects(PARTICLE_PLAYERS);
		for (KrateisCubePlayerObject $player : players)
		{
			$player.getPlayer().sendPacket(p);
		}
	}

	@Override
	public boolean isParticle(Player player)
	{
		return getParticlePlayer(player) != null;
	}

	@Override
	public void onAddEvent(GameObject o)
	{
		if (o.isPlayer())
		{
			o.getPlayer().addListener(_listeners);
		}
	}

	@Override
	public void onRemoveEvent(GameObject o)
	{
		if (o.isPlayer())
		{
			o.getPlayer().removeListener(_listeners);
		}
	}

	@Override
	public void action(String name, boolean start)
	{
		if (name.equalsIgnoreCase(PREPARE))
		{
			prepare();
		}
		else
		{
			super.action(name, start);
		}
	}

	@Override
	public void checkRestartLocs(Player player, Map<RestartType, Boolean> r)
	{
		r.clear();
	}

	public int getMinLevel()
	{
		return _minLevel;
	}

	public int getMaxLevel()
	{
		return _maxLevel;
	}

	@Override
	public boolean isInProgress()
	{
		return _runnerEvent.isInProgress();
	}

	public boolean isRegistrationOver()
	{
		return _runnerEvent.isRegistrationOver();
	}
}
