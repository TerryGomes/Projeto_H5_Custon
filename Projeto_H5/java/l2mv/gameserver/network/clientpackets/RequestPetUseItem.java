package l2mv.gameserver.network.clientpackets;

import org.apache.commons.lang3.ArrayUtils;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.instances.PetInstance;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.utils.ItemFunctions;

public class RequestPetUseItem extends L2GameClientPacket
{
	private int _objectId;

	@Override
	protected void readImpl()
	{
		this._objectId = this.readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (activeChar.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
			return;
		}

		activeChar.setActive();

		PetInstance pet = (PetInstance) activeChar.getPet();
		if (pet == null)
		{
			return;
		}

		ItemInstance item = pet.getInventory().getItemByObjectId(this._objectId);

		if (item == null || item.getCount() < 1)
		{
			return;
		}

		if (activeChar.isAlikeDead() || pet.isDead() || pet.isOutOfControl())
		{
			activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return;
		}

		// manual pet feeding
		if (pet.tryFeedItem(item))
		{
			return;
		}

		if (ArrayUtils.contains(Config.ALT_ALLOWED_PET_POTIONS, item.getItemId()))
		{
			Skill[] skills = item.getTemplate().getAttachedSkills();
			if (skills.length > 0)
			{
				for (Skill skill : skills)
				{
					Creature aimingTarget = skill.getAimingTarget(pet, pet.getTarget());
					if (skill.checkCondition(pet, aimingTarget, false, false, true))
					{
						pet.getAI().Cast(skill, aimingTarget, false, false);
					}
				}
			}
			return;
		}

		SystemMessage2 sm = ItemFunctions.checkIfCanEquip(pet, item);
		if (sm == null)
		{
			if (item.isEquipped())
			{
				pet.getInventory().unEquipItem(item);
			}
			else
			{
				pet.getInventory().equipItem(item);
			}
			pet.broadcastCharInfo();
			return;
		}

		activeChar.sendPacket(sm);
	}
}