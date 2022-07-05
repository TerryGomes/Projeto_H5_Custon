package l2f.gameserver.skills.skillclasses;

import java.util.List;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.instances.ChestInstance;
import l2f.gameserver.model.instances.DoorInstance;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.templates.StatsSet;

public class Unlock extends Skill
{
	private final int _unlockPower;

	public Unlock(StatsSet set)
	{
		super(set);
		_unlockPower = set.getInteger("unlockPower", 0) + 100;
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if (target == null || target instanceof ChestInstance && target.isDead())
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}

		if (target instanceof ChestInstance && activeChar.isPlayer())
		{
			return super.checkCondition(activeChar, target, forceUse, dontMove, first);
			// Unlock Fix
//		if (target instanceof ChestInstance && activeChar.isPlayer())
//		{
//			int charLevel = activeChar.getLevel();
//			int chestLevel = target.getLevel();
//
//			try
//			{
//				int levelDiff = chestLevel - charLevel;
//
//				if (chestLevel < 77)
//				{
//					if (levelDiff > 6 || levelDiff < -6)
//						return false;
//				}
//
//				else if (chestLevel < 78)
//				{
//					if (levelDiff > 5 || levelDiff < -5)
//						return false;
//				}
//
//				else
//				{
//					if (levelDiff > 3 || levelDiff < -3)
//						return false;
//				}
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//
//			return super.checkCondition(activeChar, target, forceUse, dontMove, first);
//		}
//		//unlock end
		}

		if (!target.isDoor() || _unlockPower == 0)
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}

		DoorInstance door = (DoorInstance) target;

		if (door.isOpen())
		{
			activeChar.sendPacket(SystemMsg.IT_IS_NOT_LOCKED);
			return false;
		}

		if (!door.isUnlockable() || (door.getKey() > 0) || (_unlockPower - door.getLevel() * 100 < 0)) // Дверь слишком высокого уровня
		{
			activeChar.sendPacket(SystemMsg.THIS_DOOR_CANNOT_BE_UNLOCKED);
			return false;
		}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for (Creature targ : targets)
		{
			if (targ != null)
			{
				if (targ.isDoor())
				{
					DoorInstance target = (DoorInstance) targ;
					if (!target.isOpen() && (target.getKey() > 0 || Rnd.chance(_unlockPower - target.getLevel() * 100)))
					{
						target.openMe((Player) activeChar, true);
					}
					else
					{
						activeChar.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_UNLOCK_THE_DOOR);
					}
				}
				else if (targ instanceof ChestInstance)
				{
					ChestInstance target = (ChestInstance) targ;
					if (!target.isDead())
					{
						target.tryOpen((Player) activeChar, this);
					}
				}
			}
		}
	}
}