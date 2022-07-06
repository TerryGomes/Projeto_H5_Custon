package l2mv.gameserver.network.serverpackets;

public class KeyPacket extends L2GameServerPacket
{
	private final byte[] _key;

	public KeyPacket(byte key[])
	{
		_key = key;
	}

	@Override
	public void writeImpl()
	{
		writeC(0x2E);
		if (_key == null || _key.length == 0)
		{
			writeC(0x00);
			return;
		}
		writeC(0x01);
		writeB(_key);
		writeD(0x01);
		writeD(0x00);
		writeC(0x00);
		writeD(0x00); // Seed (obfuscation key)
	}
}