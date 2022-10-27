package l2mv.gameserver.skills.skillclasses;

import java.util.List;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Fishing;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.templates.StatsSet;
import l2mv.gameserver.templates.item.WeaponTemplate;

public class ReelingPumping extends Skill
{

	public ReelingPumping(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if (!((Player) activeChar).isFishing())
		{
			activeChar.sendPacket(getSkillType() == SkillType.PUMPING ? SystemMsg.YOU_MAY_ONLY_USE_THE_PUMPING_SKILL_WHILE_YOU_ARE_FISHING : SystemMsg.YOU_MAY_ONLY_USE_THE_REELING_SKILL_WHILE_YOU_ARE_FISHING);
			activeChar.sendActionFailed();
			return false;
		}
		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(Creature caster, List<Creature> targets)
	{
		if (caster == null || !caster.isPlayer())
		{
			return;
		}

		Player player = caster.getPlayer();
		Fishing fishing = player.getFishing();
		if (fishing == null || !fishing.isInCombat())
		{
			return;
		}

		WeaponTemplate weaponItem = player.getActiveWeaponItem();
		int SS = player.getChargedFishShot() ? 2 : 1;
		int pen = 0;
		double gradebonus = 1 + weaponItem.getCrystalType().ordinal() * 0.1;
		int dmg = (int) (getPower() * gradebonus * SS);

		if (player.getSkillLevel(1315) < getLevel() - 2) // 1315 - Fish Expertise
		{
			// Penalty
			player.sendPacket(SystemMsg.DUE_TO_YOUR_REELING_ANDOR_PUMPING_SKILL_BEING_THREE_OR_MORE_LEVELS_HIGHER_THAN_YOUR_FISHING_SKILL_A_50_DAMAGE_PENALTY_WILL_BE_APPLIED);
			pen = 50;
			int penatlydmg = dmg - pen;
			dmg = penatlydmg;
		}

		if (SS == 2)
		{
			player.unChargeFishShot();
		}

		fishing.useFishingSkill(dmg, pen, getSkillType());
	}
}