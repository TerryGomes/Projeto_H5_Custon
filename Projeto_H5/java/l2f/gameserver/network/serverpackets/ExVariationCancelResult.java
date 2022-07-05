package l2f.gameserver.network.serverpackets;

public class ExVariationCancelResult extends L2GameServerPacket
{
	private int _closeWindow;
	private int _unk1;

	public ExVariationCancelResult(int result)
	{
		_closeWindow = 1;
		_unk1 = result;
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0x58);
		writeD(_unk1);
		writeD(_closeWindow);
	}
}