package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.cache.CrestCache;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.PledgeCrest;

public class RequestPledgeCrest extends L2GameClientPacket
{
	// format: cd

	private int _crestId;

	@Override
	protected void readImpl()
	{
		_crestId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if ((activeChar == null) || (_crestId == 0))
		{
			return;
		}
		byte[] data = CrestCache.getInstance().getPledgeCrest(_crestId);
		if (data != null)
		{
			PledgeCrest pc = new PledgeCrest(_crestId, data);
			sendPacket(pc);
		}
	}
}