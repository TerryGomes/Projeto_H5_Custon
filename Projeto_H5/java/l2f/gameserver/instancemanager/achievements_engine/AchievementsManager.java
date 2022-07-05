package l2f.gameserver.instancemanager.achievements_engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javolution.util.FastList;
import javolution.util.FastMap;
import l2f.gameserver.Config;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.instancemanager.achievements_engine.base.Achievement;
import l2f.gameserver.instancemanager.achievements_engine.base.Condition;
import l2f.gameserver.instancemanager.achievements_engine.conditions.Adena;
import l2f.gameserver.instancemanager.achievements_engine.conditions.Castle;
import l2f.gameserver.instancemanager.achievements_engine.conditions.ClanLeader;
import l2f.gameserver.instancemanager.achievements_engine.conditions.ClanLevel;
import l2f.gameserver.instancemanager.achievements_engine.conditions.CompleteAchievements;
import l2f.gameserver.instancemanager.achievements_engine.conditions.Crp;
import l2f.gameserver.instancemanager.achievements_engine.conditions.Hero;
import l2f.gameserver.instancemanager.achievements_engine.conditions.HeroCount;
import l2f.gameserver.instancemanager.achievements_engine.conditions.ItemsCount;
import l2f.gameserver.instancemanager.achievements_engine.conditions.Karma;
import l2f.gameserver.instancemanager.achievements_engine.conditions.Level;
import l2f.gameserver.instancemanager.achievements_engine.conditions.Mage;
import l2f.gameserver.instancemanager.achievements_engine.conditions.Marry;
import l2f.gameserver.instancemanager.achievements_engine.conditions.MinCMcount;
import l2f.gameserver.instancemanager.achievements_engine.conditions.Noble;
import l2f.gameserver.instancemanager.achievements_engine.conditions.OnlineTime;
import l2f.gameserver.instancemanager.achievements_engine.conditions.Pk;
import l2f.gameserver.instancemanager.achievements_engine.conditions.Pvp;
//import l2f.gameserver.instancemanager.achievements_engine.conditions.RaidKill;
import l2f.gameserver.instancemanager.achievements_engine.conditions.RaidPoints;
import l2f.gameserver.instancemanager.achievements_engine.conditions.SkillEnchant;
import l2f.gameserver.instancemanager.achievements_engine.conditions.Sub;
import l2f.gameserver.instancemanager.achievements_engine.conditions.WeaponEnchant;
import l2f.gameserver.instancemanager.achievements_engine.conditions.eventKills;
import l2f.gameserver.instancemanager.achievements_engine.conditions.eventWins;
import l2f.gameserver.instancemanager.achievements_engine.conditions.events;
import l2f.gameserver.model.Player;
import l2f.gameserver.utils.ItemFunctions;

public class AchievementsManager
{
	private final Map<Integer, Achievement> _achievementList = new FastMap<>();

	private final FastList<String> _binded = new FastList<>();

	private static Logger _log = Logger.getLogger(AchievementsManager.class.getName());

	public AchievementsManager()
	{
		loadAchievements();
	}

