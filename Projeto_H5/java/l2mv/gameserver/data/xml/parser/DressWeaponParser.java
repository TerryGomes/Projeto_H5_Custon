package l2mv.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;

import l2mv.commons.data.xml.AbstractFileParser;
import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.DressWeaponHolder;
import l2mv.gameserver.model.DressWeaponData;

public final class DressWeaponParser extends AbstractFileParser<DressWeaponHolder>
{
	private static final DressWeaponParser _instance = new DressWeaponParser();

	public static DressWeaponParser getInstance()
	{
		return _instance;
	}

	private DressWeaponParser()
	{
		super(DressWeaponHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/dress/weapon.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "weapon.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for (Iterator<Element> iterator = rootElement.elementIterator("weapon"); iterator.hasNext();)
		{
			String name, type;
			int id, itemId;
			long itemCount;
			Element dress = iterator.next();
			id = Integer.parseInt(dress.attributeValue("id"));
			name = dress.attributeValue("name");
			type = dress.attributeValue("type");

			Element price = dress.element("price");
			itemId = Integer.parseInt(price.attributeValue("id"));
			itemCount = Long.parseLong(price.attributeValue("count"));

			getHolder().addWeapon(new DressWeaponData(id, name, type, itemId, itemCount));
		}
	}
}