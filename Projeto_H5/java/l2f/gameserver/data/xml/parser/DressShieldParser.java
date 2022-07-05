package l2f.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;

import l2f.commons.data.xml.AbstractFileParser;
import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.DressShieldHolder;
import l2f.gameserver.model.DressShieldData;

public final class DressShieldParser extends AbstractFileParser<DressShieldHolder>
{
	private static final DressShieldParser _instance = new DressShieldParser();

	public static DressShieldParser getInstance()
	{
		return _instance;
	}

	private DressShieldParser()
	{
		super(DressShieldHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/dress/shield.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "shield.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for (Iterator<Element> iterator = rootElement.elementIterator("shield"); iterator.hasNext();)
		{
			String name = null;
			int id, number, itemId;
			long itemCount;
			Element dress = iterator.next();
			number = Integer.parseInt(dress.attributeValue("number"));
			id = Integer.parseInt(dress.attributeValue("id"));
			name = dress.attributeValue("name");

			Element price = dress.element("price");
			itemId = Integer.parseInt(price.attributeValue("id"));
			itemCount = Long.parseLong(price.attributeValue("count"));

			getHolder().addShield(new DressShieldData(number, id, name, itemId, itemCount));
		}
	}
}