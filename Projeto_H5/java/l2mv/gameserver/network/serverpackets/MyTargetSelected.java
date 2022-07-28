package l2mv.gameserver.network.serverpackets;

/**
 *
 * <p>
 * sample  b9 73 5d 30 49 01 00 00 00 00 00
 * <p>
 * format dhd	(objectid, color, unk)
 * <p>
 * color 	-xx -> -9 	red<p>
 * 			-8  -> -6	light-red<p>
 * 			-5	-> -3	yellow<p>
 * 			-2	-> 2    white<p>
 * 			 3	-> 5	green<p>
 * 			 6	-> 8	light-blue<p>
 * 			 9	-> xx	blue<p>
 * <p>
 * usually the color equals the level difference to the selected target
 */
public class MyTargetSelected extends L2GameServerPacket
{
	private int _objectId;
	private int _color;

	/**
	 * @param int objectId of the target
	 * @param int level difference to the target. name color is calculated from that
	 */
	public MyTargetSelected(int objectId, int color)
	{
		this._objectId = objectId;
		this._color = color;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xb9);
		this.writeD(this._objectId);
		this.writeH(this._color);
		this.writeD(0x00);
	}
}