package l2f.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;

import l2f.commons.data.xml.AbstractFileParser;
import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.ArmorSetsHolder;
import l2f.gameserver.model.ArmorSet;

public final class ArmorSetsParser extends AbstractFileParser<ArmorSetsHolder>
{
	private static final ArmorSetsParser _instance = new ArmorSetsParser();

	public static ArmorSetsParser getInstance()
	{
		return _instance;
	}

	private ArmorSetsParser()
	{
		super(ArmorSetsHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/armor_sets.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "armor_sets.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for (Iterator<Element> iterator = rootElement.elementIterator("set"); iterator.hasNext();)
		{
			String[] chest = null, legs = null, head = null, gloves = null, feet = null, skills = null, shield = null, shield_skills = null, enchant6skills = null;
			Element element = iterator.next();
			int id = Integer.parseInt(element.attributeValue("id"));
			if (element.attributeValue("chest") != null)
			{
				chest = element.attributeValue("chest").split(";");
			}
			if (element.attributeValue("legs") != null)
			{
				legs = element.attributeValue("legs").split(";");
			}
			if (element.attributeValue("head") != null)
			{
				head = element.attributeValue("head").split(";");
			}
			if (element.attributeValue("gloves") != null)
			{
				gloves = element.attributeValue("gloves").split(";");
			}
			if (element.attributeValue("feet") != null)
			{
				feet = element.attributeValue("feet").split(";");
			}
			if (element.attributeValue("skills") != null)
			{
				skills = element.attributeValue("skills").split(";");
			}
			if (element.attributeValue("shield") != null)
			{
				shield = element.attributeValue("shield").split(";");
			}
			if (element.attributeValue("shield_skills") != null)
			{
				shield_skills = element.attributeValue("shield_skills").split(";");
			}
			if (element.attributeValue("enchant6skills") != null)
			{
				enchant6skills = element.attributeValue("enchant6skills").split(";");
			}

			getHolder().addArmorSet(new ArmorSet(id, chest, legs, head, gloves, feet, skills, shield, shield_skills, enchant6skills));
		}
	}
}