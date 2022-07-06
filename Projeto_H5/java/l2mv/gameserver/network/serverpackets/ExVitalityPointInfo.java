package l2mv.gameserver.network.serverpackets;

public class ExVitalityPointInfo extends L2GameServerPacket
{
	private final int _vitality;

	public ExVitalityPointInfo(int vitality)
	{
		_vitality = vitality;
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0xA0);
		writeD(_vitality);
	}
}