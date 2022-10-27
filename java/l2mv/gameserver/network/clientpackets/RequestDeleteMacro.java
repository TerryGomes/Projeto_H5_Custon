package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;

public class RequestDeleteMacro extends L2GameClientPacket
{
	private int _id;

	/**
	 * format:		cd
	 */
	@Override
	protected void readImpl()
	{
		this._id = this.readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null || activeChar.isBlocked())
		{
			return;
		}
		activeChar.deleteMacro(this._id);
	}
}