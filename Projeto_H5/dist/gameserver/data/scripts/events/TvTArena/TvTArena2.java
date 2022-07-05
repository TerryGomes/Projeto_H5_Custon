package events.TvTArena;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Announcements;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.instancemanager.ServerVariables;
import l2f.gameserver.listener.actor.OnDeathListener;
import l2f.gameserver.listener.actor.player.OnPlayerExitListener;
import l2f.gameserver.listener.actor.player.OnTeleportListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.SimpleSpawner;
import l2f.gameserver.model.actor.listener.CharListenerList;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.Location;
import l2f.gameserver.utils.ReflectionUtils;

public class TvTArena2 extends Functions implements ScriptFile, OnDeathListener, OnTeleportListener, OnPlayerExitListener
{
	private static final Logger _log = LoggerFactory.getLogger(TvTArena2.class);

	private static class TvTArena2Impl extends TvTTemplate
	{
		@Override
		protected void onLoad()
		{
			_managerId = 31391;
			_className = "TvTArena2";
			_status = 0;

			_team1list = new CopyOnWriteArrayList<Long>();
			_team2list = new CopyOnWriteArrayList<Long>();
			_team1live = new CopyOnWriteArrayList<Long>();
			_team2live = new CopyOnWriteArrayList<Long>();

			_zoneListener = new ZoneListener();
			_zone = ReflectionUtils.getZone("[tvt_arena2]");
			_zone.addListener(_zoneListener);

			_team1points = new ArrayList<Location>();
			_team2points = new ArrayList<Location>();

			_team1points.add(new Location(-77724, -47901, -11518, -11418));
			_team1points.add(new Location(-77718, -48080, -11518, -11418));
			_team1points.add(new Location(-77699, -48280, -11518, -11418));
			_team1points.add(new Location(-77777, -48442, -11518, -11418));
			_team1points.add(new Location(-77863, -48622, -11518, -11418));
			_team1points.add(new Location(-78002, -48714, -11518, -11418));
			_team1points.add(new Location(-78168, -48835, -11518, -11418));
			_team1points.add(new Location(-78353, -48851, -11518, -11418));
			_team1points.add(new Location(-78543, -48864, -11518, -11418));
			_team1points.add(new Location(-78709, -48784, -11518, -11418));
			_team1points.add(new Location(-78881, -48702, -11518, -11418));
			_team1points.add(new Location(-78981, -48555, -11518, -11418));
			_team2points.add(new Location(-79097, -48400, -11518, -11418));
			_team2points.add(new Location(-79107, -48214, -11518, -11418));
			_team2points.add(new Location(-79125, -48027, -11518, -11418));
			_team2points.add(new Location(-79047, -47861, -11518, -11418));
			_team2points.add(new Location(-78965, -47689, -11518, -11418));
			_team2points.add(new Location(-78824, -47594, -11518, -11418));
			_team2points.add(new Location(-78660, -47474, -11518, -11418));
			_team2points.add(new Location(-78483, -47456, -11518, -11418));
			_team2points.add(new Location(-78288, -47440, -11518, -11418));
			_team2points.add(new Location(-78125, -47515, -11518, -11418));
			_team2points.add(new Location(-77953, -47599, -11518, -11418));
			_team2points.add(new Location(-77844, -47747, -11518, -11418));
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
			_instance = new TvTArena2Impl();
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
			_log.info("Loaded Event: TvT Arena 2 [state: activated]");
		}
		else
		{
			_log.info("Loaded Event: TvT Arena 2 [state: deactivated]");
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

	public String DialogAppend_31391(Integer val)
	{
		if (val == 0)
		{
			Player player = getSelf();
			if (player.isGM())
			{
				return HtmCache.getInstance().getNotNull("scripts/events/TvTArena/31391.htm", player) + HtmCache.getInstance().getNotNull("scripts/events/TvTArena/31391-4.htm", player);
			}
			return HtmCache.getInstance().getNotNull("scripts/events/TvTArena/31391.htm", player);
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
		return IsActive("TvT Arena 2");
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

		if (SetActive("TvT Arena 2", true))
		{
			spawnEventManagers();
			System.out.println("Event: TvT Arena 2 started.");
			Announcements.getInstance().announceToAll("Started TvT Arena Event 2.");
		}
		else
		{
			player.sendMessage("TvT Arena 2 Event already started.");
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

		if (SetActive("TvT Arena 2", false))
		{
			ServerVariables.unset("TvT Arena 2");
			unSpawnEventManagers();
			stop();
			System.out.println("TvT Arena 2 Event stopped.");
			Announcements.getInstance().announceToAll("TvT Arena 2 Event is over.");
		}
		else
		{
			player.sendMessage("TvT Arena 2 Event not started.");
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
				149048,
				-3472,
				0
			}
		};

		NpcTemplate template = NpcHolder.getInstance().getTemplate(31391);
		for (int[] element : EVENT_MANAGERS)
		{
			SimpleSpawner sp = new SimpleSpawner(template);
			sp.setLocx(element[0]);
			sp.setLocy(element[1]);
			sp.setLocz(element[2]);
			sp.setHeading(element[3]);
			NpcInstance npc = sp.doSpawn(true);
			npc.setName("Arena 2");
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