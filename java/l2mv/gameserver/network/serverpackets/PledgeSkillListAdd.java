package l2mv.gameserver.network.serverpackets;

public class PledgeSkillListAdd extends L2GameServerPacket
{
	private int _skillId;
	private int _skillLevel;

	public PledgeSkillListAdd(int skillId, int skillLevel)
	{
		this._skillId = skillId;
		this._skillLevel = skillLevel;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x3b);
		this.writeD(this._skillId);
		this.writeD(this._skillLevel);
	}
}