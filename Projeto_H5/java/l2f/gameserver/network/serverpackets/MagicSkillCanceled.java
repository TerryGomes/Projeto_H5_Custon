package l2f.gameserver.network.serverpackets;

public class MagicSkillCanceled extends L2GameServerPacket
{

	private int _objectId;

	public MagicSkillCanceled(int objectId)
	{
		_objectId = objectId;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x49);
		writeD(_objectId);
	}
}