package events.TvTArena;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Announcements;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.instancemanager.ServerVariables;
import l2mv.gameserver.listener.actor.OnDeathListener;
import l2mv.gameserver.listener.actor.player.OnPlayerExitListener;
import l2mv.gameserver.listener.actor.player.OnTeleportListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.model.actor.listener.CharListenerList;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.ReflectionUtils;

public class TvTArena1 extends Functions implements ScriptFile, OnDeathListener, OnTeleportListener, OnPlayerExitListener
{
	private static final Logger _log = LoggerFactory.getLogger(TvTArena1.class);

	private static class TvTArena1Impl extends TvTTemplate
	{
		@Override
		protected void onLoad()
		{
			_managerId = 31390;
			_className = "TvTArena1";
			_status = 0;

			_team1list = new CopyOnWriteArrayList<Long>();
			_team2list = new CopyOnWriteArrayList<Long>();
			_team1live = new CopyOnWriteArrayList<Long>();
			_team2live = new CopyOnWriteArrayList<Long>();

			_zoneListener = new ZoneListener();
			_zone = ReflectionUtils.getZone("[tvt_arena1]");
			_zone.addListener(_zoneListener);

			_team1points = new ArrayList<Location>();
			_team2points = new ArrayList<Location>();

			_team1points.add(new Location(-81806, -44865, -11418));
			_team1points.add(new Location(-81617, -44893, -11418));
			_team1points.add(new Location(-81440, -44945, -11418));
			_team1points.add(new Location(-81301, -48066, -11418));
			_team1points.add(new Location(-81168, -45208, -11418));
			_team1points.add(new Location(-81114, -46379, -11418));
			_team1points.add(new Location(-81068, -45570, -11418));
			_team1points.add(new Location(-81114, -45728, -11418));
			_team1points.add(new Location(-81162, -45934, -11418));
			_team1points.add(new Location(-81280, -46045, -11418));
			_team1points.add(new Location(-81424, -46196, -11418));
			_team1points.add(new Location(-81578, -46238, -11418));
			_team2points.add(new Location(-81792, -46299, -11418));
			_team2points.add(new Location(-81959, -46247, -11418));
			_team2points.add(new Location(-82147, -46206, -11418));
			_team2points.add(new Location(-82256, -46093, -11418));
			_team2points.add(new Location(-82418, -45940, -11418));
			_team2points.add(new Location(-82455, -45779, -11418));
			_team2points.add(new Location(-82513, -45573, -11418));
			_team2points.add(new Location(-82464, -45499, -11418));
			_team2points.add(new Location(-82421, -45215, -11418));
			_team2points.add(new Location(-82308, -45106, -11418));
			_team2points.add(new Location(-82160, -44948, -11418));
			_team2points.add(new Location(-81978, -44904, -11418));
		}

		@Override
		protected void onReload()
		{
			if (_status > 0)
			{
				template_stop();
			}
			_zone.removeListener(_zoneListener);
		}

	}

	private static TvTTemplate _instance;

	public static TvTTemplate getInstance()
	{
		if (_instance == null)
		{
			_instance = new TvTArena1Impl();
		}
		return _instance;
	}

	@Override
	public void onLoad()
	{
		CharListenerList.addGlobal(this);
		getInstance().onLoad();
		if (isActive())
		{
			spawnEventManagers();
			_log.info("Loaded Event: TvT Arena 1 [state: activated]");
		}
		else
		{
			_log.info("Loaded Event: TvT Arena 1 [state: deactivated]");
		}
	}

	@Override
	public void onReload()
	{
		getInstance().onReload();
		unSpawnEventManagers();
		_instance = null;
	}

	@Override
	public void onShutdown()
	{

	}

	@Override
	public void onDeath(Creature cha, Creature killer)
	{
		getInstance().onDeath(cha, killer);
	}

	@Override
	public void onPlayerExit(Player player)
	{
		getInstance().onPlayerExit(player);
	}

	@Override
	public void onTeleport(Player player, int x, int y, int z, Reflection reflection)
	{
		getInstance().onTeleport(player);
	}

	public String DialogAppend_31390(Integer val)
	{
		if (val == 0)
		{
			Player player = getSelf();
			if (player.isGM())
			{
				return HtmCache.getInstance().getNotNull("scripts/events/TvTArena/31390.htm", player) + HtmCache.getInstance().getNotNull("scripts/events/TvTArena/31390-4.htm", player);
			}
			return HtmCache.getInstance().getNotNull("scripts/events/TvTArena/31390.htm", player);
		}
		return "";
	}

	public void create1()
	{
		getInstance().template_create1(getSelf());
	}

	public void register()
	{
		getInstance().template_register(getSelf());
	}

	public void check1(String[] var)
	{
		getInstance().template_check1(getSelf(), getNpc(), var);
	}

	public void register_check()
	{
		getInstance().template_register_check(getSelf());
	}

	public void stop()
	{
		getInstance().template_stop();
	}

	public void announce()
	{
		getInstance().template_announce();
	}

	public void prepare()
	{
		getInstance().template_prepare();
	}

	public void start()
	{
		getInstance().template_start();
	}

	public void timeOut()
	{
		getInstance().template_timeOut();
	}

	private List<NpcInstance> _spawns = new ArrayList<NpcInstance>();

	/**
	 * Читает статус эвента из базы.
	 * @return
	 */
	private boolean isActive()
	{
		return IsActive("TvT Arena 1");
	}

	/**
	 * Запускает эвент
	 */
	public void startEvent()
	{
		Player player = getSelf();
		if (!player.getPlayerAccess().IsEventGm)
		{
			return;
		}

		if (SetActive("TvT Arena 1", true))
		{
			spawnEventManagers();
			System.out.println("Event: TvT Arena 1 started.");
			Announcements.getInstance().announceToAll("Started TvT Arena Event 1.");
		}
		else
		{
			player.sendMessage("TvT Arena 1 Event already started.");
		}

		show("admin/events/events.htm", player);
	}

	/**
	 * Останавливает эвент
	 */
	public void stopEvent()
	{
		Player player = getSelf();
		if (!player.getPlayerAccess().IsEventGm)
		{
			return;
		}

		if (SetActive("TvT Arena 1", false))
		{
			ServerVariables.unset("TvT Arena 1");
			unSpawnEventManagers();
			stop();
			System.out.println("TvT Arena 1 Event stopped.");
			Announcements.getInstance().announceToAll("TvT Arena Event 1 is over.");
		}
		else
		{
			player.sendMessage("TvT Arena 1 Event not started.");
		}

		show("admin/events/events.htm", player);
	}

	/**
	 * Спавнит эвент менеджеров
	 */
	private void spawnEventManagers()
	{
		final int EVENT_MANAGERS[][] =
		{
			{
				82840,
				149167,
				-3495,
				0
			}
		};

		NpcTemplate template = NpcHolder.getInstance().getTemplate(31390);
		for (int[] element : EVENT_MANAGERS)
		{
			SimpleSpawner sp = new SimpleSpawner(template);
			sp.setLocx(element[0]);
			sp.setLocy(element[1]);
			sp.setLocz(element[2]);
			sp.setHeading(element[3]);
			NpcInstance npc = sp.doSpawn(true);
			npc.setName("Arena 1");
			npc.setTitle("TvT Event");
			_spawns.add(npc);
		}
	}

	/**
	 * Удаляет спавн эвент менеджеров
	 */
	private void unSpawnEventManagers()
	{
		for (NpcInstance npc : _spawns)
		{
			npc.deleteMe();
		}
		_spawns.clear();
	}
}