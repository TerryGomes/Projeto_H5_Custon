package l2f.gameserver.network.serverpackets;

public class CameraMode extends L2GameServerPacket
{
	int _mode;

	/**
	 * Forces client camera mode change
	 * @param mode
	 * 0 - third person cam
	 * 1 - first person cam
	 */
	public CameraMode(int mode)
	{
		_mode = mode;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xf7);
		writeD(_mode);
	}
}