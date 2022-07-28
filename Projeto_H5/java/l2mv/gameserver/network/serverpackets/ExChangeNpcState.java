package l2mv.gameserver.network.serverpackets;

public class ExChangeNpcState extends L2GameServerPacket
{
	private int _objId;
	private int _state;

	public ExChangeNpcState(int objId, int state)
	{
		this._objId = objId;
		this._state = state;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xBE);
		this.writeD(this._objId);
		this.writeD(this._state);
	}
}
