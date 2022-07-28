package l2mv.gameserver.network.serverpackets;

public class ExEnchantSkillResult extends L2GameServerPacket
{
	public static final L2GameServerPacket SUCCESS = new ExEnchantSkillResult(1);
	public static final L2GameServerPacket FAIL = new ExEnchantSkillResult(0);

	private final int _result;

	public ExEnchantSkillResult(int result)
	{
		this._result = result;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xA7);
		this.writeD(this._result);
	}
}