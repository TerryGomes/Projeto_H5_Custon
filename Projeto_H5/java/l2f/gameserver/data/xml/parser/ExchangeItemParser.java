package l2f.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

import l2f.commons.data.xml.AbstractFileParser;
import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.ExchangeItemHolder;
import l2f.gameserver.model.exchange.Change;
import l2f.gameserver.model.exchange.Variant;

public final class ExchangeItemParser extends AbstractFileParser<ExchangeItemHolder>
{
	private static final ExchangeItemParser _instance = new ExchangeItemParser();

	public static ExchangeItemParser getInstance()
	{
		return _instance;
	}

	private ExchangeItemParser()
	{
		super(ExchangeItemHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/exchange.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "exchange.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for (Iterator<Element> iterator = rootElement.elementIterator("change"); iterator.hasNext();)
		{
			Element change_data = iterator.next();
			int id = Integer.parseInt(change_data.attributeValue("id"));
			String name = change_data.attributeValue("name");
			String icon = change_data.attributeValue("icon");
			int cost_id = Integer.parseInt(change_data.attributeValue("cost_id"));
			long cost_count = Long.parseLong(change_data.attributeValue("cost_count"));
			boolean attribute_change = Boolean.parseBoolean(change_data.attributeValue("attribute_change"));
			boolean is_upgrade = Boolean.parseBoolean(change_data.attributeValue("is_upgrade"));

			getHolder().addChanges(new Change(id, name, icon, cost_id, cost_count, attribute_change, is_upgrade, parseVariants(change_data)));
		}
	}

	private List<Variant> parseVariants(Element n)
	{
		List<Variant> list = new ArrayList<Variant>();
		for (Iterator<Element> iterator = n.elementIterator(); iterator.hasNext();)
		{
			Element element = iterator.next();
			if ("variant".equalsIgnoreCase(element.getName()))
			{
				int number = Integer.parseInt(element.attributeValue("number"));
				int id = Integer.parseInt(element.attributeValue("id"));
				String name = element.attributeValue("name");
				String icon = element.attributeValue("icon");

				list.add(new Variant(number, id, name, icon));
			}
		}
		return list;
	}
}
