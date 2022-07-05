package l2f.gameserver.data.xml.holder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import gnu.trove.map.hash.TIntObjectHashMap;
import l2f.commons.crypt.CryptUtil;
import l2f.gameserver.Config;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.base.MultiSellEntry;
import l2f.gameserver.model.base.MultiSellIngredient;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.MultiSellList;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.templates.item.ItemTemplate;
import l2f.gameserver.utils.XMLUtil;

/**
 * Multisell list manager
 */
public class MultiSellHolder
{
	private static final Logger _log = LoggerFactory.getLogger(MultiSellHolder.class);

	private static MultiSellHolder _instance = new MultiSellHolder();

	public static MultiSellHolder getInstance()
	{
		return _instance;
	}

	private static final String NODE_PRODUCTION = "production";
	private static final String NODE_INGRIDIENT = "ingredient";

	private final TIntObjectHashMap<MultiSellListContainer> entries = new TIntObjectHashMap<MultiSellListContainer>();

	public MultiSellListContainer getList(int id)
	{
		return entries.get(id);
	}

	public MultiSellHolder()
	{
		parseData();
	}

	public void reload()
	{
		parseData();
	}

	private void parseData()
	{
		entries.clear();
		parse();
	}

	public static class MultiSellListContainer
	{
		private int _listId;
		private boolean _showall = true;
		private boolean keep_enchanted = false;
		private boolean is_dutyfree = false;
		private boolean nokey = false;
		private final List<MultiSellEntry> entries = new ArrayList<MultiSellEntry>();

		public void setListId(int listId)
		{
			_listId = listId;
		}

		public int getListId()
		{
			return _listId;
		}

		public void setShowAll(boolean bool)
		{
			_showall = bool;
		}

		public boolean isShowAll()
		{
			return _showall;
		}

		public void setNoTax(boolean bool)
		{
			is_dutyfree = bool;
		}

		public boolean isNoTax()
		{
			return is_dutyfree;
		}

		public void setNoKey(boolean bool)
		{
			nokey = bool;
		}

		public boolean isNoKey()
		{
			return nokey;
		}

		public void setKeepEnchant(boolean bool)
		{
			keep_enchanted = bool;
		}

		public boolean isKeepEnchant()
		{
			return keep_enchanted;
		}

		public void addEntry(MultiSellEntry e)
		{
			entries.add(e);
		}

		public List<MultiSellEntry> getEntries()
		{
			return entries;
		}

		public boolean isEmpty()
		{
			return entries.isEmpty();
		}
	}

	private void hashFiles(String dirname, List<File> hash)
	{
		File dir = new File(Config.DATAPACK_ROOT, "data/" + dirname);
		if (!dir.exists())
		{
			_log.info("Dir " + dir.getAbsolutePath() + " not exists");
			return;
		}
		File[] files = dir.listFiles();
		for (File f : files)
		{
			if (f.getName().endsWith(".xml"))
			{
				hash.add(f);
			}
			else if (f.isDirectory() && !f.getName().equals(".svn"))
			{
				hashFiles(dirname + "/" + f.getName(), hash);
			}
		}
	}

	public void addMultiSellListContainer(int id, MultiSellListContainer list)
	{
		if (entries.containsKey(id))
		{
			_log.warn("MultiSell redefined: " + id);
		}

		list.setListId(id);
		entries.put(id, list);
	}

	public MultiSellListContainer remove(String s)
	{
		return remove(new File(s));
	}

	public MultiSellListContainer remove(File f)
	{
		return remove(Integer.parseInt(f.getName().replaceAll(".xml", "")));
	}

	public MultiSellListContainer remove(int id)
	{
		return entries.remove(id);
	}

