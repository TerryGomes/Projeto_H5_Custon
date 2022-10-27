package l2mv.gameserver.network.serverpackets;

public class ExVitalityPointInfo extends L2GameServerPacket
{
	private final int _vitality;

	public ExVitalityPointInfo(int vitality)
	{
		this._vitality = vitality;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xA0);
		this.writeD(this._vitality);
	}
}