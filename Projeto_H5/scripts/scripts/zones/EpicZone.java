package zones;

import java.util.LinkedHashMap;
import java.util.Map;

import l2mv.gameserver.Config;
import l2mv.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Zone;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.ReflectionUtils;

public class EpicZone implements ScriptFile
{
	private static ZoneListener _zoneListener;
	private static Map<Long, String> boxes = new LinkedHashMap<Long, String>();

	@Override
	public void onLoad()
	{
		_zoneListener = new ZoneListener();
		Zone zone = ReflectionUtils.getZone("[queen_ant_epic]");
		zone.addListener(_zoneListener);

		Zone zone1 = ReflectionUtils.getZone("[fix_exploit_beleth]");
		zone1.addListener(_zoneListener);

		Zone zone2 = ReflectionUtils.getZone("[fix_exploit_beleth_2]");
		zone2.addListener(_zoneListener);

		Zone zone3 = ReflectionUtils.getZone("[baium_epic]");
		zone3.addListener(_zoneListener);

		Zone zone4 = ReflectionUtils.getZone("[top_top_to_13_pvp]");
		zone4.addListener(_zoneListener);

		Zone zone5 = ReflectionUtils.getZone("[antharas_epic]");
		zone5.addListener(_zoneListener);

		Zone zone6 = ReflectionUtils.getZone("[Near_Antharas_Heart_PvP]");
		zone6.addListener(_zoneListener);

		Zone zone7 = ReflectionUtils.getZone("[valakas_epic]");
		zone7.addListener(_zoneListener);

		Zone zone8 = ReflectionUtils.getZone("[Valakas_Lair_Entrance_PvP]");
		zone8.addListener(_zoneListener);

	}

	@Override
	public void onReload()
	{

	}

	@Override
	public void onShutdown()
	{

	}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			if (zone.getParams() == null || !cha.isPlayable() || cha.getPlayer().isGM())
			{
				return;
			}

			// claww - Limit boxes by HWID in Epic zones
			if (!Config.ALLOW_DUALBOX_EPIC)
			{
				if (boxes.containsValue(cha.getPlayer().getNetConnection().getHWID()))
				{
					teleportOutEpic(zone, cha);
					return;
				}
				boxes.put(cha.getStoredId(), cha.getPlayer().getNetConnection().getHWID());

			}
			// Synerge - Added protection to only allow x max class level to certain zones if set. It also checks if player has subclasses, that should be the same as having 3rd class
			final int maxClassLvl = zone.getParams().getInteger("maxClassLevelAllowed", -1);
			if (cha.getLevel() > zone.getParams().getInteger("levelLimit") || (maxClassLvl >= 0 && cha.getPlayer().getClassId().getLevel() > maxClassLvl) || (maxClassLvl >= 0 && cha.getPlayer().isSubClassActive()))
			{
				teleportOut(zone, cha);
				return;
			}

			// Synerge - Added protection to only allow x grade equip to certain zones if set
			if (cha.isPlayer())
			{
				final int maxGrade = zone.getParams().getInteger("maxGradeAllowed", -1);
				if (maxGrade > 0)
				{
					final Player player = cha.getPlayer();
					for (ItemInstance item : player.getInventory().getPaperdollItems())
					{
						if ((item == null) || (!item.isArmor() && !item.isWeapon()))
						{
							continue;
						}

						if (item.getCrystalType().ordinal() > maxGrade)
						{
							teleportOut(zone, cha);
							return;
						}
					}
				}
			}
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
			if (boxes.containsKey(cha.getStoredId()))
			{
				boxes.remove(cha.getStoredId());
			}
		}

		private void teleportOut(Zone zone, Creature cha)
		{
			cha.getPlayer().sendMessage(new CustomMessage("scripts.zones.epic.banishMsg", cha.getPlayer()));
			cha.teleToLocation(Location.parseLoc(zone.getParams().getString("tele")));
			if (boxes.containsKey(cha.getStoredId()))
			{
				boxes.remove(cha.getStoredId());
			}
		}

		private void teleportOutEpic(Zone zone, Creature cha)
		{
			cha.getPlayer().sendMessage(new CustomMessage("scripts.zones.epic.dualbox", cha.getPlayer()));
			cha.teleToLocation(Location.parseLoc(zone.getParams().getString("tele")));
			if (boxes.containsKey(cha.getStoredId()))
			{
				boxes.remove(cha.getStoredId());
			}
		}

		@Override
		public void onEquipChanged(Zone zone, Creature cha)
		{
			if (zone.getParams() == null || !cha.isPlayer())
			{
				return;
			}

			// Synerge - Added protection to only allow x grade equip to certain zones if set
			final int maxGrade = zone.getParams().getInteger("maxGradeAllowed", -1);
			if (maxGrade > 0)
			{
				final Player player = cha.getPlayer();
				for (ItemInstance item : player.getInventory().getPaperdollItems())
				{
					if ((item == null) || (!item.isArmor() && !item.isWeapon()))
					{
						continue;
					}

					if (item.getCrystalType().ordinal() > maxGrade)
					{
						teleportOut(zone, cha);
						return;
					}
				}
			}
		}
	}
}
