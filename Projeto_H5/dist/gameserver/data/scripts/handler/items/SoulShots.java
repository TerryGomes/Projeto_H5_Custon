package handler.items;

import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.cache.Msg;
import l2f.gameserver.handler.items.ItemHandler;
import l2f.gameserver.model.Playable;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.ExAutoSoulShot;
import l2f.gameserver.network.serverpackets.MagicSkillUse;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.stats.Stats;
import l2f.gameserver.templates.item.WeaponTemplate;
import l2f.gameserver.templates.item.WeaponTemplate.WeaponType;

public class SoulShots extends ScriptItemHandler implements ScriptFile
{
	private static final int[] _itemIds =
	{
		5789,
		1835,
		1463,
		1464,
		1465,
		1466,
		1467,
		13037,
		13045,
		13055,
		22082,
		22083,
		22084,
		22085,
		22086
	};
	private static final int[] _skillIds =
	{
		2039,
		2150,
		2151,
		2152,
		2153,
		2154
	};

	@Override
	public boolean pickupItem(Playable playable, ItemInstance item)
	{
		return true;
	}

	@Override
	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}

	@Override
	public void onReload()
	{

	}

	@Override
	public void onShutdown()
	{

	}

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if (playable == null || !playable.isPlayer())
		{
			return false;
		}
		Player player = (Player) playable;

		WeaponTemplate weaponItem = player.getActiveWeaponItem();

		ItemInstance weaponInst = player.getActiveWeaponInstance();
		int SoulshotId = item.getItemId();
		boolean isAutoSoulShot = false;

		if (player.getAutoSoulShot().contains(SoulshotId))
		{
			isAutoSoulShot = true;
		}

		if (weaponInst == null)
		{
			if (!isAutoSoulShot)
			{
				player.sendPacket(Msg.CANNOT_USE_SOULSHOTS);
			}
			return false;
		}

		// soulshot is already active
		if (weaponInst.getChargedSoulshot() != ItemInstance.CHARGED_NONE)
		{
			return false;
		}

		int grade = weaponItem.getCrystalType().externalOrdinal;
		int soulShotConsumption = weaponItem.getSoulShotCount();

		if (soulShotConsumption == 0)
		{
			// Can't use soulshots
			if (isAutoSoulShot)
			{
				player.removeAutoSoulShot(SoulshotId);
				player.sendPacket(new ExAutoSoulShot(SoulshotId, false), new SystemMessage(SystemMessage.THE_AUTOMATIC_USE_OF_S1_WILL_NOW_BE_CANCELLED).addItemName(SoulshotId));
				return false;
			}
			player.sendPacket(Msg.CANNOT_USE_SOULSHOTS);
			return false;
		}

		if (grade == 0 && SoulshotId != 5789 && SoulshotId != 1835 // NG
					|| grade == 1 && SoulshotId != 1463 && SoulshotId != 22082 && SoulshotId != 13037 // D
					|| grade == 2 && SoulshotId != 1464 && SoulshotId != 22083 && SoulshotId != 13045 // C
					|| grade == 3 && SoulshotId != 1465 && SoulshotId != 22084 // B
					|| grade == 4 && SoulshotId != 1466 && SoulshotId != 22085 && SoulshotId != 13055 // A
					|| grade == 5 && SoulshotId != 1467 && SoulshotId != 22086 // S
		)
		{
			// wrong grade for weapon
			if (isAutoSoulShot)
			{
				return false;
			}
			player.sendPacket(Msg.SOULSHOT_DOES_NOT_MATCH_WEAPON_GRADE);
			return false;
		}

		if (weaponItem.getItemType() == WeaponType.BOW || weaponItem.getItemType() == WeaponType.CROSSBOW)
		{
			int newSS = (int) player.calcStat(Stats.SS_USE_BOW, soulShotConsumption, null, null);
			if (newSS < soulShotConsumption && Rnd.chance(player.calcStat(Stats.SS_USE_BOW_CHANCE, soulShotConsumption, null, null)))
			{
				soulShotConsumption = newSS;
			}
		}

		long count = item.getCount();

		if (Config.ALLOW_SOUL_SPIRIT_SHOT_INFINITELY && count >= 1)
		{
			weaponInst.setChargedSoulshot(ItemInstance.CHARGED_SOULSHOT);
			player.sendPacket(Msg.POWER_OF_THE_SPIRITS_ENABLED);
			player.broadcastPacket(new MagicSkillUse(player, player, _skillIds[grade], 1, 0, 0));
		}
		else if (!Config.ALLOW_SOUL_SPIRIT_SHOT_INFINITELY)
		{
			if (!player.getInventory().destroyItem(item, soulShotConsumption, null))
			{
				player.sendPacket(Msg.NOT_ENOUGH_SOULSHOTS);
				return false;
			}
			weaponInst.setChargedSoulshot(ItemInstance.CHARGED_SOULSHOT);
			player.sendPacket(Msg.POWER_OF_THE_SPIRITS_ENABLED);
			player.broadcastPacket(new MagicSkillUse(player, player, _skillIds[grade], 1, 0, 0));
		}
		return true;
	}

	@Override
	public final int[] getItemIds()
	{
		return _itemIds;
	}
}