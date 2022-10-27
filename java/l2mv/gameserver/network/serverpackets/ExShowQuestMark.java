package l2mv.gameserver.network.serverpackets;

public class ExShowQuestMark extends L2GameServerPacket
{
	private int _questId;

	public ExShowQuestMark(int questId)
	{
		this._questId = questId;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x21);
		this.writeD(this._questId);
	}
}