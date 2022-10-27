package l2mv.gameserver.network.serverpackets;

public class Dice extends L2GameServerPacket
{
	private int _playerId;
	private int _itemId;
	private int _number;
	private int _x;
	private int _y;
	private int _z;

	/**
	 * 0xd4 Dice         dddddd
	 * @param _characters
	 */
	public Dice(int playerId, int itemId, int number, int x, int y, int z)
	{
		this._playerId = playerId;
		this._itemId = itemId;
		this._number = number;
		this._x = x;
		this._y = y;
		this._z = z;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xda);
		this.writeD(this._playerId); // object id of player
		this.writeD(this._itemId); // item id of dice (spade) 4625,4626,4627,4628
		this.writeD(this._number); // number rolled
		this.writeD(this._x); // x
		this.writeD(this._y); // y
		this.writeD(this._z); // z
	}
}