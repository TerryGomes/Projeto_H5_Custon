package l2f.gameserver.network.clientpackets;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.templates.item.ItemTemplate;

//@Deprecated
public class RequestUnEquipItem extends L2GameClientPacket
{
	private int _slot;

	/**
	 * packet type id 0x16
	 * format:		cd
	 */
	@Override
	protected void readImpl()
	{
		_slot = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (activeChar.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}

		// You cannot do anything else while fishing
		if (activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
			return;
		}

		// Нельзя снимать проклятое оружие и флаги
		if ((_slot == ItemTemplate.SLOT_R_HAND || _slot == ItemTemplate.SLOT_L_HAND || _slot == ItemTemplate.SLOT_LR_HAND)
					&& (activeChar.isCursedWeaponEquipped() || activeChar.getActiveWeaponFlagAttachment() != null))
		{
			return;
		}

		if (_slot == ItemTemplate.SLOT_R_HAND)
		{
			ItemInstance weapon = activeChar.getActiveWeaponInstance();
			if (weapon == null)
			{
				return;
			}
			activeChar.abortAttack(true, true);
			activeChar.abortCast(true, true);
			activeChar.sendDisarmMessage(weapon);
		}

		activeChar.getInventory().unEquipItemInBodySlot(_slot);
	}
}