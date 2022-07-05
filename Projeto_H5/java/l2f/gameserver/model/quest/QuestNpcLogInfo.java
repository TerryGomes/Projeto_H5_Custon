package l2f.gameserver.model.quest;

public class QuestNpcLogInfo
{
	private final int[] _npcIds;
	private final String _varName;
	private final int _maxCount;

	public QuestNpcLogInfo(int[] npcIds, String varName, int maxCount)
	{
		_npcIds = npcIds;
		_varName = varName;
		_maxCount = maxCount;
	}

	public int[] getNpcIds()
	{
		return _npcIds;
	}

	public String getVarName()
	{
		return _varName;
	}

	public int getMaxCount()
	{
		return _maxCount;
	}
}
