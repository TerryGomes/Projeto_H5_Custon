package l2f.gameserver.network.serverpackets;

public class ExChangeNpcState extends L2GameServerPacket
{
	private int _objId;
	private int _state;

	public ExChangeNpcState(int objId, int state)
	{
		_objId = objId;
		_state = state;
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0xBE);
		writeD(_objId);
		writeD(_state);
	}
}
