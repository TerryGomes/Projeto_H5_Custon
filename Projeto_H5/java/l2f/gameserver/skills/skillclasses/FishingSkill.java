package l2f.gameserver.skills.skillclasses;

import java.util.ArrayList;
import java.util.List;

import l2f.commons.util.Rnd;
import l2f.gameserver.geodata.GeoEngine;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.World;
import l2f.gameserver.model.Zone;
import l2f.gameserver.model.Zone.ZoneType;
import l2f.gameserver.model.items.Inventory;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.tables.FishTable;
import l2f.gameserver.templates.FishTemplate;
import l2f.gameserver.templates.StatsSet;
import l2f.gameserver.templates.item.WeaponTemplate;
import l2f.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2f.gameserver.utils.Location;
import l2f.gameserver.utils.PositionUtils;

public class FishingSkill extends Skill
{
	public FishingSkill(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		Player player = (Player) activeChar;

		if (player.getSkillLevel(SKILL_FISHING_MASTERY) == -1)
		{
			return false;
		}

		if (player.isFishing())
		{
			player.stopFishing();
			player.sendPacket(SystemMsg.CANCELS_FISHING);
			return false;
		}

		if (player.isInBoat())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_FISH_WHILE_RIDING_AS_A_PASSENGER_OF_A_BOAT__ITS_AGAINST_THE_RULES);
			return false;
		}

		if (player.getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_FISH_WHILE_USING_A_RECIPE_BOOK_PRIVATE_MANUFACTURE_OR_PRIVATE_STORE);
			return false;
		}

		if (!player.isInZone(ZoneType.FISHING) || player.isInWater())
		{
			player.sendPacket(SystemMsg.YOU_CANT_FISH_HERE);
			return false;
		}

		WeaponTemplate weaponItem = player.getActiveWeaponItem();
		if (weaponItem == null || weaponItem.getItemType() != WeaponType.ROD)
		{
			// Fishing poles are not installed
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_A_FISHING_POLE_EQUIPPED);
			return false;
		}

		ItemInstance lure = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		if (lure == null || lure.getCount() < 1)
		{
			player.sendPacket(SystemMsg.YOU_MUST_PUT_BAIT_ON_YOUR_HOOK_BEFORE_YOU_CAN_FISH);
			return false;
		}

		// Вычисляем координаты поплавка
		int rnd = Rnd.get(50) + 150;
		double angle = PositionUtils.convertHeadingToDegree(player.getHeading());
		double radian = Math.toRadians(angle - 90);
		double sin = Math.sin(radian);
		double cos = Math.cos(radian);
		int x1 = -(int) (sin * rnd);
		int y1 = (int) (cos * rnd);
		int x = player.getX() + x1;
		int y = player.getY() + y1;
		// z - уровень карты
		int z = GeoEngine.getHeight(x, y, player.getZ(), player.getGeoIndex()) + 1;

		// Проверяем, что поплавок оказался в воде
		boolean isInWater = false;
		List<Zone> zones = new ArrayList<>();
		World.getZones(zones, new Location(x, y, z), player.getReflection());
		for (Zone zone : zones)
		{
			if (zone.getType() == ZoneType.water)
			{
				// z - уровень воды
				z = zone.getTerritory().getZmax();
				isInWater = true;
				break;
			}
		}

		if (!isInWater)
		{
			player.sendPacket(SystemMsg.YOU_CANT_FISH_HERE);
			return false;
		}

		player.getFishing().setFishLoc(new Location(x, y, z));

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@SuppressWarnings("unused")
	@Override
	public void useSkill(Creature caster, List<Creature> targets)
	{
		if (caster == null || !caster.isPlayer())
		{
			return;
		}

		Player player = (Player) caster;

		ItemInstance lure = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		if (lure == null || lure.getCount() < 1)
		{
			player.sendPacket(SystemMsg.YOU_MUST_PUT_BAIT_ON_YOUR_HOOK_BEFORE_YOU_CAN_FISH);
			return;
		}
		Zone zone = player.getZone(ZoneType.FISHING);
		if (zone == null)
		{
			return;
		}

		int distributionId = zone.getParams().getInteger("distribution_id");

		int lureId = lure.getItemId();

		int group = l2f.gameserver.model.Fishing.getFishGroup(lure.getItemId());
		int type = l2f.gameserver.model.Fishing.getRandomFishType(lureId);
		int lvl = l2f.gameserver.model.Fishing.getRandomFishLvl(player);

		List<FishTemplate> fishs = FishTable.getInstance().getFish(group, type, lvl);
		if (fishs == null || fishs.size() == 0)
		{
			player.sendPacket(SystemMsg.SYSTEM_ERROR);
			return;
		}

		if (!player.getInventory().destroyItemByObjectId(player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LHAND), 1L, "FishingSkill"))
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_BAIT);
			return;
		}

		int check = Rnd.get(fishs.size());
		FishTemplate fish = fishs.get(check);

		player.startFishing(fish, lureId);
	}
}