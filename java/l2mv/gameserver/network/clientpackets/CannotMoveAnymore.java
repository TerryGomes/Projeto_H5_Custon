package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.ai.CtrlEvent;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.utils.Location;

public class CannotMoveAnymore extends L2GameClientPacket
{
	private Location _loc = new Location();

	@Override
	protected void readImpl()
	{
		this._loc.x = this.readD();
		this._loc.y = this.readD();
		this._loc.z = this.readD();
		this._loc.h = this.readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		activeChar.getAI().notifyEvent(CtrlEvent.EVT_ARRIVED_BLOCKED, this._loc, null);
	}
}