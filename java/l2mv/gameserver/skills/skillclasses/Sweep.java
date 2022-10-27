package l2mv.gameserver.skills.skillclasses;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.instances.MonsterInstance;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.reward.RewardItem;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.templates.StatsSet;
import l2mv.gameserver.utils.ItemFunctions;

public class Sweep extends Skill
{
	public Sweep(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if (isNotTargetAoE())
		{
			return super.checkCondition(activeChar, target, forceUse, dontMove, first);
		}

		if (target == null)
		{
			return false;
		}

		if (!target.isMonster() || !target.isDead())
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}

		if (!((MonsterInstance) target).isSpoiled())
		{
			activeChar.sendPacket(SystemMsg.SWEEPER_FAILED_TARGET_NOT_SPOILED);
			return false;
		}

		if (!((MonsterInstance) target).isSpoiled((Player) activeChar))
		{
			activeChar.sendPacket(SystemMsg.THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER);
			return false;
		}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		if (!activeChar.isPlayer())
		{
			return;
		}

		Player player = (Player) activeChar;

		List<MonsterInstance> monstersToDecay = new ArrayList<>();

		for (Creature targ : targets)
		{
			if (targ == null || !targ.isMonster() || !targ.isDead() || !((MonsterInstance) targ).isSpoiled())
			{
				continue;
			}

			MonsterInstance target = (MonsterInstance) targ;

			if (!target.isSpoiled(player))
			{
				activeChar.sendPacket(SystemMsg.THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER);
				continue;
			}

			List<RewardItem> items = target.takeSweep();

			if (items == null)
			{
				activeChar.getAI().setAttackTarget(null);
				target.endDecayTask();
				continue;
			}

			for (RewardItem item : items)
			{
				ItemInstance sweep = ItemFunctions.createItem(item.itemId);
				sweep.setCount(item.count);

				if (player.isInParty() && player.getParty().isDistributeSpoilLoot())
				{
					player.getParty().distributeItem(player, sweep, null);
					continue;
				}

				if (!player.getInventory().validateCapacity(sweep) || !player.getInventory().validateWeight(sweep))
				{
					sweep.dropToTheGround(player, target);
					continue;
				}

				player.getInventory().addItem(sweep, "Sweep");

				SystemMessage2 smsg;
				if (item.count == 1)
				{
					smsg = new SystemMessage2(SystemMsg.YOU_HAVE_OBTAINED_S1);
					smsg.addItemName(item.itemId);
					player.sendPacket(smsg);
				}
				else
				{
					smsg = new SystemMessage2(SystemMsg.YOU_HAVE_OBTAINED_S2_S1);
					smsg.addItemName(item.itemId);
					smsg.addInteger(item.count);
					player.sendPacket(smsg);
				}
				if (player.isInParty())
				{
					if (item.count == 1)
					{
						smsg = new SystemMessage2(SystemMsg.C1_HAS_OBTAINED_S2_BY_USING_SWEEPER);
						smsg.addName(player);
						smsg.addItemName(item.itemId);
						player.getParty().sendPacket(player, smsg);
					}
					else
					{
						smsg = new SystemMessage2(SystemMsg.C1_HAS_OBTAINED_S3_S2_BY_USING_SWEEPER);
						smsg.addName(player);
						smsg.addItemName(item.itemId);
						smsg.addInteger(item.count);
						player.getParty().sendPacket(player, smsg);
					}
				}
			}

			activeChar.getAI().setAttackTarget(null);
			monstersToDecay.add(target);
		}

		for (MonsterInstance c : monstersToDecay)
		{
			c.endDecayTask();
		}
	}
}