package l2mv.gameserver.network.serverpackets;

public class TutorialShowQuestionMark extends L2GameServerPacket
{
	/**
	 * После клика по знаку вопроса клиент попросит html-ку с этим номером.
	 */
	private int _number;

	public TutorialShowQuestionMark(int number)
	{
		this._number = number;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xa7);
		this.writeD(this._number);
	}
}