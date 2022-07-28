package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.FinishRotating;

/**
 * format:		cdd
 */
public class FinishRotatingC extends L2GameClientPacket
{
	private int _degree;
	@SuppressWarnings("unused")
	private int _unknown;

	@Override
	protected void readImpl()
	{
		this._degree = this.readD();
		this._unknown = this.readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		activeChar.broadcastPacket(new FinishRotating(activeChar, this._degree, 0));
	}
}