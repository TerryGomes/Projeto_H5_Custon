package l2f.gameserver.network.serverpackets;

public class TutorialEnableClientEvent extends L2GameServerPacket
{
	private int _event = 0;

	public TutorialEnableClientEvent(int event)
	{
		_event = event;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xa8);
		writeD(_event);
	}
}