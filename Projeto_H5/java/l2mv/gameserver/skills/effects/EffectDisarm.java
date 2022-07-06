package l2mv.gameserver.skills.effects;

import l2mv.gameserver.model.Effect;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.stats.Env;

public final class EffectDisarm extends Effect
{
	public EffectDisarm(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectDisarm(Effect effect)
	{
		super(effect);
	}

	@Override
	public boolean checkCondition()
	{
		if (!_effected.isPlayer())
		{
			return false;
		}
		Player player = _effected.getPlayer();
		// Нельзя снимать/одевать проклятое оружие и флаги
		if (player.isCursedWeaponEquipped() || player.getActiveWeaponFlagAttachment() != null)
		{
			return false;
		}
		// Synerge - Never let wards be disarmed
		if (player.getActiveWeaponInstance() != null && player.getActiveWeaponInstance().getItemId() >= 13560 && player.getActiveWeaponInstance().getItemId() <= 13568)
		{
			return false;
		}
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		Player player = (Player) _effected;

		ItemInstance wpn = player.getActiveWeaponInstance();
		if (wpn != null)
		{
			player.getInventory().unEquipItem(wpn);
			player.sendDisarmMessage(wpn);
		}
		player.startWeaponEquipBlocked();
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.stopWeaponEquipBlocked();
	}

	@Override
	public boolean onActionTime()
	{
		// just stop this effect
		return false;
	}
}