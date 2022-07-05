package l2f.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

import l2f.commons.data.xml.AbstractFileParser;
import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.DonationHolder;
import l2f.gameserver.model.donatesystem.Attribution;
import l2f.gameserver.model.donatesystem.DonateItem;
import l2f.gameserver.model.donatesystem.Donation;
import l2f.gameserver.model.donatesystem.Enchant;
import l2f.gameserver.model.donatesystem.FoundList;
import l2f.gameserver.model.donatesystem.SimpleList;

public final class DonationParse extends AbstractFileParser<DonationHolder>
{
	private static final DonationParse _instance = new DonationParse();

	private DonationParse()
	{
		super(DonationHolder.getInstance());
	}

	public static DonationParse getInstance()
	{
		return _instance;
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/donation/donation.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "donation.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		Donation donate;
		for (Iterator<?> iterator = rootElement.elementIterator("donation"); iterator.hasNext(); getHolder().addDonate(donate))
		{
			Element donation = (Element) iterator.next();
			int id = Integer.parseInt(donation.attributeValue("id"));
			String name = donation.attributeValue("name");
			String icon = donation.attributeValue("icon");
			int group = Integer.parseInt(donation.attributeValue("group"));
			boolean found = Boolean.parseBoolean(donation.attributeValue("found"));
			donate = new Donation(id, name, icon, group, found);
			Element simple = donation.element("simples");
			int s_id = Integer.parseInt(simple.attributeValue("cost_id"));
			long s_count = Long.parseLong(simple.attributeValue("cost_count"));
			SimpleList s_list = new SimpleList(s_id, s_count, simple_parse(simple));
			donate.addSimple(s_list);
			Element foundation = donation.element("foundations");
			if (found)
			{
				if (foundation != null)
				{
					int enchant = Integer.parseInt(foundation.attributeValue("cost_id"));
					long attribution = Long.parseLong(foundation.attributeValue("cost_count"));
					FoundList a_count = new FoundList(enchant, attribution, donate_parse(foundation));
					donate.addFound(a_count);
				}
				else
				{
					_log.error("Problem with donate " + name + ", found is on but is null!");
				}
			}

			Element enchant1 = donation.element("enchant");
			if (enchant1 != null)
			{
				int attribution1 = Integer.parseInt(enchant1.attributeValue("cost_id"));
				long a_id = Long.parseLong(enchant1.attributeValue("cost_count"));
				int e_value = Integer.parseInt(enchant1.attributeValue("value"));
				Enchant a_value = new Enchant(attribution1, a_id, e_value);
				donate.setEnchant(a_value);
			}

			Element attribution2 = donation.element("attribution");
			if (attribution2 != null)
			{
				int a_id1 = Integer.parseInt(attribution2.attributeValue("cost_id"));
				long a_count1 = Long.parseLong(attribution2.attributeValue("cost_count"));
				int a_value1 = Integer.parseInt(attribution2.attributeValue("value"));
				int size = Integer.parseInt(attribution2.attributeValue("size"));
				Attribution atr = new Attribution(a_id1, a_count1, a_value1, size);
				donate.setAttribution(atr);
			}
		}

	}

	private List<DonateItem> simple_parse(Element n)
	{
		ArrayList<DonateItem> list = new ArrayList<DonateItem>();
		Iterator<?> iterator = n.elementIterator();

		while (iterator.hasNext())
		{
			Element d = (Element) iterator.next();
			if ("simple".equalsIgnoreCase(d.getName()))
			{
				int id = Integer.parseInt(d.attributeValue("id"));
				long count = Long.parseLong(d.attributeValue("count"));
				int enchant = Integer.parseInt(d.attributeValue("enchant"));
				DonateItem donate = new DonateItem(id, count, enchant);
				list.add(donate);
			}
		}

		return list;
	}

	private List<DonateItem> donate_parse(Element n)
	{
		ArrayList<DonateItem> list = new ArrayList<DonateItem>();
		Iterator<?> iterator = n.elementIterator();

		while (iterator.hasNext())
		{
			Element d = (Element) iterator.next();
			if ("foundation".equalsIgnoreCase(d.getName()))
			{
				int id = Integer.parseInt(d.attributeValue("id"));
				long count = Long.parseLong(d.attributeValue("count"));
				int enchant = Integer.parseInt(d.attributeValue("enchant"));
				DonateItem donate = new DonateItem(id, count, enchant);
				list.add(donate);
			}
		}

		return list;
	}
}
