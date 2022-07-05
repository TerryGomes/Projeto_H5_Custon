package l2f.gameserver.network.serverpackets;

public class TutorialShowQuestionMark extends L2GameServerPacket
{
	/**
	 * После клика по знаку вопроса клиент попросит html-ку с этим номером.
	 */
	private int _number;

	public TutorialShowQuestionMark(int number)
	{
		_number = number;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xa7);
		writeD(_number);
	}
}