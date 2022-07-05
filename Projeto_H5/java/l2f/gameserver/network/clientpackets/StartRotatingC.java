package l2f.gameserver.network.clientpackets;

import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.StartRotating;

/**
 * packet type id 0x5b
 * format:		cdd
 */
public class StartRotatingC extends L2GameClientPacket
{
	private int _degree;
	private int _side;

	@Override
	protected void readImpl()
	{
		_degree = readD();
		_side = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		activeChar.setHeading(_degree);
		activeChar.broadcastPacket(new StartRotating(activeChar, _degree, _side, 0));
	}
}