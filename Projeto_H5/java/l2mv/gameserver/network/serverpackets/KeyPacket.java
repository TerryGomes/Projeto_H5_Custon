package l2mv.gameserver.network.serverpackets;

public class KeyPacket extends L2GameServerPacket
{
	private final byte[] _key;

	public KeyPacket(byte key[])
	{
		this._key = key;
	}

	@Override
	public void writeImpl()
	{
		this.writeC(0x2E);
		if (this._key == null || this._key.length == 0)
		{
			this.writeC(0x00);
			return;
		}
		this.writeC(0x01);
		this.writeB(this._key);
		this.writeD(0x01);
		this.writeD(0x00);
		this.writeC(0x00);
		this.writeD(0x00); // Seed (obfuscation key)
	}
}