package l2mv.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import l2mv.commons.data.xml.AbstractHolder;
import l2mv.gameserver.model.donatesystem.Donation;

public final class DonationHolder extends AbstractHolder
{
	private static final DonationHolder _instance = new DonationHolder();
	private List<Donation> _donate = new ArrayList<Donation>();

	public static DonationHolder getInstance()
	{
		return _instance;
	}

	public void addDonate(Donation donate)
	{
		_donate.add(donate);
	}

	public List<Donation> getAllDonates()
	{
		return _donate;
	}

	public Donation getDonate(int id)
	{
		Iterator<Donation> i = _donate.iterator();

		Donation donate;
		do
		{
			if (!i.hasNext())
			{
				return null;
			}

			donate = i.next();
		}
		while (donate.getId() != id);

		return donate;
	}

	public List<Donation> getGroup(int id)
	{
		ArrayList<Donation> group = new ArrayList<Donation>();
		Iterator<Donation> i = _donate.iterator();

		while (i.hasNext())
		{
			Donation donate = i.next();
			if (donate.getGroup() == id)
			{
				group.add(donate);
			}
		}

		return group;
	}

	@Override
	public int size()
	{
		return _donate.size();
	}

	@Override
	public void clear()
	{
		_donate.clear();
	}
}
