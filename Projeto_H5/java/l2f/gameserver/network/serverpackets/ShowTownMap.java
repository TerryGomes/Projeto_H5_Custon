package l2f.gameserver.network.serverpackets;

public class ShowTownMap extends L2GameServerPacket
{
	/**
	 * Format: csdd
	 */

	String _texture;
	int _x;
	int _y;

	public ShowTownMap(String texture, int x, int y)
	{
		_texture = texture;
		_x = x;
		_y = y;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xea);
		writeS(_texture);
		writeD(_x);
		writeD(_y);
	}
}