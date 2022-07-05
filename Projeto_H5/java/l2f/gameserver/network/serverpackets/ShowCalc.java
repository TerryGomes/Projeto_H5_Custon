package l2f.gameserver.network.serverpackets;

/**
 * sample: d
 */
public class ShowCalc extends L2GameServerPacket
{
	private int _calculatorId;

	public ShowCalc(int calculatorId)
	{
		_calculatorId = calculatorId;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xe2);
		writeD(_calculatorId);
	}
}