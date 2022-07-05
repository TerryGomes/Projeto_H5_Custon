package services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Config;
import l2f.gameserver.cache.Msg;
import l2f.gameserver.instancemanager.ReflectionManager;
import l2f.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.SimpleSpawner;
import l2f.gameserver.model.Zone;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.utils.Location;
import l2f.gameserver.utils.PositionUtils;
import l2f.gameserver.utils.ReflectionUtils;

public class TeleToParnassus extends Functions implements ScriptFile
{
	private static final Logger _log = LoggerFactory.getLogger(TeleToParnassus.class);
	private static List<SimpleSpawner> _spawns = new ArrayList<SimpleSpawner>();
	private static Zone _zone = ReflectionUtils.getZone("[parnassus_offshore]");
	private static ZoneListener _zoneListener;

	@Override
	public void onLoad()
	{
		if (!Config.SERVICES_PARNASSUS_ENABLED)
		{
			return;
		}

		ReflectionManager.PARNASSUS.setCoreLoc(new Location(149384, 171896, -952));

		// spawn wh keeper
		SimpleSpawner spawn = new SimpleSpawner(30086);
		spawn.setLocx(149960);
		spawn.setLocy(174136);
		spawn.setLocz(-920);
		spawn.setAmount(1);
		spawn.setHeading(32768);
		spawn.setRespawnDelay(5);
		spawn.setReflection(ReflectionManager.PARNASSUS);
		spawn.init();
		_spawns.add(spawn);

		// spawn grocery trader (Helvetia)
		spawn = new SimpleSpawner(30839);
		spawn.setLocx(149368);
		spawn.setLocy(174264);
		spawn.setLocz(-896);
		spawn.setAmount(1);
		spawn.setHeading(49152);
		spawn.setRespawnDelay(5);
		spawn.setReflection(ReflectionManager.PARNASSUS);
		spawn.init();
		_spawns.add(spawn);

		// spawn gk
		spawn = new SimpleSpawner(13129);
		spawn.setLocx(149368);
		spawn.setLocy(172568);
		spawn.setLocz(-952);
		spawn.setAmount(1);
		spawn.setHeading(49152);
		spawn.setRespawnDelay(5);
		spawn.setReflection(ReflectionManager.PARNASSUS);
		spawn.init();
		_spawns.add(spawn);

		// spawn Orion the Cat
		spawn = new SimpleSpawner(31860);
		spawn.setLocx(148904);
		spawn.setLocy(173656);
		spawn.setLocz(-952);
		spawn.setAmount(1);
		spawn.setHeading(49152);
		spawn.setRespawnDelay(5);
		spawn.setReflection(ReflectionManager.PARNASSUS);
		spawn.init();
		_spawns.add(spawn);

		// spawn blacksmith (Pushkin)
		spawn = new SimpleSpawner(30300);
		spawn.setLocx(148760);
		spawn.setLocy(174136);
		spawn.setLocz(-920);
		spawn.setAmount(1);
		spawn.setHeading(0);
		spawn.setRespawnDelay(5);
		spawn.setReflection(ReflectionManager.PARNASSUS);
		spawn.init();
		_spawns.add(spawn);

		// spawn Item Broker
		spawn = new SimpleSpawner(32320);
		spawn.setLocx(149368);
		spawn.setLocy(173064);
		spawn.setLocz(-952);
		spawn.setAmount(1);
		spawn.setHeading(16384);
		spawn.setRespawnDelay(5);
		spawn.setReflection(ReflectionManager.PARNASSUS);
		spawn.init();
		_spawns.add(spawn);

		_zoneListener = new ZoneListener();
		_zone.addListener(_zoneListener);
		_zone.setReflection(ReflectionManager.PARNASSUS);
		_zone.setActive(true);
		Zone zone = ReflectionUtils.getZone("[parnassus_peace]");
		zone.setReflection(ReflectionManager.PARNASSUS);
		zone.setActive(true);
		zone = ReflectionUtils.getZone("[parnassus_no_trade]");
		zone.setReflection(ReflectionManager.PARNASSUS);
		zone.setActive(true);

		_log.info("Loaded Service: Teleport to Parnassus");
	}