	private void loadAchievements()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);

		File file = new File(Config.DATAPACK_ROOT, "data/Achievements.xml");

		if (!file.exists())
		{
			_log.warning("AchievementsEngine: Error achievements xml file does not exist, check directory!");
		}
		try
		{
			InputSource in = new InputSource(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			in.setEncoding("UTF-8");
			Document doc = factory.newDocumentBuilder().parse(in);

			for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if (n.getNodeName().equalsIgnoreCase("list"))
				{
					for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if (d.getNodeName().equalsIgnoreCase("achievement"))
						{
							int id = checkInt(d, "id");

							String name = String.valueOf(d.getAttributes().getNamedItem("name").getNodeValue());
							String description = String.valueOf(d.getAttributes().getNamedItem("description").getNodeValue());
							String reward = String.valueOf(d.getAttributes().getNamedItem("reward").getNodeValue());
							boolean repeat = checkBoolean(d, "repeatable");

							FastList<Condition> conditions = conditionList(d.getAttributes());

							_achievementList.put(id, new Achievement(id, name, description, reward, repeat, conditions));
							alterTable(id);
						}
					}
				}
			}

			// _log.info("AchievementsEngine: loaded " + getAchievementList().size() + " achievements from xml!");
		}
		catch (Exception e)
		{
			_log.warning("AchievementsEngine: Error " + e);
			e.printStackTrace();
		}
	}

	public void rewardForAchievement(int achievementID, Player player)
	{
		Achievement achievement = _achievementList.get(achievementID);

		for (int id : achievement.getRewardList().keySet())
		{
			int count = achievement.getRewardList().get(id).intValue();
			// player.addItem(achievement.getName(), id, count, player, true);
			ItemFunctions.addItem(player, id, count, true, "Achievemented");

		}
	}

	private static boolean checkBoolean(Node d, String nodename)
	{
		boolean b = false;

		try
		{
			b = Boolean.valueOf(d.getAttributes().getNamedItem(nodename).getNodeValue());
		}
		catch (Exception e)
		{

		}
		return b;
	}

	@SuppressWarnings("static-method")
	private int checkInt(Node d, String nodename)
	{
		int i = 0;

		try
		{
			i = Integer.valueOf(d.getAttributes().getNamedItem(nodename).getNodeValue());
		}
		catch (Exception e)
		{

		}
		return i;
	}

	/**
	 * Alter table, catch exception if already exist.
	 *
	 * @param fieldID
	 */
	private static void alterTable(int fieldID)
	{
		// Connection con = null;
		try (Connection con = DatabaseFactory.getInstance().getConnection();)
		{
			// con = L2DatabaseFactory.getInstance().getConnection();
			Statement statement = con.createStatement();
			statement.executeUpdate("ALTER TABLE achievements ADD a" + fieldID + " INT DEFAULT 0");
			// statement.close();
		}
		catch (SQLException e)
		{

		}
		/*
		 * finally { try { con.close(); } catch (Exception e) { } }
		 */
	}

	public FastList<Condition> conditionList(NamedNodeMap attributesList)
	{
		FastList<Condition> conditions = new FastList<>();

		for (int j = 0; j < attributesList.getLength(); j++)
		{
			addToConditionList(attributesList.item(j).getNodeName(), attributesList.item(j).getNodeValue(), conditions);
		}

		return conditions;
	}

	public Map<Integer, Achievement> getAchievementList()
	{
		return _achievementList;
	}

	public FastList<String> getBinded()
	{
		return _binded;
	}

	public boolean isBinded(int obj, int ach)
	{
		for (String binds : _binded)
		{
			String[] spl = binds.split("@");
			if (spl[0].equals(String.valueOf(obj)) && spl[1].equals(String.valueOf(ach)))
			{
				return true;
			}
		}
		return false;
	}

	public static AchievementsManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final AchievementsManager _instance = new AchievementsManager();
	}

	private static void addToConditionList(String nodeName, Object value, FastList<Condition> conditions)
	{
		if (nodeName.equals("minLevel"))
		{
			conditions.add(new Level(value));
		}
		else if (nodeName.equals("minPvPCount"))
		{
			conditions.add(new Pvp(value));
		}
		else if (nodeName.equals("minPkCount"))
		{
			conditions.add(new Pk(value));
		}
		else if (nodeName.equals("minClanLevel"))
		{
			conditions.add(new ClanLevel(value));
		}
		else if (nodeName.equals("mustBeHero"))
		{
			conditions.add(new Hero(value));
		}
		else if (nodeName.equals("mustBeNoble"))
		{
			conditions.add(new Noble(value));
		}
		else if (nodeName.equals("minWeaponEnchant"))
		{
			conditions.add(new WeaponEnchant(value));
		}
		else if (nodeName.equals("minKarmaCount"))
		{
			conditions.add(new Karma(value));
		}
		else if (nodeName.equals("minAdenaCount"))
		{
			conditions.add(new Adena(value));
		}
		else if (nodeName.equals("minClanMembersCount"))
		{
			conditions.add(new MinCMcount(value));
		}
		else if (nodeName.equals("mustBeClanLeader"))
		{
			conditions.add(new ClanLeader(value));
		}
		else if (nodeName.equals("mustBeMarried"))
		{
			conditions.add(new Marry(value));
		}
		else if (nodeName.equals("itemAmmount"))
		{
			conditions.add(new ItemsCount(value));
		}
		else if (nodeName.equals("crpAmmount"))
		{
			conditions.add(new Crp(value));
		}
		else if (nodeName.equals("lordOfCastle"))
		{
			conditions.add(new Castle(value));
		}
		else if (nodeName.equals("mustBeMageClass"))
		{
			conditions.add(new Mage(value));
		}
		else if (nodeName.equals("minSubclassCount"))
		{
			conditions.add(new Sub(value));
		}
		else if (nodeName.equals("CompleteAchievements"))
		{
			conditions.add(new CompleteAchievements(value));
		}
		else if (nodeName.equals("minSkillEnchant"))
		{
			conditions.add(new SkillEnchant(value));
		}
		else if (nodeName.equals("minOnlineTime"))
		{
			conditions.add(new OnlineTime(value));
		}
		else if (nodeName.equals("minHeroCount"))
		{
			conditions.add(new HeroCount(value));
		}
		/*
		 * else if (nodeName.equals("raidToKill")) { conditions.add(new RaidKill(value)); } else if (nodeName.equals("raidToKill1")) { conditions.add(new RaidKill(value)); } //else if
		 * (nodeName.equals("raidToKill2")) //{ // conditions.add(new RaidKill(value)); //}
		 */
		else if (nodeName.equals("minRaidPoints"))
		{
			conditions.add(new RaidPoints(value));
		}
		else if (nodeName.equals("eventKills"))
		{
			conditions.add(new eventKills(value));
		}
		else if (nodeName.equals("events"))
		{
			conditions.add(new events(value));
		}
		else if (nodeName.equals("eventWins"))
		{
			conditions.add(new eventWins(value));
		}
	}

	public void loadUsed()
	{
		Connection con = null;
		try
		{
			PreparedStatement statement;
			ResultSet rs;
			con = DatabaseFactory.getInstance().getConnection();
			String sql = "SELECT ";
			for (int i = 1; i <= getAchievementList().size(); i++)
			{
				if (i != getAchievementList().size())
				{
					sql = sql + "a" + i + ",";
				}
				else
				{
					sql = sql + "a" + i;
				}

			}

			sql = sql + " FROM achievements";
			statement = con.prepareStatement(sql);

			rs = statement.executeQuery();
			while (rs.next())
			{
				for (int i = 1; i <= getAchievementList().size(); i++)
				{
					String ct = rs.getString(i);
					if ((ct.length() > 1) && ct.startsWith("1"))
					{
						_binded.add(ct.substring(ct.indexOf("1") + 1) + "@" + i);
					}
				}
			}
			statement.close();
			rs.close();
		}
		catch (SQLException e)
		{
			_log.warning("[ACHIEVEMENTS SAVE GETDATA]");
			if (Config.ALT_DEBUG_ENABLED)
			{
				e.printStackTrace();
			}
		}
		finally
		{

			try
			{
				if (con != null)
				{
					con.close();
					// or try ( resourse) )
				}
			}
			catch (Exception e)
			{
			}
		}
	}
}