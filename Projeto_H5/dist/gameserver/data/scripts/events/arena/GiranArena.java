package events.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.listener.actor.OnDeathListener;
import l2f.gameserver.listener.actor.player.OnPlayerExitListener;
import l2f.gameserver.listener.actor.player.OnTeleportListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.actor.listener.CharListenerList;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.utils.Location;
import l2f.gameserver.utils.ReflectionUtils;

public class GiranArena extends Functions implements ScriptFile, OnDeathListener, OnTeleportListener, OnPlayerExitListener
{
	private static class GiranArenaImpl extends ArenaTemplate
	{
		@Override
		protected void onLoad()
		{
			_managerId = 22220001;
			_className = "GiranArena";
			_status = 0;

			_team1list = new CopyOnWriteArrayList<Long>();
			_team2list = new CopyOnWriteArrayList<Long>();
			_team1live = new CopyOnWriteArrayList<Long>();
			_team2live = new CopyOnWriteArrayList<Long>();

			_expToReturn = new HashMap<Integer, Integer>();
			_classToReturn = new HashMap<Integer, Integer>();

			_zoneListener = new ZoneListener();
			_zone = ReflectionUtils.getZone("[giran_pvp_battle]");
			_zone.addListener(_zoneListener);

			_team1points = new ArrayList<Location>();
			_team2points = new ArrayList<Location>();

			_team1points.add(new Location(72609, 142346, -3798));
			_team1points.add(new Location(72809, 142346, -3798));
			_team1points.add(new Location(73015, 142346, -3798));
			_team1points.add(new Location(73215, 142346, -3798));
			_team1points.add(new Location(73407, 142346, -3798));
			_team2points.add(new Location(73407, 143186, -3798));
			_team2points.add(new Location(73215, 143186, -3798));
			_team2points.add(new Location(73015, 143186, -3798));
			_team2points.add(new Location(72809, 143186, -3798));
			_team2points.add(new Location(72609, 143186, -3798));
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

	private static ArenaTemplate _instance;

	public static ArenaTemplate getInstance()
	{
		if (_instance == null)
		{
			_instance = new GiranArenaImpl();
		}
		return _instance;
	}

	@Override
	public void onLoad()
	{
		getInstance().onLoad();
		CharListenerList.addGlobal(this);
	}

	@Override
	public void onReload()
	{
		getInstance().onReload();
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

	public String DialogAppend_22220001(Integer val)
	{
		if (val == 0)
		{
			Player player = getSelf();
			if (player.isGM())
			{
				return HtmCache.getInstance().getNotNull("scripts/events/arena/22220001.htm", player) + HtmCache.getInstance().getNotNull("scripts/events/arena/22220001-4.htm", player);
			}
			return HtmCache.getInstance().getNotNull("scripts/events/arena/22220001.htm", player);
		}
		return "";
	}

	public String DialogAppend_22220002(Integer val)
	{
		return DialogAppend_22220001(val);
	}

	public void create1()
	{
		getInstance().template_create1(getSelf());
	}

	public void create2()
	{
		getInstance().template_create2(getSelf());
	}

	public void register()
	{
		getInstance().template_register(getSelf());
	}

	public void check1(String[] var)
	{
		getInstance().template_check1(getSelf(), var);
	}

	public void check2(String[] var)
	{
		getInstance().template_check2(getSelf(), var);
	}

	public void register_check(String[] var)
	{
		getInstance().template_register_check(getSelf(), var);
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

	public static void timeOut()
	{
		getInstance().template_timeOut();
	}
}