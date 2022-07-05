package l2f.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.List;

import l2f.commons.data.xml.AbstractHolder;
import l2f.gameserver.model.premium.PremiumAccount;

public final class PremiumHolder extends AbstractHolder
{
	private static final PremiumHolder _instance = new PremiumHolder();

	public static PremiumHolder getInstance()
	{
		return _instance;
	}

	private List<PremiumAccount> _premium = new ArrayList<PremiumAccount>();

	public void addPremium(PremiumAccount premium)
	{
		_premium.add(premium);
	}

	public List<PremiumAccount> getAllPremiums()
	{
		return _premium;
	}

	public PremiumAccount getPremium(int id)
	{
		for (PremiumAccount premium : _premium)
		{
			if (premium.getId() == id)
			{
				return premium;
			}
		}
		return null;
	}

	@Override
	public int size()
	{
		return _premium.size();
	}

	@Override
	public void clear()
	{
		_premium.clear();
	}
}
