package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.cache.CrestCache;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.pledge.Alliance;

public class RequestSetAllyCrest extends L2GameClientPacket
{
	private int _length;
	private byte[] _data;

	@Override
	protected void readImpl()
	{
		this._length = this.readD();
		if (this._length == CrestCache.ALLY_CREST_SIZE && this._length == this._buf.remaining())
		{
			this._data = new byte[this._length];
			this.readB(this._data);
		}
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		Alliance ally = activeChar.getAlliance();
		if (ally != null && activeChar.isAllyLeader())
		{
			int crestId = 0;

			if (this._data != null)
			{
				crestId = CrestCache.getInstance().saveAllyCrest(ally.getAllyId(), this._data);
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