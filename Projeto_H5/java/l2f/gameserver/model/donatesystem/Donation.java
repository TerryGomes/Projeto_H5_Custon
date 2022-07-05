package l2f.gameserver.model.donatesystem;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.data.xml.holder.ItemHolder;

public class Donation
{
	private final int _id;
	private final String _name;
	private final String _icon;
	private final int _group;
	private final boolean _havefound;
	private SimpleList _simple;
	private FoundList _found;
	private Enchant _enchant;
	private Attribution _attribution;

	public Donation(int id, String name, String icon, int group, boolean havefound)
	{
		_id = id;
		_name = name;
		_icon = icon;
		_group = group;
		_havefound = havefound;
	}

	public int getId()
	{
		return _id;
	}

	public String getName()
	{
		return _name;
	}

	public String getIcon()
	{
		return _icon;
	}

	public int getGroup()
	{
		return _group;
	}

	public boolean haveFound()
	{
		return _havefound;
	}

	public void addSimple(SimpleList list)
	{
		_simple = list;
	}

	public SimpleList getSimple()
	{
		return _simple;
	}

	public void addFound(FoundList list)
	{
		_found = list;
	}

	public FoundList getFound()
	{
		return _found;
	}

	public Enchant getEnchant()
	{
		return _enchant;
	}

	public void setEnchant(Enchant enchant)
	{
		_enchant = enchant;
	}

	public Attribution getAttribution()
	{
		return _attribution;
	}

	public void setAttribution(Attribution att)
	{
		_attribution = att;
	}

	public void print()
	{
		Iterator<DonateItem> i = _simple.getList().iterator();
		Logger _log = LoggerFactory.getLogger(Donation.class);

		_log.info("=========== Donate: " + _name + " (id: " + _id + ") ===========");
		_log.info("=== Icon: " + _icon);
		_log.info("=== Group: " + _group);
		_log.info("=== Have found: " + _havefound);
		_log.info("=== Simple items:");

		DonateItem item;
		while (i.hasNext())
		{
			item = i.next();
			_log.info("====> Item:" + ItemHolder.getInstance().getTemplate(item.getId()).getName() + " (id: " + item.getId() + ")");
			_log.info("====> Count: " + item.getCount());
			_log.info("====> Enchant: " + item.getEnchant());
		}

		if (_havefound)
		{
			_log.info("=== Foundation items:");
			i = _found.getList().iterator();

			while (i.hasNext())
			{
				item = i.next();
				_log.info("====> Item:" + ItemHolder.getInstance().getTemplate(item.getId()).getName() + " (id: " + item.getId() + ")");
				_log.info("====> Count: " + item.getCount());
				_log.info("====> Enchant: " + item.getEnchant());
			}
		}

		_log.info("=== Enchant: cost -> " + _enchant.getCount() + " " + _enchant.getId() + ", value -> " + _enchant.getEnchant());
		_log.info("=== Attribution: cost -> " + _attribution.getCount() + " " + _attribution.getId() + ", value -> " + _attribution.getValue() + ", size (Element count) -> " + _attribution.getSize());
	}
}