	@Override
	public void onReload()
	{
		_zone.removeListener(_zoneListener);
		for (SimpleSpawner spawn : _spawns)
		{
			spawn.deleteAll();
		}
		_spawns.clear();
	}

	@Override
	public void onShutdown()
	{
	}

	public void toParnassus()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null || !npc.isInRange(player, 1000L) || !NpcInstance.canBypassCheck(player, npc))
		{
			return;
		}

		if (player.getAdena() < Config.SERVICES_PARNASSUS_PRICE)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		player.reduceAdena(Config.SERVICES_PARNASSUS_PRICE, true, "TeleToParnassus");
		player.setVar("backCoords", player.getLoc().toXYZString(), -1);
		player.teleToLocation(Location.findPointToStay(_zone.getSpawn(), 30, 200, ReflectionManager.PARNASSUS.getGeoIndex()), ReflectionManager.PARNASSUS);
	}

	public void fromParnassus()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null || !npc.isInRange(player, 1000L) || !NpcInstance.canBypassCheck(player, npc))
		{
			return;
		}

		String var = player.getVar("backCoords");
		if (var == null || var.equals(""))
		{
			teleOut();
			return;
		}
		player.teleToLocation(Location.parseLoc(var), 0);
	}

	public void teleOut()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null || !npc.isInRange(player, 1000L))
		{
			return;
		}
		player.teleToLocation(46776, 185784, -3528, 0);
		show("I don't know from where you came here, but I can teleport you the another border side.", player, npc);
	}

	public String DialogAppend_30059(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30080(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30177(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30233(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30256(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30320(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30848(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30878(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30899(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_31210(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_31275(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_31320(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_31964(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30006(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30134(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30146(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_32163(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30576(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_30540(Integer val)
	{
		return getHtmlAppends(val);
	}

	private static final String en = "<br>[scripts_services.TeleToParnassus:toParnassus @811;Parnassus|\"Move to Parnassus (offshore zone) - " + Config.SERVICES_PARNASSUS_PRICE + " Adena.\"]";

	public String getHtmlAppends(Integer val)
	{
		if (val != 0 || !Config.SERVICES_PARNASSUS_ENABLED)
		{
			return "";
		}
		Player player = getSelf();
		if (player == null)
		{
			return "";
		}
		return en;
	}

	public String DialogAppend_13129(Integer val)
	{
		return getHtmlAppends2(val);
	}

	private static final String en2 = "<br>[scripts_services.ManaRegen:DoManaRegen|Full MP Regeneration. (1 MP for 5 Adena)]<br>[scripts_services.TeleToParnassus:fromParnassus @811;From Parnassus|\"Exit the Parnassus.\"]<br>";

	public String getHtmlAppends2(Integer val)
	{
		if (val != 0 || !Config.SERVICES_PARNASSUS_ENABLED)
		{
			return "";
		}
		Player player = getSelf();
		if (player == null || player.getReflection() != ReflectionManager.PARNASSUS)
		{
			return "";
		}
		return en2;
	}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
			Player player = cha.getPlayer();
			if (player != null)
			{
				if (Config.SERVICES_PARNASSUS_ENABLED && player.getReflection() == ReflectionManager.PARNASSUS && player.isVisible())
				{
					double angle = PositionUtils.convertHeadingToDegree(cha.getHeading());
					double radian = Math.toRadians(angle - 90);
					cha.teleToLocation((int) (cha.getX() + 50 * Math.sin(radian)), (int) (cha.getY() - 50 * Math.cos(radian)), cha.getZ());
				}
			}
		}

		@Override
		public void onEquipChanged(Zone zone, Creature actor)
		{
		}
	}
}