package l2f.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Config;
import l2f.gameserver.ConfigHolder;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.database.mysql;
import l2f.gameserver.database.merge.MergeDatabaseFactory;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.utils.Language;
import l2f.gameserver.utils.Location;

public class CharacterDAO
{
	private static final Logger _log = LoggerFactory.getLogger(CharacterDAO.class);

	private static CharacterDAO _instance = new CharacterDAO();

	public static CharacterDAO getInstance()
	{
		return _instance;
	}

	public void markTooOldChars()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("UPDATE characters SET characters.deletetime=1 WHERE characters.onlinetime < 3600 and characters.lastAccess < 1376610861 LIMIT 500"))
		{
			statement.executeUpdate();
		}
		catch (SQLException e)
		{
			_log.error("Error while markTooOldChars! ", e);
		}
	}

	public void checkCharactersToDelete()
	{
		List<Integer> idsToDelete = new ArrayList<>();
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT obj_Id FROM characters WHERE deletetime > 0 AND deletetime < ?"))
		{
			statement.setLong(1, (System.currentTimeMillis() / 1000 - Config.DELETE_DAYS * 3600 * 24));
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					idsToDelete.add(rset.getInt("obj_Id"));
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while finding chars to delete!", e);
		}

		_log.info("Found " + idsToDelete.size() + " characters to delete!");

		for (int i = 0; i < idsToDelete.size(); i += 100)
		{
			int[] ids = new int[Math.min(100, idsToDelete.size() - (i))];
			int index = 0;
			for (int iter = i; iter < i + ids.length; iter++)
			{
				ids[index] = idsToDelete.get(iter);
				index++;
			}
			deleteCharByObjId(ids);
			_log.info("Deleted " + ids.length + " ids!");
		}
	}

	public void deleteCharByObjId(int... objids)
	{
		if (objids.length == 0 || objids.length == 1 && objids[0] < 0)
		{
			return;
		}

		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			StringBuilder queryFinishBuilder = new StringBuilder();
			for (int i = 0; i < objids.length; i++)
			{
				if (i != 0)
				{
					queryFinishBuilder.append(" OR ");
				}
				queryFinishBuilder.append("obj_Id=").append(objids[i]).append(" OR target_Id=").append(objids[i]);
			}

			String queryFinish = queryFinishBuilder.toString();

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_blocklist WHERE " + queryFinish))
			{
				statement.execute();
			}

			queryFinish = queryFinish.replace("obj_Id", "char_id");
			queryFinish = queryFinish.replace("target_Id", "friend_id");

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_friends WHERE " + queryFinish))
			{
				statement.execute();
			}

			queryFinish = queryFinish.replace("char_id", "object_id");
			queryFinish = queryFinish.replace("friend_id", "post_friend");

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_post_friends WHERE " + queryFinish))
			{
				statement.execute();
			}

			queryFinishBuilder.delete(0, queryFinishBuilder.length());
			for (int i = 0; i < objids.length; i++)
			{
				if (i != 0)
				{
					queryFinishBuilder.append(" OR ");
				}
				queryFinishBuilder.append("object_id=").append(objids[i]);
			}

			queryFinish = queryFinishBuilder.toString();

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_effects_save WHERE " + queryFinish))
			{
				statement.execute();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_group_reuse WHERE " + queryFinish))
			{
				statement.execute();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE " + queryFinish))
			{
				statement.execute();
			}

			queryFinish = queryFinish.replace("object_id", "char_obj_id");

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_hennas WHERE " + queryFinish))
			{
				statement.execute();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_macroses WHERE " + queryFinish))
			{
				statement.execute();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_skills WHERE " + queryFinish))
			{
				statement.execute();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_skills_save WHERE " + queryFinish))
			{
				statement.execute();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_subclasses WHERE " + queryFinish))
			{
				statement.execute();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM seven_signs WHERE " + queryFinish))
			{
				statement.execute();
			}

			queryFinish = queryFinish.replace("char_obj_id", "char_id");

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_mail WHERE " + queryFinish))
			{
				statement.execute();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_quests WHERE " + queryFinish))
			{
				statement.execute();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_recipebook WHERE " + queryFinish))
			{
				statement.execute();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM olympiad_nobles WHERE " + queryFinish))
			{
				statement.execute();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM heroes WHERE " + queryFinish))
			{
				statement.execute();
			}

			queryFinish = queryFinish.replace("char_id", "obj_id");

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_instances WHERE " + queryFinish))
			{
				statement.execute();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_variables WHERE " + queryFinish))
			{
				statement.execute();
			}

			queryFinish = queryFinish.replace("obj_id", "obj_Id");

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_logs WHERE " + queryFinish))
			{
				statement.execute();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM characters WHERE " + queryFinish))
			{
				statement.execute();
			}

			queryFinish = queryFinish.replace("obj_Id", "owner_id");

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM items WHERE " + queryFinish))
			{
				statement.execute();
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while deleting character!", e);
		}
	}

	public boolean insert(Player player)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			try (PreparedStatement statement = con.prepareStatement("INSERT INTO `characters` (account_name, obj_Id, char_name, face, hairStyle, hairColor, sex, karma, pvpkills, pkkills, clanid, createtime, deletetime, title, accesslevel, online, leaveclan, deleteclan, nochannel, pledge_type, pledge_rank, lvl_joined_academy, apprentice) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"))
			{
				statement.setString(1, player.getAccountName());
				statement.setInt(2, player.getObjectId());
				statement.setString(3, player.getName());
				statement.setInt(4, player.getFace());
				statement.setInt(5, player.getHairStyle());
				statement.setInt(6, player.getHairColor());
				statement.setInt(7, player.getSex());
				statement.setInt(8, player.getKarma());
				statement.setInt(9, player.getPvpKills());
				statement.setInt(10, player.getPkKills());
				statement.setInt(11, player.getClanId());
				statement.setLong(12, player.getCreateTime() / 1000);
				statement.setInt(13, player.getDeleteTimer());
				statement.setString(14, player.getTitle());
				statement.setInt(15, player.getAccessLevel());
				statement.setInt(16, player.isOnline() ? 1 : 0);
				statement.setLong(17, player.getLeaveClanTime() / 1000);
				statement.setLong(18, player.getDeleteClanTime() / 1000);
				statement.setLong(19, player.getNoChannel() > 0 ? player.getNoChannel() / 1000 : player.getNoChannel());
				statement.setInt(20, player.getPledgeType());
				statement.setInt(21, player.getPowerGrade());
				statement.setInt(22, player.getLvlJoinedAcademy());
				statement.setInt(23, player.getApprentice());
				statement.executeUpdate();
			}

			try (PreparedStatement statement = con.prepareStatement("INSERT INTO character_subclasses (char_obj_id, class_id, exp, sp, curHp, curMp, curCp, maxHp, maxMp, maxCp, level, active, isBase, death_penalty, certification) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"))
			{
				statement.setInt(1, player.getObjectId());
				statement.setInt(2, player.getTemplate().classId.getId());
				statement.setInt(3, 0);
				statement.setInt(4, 0);
				statement.setDouble(5, player.getTemplate().baseHpMax + player.getTemplate().lvlHpAdd + player.getTemplate().lvlHpMod);
				statement.setDouble(6, player.getTemplate().baseMpMax + player.getTemplate().lvlMpAdd + player.getTemplate().lvlMpMod);
				statement.setDouble(7, player.getTemplate().baseCpMax + player.getTemplate().lvlCpAdd + player.getTemplate().lvlCpMod);
				statement.setDouble(8, player.getTemplate().baseHpMax + player.getTemplate().lvlHpAdd + player.getTemplate().lvlHpMod);
				statement.setDouble(9, player.getTemplate().baseMpMax + player.getTemplate().lvlMpAdd + player.getTemplate().lvlMpMod);
				statement.setDouble(10, player.getTemplate().baseCpMax + player.getTemplate().lvlCpAdd + player.getTemplate().lvlCpMod);
				statement.setInt(11, 1);
				statement.setInt(12, 1);
				statement.setInt(13, 1);
				statement.setInt(14, 0);
				statement.setInt(15, 0);
				statement.executeUpdate();
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while inserting new Player to Database ", e);
			return false;
		}
		return true;
	}

	public Location getLocation(String name)
	{
		return getLocation(getObjectIdByName(name));
	}

	public Location getLocation(int id)
	{
		if (id == 0)
		{
			return null;
		}

		Location result = null;

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT x, y, z FROM characters WHERE obj_Id=?"))
		{
			statement.setInt(1, id);

			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					result = new Location(rset.getInt(1), rset.getInt(2), rset.getInt(3));
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("CharNameTable.getLocation(int): ", e);
		}

		return result;
	}

	public void deleteUserVar(String cha, String param)
	{
		deleteUserVar(getObjectIdByName(cha), param);
	}

	public void deleteUserVar(int objId, String param)
	{
		if (objId == 0)
		{
			return;
		}

		mysql.set("DELETE FROM `character_variables` WHERE `obj_id`=? AND `type`='user-var' AND `name`=? LIMIT 1", objId, param);
	}

	public String getUserVar(String cha, String param)
	{
		return getUserVar(getObjectIdByName(cha), param);
	}

	public String getUserVar(int objId, String param)
	{
		if (objId == 0)
		{
			return null;
		}

		return (String) mysql.get("SELECT `value` FROM `character_variables` WHERE `obj_id` = " + objId + " AND `type`='user-var' AND `name` = '" + param + "'");
	}

	public void setDbLocatio(int objId, int x, int y, int z)
	{
		mysql.set("UPDATE `characters` SET `x`=?, `y`=?, `z`=? WHERE `obj_id`=? LIMIT 1", x, y, z, objId);
	}

	public int getObjectIdByName(String name)
	{
		int result = 0;

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT obj_Id FROM characters WHERE char_name=?"))
		{
			statement.setString(1, name);

			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					result = rset.getInt(1);
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("CharNameTable.getObjectIdByName(String): ", e);
		}

		return result;
	}

	public static String getNameByObjectId(int objectId)
	{
		String result = StringUtils.EMPTY;

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters WHERE obj_Id=?"))
		{
			statement.setInt(1, objectId);

			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					result = rset.getString(1);
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("CharNameTable.getObjectIdByName(int): ", e);
		}

		return result;
	}

	public int accountCharNumber(String account)
	{
		int number = 0;

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT COUNT(char_name) FROM characters WHERE account_name=?"))
		{
			statement.setString(1, account);

			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					number = rset.getInt(1);
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while loading accountCharNumber ", e);
		}

		return number;
	}

	public static List<Integer> getCharIdsFromAccount(String account)
	{
		final List<Integer> charIds = new ArrayList<Integer>(8);
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT obj_Id FROM characters WHERE account_name=? AND accesslevel >= 0"))
		{
			statement.setString(1, account);
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					charIds.add(rset.getInt("obj_Id"));
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while loading Char IDs From Account: " + account, e);
		}
		return charIds;
	}

	public static List<String> getCharNamesFromAccount(String account)
	{
		final List<String> charNames = new ArrayList<String>(8);
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters WHERE account_name=? AND accesslevel >= 0"))
		{
			statement.setString(1, account);
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					charNames.add(rset.getString("char_name"));
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while loading Char Names From Account: " + account, e);
		}
		return charNames;
	}

	public static Language getLanguage(int playerObjectId, boolean checkOnlinePlayer)
	{
		if (checkOnlinePlayer)
		{
			final Player onlinePlayer = GameObjectsStorage.getPlayer(playerObjectId);
			if (onlinePlayer != null)
			{
				return onlinePlayer.getLanguage();
			}
		}
		return getLanguage(playerObjectId);
	}

	public static Language getLanguage(int playerObjectId)
	{
		/*
		 * final String value = CharacterVariablesDAO.selectFirst(playerObjectId, "Language");
		 * if (value == null)
		 * return ConfigHolder.getEnum("DefaultLanguage", Language.class);
		 */
		return Language.ENGLISH;
	}

	public boolean checkNameExists(String name)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT obj_Id FROM characters WHERE char_name=?"))
		{
			statement.setString(1, name);
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next() && rset.getInt(1) > 0)
				{
					return true;
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while checking If name(" + name + ") already exists!", e);
		}

		if (ConfigHolder.getBool("EnableMerge"))
		{
			try (Connection con = MergeDatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT COUNT(old_login) FROM merge_data WHERE finished=0 AND new_char_name_1 = ? OR new_char_name_2 = ? OR new_char_name_3 = ? OR new_char_name_4 = ? OR new_char_name_5 = ? OR new_char_name_6 = ? OR new_char_name_7 = ? OR new_char_name_8 = ?"))
			{
				statement.setString(1, name);
				statement.setString(2, name);
				statement.setString(3, name);
				statement.setString(4, name);
				statement.setString(5, name);
				statement.setString(6, name);
				statement.setString(7, name);
				statement.setString(8, name);
				try (ResultSet rset = statement.executeQuery())
				{
					if (rset.next() && rset.getInt(1) > 0)
					{
						return true;
					}
				}
			}
			catch (SQLException e)
			{
				_log.error("Error checking if Char Name(" + name + ") exists in merge_data", e);
			}
		}
		return false;
	}

	public static void setFacebookId(int playerObjectId, String facebookId)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("UPDATE characters SET facebook_id = ? WHERE obj_Id = ?"))
		{
			statement.setString(1, facebookId);
			statement.setInt(2, playerObjectId);
			statement.executeUpdate();
		}
		catch (SQLException e)
		{
			_log.error("Error while setFacebookId(" + playerObjectId + ", " + facebookId + ")", e);
		}
	}

	public static String getLastHWIDByName(String playerName)
	{
		String result = "";
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT lastHWID FROM characters WHERE char_name=?"))
		{
			statement.setString(1, playerName);
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					result = rset.getString(1);
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("CharNameTable.getLastHWIDByName(" + playerName + "): ", e);
		}
		return result;
	}

	public String getAccountName(String charName)
	{
		String result = StringUtils.EMPTY;
		ResultSet rset = null;
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT account_name FROM characters WHERE char_name=?"))
		{
			statement.setString(1, charName);
			rset = statement.executeQuery();
			if (rset.next())
			{
				result = rset.getString(1);
			}
		}
		catch (Exception e)
		{
			_log.error("CharacterDAO.getAccountName(String): " + e, e);
		}

		return result;
	}
}