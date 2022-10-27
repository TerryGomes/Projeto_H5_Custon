package l2mv.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;

import l2mv.commons.data.xml.AbstractFileParser;
import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.DressCloakHolder;
import l2mv.gameserver.model.DressCloakData;

public final class DressCloakParser extends AbstractFileParser<DressCloakHolder>
{
	private static final DressCloakParser _instance = new DressCloakParser();

	public static DressCloakParser getInstance()
	{
		return _instance;
	}

	private DressCloakParser()
	{
		super(DressCloakHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/dress/cloak.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "cloak.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for (Iterator<Element> iterator = rootElement.elementIterator("cloak"); iterator.hasNext();)
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

			getHolder().addCloak(new DressCloakData(number, id, name, itemId, itemCount));
		}
	}
}