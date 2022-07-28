package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.templates.item.ItemTemplate;

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
		this._slot = this.readD();
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

		// You cannot do anything else while fishing
		if (activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
			return;
		}

		// Нельзя снимать проклятое оружие и флаги
		if ((this._slot == ItemTemplate.SLOT_R_HAND || this._slot == ItemTemplate.SLOT_L_HAND || this._slot == ItemTemplate.SLOT_LR_HAND) && (activeChar.isCursedWeaponEquipped() || activeChar.getActiveWeaponFlagAttachment() != null))
		{
			return;
		}

		if (this._slot == ItemTemplate.SLOT_R_HAND)
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

		activeChar.getInventory().unEquipItemInBodySlot(this._slot);
	}
}