package services;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.Config;
import l2mv.gameserver.instancemanager.ReflectionManager;
import l2mv.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.model.Zone;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.PositionUtils;
import l2mv.gameserver.utils.ReflectionUtils;

public class TeleToGH extends Functions implements ScriptFile
{
	private static List<SimpleSpawner> _spawns = new ArrayList<SimpleSpawner>();

	private static Zone _zone = ReflectionUtils.getZone("[giran_harbor_offshore]");
	private static ZoneListener _zoneListener;

	@Override
	public void onLoad()
	{
		if (!Config.SERVICES_GIRAN_HARBOR_ENABLED)
		{
			return;
		}

		ReflectionManager.GIRAN_HARBOR.setCoreLoc(new Location(47416, 186568, -3480));

		// spawn wh keeper
		SimpleSpawner spawn = new SimpleSpawner(30086);
		spawn.setLocx(48059);
		spawn.setLocy(186791);
		spawn.setLocz(-3512);
		spawn.setAmount(1);
		spawn.setHeading(42000);
		spawn.setRespawnDelay(5);
		spawn.setReflection(ReflectionManager.GIRAN_HARBOR);
		spawn.init();
		_spawns.add(spawn);

		// spawn grocery trader
		spawn = new SimpleSpawner(32169);
		spawn.setLocx(48146);
		spawn.setLocy(186753);
		spawn.setLocz(-3512);
		spawn.setAmount(1);
		spawn.setHeading(42000);
		spawn.setRespawnDelay(5);
		spawn.setReflection(ReflectionManager.GIRAN_HARBOR);
		spawn.init();
		_spawns.add(spawn);

		// spawn gk
		spawn = new SimpleSpawner(13129);
		spawn.setLocx(47984);
		spawn.setLocy(186832);
		spawn.setLocz(-3445);
		spawn.setAmount(1);
		spawn.setHeading(42000);
		spawn.setRespawnDelay(5);
		spawn.setReflection(ReflectionManager.GIRAN_HARBOR);
		spawn.init();
		_spawns.add(spawn);

		// spawn Orion the Cat
		spawn = new SimpleSpawner(31860);
		spawn.setLocx(48129);
		spawn.setLocy(186828);
		spawn.setLocz(-3512);
		spawn.setAmount(1);
		spawn.setHeading(45452);
		spawn.setRespawnDelay(5);
		spawn.setReflection(ReflectionManager.GIRAN_HARBOR);
		spawn.init();
		_spawns.add(spawn);

		// spawn blacksmith (Pushkin)
		spawn = new SimpleSpawner(30300);
		spawn.setLocx(48102);
		spawn.setLocy(186772);
		spawn.setLocz(-3512);
		spawn.setAmount(1);
		spawn.setHeading(42000);
		spawn.setRespawnDelay(5);
		spawn.setReflection(ReflectionManager.GIRAN_HARBOR);
		spawn.init();
		_spawns.add(spawn);

		// spawn Item Broker
		spawn = new SimpleSpawner(32320);
		spawn.setLocx(47772);
		spawn.setLocy(186905);
		spawn.setLocz(-3480);
		spawn.setAmount(1);
		spawn.setHeading(42000);
		spawn.setRespawnDelay(5);
		spawn.setReflection(ReflectionManager.GIRAN_HARBOR);
		spawn.init();
		_spawns.add(spawn);

		// spawn Item Broker
		spawn = new SimpleSpawner(32320);
		spawn.setLocx(46360);
		spawn.setLocy(187672);
		spawn.setLocz(-3480);
		spawn.setAmount(1);
		spawn.setHeading(42000);
		spawn.setRespawnDelay(5);
		spawn.setReflection(ReflectionManager.GIRAN_HARBOR);
		spawn.init();
		_spawns.add(spawn);

		// spawn Item Broker
		spawn = new SimpleSpawner(32320);
		spawn.setLocx(49016);
		spawn.setLocy(185960);
		spawn.setLocz(-3480);
		spawn.setAmount(1);
		spawn.setHeading(42000);
		spawn.setRespawnDelay(5);
		spawn.setReflection(ReflectionManager.GIRAN_HARBOR);
		spawn.init();
		_spawns.add(spawn);

		_zoneListener = new ZoneListener();
		_zone.addListener(_zoneListener);
		_zone.setReflection(ReflectionManager.GIRAN_HARBOR);
		_zone.setActive(true);
		Zone zone = ReflectionUtils.getZone("[giran_harbor_peace_alt]");
		zone.setReflection(ReflectionManager.GIRAN_HARBOR);
		zone.setActive(true);
		zone = ReflectionUtils.getZone("[giran_harbor_no_trade]");
		zone.setReflection(ReflectionManager.GIRAN_HARBOR);
		zone.setActive(true);
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

	public void toGH()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null || !npc.isInRange(player, 1000L) || !NpcInstance.canBypassCheck(player, npc))
		{
			return;
		}

		player.setVar("backCoords", player.getLoc().toXYZString(), -1);
		player.teleToLocation(Location.findPointToStay(_zone.getSpawn(), 30, 200, ReflectionManager.GIRAN_HARBOR.getGeoIndex()), ReflectionManager.GIRAN_HARBOR);
	}

	public void fromGH()
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

	private static final String en = "<br>[scripts_services.TeleToGH:toGH @811;Giran Harbor|\"I want free admission to Giran Harbor.\"]";

	public String getHtmlAppends(Integer val)
	{
		if (val != 0 || !Config.SERVICES_GIRAN_HARBOR_ENABLED)
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

	private static final String en2 = "<br>[scripts_services.ManaRegen:DoManaRegen|Full MP Regeneration. (1 MP for 5 Adena)]<br>[scripts_services.TeleToGH:fromGH @811;From Giran Harbor|\"Exit the Giran Harbor.\"]<br>";

	public String getHtmlAppends2(Integer val)
	{
		if (val != 0 || !Config.SERVICES_GIRAN_HARBOR_ENABLED)
		{
			return "";
		}
		Player player = getSelf();
		if (player == null || player.getReflectionId() != -2)
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
			// обрабатывать вход в зону не надо, только выход
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
			Player player = cha.getPlayer();
			if (player != null)
			{
				if (Config.SERVICES_GIRAN_HARBOR_ENABLED && player.getReflection() == ReflectionManager.GIRAN_HARBOR && player.isVisible())
				{
					double angle = PositionUtils.convertHeadingToDegree(cha.getHeading()); // угол в градусах
					double radian = Math.toRadians(angle - 90); // угол в радианах
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