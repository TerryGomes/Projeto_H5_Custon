package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.cache.CrestCache;
import l2mv.gameserver.network.serverpackets.AllianceCrest;

public class RequestAllyCrest extends L2GameClientPacket
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
		if (_crestId == 0)
		{
			return;
		}
		byte[] data = CrestCache.getInstance().getAllyCrest(_crestId);
		if (data != null)
		{
			AllianceCrest ac = new AllianceCrest(_crestId, data);
			sendPacket(ac);
		}
	}
}