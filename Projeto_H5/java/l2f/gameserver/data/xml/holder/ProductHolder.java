package l2f.gameserver.data.xml.holder;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import l2f.gameserver.Config;
import l2f.gameserver.model.ProductItem;
import l2f.gameserver.model.ProductItemComponent;

public class ProductHolder
{
	private static Logger _log = LoggerFactory.getLogger(ProductHolder.class.getName());
	TreeMap<Integer, ProductItem> _itemsList;

	private static ProductHolder _instance = new ProductHolder();

	public static ProductHolder getInstance()
	{
		if (_instance == null)
		{
			_instance = new ProductHolder();
		}
		return _instance;
	}

	public void reload()
	{
		_instance = new ProductHolder();
	}

	private ProductHolder()
	{
		_itemsList = new TreeMap<Integer, ProductItem>();

		try
		{
			File file = new File(Config.DATAPACK_ROOT, "data/item-mall.xml");
			DocumentBuilderFactory factory1 = DocumentBuilderFactory.newInstance();
			factory1.setValidating(false);
			factory1.setIgnoringComments(true);
			Document doc1 = factory1.newDocumentBuilder().parse(file);

			for (Node n1 = doc1.getFirstChild(); n1 != null; n1 = n1.getNextSibling())
			{
				if ("list".equalsIgnoreCase(n1.getNodeName()))
				{
					for (Node d1 = n1.getFirstChild(); d1 != null; d1 = d1.getNextSibling())
					{
						if ("product".equalsIgnoreCase(d1.getNodeName()))
						{
							Node onSaleNode = d1.getAttributes().getNamedItem("on_sale");
							Boolean onSale = onSaleNode != null && Boolean.parseBoolean(onSaleNode.getNodeValue());
							if (!onSale)
							{
								continue;
							}

							int productId = Integer.parseInt(d1.getAttributes().getNamedItem("id").getNodeValue());

							Node categoryNode = d1.getAttributes().getNamedItem("category");
							int category = categoryNode != null ? Integer.parseInt(categoryNode.getNodeValue()) : 5;

							Node priceNode = d1.getAttributes().getNamedItem("price");
							int price = priceNode != null ? Integer.parseInt(priceNode.getNodeValue()) : 0;

							Node isEventNode = d1.getAttributes().getNamedItem("is_event");
							Boolean isEvent = isEventNode != null && Boolean.parseBoolean(isEventNode.getNodeValue());

							Node isBestNode = d1.getAttributes().getNamedItem("is_best");
							Boolean isBest = isBestNode != null && Boolean.parseBoolean(isBestNode.getNodeValue());

							Node isNewNode = d1.getAttributes().getNamedItem("is_new");
							Boolean isNew = isNewNode != null && Boolean.parseBoolean(isNewNode.getNodeValue());

							int tabId = getProductTabId(isEvent, isBest, isNew);

							Node startTimeNode = d1.getAttributes().getNamedItem("sale_start_date");
							long startTimeSale = startTimeNode != null ? getMillisecondsFromString(startTimeNode.getNodeValue()) : 0;

							Node endTimeNode = d1.getAttributes().getNamedItem("sale_end_date");
							long endTimeSale = endTimeNode != null ? getMillisecondsFromString(endTimeNode.getNodeValue()) : 0;

							ArrayList<ProductItemComponent> components = new ArrayList<ProductItemComponent>();
							ProductItem pr = new ProductItem(productId, category, price, tabId, startTimeSale, endTimeSale);
							for (Node t1 = d1.getFirstChild(); t1 != null; t1 = t1.getNextSibling())
							{
								if ("component".equalsIgnoreCase(t1.getNodeName()))
								{
									int item_id = Integer.parseInt(t1.getAttributes().getNamedItem("item_id").getNodeValue());
									int count = Integer.parseInt(t1.getAttributes().getNamedItem("count").getNodeValue());
									ProductItemComponent component = new ProductItemComponent(item_id, count);
									components.add(component);
								}
							}

							pr.setComponents(components);
							_itemsList.put(productId, pr);
						}
					}
				}
			}

			_log.info(String.format("ProductItemTable: Loaded %d product item on sale.", _itemsList.size()));
		}
		catch (DOMException | IOException | NumberFormatException | ParserConfigurationException e)
		{
			_log.warn("ProductItemTable: Lists could not be initialized.", e);
		}
		catch (SAXException e)
		{
			_log.warn("ProductItemTable: Lists could not be initialized. SAXException: ", e);
		}
	}

	private static int getProductTabId(boolean isEvent, boolean isBest, boolean isNew)
	{
		// TODO: Заюзать isNew
		if (isEvent && isBest)
		{
			return 3;
		}

		if (isEvent)
		{
			return 1;
		}

		if (isBest)
		{
			return 2;
		}

		return 4;
	}

	private static long getMillisecondsFromString(String datetime)
	{
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		try
		{
			Date time = df.parse(datetime);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(time);

			return calendar.getTimeInMillis();
		}
		catch (ParseException e)
		{
			_log.error("Error while gettingMillisecondsFromString ", e);
		}

		return 0L;
	}

	public Collection<ProductItem> getAllItems()
	{
		return _itemsList.values();
	}

	public ProductItem getProduct(int id)
	{
		return _itemsList.get(id);
	}
}
