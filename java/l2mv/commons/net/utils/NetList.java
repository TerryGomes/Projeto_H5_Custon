package l2mv.commons.net.utils;

import java.util.ArrayList;
import java.util.Iterator;

public final class NetList extends ArrayList<Net>
{
	private static final long serialVersionUID = 4266033257195615387L;

	public boolean isInRange(final String address)
	{
		return stream().anyMatch(net -> net.isInRange(address));
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (Iterator<Net> itr = iterator(); itr.hasNext();)
		{
			sb.append(itr.next());
			if (itr.hasNext())
			{
				sb.append(',');
			}
		}
		return sb.toString();
	}
}