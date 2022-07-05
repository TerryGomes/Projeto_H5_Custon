package l2f.gameserver.network.clientpackets;

import l2f.gameserver.cache.CrestCache;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.pledge.Alliance;

public class RequestSetAllyCrest extends L2GameClientPacket
{
	private int _length;
	private byte[] _data;

	@Override
	protected void readImpl()
	{
		_length = readD();
		if (_length == CrestCache.ALLY_CREST_SIZE && _length == _buf.remaining())
		{
			_data = new byte[_length];
			readB(_data);
		}
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		Alliance ally = activeChar.getAlliance();
		if (ally != null && activeChar.isAllyLeader())
		{
			int crestId = 0;

			if (_data != null)
			{
				crestId = CrestCache.getInstance().saveAllyCrest(ally.getAllyId(), _data);
			}
			else if (ally.hasAllyCrest())
			{
				CrestCache.getInstance().removeAllyCrest(ally.getAllyId());
			}

			ally.setAllyCrestId(crestId);
			ally.broadcastAllyStatus();
		}
	}
}