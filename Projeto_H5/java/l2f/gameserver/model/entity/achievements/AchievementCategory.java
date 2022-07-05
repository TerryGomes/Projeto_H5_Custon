package l2f.gameserver.model.entity.achievements;

import java.util.LinkedList;
import java.util.List;

import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.model.Player;

/**
 * @author Nik (total rework)
 * @author Midnex
 * @author Promo (htmls)
 */
public class AchievementCategory
{
	private static final int BAR_MAX = 26;
	private final List<Achievement> _achievements = new LinkedList<>();
	private final int _categoryId;
	// private String _html;
	private final String _name;
	private final String _icon;
	private final String _desc;

	public AchievementCategory(int categoryId, String categoryName, String categoryIcon, String categoryDesc)
	{
		_categoryId = categoryId;
		_name = categoryName;
		_icon = categoryIcon;
		_desc = categoryDesc;
	}

	public String getHtml(Player player)
	{
		return getHtml(Achievements.getAchievementLevelSum(player, getCategoryId()));
	}

	public String getHtml(int totalPlayerLevel)
	{
		int greenbar = 0;

		if (totalPlayerLevel > 0)
		{
			greenbar = (BAR_MAX * ((totalPlayerLevel * 100) / _achievements.size())) / 100;
			greenbar = Math.min(greenbar, BAR_MAX);
		}

		String temp = HtmCache.getInstance().getNullable("achievements/AchievementsCat.htm", null);

		temp = temp.replaceAll("%desc%", getDesc());
		temp = temp.replaceAll("%icon%", getIcon());
		temp = temp.replaceAll("%name%", getName());
		temp = temp.replaceAll("%id%", "" + getCategoryId());

		temp = temp.replaceFirst("%caps1%", greenbar > 0 ? "Gauge_DF_Food_Left" : "Gauge_DF_Exp_bg_Left");
		temp = temp.replaceFirst("%caps2%", greenbar >= BAR_MAX ? "Gauge_DF_Food_Right" : "Gauge_DF_Exp_bg_Right");

		temp = temp.replaceAll("%bar1%", "" + greenbar);
		temp = temp.replaceAll("%bar2%", "" + (BAR_MAX - greenbar));
		return temp;
	}

	public int getCategoryId()
	{
		return _categoryId;
	}

	public List<Achievement> getAchievements()
	{
		return _achievements;
	}

	public String getDesc()
	{
		return _desc;
	}

	public String getIcon()
	{
		return _icon;
	}

	public String getName()
	{
		return _name;
	}
}
