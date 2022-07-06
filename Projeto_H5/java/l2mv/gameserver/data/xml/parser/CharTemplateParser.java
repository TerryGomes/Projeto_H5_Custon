package l2mv.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

import l2mv.commons.data.xml.AbstractFileParser;
import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.CharTemplateHolder;
import l2mv.gameserver.templates.StatsSet;
import l2mv.gameserver.templates.item.CreateItem;

public class CharTemplateParser extends AbstractFileParser<CharTemplateHolder>
{
	private static final CharTemplateParser _instance = new CharTemplateParser();

	public static CharTemplateParser getInstance()
	{
		return _instance;
	}

	protected CharTemplateParser()
	{
		super(CharTemplateHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/char_templates.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "char_templates.dtd";
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for (Iterator interator = rootElement.elementIterator(); interator.hasNext();)
		{
			List<CreateItem> items = new ArrayList<CreateItem>();

			Element element = (org.dom4j.Element) interator.next();
			StatsSet set = new StatsSet();

			int classId = Integer.parseInt(element.attributeValue("id"));
			String name = element.attributeValue("name");
			set.set("name", name);

			for (Iterator template = element.elementIterator(); template.hasNext();)
			{
				Element templat = (org.dom4j.Element) template.next();
				if (templat.getName().equalsIgnoreCase("set"))
				{
					set.set(templat.attributeValue("name"), templat.attributeValue("value"));
				}
				else if (templat.getName().equalsIgnoreCase("item"))
				{
					try
					{
						int itemId = Integer.parseInt(templat.attributeValue("id"));
						int count = Integer.parseInt(templat.attributeValue("count"));
						boolean equipable = false;
						int shortcat = -1;
						if (templat.attributeValue("equipable") != null)
						{
							equipable = Boolean.parseBoolean(templat.attributeValue("equipable"));
						}
						if (templat.attributeValue("shortcut") != null)
						{
							shortcat = Integer.parseInt(templat.attributeValue("shortcut"));
						}
						items.add(new CreateItem(itemId, count, equipable, shortcat));
					}
					catch (NumberFormatException e)
					{
						_log.error("Error parsing char_template, add item for classId " + set.get("classId") + ": ", e);
					}
				}
			}

			getHolder().addTemplate(classId, set, items);
		}
	}
}
