package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.skills.TimeStamp;

public class ExUseSharedGroupItem extends L2GameServerPacket
{
	private int _itemId, _grpId, _remainedTime, _totalTime;

	public ExUseSharedGroupItem(int grpId, TimeStamp timeStamp)
	{
		_grpId = grpId;
		_itemId = timeStamp.getId();
		_remainedTime = (int) (timeStamp.getReuseCurrent() / 1000);
		_totalTime = (int) (timeStamp.getReuseBasic() / 1000);
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0x4a);

		writeD(_itemId);
		writeD(_grpId);
		writeD(_remainedTime);
		writeD(_totalTime);
	}
}