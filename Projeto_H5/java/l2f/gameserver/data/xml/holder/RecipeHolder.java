package l2f.gameserver.data.xml.holder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import l2f.gameserver.Config;
import l2f.gameserver.model.Recipe;
import l2f.gameserver.model.RecipeComponent;
import l2f.gameserver.templates.StatsSet;

public class RecipeHolder
{
	private static final Logger _log = LoggerFactory.getLogger(RecipeHolder.class);
	private static RecipeHolder _instance;

	private ConcurrentHashMap<Integer, Recipe> _listByRecipeId = new ConcurrentHashMap<Integer, Recipe>();
	private ConcurrentHashMap<Integer, Recipe> _listByRecipeItem = new ConcurrentHashMap<Integer, Recipe>();

	private RecipeHolder()
	{
		_listByRecipeId.clear();
		_listByRecipeItem.clear();
		try
		{
			loadFromXML();
		}
		catch (IOException | ParserConfigurationException | SAXException e)
		{
			_log.error("Error while loading Recipes From XML ", e);
		}
	}

	public static RecipeHolder getInstance()
	{
		if (_instance == null)
		{
			_instance = new RecipeHolder();
		}
		return _instance;
	}

	@SuppressWarnings("unused")
	public void loadFromXML() throws SAXException, IOException, ParserConfigurationException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		File file = new File(Config.DATAPACK_ROOT + "/data/recipes.xml");
		if (file.exists())
		{
			Document doc = factory.newDocumentBuilder().parse(file);
			List<RecipeComponent> recipePartList = new ArrayList<RecipeComponent>();
			for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if ("list".equalsIgnoreCase(n.getNodeName()))
				{
					recipesFile:
					for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if ("recipes".equalsIgnoreCase(d.getNodeName()))
						{
							recipePartList.clear();
							NamedNodeMap attrs = d.getAttributes();
							Node att;
							int id = -1;
							boolean haveRare = false;
							StatsSet set = new StatsSet();

							att = attrs.getNamedItem("id");
							if (att == null)
							{
								_log.error("Missing id for recipe item, skipping");
								continue;
							}
							id = Integer.parseInt(att.getNodeValue());
							set.set("id", id);

							att = attrs.getNamedItem("level");
							if (att == null)
							{
								_log.error("Missing level for recipe item id: " + id + ", skipping");
								continue;
							}
							set.set("level", Integer.parseInt(att.getNodeValue()));

							att = attrs.getNamedItem("recid");
							if (att == null)
							{
								_log.error("Missing recid for recipe item id: " + id + ", skipping");
								continue;
							}
							set.set("recid", Integer.parseInt(att.getNodeValue()));

							att = attrs.getNamedItem("recipeName");
							if (att == null)
							{
								_log.error("Missing recipeName for recipe item id: " + id + ", skipping");
								continue;
							}
							set.set("recipeName", att.getNodeValue());

							att = attrs.getNamedItem("successRate");
							if (att == null)
							{
								_log.error("Missing successRate for recipe item id: " + id + ", skipping");
								continue;
							}
							set.set("successRate", Integer.parseInt(att.getNodeValue()));

							att = attrs.getNamedItem("mp");
							if (att == null)
							{
								_log.error("Missing mp for recipe item id: " + id + ", skipping");
								continue;
							}
							set.set("mp", Integer.parseInt(att.getNodeValue()));

							att = attrs.getNamedItem("itemId");
							if (att == null)
							{
								_log.error("Missing itemId for recipe item id: " + id + ", skipping");
								continue;
							}
							set.set("itemId", Short.parseShort(att.getNodeValue()));

							att = attrs.getNamedItem("foundation");
							if (att == null)
							{
								_log.error("Missing foundation for recipe item id: " + id + ", skipping");
								continue;
							}
							set.set("foundation", Short.parseShort(att.getNodeValue()));

							att = attrs.getNamedItem("count");
							if (att == null)
							{
								_log.error("Missing count for recipe item id: " + id + ", skipping");
								continue;
							}
							set.set("count", Short.parseShort(att.getNodeValue()));

							att = attrs.getNamedItem("exp");
							if (att == null)
							{
								_log.error("Missing exp for recipe item id: " + id + ", skipping");
								continue;
							}
							set.set("exp", Long.parseLong(att.getNodeValue()));

							att = attrs.getNamedItem("sp");
							if (att == null)
							{
								_log.error("Missing sp for recipe item id: " + id + ", skipping");
								continue;
							}
							set.set("sp", Long.parseLong(att.getNodeValue()));

							att = attrs.getNamedItem("dwarven");
							if (att == null)
							{
								_log.error("Missing type for recipe item id: " + id + ", skipping");
								continue;
							}
							set.set("isDvarvenCraft", Boolean.parseBoolean(att.getNodeValue()));

							for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling())
							{
								if ("recitem".equalsIgnoreCase(c.getNodeName()))
								{
									int rpItemId = Integer.parseInt(c.getAttributes().getNamedItem("item").getNodeValue());
									int quantity = Integer.parseInt(c.getAttributes().getNamedItem("icount").getNodeValue());
									recipePartList.add(new RecipeComponent(rpItemId, quantity));
								}
							}

							int level = set.getInteger("level");
							int recipeId = set.getInteger("recid");
							String recipeName = set.getString("recipeName");
							int successRate = set.getInteger("successRate");
							int mpCost = set.getInteger("mp");
							int itemId = set.getInteger("itemId");
							int count = set.getInteger("count");
							int foundation = set.getInteger("foundation");
							long exp = set.getLong("exp");
							long sp = set.getLong("sp");
							boolean isDvarvenCraft = set.getBool("isDvarvenCraft");

							Recipe recipeList = new Recipe(id, level, recipeId, recipeName, successRate, mpCost, itemId, foundation, count, exp, sp, isDvarvenCraft);
							for (RecipeComponent recipePart : recipePartList)
							{
								recipeList.addRecipe(recipePart);
							}
							_listByRecipeId.put(id, recipeList);
							_listByRecipeItem.put(recipeId, recipeList);
						}
					}
				}
			}
			_log.info("RecipeController: Loaded " + _listByRecipeId.size() + " Recipes.");
		}
		else
		{
			_log.error("Recipes file (" + file.getAbsolutePath() + ") doesnt exists.");
		}
	}

	public Collection<Recipe> getRecipes()
	{
		return _listByRecipeId.values();
	}

	public Recipe getRecipeByRecipeId(int listId)
	{
		return _listByRecipeId.get(listId);
	}

	public Recipe getRecipeByRecipeItem(int itemId)
	{
		return _listByRecipeItem.get(itemId);
	}
}