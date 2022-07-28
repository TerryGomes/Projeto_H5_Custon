package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.ActionFail;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

public class Action extends L2GameClientPacket
{
	private int _objectId;
	private int _actionId;

	@Override
	protected void readImpl()
	{
		this._objectId = this.readD();
		this.readD(); // x
		this.readD(); // y
		this.readD(); // z
		this._actionId = this.readC();// 0 for simple click 1 for shift click
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (activeChar.isOutOfControl() || activeChar.isInStoreMode())
		{
			activeChar.sendActionFailed();
			return;
		}

		GameObject obj = activeChar.getVisibleObject(this._objectId);
		if (obj == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.setActive();

		if (activeChar.getAggressionTarget() != null && activeChar.getAggressionTarget() != obj)
		{
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.isLockedTarget())
		{
			if (activeChar.isClanAirShipDriver())
			{
				activeChar.sendPacket(SystemMsg.THIS_ACTION_IS_PROHIBITED_WHILE_STEERING);
			}

			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.isFrozen())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_FROZEN, ActionFail.STATIC);
			return;
		}

		obj.onAction(activeChar, this._actionId == 1);
	}
}