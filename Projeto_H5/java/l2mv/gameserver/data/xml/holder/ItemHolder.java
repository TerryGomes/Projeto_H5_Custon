package l2mv.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import l2mv.commons.data.xml.AbstractHolder;
import l2mv.commons.lang.ArrayUtils;
import l2mv.gameserver.model.reward.CalculateRewardChances;
import l2mv.gameserver.templates.item.ItemTemplate;

public final class ItemHolder extends AbstractHolder
{
	private static final ItemHolder _instance = new ItemHolder();

	private final TIntObjectHashMap<ItemTemplate> _items = new TIntObjectHashMap<ItemTemplate>();
	private ItemTemplate[] _allTemplates;
	private ItemTemplate[] droppableTemplates;

	public static ItemHolder getInstance()
	{
		return _instance;
	}

	private ItemHolder()
	{
		//
	}

	public void addItem(ItemTemplate template)
	{
		_items.put(template.getItemId(), template);
	}

	private void buildFastLookupTable()
	{
		int highestId = 0;

		for (int id : _items.keys())
		{
			if (id > highestId)
			{
				highestId = id;
			}
		}

		_allTemplates = new ItemTemplate[highestId + 1];

		for (TIntObjectIterator<ItemTemplate> iterator = _items.iterator(); iterator.hasNext();)
		{
			iterator.advance();
			_allTemplates[iterator.key()] = iterator.value();
		}
	}

	/**
	 * Returns the item corresponding to the item ID
	 * @param id : int designating the item
	 * @return
	 */
	public ItemTemplate getTemplate(int id)
	{
		ItemTemplate item = ArrayUtils.valid(_allTemplates, id);
		if (item == null)
		{
			if (id != 0)
			{
				warn("Not defined item id : " + id + ", or out of range!", new Exception());
			}
			return null;
		}
		return _allTemplates[id];
	}

	public boolean checkTemplateExists(int id)
	{
		return ArrayUtils.valid(_allTemplates, id) != null;
	}

	public ItemTemplate[] getAllTemplates()
	{
		return _allTemplates;
	}

	public List<ItemTemplate> getItemsByNameContainingString(CharSequence name, boolean onlyDroppable)
	{
		ItemTemplate[] toChooseFrom = onlyDroppable ? getDroppableTemplates() : _allTemplates;
		List<ItemTemplate> templates = new ArrayList<>();
		for (ItemTemplate template : toChooseFrom)
		{
			if (template != null && StringUtils.containsIgnoreCase(template.getName(), name))
			{
				templates.add(template);
			}
		}
		return templates;
	}

	public ItemTemplate[] getDroppableTemplates()
	{
		if (droppableTemplates == null)
		{
			List<ItemTemplate> templates = CalculateRewardChances.getDroppableItems();
			droppableTemplates = templates.toArray(new ItemTemplate[templates.size()]);
		}

		return droppableTemplates;
	}

	public String getItemName(int itemId)
	{
		return getTemplate(itemId).getName();
	}

	public String getTemplateName(int itemId)
	{
		final ItemTemplate template = getTemplate(itemId);
		if (template != null)
		{
			return template.getName();
		}
		return "[Unknown]";
	}

	@Override
	protected void process()
	{
		buildFastLookupTable();
	}

	@Override
	public int size()
	{
		return _items.size();
	}

	@Override
	public void clear()
	{
		_items.clear();
	}
}