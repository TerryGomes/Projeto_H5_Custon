package l2f.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;

import gnu.trove.list.array.TIntArrayList;
import l2f.commons.data.xml.AbstractFileParser;
import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.HennaHolder;
import l2f.gameserver.templates.Henna;

/**
 * @author: VISTALL
 * @date:  9:04/06.12.2010
 */
public final class HennaParser extends AbstractFileParser<HennaHolder>
{
	private static final HennaParser _instance = new HennaParser();

	public static HennaParser getInstance()
	{
		return _instance;
	}

	protected HennaParser()
	{
		super(HennaHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/hennas.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "hennas.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element hennaElement = iterator.next();
			int symbolId = Integer.parseInt(hennaElement.attributeValue("symbol_id"));
			int dyeId = Integer.parseInt(hennaElement.attributeValue("dye_id"));
			long price = Integer.parseInt(hennaElement.attributeValue("price"));
			long drawCount = hennaElement.attributeValue("draw_count") == null ? 10 : Integer.parseInt(hennaElement.attributeValue("draw_count"));
			int wit = Integer.parseInt(hennaElement.attributeValue("wit"));
			int str = Integer.parseInt(hennaElement.attributeValue("str"));
			int _int = Integer.parseInt(hennaElement.attributeValue("int"));
			int con = Integer.parseInt(hennaElement.attributeValue("con"));
			int dex = Integer.parseInt(hennaElement.attributeValue("dex"));
			int men = Integer.parseInt(hennaElement.attributeValue("men"));

			TIntArrayList list = new TIntArrayList();
			for (Iterator<Element> classIterator = hennaElement.elementIterator("class"); classIterator.hasNext();)
			{
				Element classElement = classIterator.next();
				list.add(Integer.parseInt(classElement.attributeValue("id")));
			}

			Henna henna = new Henna(symbolId, dyeId, price, drawCount, wit, _int, con, str, dex, men, list);

			getHolder().addHenna(henna);
		}
	}
}
