package l2f.gameserver.tables;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import gnu.trove.map.hash.TIntObjectHashMap;
import l2f.gameserver.Config;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.templates.item.ItemTemplate;

public class EnchantHPBonusTable
{
	private static Logger _log = LoggerFactory.getLogger(EnchantHPBonusTable.class);

	private final TIntObjectHashMap<Integer[]> _armorHPBonus = new TIntObjectHashMap<Integer[]>();

	private int _onepieceFactor = 100;

	private static EnchantHPBonusTable _instance = new EnchantHPBonusTable();

	public static EnchantHPBonusTable getInstance()
	{
		if (_instance == null)
		{
			_instance = new EnchantHPBonusTable();
		}
		return _instance;
	}

	public void reload()
	{
		_instance = new EnchantHPBonusTable();
	}

	private EnchantHPBonusTable()
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			File file = new File(Config.DATAPACK_ROOT, "data/enchant_bonus.xml");
			Document doc = factory.newDocumentBuilder().parse(file);

			for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if ("list".equalsIgnoreCase(n.getNodeName()))
				{
					for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						NamedNodeMap attrs = d.getAttributes();
						Node att;
						if ("options".equalsIgnoreCase(d.getNodeName()))
						{
							att = attrs.getNamedItem("onepiece_factor");
							if (att == null)
							{
								_log.info("EnchantHPBonusTable: Missing onepiece_factor, skipping");
								continue;
							}
							_onepieceFactor = Integer.parseInt(att.getNodeValue());
						}
						else if ("enchant_bonus".equalsIgnoreCase(d.getNodeName()))
						{
							Integer grade;

							att = attrs.getNamedItem("grade");
							if (att == null)
							{
								_log.info("EnchantHPBonusTable: Missing grade, skipping");
								continue;
							}
							grade = Integer.parseInt(att.getNodeValue());

							att = attrs.getNamedItem("values");
							if (att == null)
							{
								_log.info("EnchantHPBonusTable: Missing bonus id: " + grade + ", skipping");
								continue;
							}
							StringTokenizer st = new StringTokenizer(att.getNodeValue(), ",");
							int tokenCount = st.countTokens();
							Integer[] bonus = new Integer[tokenCount];
							for (int i = 0; i < tokenCount; i++)
							{
								Integer value = Integer.decode(st.nextToken().trim());
								if (value == null)
								{
									_log.info("EnchantHPBonusTable: Bad Hp value!! grade: " + grade + " token: " + i);
									value = 0;
								}
								bonus[i] = value;
							}
							_armorHPBonus.put(grade, bonus);
						}
					}
				}
			}
			_log.info("EnchantHPBonusTable: Loaded bonuses for " + _armorHPBonus.size() + " grades.");
		}
		catch (DOMException | SAXException | ParserConfigurationException | NumberFormatException | IOException e)
		{
			_log.warn("EnchantHPBonusTable: Lists could not be initialized.", e);
		}
	}

	public final int getHPBonus(ItemInstance item)
	{
		final Integer[] values;

		if (item.getEnchantLevel() == 0)
		{
			return 0;
		}

		values = _armorHPBonus.get(item.getTemplate().getCrystalType().externalOrdinal);

		if (values == null || values.length == 0)
		{
			return 0;
		}

		int bonus = values[Math.min(item.getEnchantLevel(), values.length) - 1];
		if (item.getTemplate().getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR)
		{
			bonus = (int) (bonus * _onepieceFactor / 100.0D);
		}

		return bonus;
	}
}
