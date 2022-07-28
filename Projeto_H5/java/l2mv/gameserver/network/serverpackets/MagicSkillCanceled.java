package l2mv.gameserver.network.serverpackets;

public class MagicSkillCanceled extends L2GameServerPacket
{

	private int _objectId;

	public MagicSkillCanceled(int objectId)
	{
		this._objectId = objectId;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x49);
		this.writeD(this._objectId);
	}
}