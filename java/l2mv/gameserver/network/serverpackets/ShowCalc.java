package l2mv.gameserver.network.serverpackets;

/**
 * sample: d
 */
public class ShowCalc extends L2GameServerPacket
{
	private int _calculatorId;

	public ShowCalc(int calculatorId)
	{
		this._calculatorId = calculatorId;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xe2);
		this.writeD(this._calculatorId);
	}
}