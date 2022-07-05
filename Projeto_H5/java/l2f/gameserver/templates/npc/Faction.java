package l2f.gameserver.templates.npc;

import gnu.trove.list.array.TIntArrayList;
import l2f.commons.util.TroveUtils;

public class Faction
{
	public final static String none = "none";
	public final static Faction NONE = new Faction(none);

	public final String factionId;
	public int factionRange;
	public TIntArrayList ignoreId = TroveUtils.EMPTY_INT_ARRAY_LIST;

	public Faction(String factionId)
	{
		this.factionId = factionId;
	}

	public String getName()
	{
		return factionId;
	}

	public void setRange(int factionRange)
	{
		this.factionRange = factionRange;
	}

	public int getRange()
	{
		return factionRange;
	}

	public void addIgnoreNpcId(int npcId)
	{
		if (ignoreId.isEmpty())
		{
			ignoreId = new TIntArrayList();
		}
		ignoreId.add(npcId);
	}

	public boolean isIgnoreNpcId(int npcId)
	{
		return ignoreId.contains(npcId);
	}

	public boolean isNone()
	{
		return factionId.isEmpty() || factionId.equals(none);
	}

	public boolean equals(Faction faction)
	{
		if (isNone() || !faction.getName().equalsIgnoreCase(factionId))
		{
			return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if ((o == null) || (o.getClass() != this.getClass()))
		{
			return false;
		}
		return equals((Faction) o);
	}

	@Override
	public String toString()
	{
		return isNone() ? none : factionId;
	}
}
