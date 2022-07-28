package l2mv.gameserver.network.serverpackets;

public class ExChangeClientEffectInfo extends L2GameServerPacket
{
	private int _state;

	public ExChangeClientEffectInfo(int state)
	{
		this._state = state;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xC1);
		this.writeD(0);
		this.writeD(this._state);
	}
}
