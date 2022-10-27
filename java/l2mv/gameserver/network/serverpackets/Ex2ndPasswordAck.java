package l2mv.gameserver.network.serverpackets;

public class Ex2ndPasswordAck extends L2GameServerPacket
{
	public static final int SUCCESS = 0x00;
	public static final int WRONG_PATTERN = 0x01;

	int _response;

	public Ex2ndPasswordAck(int response)
	{
		this._response = response;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xE7);
		this.writeC(0x00);
		this.writeD(this._response == WRONG_PATTERN ? 0x01 : 0x00);
		this.writeD(0x00);
	}
}
