package events.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.listener.actor.OnDeathListener;
import l2mv.gameserver.listener.actor.player.OnPlayerExitListener;
import l2mv.gameserver.listener.actor.player.OnTeleportListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.actor.listener.CharListenerList;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.ReflectionUtils;

public class GludinArena extends Functions implements ScriptFile, OnDeathListener, OnTeleportListener, OnPlayerExitListener
{
	private static class GludinArenaImpl extends ArenaTemplate
	{
		@Override
		protected void onLoad()
		{
			_managerId = 17220015;
			_className = "GludinArena";
			_status = 0;

			_team1list = new CopyOnWriteArrayList<Long>();
			_team2list = new CopyOnWriteArrayList<Long>();
			_team1live = new CopyOnWriteArrayList<Long>();
			_team2live = new CopyOnWriteArrayList<Long>();

			_expToReturn = new HashMap<Integer, Integer>();
			_classToReturn = new HashMap<Integer, Integer>();

			_zoneListener = new ZoneListener();
			_zone = ReflectionUtils.getZone("[gludin_pvp]");
			_zone.addListener(_zoneListener);

			_team1points = new ArrayList<Location>();
			_team2points = new ArrayList<Location>();

			_team1points.add(new Location(-88313, 141815, -3672));
			_team1points.add(new Location(-88113, 141815, -3672));
			_team1points.add(new Location(-87907, 141815, -3672));
			_team1points.add(new Location(-87707, 141815, -3672));
			_team1points.add(new Location(-87515, 141815, -3672));
			_team2points.add(new Location(-87515, 142655, -3672));
			_team2points.add(new Location(-87707, 142655, -3672));
			_team2points.add(new Location(-87907, 142655, -3672));
			_team2points.add(new Location(-88113, 142655, -3672));
			_team2points.add(new Location(-88313, 142655, -3672));
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
			_instance = new GludinArenaImpl();
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
		onReload();
	}

	public String DialogAppend_17220015(Integer val)
	{
		if (val == 0)
		{
			Player player = getSelf();
			if (player.isGM())
			{
				return HtmCache.getInstance().getNotNull("scripts/events/arena/17220015.htm", player) + HtmCache.getInstance().getNotNull("scripts/events/arena/17220015-4.htm", player);
			}
			return HtmCache.getInstance().getNotNull("scripts/events/arena/17220015.htm", player);
		}
		return "";
	}

	public String DialogAppend_17220016(Integer val)
	{
		return DialogAppend_17220015(val);
	}

	@Override
	public void onDeath(Creature self, Creature killer)
	{
		getInstance().onDeath(self, killer);
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