package l2mv.gameserver.model.entity.achievements;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javolution.util.FastMap;
import l2mv.gameserver.Config;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.TutorialCloseHtml;
import l2mv.gameserver.network.serverpackets.TutorialShowHtml;

/**
 * @author Nik (total rework)
 */
public class Achievements
{
	// id-max
	private final FastMap<Integer, Integer> _achievementMaxLevels = new FastMap<>();
	private final List<AchievementCategory> _achievementCategories = new LinkedList<>();
	private static Achievements _instance;

	private static final Logger _log = LoggerFactory.getLogger(Achievements.class);

	public Achievements()
	{
		load();
	}

	public void onBypass(Player player, String bypass, String[] cm)
	{
		if (bypass.startsWith("_bbs_achievements_cat"))
		{
			generatePage(player, Integer.parseInt(cm[1]), Integer.parseInt(cm[2]));
		}
		else if (bypass.equals("_bbs_achievements_close"))
		{
			player.sendPacket(TutorialCloseHtml.STATIC);
		}
		else if (bypass.startsWith("_bbs_achievements"))
		{
			checkAchievementRewards(player);
			generatePage(player);
		}
		else
		{
			_log.warn("Invalid achievements bypass: " + bypass);
		}
	}

	public void generatePage(Player player)
	{
		if (player == null)
		{
			return;
		}

		String achievements = HtmCache.getInstance().getNotNull("achievements/Achievements.htm", player);

		String ac = "";
		for (AchievementCategory cat : _achievementCategories)
		{
			ac += cat.getHtml(player);
		}

		achievements = achievements.replace("%categories%", ac);

		// player.sendPacket(html);
		player.sendPacket(new TutorialShowHtml(achievements));
	}

	public void generatePage(Player player, int category, int page)
	{
		if (player == null)
		{
			return;
		}

		String FULL_PAGE = HtmCache.getInstance().getNotNull("achievements/inAchievements.htm", player);

		final int totalpages = (int) (Math.ceil(player.getAchievements(category).size() / 5.0));

		FULL_PAGE = FULL_PAGE.replaceAll("%back%", page == 1 ? "<button value=\"\" action=\"bypass _bbs_achievements\" width=40 height=20 back=\"L2UI_CT1.Inventory_DF_Btn_RotateRight\" fore=\"L2UI_CT1.Inventory_DF_Btn_RotateRight\">" : "<button value=\"\" action=\"bypass _bbs_achievements_cat " + category + " " + (page - 1) + "\" width=40 height=20 back=\"L2UI_CT1.Inventory_DF_Btn_RotateRight\" fore=\"L2UI_CT1.Inventory_DF_Btn_RotateRight\">");
		FULL_PAGE = FULL_PAGE.replaceAll("%more%", totalpages <= page ? "&nbsp;" : "<button value=\"\" action=\"bypass _bbs_achievements_cat " + category + " " + (page + 1) + "\" width=40 height=20 back=\"L2UI_CT1.Inventory_DF_Btn_RotateLeft\" fore=\"L2UI_CT1.Inventory_DF_Btn_RotateLeft\">");

		AchievementCategory cat = _achievementCategories.stream().filter(ctg -> ctg.getCategoryId() == category).findAny().orElse(null);
		if (cat == null)
		{
			_log.warn("Achievements: getCatById - cat - is null, return. for " + player.getName());
			return;
		}

		int all = 0;
		String achievementsHTML = "";
		Map<Integer, Integer> playerAchievements = player.getAchievements(category);
		for (Entry<Integer, Integer> entry : playerAchievements.entrySet())
		{
			all++;
			if ((all > (page * 5)) || (all <= ((page - 1) * 5)))
			{
				continue;
			}

			int aId = entry.getKey();
			int nextLevel = (entry.getValue() + 1) >= getMaxLevel(aId) ? getMaxLevel(aId) : (entry.getValue() + 1);
			Achievement a = getAchievement(aId, Math.max(1, nextLevel));

			if (a == null)
			{
				_log.warn("Achievements: GetAchievement - a - is null, return. for " + player.getName());
				return;
			}

			long playerPoints = player.getCounters().getPoints(a.getType());
			achievementsHTML += a.getHtml(player, playerPoints);
		}

		int greenbar = 0;
		if (getAchievementLevelSum(player, category) > 0)
		{
			greenbar = (248/* BAR_MAX */ * ((getAchievementLevelSum(player, category) * 100) / cat.getAchievements().size())) / 100;
			greenbar = Math.min(greenbar, 248/* BAR_MAX */);
		}
		String fp = FULL_PAGE;
		fp = fp.replaceAll("%bar1up%", "" + greenbar);
		fp = fp.replaceAll("%bar2up%", "" + (248 - greenbar));

		fp = fp.replaceFirst("%caps1%", greenbar > 0 ? "Gauge_DF_Large_Food_Left" : "Gauge_DF_Large_Exp_bg_Left");

		fp = fp.replaceFirst("%caps2%", greenbar >= 248 ? "Gauge_DF_Large_Food_Right" : "Gauge_DF_Large_Exp_bg_Right");

		fp = fp.replaceFirst("%achievements%", achievementsHTML.isEmpty() ? "&nbsp;" : achievementsHTML);
		fp = fp.replaceFirst("%catName%", cat.getName());
		fp = fp.replaceFirst("%catDesc%", cat.getDesc());
		fp = fp.replaceFirst("%catIcon%", cat.getIcon());

		player.sendPacket(new TutorialShowHtml(fp));
	}

