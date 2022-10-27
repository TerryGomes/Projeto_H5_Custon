package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.skills.TimeStamp;

public class ExUseSharedGroupItem extends L2GameServerPacket
{
	private int _itemId, _grpId, _remainedTime, _totalTime;

	public ExUseSharedGroupItem(int grpId, TimeStamp timeStamp)
	{
		this._grpId = grpId;
		this._itemId = timeStamp.getId();
		this._remainedTime = (int) (timeStamp.getReuseCurrent() / 1000);
		this._totalTime = (int) (timeStamp.getReuseBasic() / 1000);
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x4a);

		this.writeD(this._itemId);
		this.writeD(this._grpId);
		this.writeD(this._remainedTime);
		this.writeD(this._totalTime);
	}
}