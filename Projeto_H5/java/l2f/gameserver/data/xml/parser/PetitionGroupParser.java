package l2f.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;

import l2f.commons.data.xml.AbstractFileParser;
import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.PetitionGroupHolder;
import l2f.gameserver.model.petition.PetitionMainGroup;
import l2f.gameserver.model.petition.PetitionSubGroup;
import l2f.gameserver.utils.Language;

public class PetitionGroupParser extends AbstractFileParser<PetitionGroupHolder>
{
	private static PetitionGroupParser _instance = new PetitionGroupParser();

	public static PetitionGroupParser getInstance()
	{
		return _instance;
	}

	private PetitionGroupParser()
	{
		super(PetitionGroupHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/petition_group.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "petition_group.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element groupElement = iterator.next();
			PetitionMainGroup group = new PetitionMainGroup(Integer.parseInt(groupElement.attributeValue("id")));
			getHolder().addPetitionGroup(group);

			for (Iterator<Element> subIterator = groupElement.elementIterator(); subIterator.hasNext();)
			{
				Element subElement = subIterator.next();
				if ("name".equals(subElement.getName()))
				{
					group.setName(Language.valueOf(subElement.attributeValue("lang")), subElement.getText());
				}
				else if ("description".equals(subElement.getName()))
				{
					group.setDescription(Language.valueOf(subElement.attributeValue("lang")), subElement.getText());
				}
				else if ("sub_group".equals(subElement.getName()))
				{
					PetitionSubGroup subGroup = new PetitionSubGroup(Integer.parseInt(subElement.attributeValue("id")), subElement.attributeValue("handler"));
					group.addSubGroup(subGroup);
					for (Iterator<Element> sub2Iterator = subElement.elementIterator(); sub2Iterator.hasNext();)
					{
						Element sub2Element = sub2Iterator.next();
						if ("name".equals(sub2Element.getName()))
						{
							subGroup.setName(Language.valueOf(sub2Element.attributeValue("lang")), sub2Element.getText());
						}
						else if ("description".equals(sub2Element.getName()))
						{
							subGroup.setDescription(Language.valueOf(sub2Element.attributeValue("lang")), sub2Element.getText());
						}
					}
				}
			}
		}
	}
}
