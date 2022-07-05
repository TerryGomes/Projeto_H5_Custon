package l2f.gameserver.model;

public final class SkillLearn implements Comparable<SkillLearn>
{
	private final int _id;
	private final int _level;
	private final int _minLevel;
	private final int _cost;
	private final int _itemId;
	private final long _itemCount;
	private final boolean _clicked;

	public SkillLearn(int id, int lvl, int minLvl, int cost, int itemId, long itemCount, boolean clicked)
	{
		_id = id;
		_level = lvl;
		_minLevel = minLvl;
		_cost = cost;

		_itemId = itemId;
		_itemCount = itemCount;
		_clicked = clicked;
	}

	public int getId()
	{
		return _id;
	}

	public int getLevel()
	{
		return _level;
	}

	public int getMinLevel()
	{
		return _minLevel;
	}

	public int getCost()
	{
		return _cost;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public long getItemCount()
	{
		return _itemCount;
	}

	public boolean isClicked()
	{
		return _clicked;
	}

	@Override
	public int compareTo(SkillLearn o)
	{
		if (getId() == o.getId())
		{
			return getLevel() - o.getLevel();
		}
		else
		{
			return getId() - o.getId();
		}
	}
}