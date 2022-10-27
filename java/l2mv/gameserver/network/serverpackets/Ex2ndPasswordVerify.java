package l2mv.gameserver.network.serverpackets;

public class Ex2ndPasswordVerify extends L2GameServerPacket
{
	public static final int PASSWORD_OK = 0x00;
	public static final int PASSWORD_WRONG = 0x01;
	public static final int PASSWORD_BAN = 0x02;

	int _wrongTentatives, _mode;

	public Ex2ndPasswordVerify(int mode, int wrongTentatives)
	{
		this._mode = mode;
		this._wrongTentatives = wrongTentatives;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xE6);
		this.writeD(this._mode);
		this.writeD(this._wrongTentatives);
	}
}
