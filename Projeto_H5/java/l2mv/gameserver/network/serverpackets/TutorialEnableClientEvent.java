package l2mv.gameserver.network.serverpackets;

public class TutorialEnableClientEvent extends L2GameServerPacket
{
	private int _event = 0;

	public TutorialEnableClientEvent(int event)
	{
		this._event = event;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xa8);
		this.writeD(this._event);
	}
}