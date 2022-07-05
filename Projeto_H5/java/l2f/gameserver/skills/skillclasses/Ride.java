package l2f.gameserver.skills.skillclasses;

import java.util.List;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.templates.StatsSet;

public class Ride extends Skill
{
	public Ride(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if (!activeChar.isPlayer())
		{
			return false;
		}

		Player player = (Player) activeChar;
		if (getNpcId() != 0)
		{
			if (player.isInOlympiadMode())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_USE_THAT_ITEM_IN_A_GRAND_OLYMPIAD_MATCH);
				return false;
			}
			if (player.isInDuel() || player.isSitting() || player.isInCombat() || player.isFishing() || player.isCursedWeaponEquipped() || player.getTransformation() != 0 || player.getPet() != null
						|| player.isMounted() || player.isInBoat())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_BOARD_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
				return false;
			}
		}
		else if (getNpcId() == 0 && !player.isMounted())
		{
			return false;
		}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(Creature caster, List<Creature> targets)
	{
		if (!caster.isPlayer())
		{
			return;
		}

		Player activeChar = (Player) caster;
		activeChar.setMount(getNpcId(), 0, 0);
	}
}