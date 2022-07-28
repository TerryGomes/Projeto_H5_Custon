package l2mv.gameserver.network.serverpackets;

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
		this._texture = texture;
		this._x = x;
		this._y = y;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xea);
		this.writeS(this._texture);
		this.writeD(this._x);
		this.writeD(this._y);
	}
}