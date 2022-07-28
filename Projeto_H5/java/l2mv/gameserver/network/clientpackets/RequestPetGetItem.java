package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.ai.CtrlIntention;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Summon;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.utils.ItemFunctions;

public class RequestPetGetItem extends L2GameClientPacket
{
	// format: cd
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

		if (activeChar.isOutOfControl())
		{
			activeChar.sendActionFailed();
			return;
		}

		Summon summon = activeChar.getPet();
		if (summon == null || !summon.isPet() || summon.isDead() || summon.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}

		ItemInstance item = (ItemInstance) activeChar.getVisibleObject(this._objectId);
		if (item == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if (!ItemFunctions.checkIfCanPickup(summon, item))
		{
			SystemMessage2 sm;
			if (item.getItemId() == 57)
			{
				sm = new SystemMessage2(SystemMsg.YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA);
				sm.addInteger((int) item.getCount());
			}
			else
			{
				sm = new SystemMessage2(SystemMsg.YOU_HAVE_FAILED_TO_PICK_UP_S1);
				sm.addItemName(item.getItemId());
			}
			this.sendPacket(sm);
			activeChar.sendActionFailed();
			return;
		}

		summon.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, item, null);
	}
}