	public void checkAchievementRewards(Player player)
	{
		synchronized (player.getAchievements())
		{
			for (Entry<Integer, Integer> arco : player.getAchievements().entrySet())
			{
				int achievementId = arco.getKey();
				int achievementLevel = arco.getValue();
				if (getMaxLevel(achievementId) <= achievementLevel)
				{
					continue;
				}

				Achievement nextLevelAchievement;
				do
				{
					achievementLevel++;
					nextLevelAchievement = getAchievement(achievementId, achievementLevel);
					if ((nextLevelAchievement != null) && nextLevelAchievement.isDone(player.getCounters().getPoints(nextLevelAchievement.getType())))
					{
						nextLevelAchievement.reward(player);
					}
				}
				while (nextLevelAchievement != null);
			}
		}
	}

	public int getPointsForThisLevel(int totalPoints, int achievementId, int achievementLevel)
	{
		if (totalPoints == 0)
		{
			return 0;
		}

		int result = 0;
		for (int i = achievementLevel; i > 0; i--)
		{
			Achievement a = getAchievement(achievementId, i);
			if (a != null)
			{
				result += a.getPointsToComplete();
			}
		}

		return totalPoints - result;
	}

	public Achievement getAchievement(int achievementId, int achievementLevel)
	{
		for (AchievementCategory cat : _achievementCategories)
		{
			for (Achievement ach : cat.getAchievements())
			{
				if ((ach.getId() == achievementId) && (ach.getLevel() == achievementLevel))
				{
					return ach;
				}
			}
		}

		return null;
	}

	public Collection<Integer> getAchievementIds()
	{
		return _achievementMaxLevels.keySet();
	}

	public int getMaxLevel(int id)
	{
		return _achievementMaxLevels.getOrDefault(id, 0);
	}

	public static int getAchievementLevelSum(Player player, int categoryId)
	{
		return player.getAchievements(categoryId).values().stream().mapToInt(level -> level).sum();
	}

	public void load()
	{
		_achievementMaxLevels.clear();
		_achievementCategories.clear();
		try
		{
			File file = Config.findNonCustomResource("config/mod/achievements.xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			Document doc = factory.newDocumentBuilder().parse(file);

			for (Node g = doc.getFirstChild(); g != null; g = g.getNextSibling())
			{
				for (Node z = g.getFirstChild(); z != null; z = z.getNextSibling())
				{
					if (z.getNodeName().equals("categories"))
					{
						for (Node i = z.getFirstChild(); i != null; i = i.getNextSibling())
						{
							if ("cat".equalsIgnoreCase(i.getNodeName()))
							{
								int categoryId = Integer.valueOf(i.getAttributes().getNamedItem("id").getNodeValue());
								String categoryName = String.valueOf(i.getAttributes().getNamedItem("name").getNodeValue());
								String categoryIcon = String.valueOf(i.getAttributes().getNamedItem("icon").getNodeValue());
								String categoryDesc = String.valueOf(i.getAttributes().getNamedItem("desc").getNodeValue());
								_achievementCategories.add(new AchievementCategory(categoryId, categoryName, categoryIcon, categoryDesc));
							}
						}
					}
					else if (z.getNodeName().equals("achievement"))
					{
						int achievementId = Integer.valueOf(z.getAttributes().getNamedItem("id").getNodeValue());
						int achievementCategory = Integer.valueOf(z.getAttributes().getNamedItem("cat").getNodeValue());
						String desc = String.valueOf(z.getAttributes().getNamedItem("desc").getNodeValue());
						String fieldType = String.valueOf(z.getAttributes().getNamedItem("type").getNodeValue());
						int achievementMaxLevel = 0;

						for (Node i = z.getFirstChild(); i != null; i = i.getNextSibling())
						{
							if ("level".equalsIgnoreCase(i.getNodeName()))
							{
								int level = Integer.valueOf(i.getAttributes().getNamedItem("id").getNodeValue());
								long pointsToComplete = Long.parseLong(i.getAttributes().getNamedItem("need").getNodeValue());
								int fame = Integer.valueOf(i.getAttributes().getNamedItem("fame").getNodeValue());
								String name = String.valueOf(i.getAttributes().getNamedItem("name").getNodeValue());
								String icon = String.valueOf(i.getAttributes().getNamedItem("icon").getNodeValue());
								Achievement achievement = new Achievement(achievementId, level, name, achievementCategory, icon, desc, pointsToComplete, fieldType, fame);

								if (achievementMaxLevel < level)
								{
									achievementMaxLevel = level;
								}

								for (Node o = i.getFirstChild(); o != null; o = o.getNextSibling())
								{
									if ("reward".equalsIgnoreCase(o.getNodeName()))
									{
										int Itemid = Integer.valueOf(o.getAttributes().getNamedItem("id").getNodeValue());
										long Itemcount = Long.parseLong(o.getAttributes().getNamedItem("count").getNodeValue());
										achievement.addReward(Itemid, Itemcount);
									}
								}

								AchievementCategory lastCategory = _achievementCategories.stream().filter(ctg -> ctg.getCategoryId() == achievementCategory).findAny().orElse(null);
								if (lastCategory != null)
								{
									lastCategory.getAchievements().add(achievement);
								}
							}
						}

						_achievementMaxLevels.put(achievementId, achievementMaxLevel);
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		_log.info("Achievement System: Loaded " + _achievementCategories.size() + " achievement categories and " + _achievementMaxLevels.size() + " achievements.");
	}

	public static Achievements getInstance()
	{
		if (_instance == null)
		{
			_instance = new Achievements();
		}
		return _instance;
	}
}
