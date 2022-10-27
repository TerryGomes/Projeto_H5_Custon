package l2mv.gameserver.fandc.academy;

import java.util.ArrayList;
import java.util.StringTokenizer;

import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.templates.item.ItemTemplate;

/**
 * @author Infern0
 */
public class AcademyRewards
{
	private static ArrayList<AcademyReward> _academyRewards = new ArrayList<AcademyReward>();

	public void load()
	{
		_academyRewards.clear();
		final StringTokenizer st = new StringTokenizer(Config.SERVICES_ACADEMY_REWARD, ";");
		if (st.hasMoreTokens())
		{
			final int itemId = Integer.parseInt(st.nextToken());
			String itemName = "No Name";
			final ItemTemplate tmp = ItemHolder.getInstance().getTemplate(itemId);
			if (tmp != null)
			{
				itemName = tmp.getName();
			}
			_academyRewards.add(new AcademyReward(itemName, itemId));
		}
	}

	public void reload()
	{
		load();
	}

	public int getItemId(String itemName)
	{
		int id = -1;
		for (AcademyReward item : _academyRewards)
		{
			if (item.getName().equalsIgnoreCase(itemName))
			{
				id = item.getItemId();
			}
		}
		return id;
	}

	public String toList()
	{
		load();
		String list = "";
		for (AcademyReward a : _academyRewards)
		{
			list += a.getName() + ";";
		}
		return list;
	}

	public class AcademyReward
	{
		private final String _itemName;
		private final int _itemId;

		public AcademyReward(String name, int id)
		{
			_itemName = name;
			_itemId = id;
		}

		public String getName()
		{
			return _itemName;
		}

		public int getItemId()
		{
			return _itemId;
		}
	}

	public static AcademyRewards getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final AcademyRewards _instance = new AcademyRewards();
	}
}
