package l2f.gameserver.skills.skillclasses;

import java.util.List;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.GameObject;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.World;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.tables.PetDataTable;
import l2f.gameserver.templates.StatsSet;

public class PetSummon extends Skill
{
	public PetSummon(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		Player player = activeChar.getPlayer();
		if ((player == null) || (player.getPetControlItem() == null))
		{
			return false;
		}

		int npcId = PetDataTable.getSummonId(player.getPetControlItem());
		if (npcId == 0)
		{
			return false;
		}

		if (player.isInCombat())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_SUMMON_DURING_COMBAT);
			return false;
		}

		if (player.isProcessingRequest())
		{
			player.sendPacket(SystemMsg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
			return false;
		}

		if (player.isMounted() || player.getPet() != null)
		{
			player.sendPacket(SystemMsg.YOU_ALREADY_HAVE_A_PET);
			return false;
		}

		if (player.isInBoat())
		{
			player.sendPacket(SystemMsg.YOU_MAY_NOT_CALL_FORTH_A_PET_OR_SUMMONED_CREATURE_FROM_THIS_LOCATION);
			return false;
		}

		if (player.isInFlyingTransform())
		{
			return false;
		}

		if (player.isInOlympiadMode())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_USE_THAT_ITEM_IN_A_GRAND_OLYMPIAD_MATCH);
			return false;
		}

		if (player.isCursedWeaponEquipped())
		{
			player.sendPacket(SystemMsg.YOU_MAY_NOT_USE_MULTIPLE_PETS_OR_SERVITORS_AT_THE_SAME_TIME);
			return false;
		}

		if (player.isInFightClub())
		{
			player.sendMessage("You cannot use that item in Fight Club!");
			return false;
		}

		if (activeChar.getPlayer().isJailed())
		{
			player.sendMessage("You cannot use that item in Jail!");
			return false;
		}

		if (target.isPlayable() && target.getPlayer().isInFightClub())
		{
			player.sendMessage("You cannot use that item while target is on Fight Club!");
			return false;
		}

		for (GameObject o : World.getAroundObjects(player, 120, 200))
		{
			if (o.isDoor())
			{
				player.sendPacket(SystemMsg.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION);
				return false;
			}
		}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(Creature caster, List<Creature> targets)
	{
		Player activeChar = caster.getPlayer();

		activeChar.summonPet();

		if (isSSPossible())
		{
			caster.unChargeShots(isMagic());
		}
	}
}