package l2mv.gameserver.network.serverpackets;

/**
 * Author: VISTALL
 */
public class ExSubPledgeSkillAdd extends L2GameServerPacket
{
	private int _type, _id, _level;

	public ExSubPledgeSkillAdd(int type, int id, int level)
	{
		this._type = type;
		this._id = id;
		this._level = level;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x76);
		this.writeD(this._type);
		this.writeD(this._id);
		this.writeD(this._level);
	}
}