	public void parseFile(File f)
	{
		int id = 0;
		try
		{
			id = Integer.parseInt(f.getName().replaceAll(".xml", ""));
		}
		catch (NumberFormatException e)
		{
			_log.error("Error loading file " + f, e);
			return;
		}
		Document doc = null;
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			doc = factory.newDocumentBuilder().parse(CryptUtil.decryptOnDemand(f));
		}
		catch (IOException | ParserConfigurationException | SAXException e)
		{
			_log.error("Error loading file " + f, e);
			return;
		}
		try
		{
			addMultiSellListContainer(id, parseDocument(doc, id));
		}
		catch (RuntimeException e)
		{
			_log.error("Error in file " + f, e);
		}
	}

	private void parse()
	{
		List<File> files = new ArrayList<File>();
		hashFiles("multisell", files);
		for (File f : files)
		{
			parseFile(f);
		}
	}

	protected MultiSellListContainer parseDocument(Document doc, int id)
	{
		MultiSellListContainer list = new MultiSellListContainer();
		int entId = 1;

		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("item".equalsIgnoreCase(d.getNodeName()))
					{
						MultiSellEntry e = parseEntry(d, id);
						if (e != null)
						{
							e.setEntryId(entId++);
							list.addEntry(e);
						}
					}
					else if ("config".equalsIgnoreCase(d.getNodeName()))
					{
						list.setShowAll(XMLUtil.getAttributeBooleanValue(d, "showall", true));
						list.setNoTax(XMLUtil.getAttributeBooleanValue(d, "notax", false));
						list.setKeepEnchant(XMLUtil.getAttributeBooleanValue(d, "keepenchanted", false));
						list.setNoKey(XMLUtil.getAttributeBooleanValue(d, "nokey", false));
					}
				}
			}
		}

		return list;
	}

	protected MultiSellEntry parseEntry(Node n, int multiSellId)
	{
		MultiSellEntry entry = new MultiSellEntry();

		for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
		{
			if (NODE_INGRIDIENT.equalsIgnoreCase(d.getNodeName()))
			{
				int id = Integer.parseInt(d.getAttributes().getNamedItem("id").getNodeValue());
				long count = Long.parseLong(d.getAttributes().getNamedItem("count").getNodeValue());
				MultiSellIngredient mi = new MultiSellIngredient(id, count);
				if (d.getAttributes().getNamedItem("enchant") != null)
				{
					mi.setItemEnchant(Integer.parseInt(d.getAttributes().getNamedItem("enchant").getNodeValue()));
				}
				if (d.getAttributes().getNamedItem("mantainIngredient") != null)
				{
					mi.setMantainIngredient(Boolean.parseBoolean(d.getAttributes().getNamedItem("mantainIngredient").getNodeValue()));
				}
				// Elements
				if (d.getAttributes().getNamedItem("fireAttr") != null)
				{
					mi.getItemAttributes().setFire(Integer.parseInt(d.getAttributes().getNamedItem("fireAttr").getNodeValue()));
				}
				if (d.getAttributes().getNamedItem("waterAttr") != null)
				{
					mi.getItemAttributes().setWater(Integer.parseInt(d.getAttributes().getNamedItem("waterAttr").getNodeValue()));
				}
				if (d.getAttributes().getNamedItem("earthAttr") != null)
				{
					mi.getItemAttributes().setEarth(Integer.parseInt(d.getAttributes().getNamedItem("earthAttr").getNodeValue()));
				}
				if (d.getAttributes().getNamedItem("windAttr") != null)
				{
					mi.getItemAttributes().setWind(Integer.parseInt(d.getAttributes().getNamedItem("windAttr").getNodeValue()));
				}
				if (d.getAttributes().getNamedItem("holyAttr") != null)
				{
					mi.getItemAttributes().setHoly(Integer.parseInt(d.getAttributes().getNamedItem("holyAttr").getNodeValue()));
				}
				if (d.getAttributes().getNamedItem("unholyAttr") != null)
				{
					mi.getItemAttributes().setUnholy(Integer.parseInt(d.getAttributes().getNamedItem("unholyAttr").getNodeValue()));
				}

				entry.addIngredient(mi);
			}
			else if (NODE_PRODUCTION.equalsIgnoreCase(d.getNodeName()))
			{
				int id = Integer.parseInt(d.getAttributes().getNamedItem("id").getNodeValue());
				long count = Long.parseLong(d.getAttributes().getNamedItem("count").getNodeValue());
				MultiSellIngredient mi = new MultiSellIngredient(id, count);
				if (d.getAttributes().getNamedItem("enchant") != null)
				{
					mi.setItemEnchant(Integer.parseInt(d.getAttributes().getNamedItem("enchant").getNodeValue()));
				}
				// Elements
				if (d.getAttributes().getNamedItem("fireAttr") != null)
				{
					mi.getItemAttributes().setFire(Integer.parseInt(d.getAttributes().getNamedItem("fireAttr").getNodeValue()));
				}
				if (d.getAttributes().getNamedItem("waterAttr") != null)
				{
					mi.getItemAttributes().setWater(Integer.parseInt(d.getAttributes().getNamedItem("waterAttr").getNodeValue()));
				}
				if (d.getAttributes().getNamedItem("earthAttr") != null)
				{
					mi.getItemAttributes().setEarth(Integer.parseInt(d.getAttributes().getNamedItem("earthAttr").getNodeValue()));
				}
				if (d.getAttributes().getNamedItem("windAttr") != null)
				{
					mi.getItemAttributes().setWind(Integer.parseInt(d.getAttributes().getNamedItem("windAttr").getNodeValue()));
				}
				if (d.getAttributes().getNamedItem("holyAttr") != null)
				{
					mi.getItemAttributes().setHoly(Integer.parseInt(d.getAttributes().getNamedItem("holyAttr").getNodeValue()));
				}
				if (d.getAttributes().getNamedItem("unholyAttr") != null)
				{
					mi.getItemAttributes().setUnholy(Integer.parseInt(d.getAttributes().getNamedItem("unholyAttr").getNodeValue()));
				}

				if (!Config.ALT_ALLOW_SHADOW_WEAPONS && id > 0)
				{
					ItemTemplate item = ItemHolder.getInstance().getTemplate(id);
					if (item != null && item.isShadowItem() && item.isWeapon() && !Config.ALT_ALLOW_SHADOW_WEAPONS)
					{
						return null;
					}
				}

				entry.addProduct(mi);
			}
		}

		if (entry.getProduction().isEmpty())
		{
			_log.warn("MultiSell [" + multiSellId + "] is empty!");
			return null;
		}

		if (entry.getIngredients().size() == 1 && entry.getProduction().size() == 1 && entry.getIngredients().get(0).getItemId() == 57)
		{
			ItemTemplate item = ItemHolder.getInstance().getTemplate(entry.getProduction().get(0).getItemId());
			if (item == null)
			{
				_log.warn("MultiSell [" + multiSellId + "] Production [" + entry.getProduction().get(0).getItemId() + "] not found!");
				return null;
			}
			if (multiSellId < 70000 || multiSellId > 70010) // FIXME hardcode. Все кроме GM Shop
			{
				if (item.getReferencePrice() > entry.getIngredients().get(0).getItemCount())
				{
					_log.warn("MultiSell [" + multiSellId + "] Production '" + item.getName() + "' [" + entry.getProduction().get(0).getItemId() + "] price is lower than referenced | "
								+ item.getReferencePrice() + " > " + entry.getIngredients().get(0).getItemCount());
				}
			}
		}

		return entry;
	}

	private static long[] parseItemIdAndCount(String s)
	{
		if (s == null || s.isEmpty())
		{
			return null;
		}
		String[] a = s.split(":");
		try
		{
			long id = Integer.parseInt(a[0]);
			long count = a.length > 1 ? Long.parseLong(a[1]) : 1;
			return new long[]
			{
				id,
				count
			};
		}
		catch (NumberFormatException e)
		{
			_log.error("Error while parsing ItemId and Count in Multisell! ", e);
			return null;
		}
	}

	public static MultiSellEntry parseEntryFromStr(String s)
	{
		if (s == null || s.isEmpty())
		{
			return null;
		}

		String[] a = s.split("->");
		if (a.length != 2)
		{
			return null;
		}

		long[] ingredient, production;
		if ((ingredient = parseItemIdAndCount(a[0])) == null || (production = parseItemIdAndCount(a[1])) == null)
		{
			return null;
		}

		MultiSellEntry entry = new MultiSellEntry();
		entry.addIngredient(new MultiSellIngredient((int) ingredient[0], ingredient[1]));
		entry.addProduct(new MultiSellIngredient((int) production[0], production[1]));
		return entry;
	}

	public void SeparateAndSend(int listId, Player player, double taxRate)
	{
		for (int i : Config.ALT_DISABLED_MULTISELL)
		{
			if (i == listId)
			{
				player.sendMessage(new CustomMessage("common.Disabled", player));
				return;
			}
		}

		MultiSellListContainer list = getList(listId);

		if (list == null)
		{
			player.sendMessage(new CustomMessage("common.Disabled", player));
			return;
		}

		SeparateAndSend(list, player, taxRate);

		if (player.isGM())
		{
			Functions.sendDebugMessage(player, "MULTISELL: " + listId + ".xml " + "Tax: " + taxRate + "%");
		}
	}

	public void SeparateAndSend(MultiSellListContainer list, Player player, double taxRate)
	{
		list = generateMultiSell(list, player, taxRate);

		MultiSellListContainer temp = new MultiSellListContainer();
		int page = 1;

		temp.setListId(list.getListId());

		// Запоминаем отсылаемый лист, чтобы не подменили
		player.setMultisell(list);

		for (MultiSellEntry e : list.getEntries())
		{
			if (temp.getEntries().size() == Config.MULTISELL_SIZE)
			{
				player.sendPacket(new MultiSellList(temp, page, 0));
				page++;
				temp = new MultiSellListContainer();
				temp.setListId(list.getListId());
			}
			temp.addEntry(e);
		}

		player.sendPacket(new MultiSellList(temp, page, 1));
	}

	private MultiSellListContainer generateMultiSell(MultiSellListContainer container, Player player, double taxRate)
	{
		MultiSellListContainer list = new MultiSellListContainer();
		list.setListId(container.getListId());

		// Все мультиселлы из датапака
		boolean enchant = container.isKeepEnchant();
		boolean notax = container.isNoTax();
		boolean showall = container.isShowAll();
		boolean nokey = container.isNoKey();

		list.setShowAll(showall);
		list.setKeepEnchant(enchant);
		list.setNoTax(notax);
		list.setNoKey(nokey);

		ItemInstance[] items = player.getInventory().getItems();
		for (MultiSellEntry origEntry : container.getEntries())
		{
			MultiSellEntry ent = origEntry.clone();

			// Обработка налога, если лист не безналоговый
			// Адены добавляются в лист если отсутствуют или прибавляются к существующим
			List<MultiSellIngredient> ingridients;
			if (!notax && taxRate > 0.)
			{
				double tax = 0;
				long adena = 0;
				ingridients = new ArrayList<MultiSellIngredient>(ent.getIngredients().size() + 1);
				for (MultiSellIngredient i : ent.getIngredients())
				{
					if (i.getItemId() == 57)
					{
						adena += i.getItemCount();
						tax += i.getItemCount() * taxRate;
						continue;
					}
					ingridients.add(i);
					if (i.getItemId() == ItemTemplate.ITEM_ID_CLAN_REPUTATION_SCORE)
					{
						// FIXME hardcoded. Налог на клановую репутацию. Формула проверена на с6 и соответсвует на 100%.
						// TODO Проверить на корейском(?) оффе налог на банг поинты и fame
						tax += i.getItemCount() / 120 * 1000 * taxRate * 100;
					}
					if (i.getItemId() < 1)
					{
						continue;
					}

					ItemTemplate item = ItemHolder.getInstance().getTemplate(i.getItemId());
					if (item.isStackable())
					{
						tax += item.getReferencePrice() * i.getItemCount() * taxRate;
					}
				}

				adena = Math.round(adena + tax);
				if (adena > 0)
				{
					ingridients.add(new MultiSellIngredient(57, adena));
				}

				ent.setTax(Math.round(tax));

				ent.getIngredients().clear();
				ent.getIngredients().addAll(ingridients);
			}
			else
			{
				ingridients = ent.getIngredients();
			}

			// Если стоит флаг "показывать все" не проверять наличие ингридиентов
			if (showall)
			{
				list.entries.add(ent);
			}
			else
			{
				List<Integer> itms = new ArrayList<Integer>();
				boolean containsMammonVarnishEnhancer = false;
				for (MultiSellIngredient ingredient : ingridients)
				{
					if (ingredient.getItemId() == 12374)
					{
						containsMammonVarnishEnhancer = true;
					}
				}
				// Проверка наличия у игрока ингридиентов
				for (MultiSellIngredient ingredient : ingridients)
				{
					ItemTemplate template = ingredient.getItemId() <= 0 ? null : ItemHolder.getInstance().getTemplate(ingredient.getItemId());
					if (ingredient.getItemId() <= 0 || nokey || template.isEquipment() || containsMammonVarnishEnhancer)
					{
						if (ingredient.getItemId() == 12374) // Mammon's Varnish Enhancer
						{
							continue;
						}

						// TODO: а мы должны тут сверять count?
						if (ingredient.getItemId() == ItemTemplate.ITEM_ID_CLAN_REPUTATION_SCORE)
						{
							if (!itms.contains(ingredient.getItemId()) && player.getClan() != null && player.getClan().getReputationScore() >= ingredient.getItemCount())
							{
								itms.add(ingredient.getItemId());
							}
							continue;
						}
						else if (ingredient.getItemId() == ItemTemplate.ITEM_ID_PC_BANG_POINTS)
						{
							if (!itms.contains(ingredient.getItemId()) && player.getPcBangPoints() >= ingredient.getItemCount())
							{
								itms.add(ingredient.getItemId());
							}
							continue;
						}
						else if (ingredient.getItemId() == ItemTemplate.ITEM_ID_FAME)
						{
							if (!itms.contains(ingredient.getItemId()) && player.getFame() >= ingredient.getItemCount())
							{
								itms.add(ingredient.getItemId());
							}
							continue;
						}

						for (ItemInstance item : items)
						{
							if (item.getItemId() == ingredient.getItemId() && item.canBeExchanged(player))
							{
								// FIX ME если перевалит за long - косяк(VISTALL)
								if (itms.contains(enchant ? ingredient.getItemId() + ingredient.getItemEnchant() * 100000L : ingredient.getItemId()) || (item.getEnchantLevel() < ingredient.getItemEnchant())) // Некоторые
																																																				// мультиселлы
																																																				// требуют
																																																				// заточки
								{
									continue;
								}

								if (item.isStackable() && item.getCount() < ingredient.getItemCount())
								{
									break;
								}

								itms.add(enchant ? ingredient.getItemId() + ingredient.getItemEnchant() * 100000 : ingredient.getItemId());
								MultiSellEntry possibleEntry = new MultiSellEntry(enchant ? ent.getEntryId() + item.getEnchantLevel() * 100000 : ent.getEntryId());

								for (MultiSellIngredient p : ent.getProduction())
								{
									if (enchant && template.canBeEnchanted(true) && ItemHolder.getInstance().getTemplate(p.getItemId()).getCrystalType().equals(item.getCrystalType()))
									{
										p.setItemEnchant(item.getEnchantLevel());
										p.setItemAttributes(item.getAttributes().clone());
									}
									possibleEntry.addProduct(p);
								}

								for (MultiSellIngredient ig : ingridients)
								{
									if (enchant && ig.getItemId() > 0 && ItemHolder.getInstance().getTemplate(ig.getItemId()).canBeEnchanted(true))
									{
										ig.setItemEnchant(item.getEnchantLevel());
										ig.setItemAttributes(item.getAttributes().clone());
									}
									possibleEntry.addIngredient(ig);
								}

								list.entries.add(possibleEntry);
								break;
							}
						}
					}
				}
			}
		}

		return list;
	}
}