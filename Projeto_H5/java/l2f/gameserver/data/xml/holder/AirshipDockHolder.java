package l2f.gameserver.data.xml.holder;

import gnu.trove.map.hash.TIntObjectHashMap;
import l2f.commons.data.xml.AbstractHolder;
import l2f.gameserver.templates.AirshipDock;

public final class AirshipDockHolder extends AbstractHolder
{
	private static final AirshipDockHolder _instance = new AirshipDockHolder();
	private TIntObjectHashMap<AirshipDock> _docks = new TIntObjectHashMap<AirshipDock>(4);

	public static AirshipDockHolder getInstance()
	{
		return _instance;
	}

	public void addDock(AirshipDock dock)
	{
		_docks.put(dock.getId(), dock);
	}

	public AirshipDock getDock(int dock)
	{
		return _docks.get(dock);
	}

	@Override
	public int size()
	{
		return _docks.size();
	}

	@Override
	public void clear()
	{
		_docks.clear();
	}
}
