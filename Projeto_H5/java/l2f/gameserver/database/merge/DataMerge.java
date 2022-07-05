package l2f.gameserver.database.merge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.ConfigHolder;
import l2f.gameserver.dao.CharacterDAO;
import l2f.gameserver.dao.OlympiadNobleDAO;
import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.idfactory.IdFactory;
import l2f.gameserver.model.entity.Hero;

public class DataMerge
{
	private static final Logger LOG = LoggerFactory.getLogger(DataMerge.class);

	private final Set<String> mergedAccounts = new HashSet<String>();
	private final Object lock = new Object();

	public DataMerge()
	{
	}

	public void checkMergeToComplete(String newAccount)
	{
		synchronized (lock)
		{
			if (mergedAccounts.contains(newAccount))
			{
				return;
			}

			mergedAccounts.add(newAccount);
		}

		final String oldAccount = getAccountNameToMerge(newAccount);
		if (oldAccount.isEmpty())
		{
			return;
		}

		if (CharacterDAO.getInstance().accountCharNumber(newAccount) > 0)
		{
			LOG.error(newAccount + " Trying to merge account, but there are already characters on that account!");
			markMergeCompleted(oldAccount);
			return;
		}

		try (Connection oldServerCon = MergeDatabaseFactory.getInstance().getConnection(); Connection newServerCon = DatabaseFactory.getInstance().getConnection())
		{
			final Map<Integer, String> charIdsToMerge = getCharIdsToMerge(oldAccount, oldServerCon);
			for (Map.Entry<Integer, String> charToMerge : charIdsToMerge.entrySet())
			{
				final int newCharId = mergeCharacter(charToMerge.getKey(), oldServerCon, charToMerge.getValue(), newAccount, newServerCon);
				ClanDataMerge.getInstance().onCharacterMerged(oldServerCon, charToMerge.getKey(), newServerCon, newCharId);
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while merging Account " + oldAccount, e);
		}

		markMergeCompleted(oldAccount);
	}

	private static String getAccountNameToMerge(String currentAccount)
	{
		try (Connection con = MergeDatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT old_login FROM merge_data WHERE new_login = ? AND finished = 0"))
		{
			statement.setString(1, currentAccount);
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					return rset.getString("old_login");
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while getting Account To Merge by Account Name: " + currentAccount, e);
		}
		return "";
	}

	private static void markMergeCompleted(String oldAccountName)
	{
		try (Connection con = MergeDatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("UPDATE merge_data SET finished = 1 WHERE old_login = ?"))
		{
			statement.setString(1, oldAccountName);
			statement.executeUpdate();
		}
		catch (SQLException e)
		{
			LOG.error("Error setting Merge as Finished for Old Account Name: " + oldAccountName, e);
		}
	}

	private static int mergeCharacter(int oldCharId, Connection oldServerCon, String newCharName, String newAccount, Connection newServerCon)
	{
		final int newCharId = IdFactory.getInstance().getNextId();
		mergeCharactersTable(oldCharId, oldServerCon, newCharName, newAccount, newCharId, newServerCon);
		mergeSubclassesTable(oldCharId, oldServerCon, newCharId, newServerCon);
		mergeHennasTable(oldCharId, oldServerCon, newCharId, newServerCon);
		mergeMacrosesTable(oldCharId, oldServerCon, newCharId, newServerCon);
		mergeQuestsTable(oldCharId, oldServerCon, newCharId, newServerCon);
		mergeShortcutsTable(oldCharId, oldServerCon, newCharId, newServerCon);
		mergeSkillsTable(oldCharId, oldServerCon, newCharId, newServerCon);
		mergeVariablesTable(oldCharId, oldServerCon, newCharId, newServerCon);
		mergeOlympiadNoblessTable(oldCharId, oldServerCon, newCharId, newServerCon);
		mergeHeroesTable(oldCharId, oldServerCon, newCharId, newServerCon);
		mergeItemsTable(oldCharId, oldServerCon, newCharId, newServerCon);
		return newCharId;
	}

	private static Map<Integer, String> getCharIdsToMerge(String oldAccountName, Connection oldServerCon)
	{
		final Map<Integer, String> charsToMerge = new HashMap<Integer, String>(8);
		try (PreparedStatement oldServerStatement = oldServerCon.prepareStatement("SELECT * FROM merge_data WHERE old_login = ?"))
		{
			oldServerStatement.setString(1, oldAccountName);
			try (ResultSet rset = oldServerStatement.executeQuery())
			{
				if (rset.next())
				{
					for (int i = 1; i <= 8; ++i)
					{
						if (rset.getInt("old_char_id_" + i) > 0)
						{
							charsToMerge.put(rset.getInt("old_char_id_" + i), rset.getString("new_char_name_" + i));
						}
					}
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while selecting Character Ids to be merged for Old Account Name: " + oldAccountName, e);
		}
		return charsToMerge;
	}

	private static void mergeCharactersTable(int oldCharId, Connection oldServerCon, String newCharName, String newAccountName, int newCharId, Connection newServerCon)
	{
		try (PreparedStatement oldServerStatement = oldServerCon.prepareStatement(
					"SELECT char_name, face, hairStyle, hairColor, sex, x, y, z, karma, pvpkills, pkkills, clanid, createtime, deletetime, title, rec_have, rec_left, rec_bonus_time, hunt_points, hunt_time, accesslevel, online, onlinetime, lastAccess, leaveclan, deleteclan, nochannel, pledge_type, pledge_rank, lvl_joined_academy, apprentice, key_bindings, pcBangPoints, vitality, fame, bookmarks, hwid_lock, raidkills, eventKills, siege_kills, lastHWID, lastIP, forum_login from characters WHERE obj_Id = ?");
					PreparedStatement newServerStatement = newServerCon.prepareStatement(
								"INSERT INTO characters (account_name, obj_Id,\tchar_name, face, hairStyle, hairColor, sex, x, y, z, karma, pvpkills, pkkills, clanid, createtime, deletetime, title, rec_have, rec_left, rec_bonus_time, hunt_points, hunt_time, accesslevel, online, onlinetime, lastAccess, leaveclan, deleteclan, nochannel, pledge_type, pledge_rank, lvl_joined_academy, apprentice, key_bindings, pcBangPoints, vitality, fame, bookmarks, hwid_lock, raidkills, eventKills, siege_kills, lastHWID, lastIP, forum_login) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"))
		{
			oldServerStatement.setInt(1, oldCharId);
			try (ResultSet rset = oldServerStatement.executeQuery())
			{
				if (rset.next())
				{
					final int oldClanId = rset.getInt("clanid");
					final int newClanId = oldClanId > 0 ? ClanDataMerge.getInstance().getNewClanId(oldClanId, 0) : 0;
					int i = 0;
					newServerStatement.setString(++i, newAccountName);
					newServerStatement.setInt(++i, newCharId);
					newServerStatement.setString(++i, newCharName);
					newServerStatement.setInt(++i, rset.getInt("face"));
					newServerStatement.setInt(++i, rset.getInt("hairStyle"));
					newServerStatement.setInt(++i, rset.getInt("hairColor"));
					newServerStatement.setInt(++i, rset.getInt("sex"));
					newServerStatement.setInt(++i, rset.getInt("x"));
					newServerStatement.setInt(++i, rset.getInt("y"));
					newServerStatement.setInt(++i, rset.getInt("z"));
					newServerStatement.setInt(++i, rset.getInt("karma"));
					newServerStatement.setInt(++i, rset.getInt("pvpkills"));
					newServerStatement.setInt(++i, rset.getInt("pkkills"));
					newServerStatement.setInt(++i, newClanId);
					newServerStatement.setInt(++i, rset.getInt("createtime"));
					newServerStatement.setInt(++i, rset.getInt("deletetime"));
					newServerStatement.setString(++i, ConfigHolder.getString("MergeNewTitle"));
					newServerStatement.setInt(++i, rset.getInt("rec_have"));
					newServerStatement.setInt(++i, rset.getInt("rec_left"));
					newServerStatement.setInt(++i, rset.getInt("rec_bonus_time"));
					newServerStatement.setInt(++i, rset.getInt("hunt_points"));
					newServerStatement.setInt(++i, rset.getInt("hunt_time"));
					newServerStatement.setInt(++i, rset.getInt("accesslevel"));
					newServerStatement.setInt(++i, rset.getInt("online"));
					newServerStatement.setInt(++i, rset.getInt("onlinetime"));
					newServerStatement.setInt(++i, rset.getInt("lastAccess"));
					newServerStatement.setLong(++i, rset.getLong("leaveclan"));
					newServerStatement.setLong(++i, rset.getLong("deleteclan"));
					newServerStatement.setLong(++i, rset.getLong("nochannel"));
					newServerStatement.setInt(++i, newClanId > 0 ? rset.getInt("pledge_type") : -128);
					newServerStatement.setInt(++i, newClanId > 0 ? rset.getInt("pledge_rank") : 0);
					newServerStatement.setInt(++i, rset.getInt("lvl_joined_academy"));
					newServerStatement.setInt(++i, rset.getInt("apprentice"));
					newServerStatement.setBytes(++i, rset.getBytes("key_bindings"));
					newServerStatement.setInt(++i, rset.getInt("pcBangPoints"));
					newServerStatement.setInt(++i, rset.getInt("vitality"));
					newServerStatement.setInt(++i, rset.getInt("fame"));
					newServerStatement.setInt(++i, rset.getInt("bookmarks"));
					newServerStatement.setString(++i, rset.getString("hwid_lock"));
					newServerStatement.setInt(++i, rset.getInt("raidkills"));
					newServerStatement.setInt(++i, rset.getInt("eventKills"));
					newServerStatement.setInt(++i, rset.getInt("siege_kills"));
					newServerStatement.setString(++i, rset.getString("lastHWID"));
					newServerStatement.setString(++i, rset.getString("lastIP"));
					newServerStatement.setString(++i, rset.getString("forum_login"));
					newServerStatement.executeUpdate();
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while merging Character. Old Char Id: " + oldCharId, e);
		}
	}

	private static void mergeSubclassesTable(int oldCharId, Connection oldServerCon, int newCharId, Connection newServerCon)
	{
		try (PreparedStatement oldServerStatement = oldServerCon
					.prepareStatement("SELECT class_id, level, exp, sp, curHp, curMp, curCp, maxHp, maxMp, maxCp, active, isBase, death_penalty, certification from character_subclasses WHERE char_obj_id = ?");
					PreparedStatement newServerStatement = newServerCon.prepareStatement(
								"INSERT INTO character_subclasses (char_obj_id, class_id, level, exp, sp, curHp, curMp, curCp, maxHp, maxMp, maxCp, active, isBase, death_penalty, certification) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"))
		{
			oldServerStatement.setInt(1, oldCharId);
			try (ResultSet rset = oldServerStatement.executeQuery())
			{
				while (rset.next())
				{
					int i = 0;
					newServerStatement.setInt(++i, newCharId);
					newServerStatement.setInt(++i, rset.getInt("class_id"));
					newServerStatement.setInt(++i, rset.getInt("level"));
					newServerStatement.setLong(++i, rset.getLong("exp"));
					newServerStatement.setLong(++i, rset.getLong("sp"));
					newServerStatement.setDouble(++i, rset.getDouble("curHp"));
					newServerStatement.setDouble(++i, rset.getDouble("curMp"));
					newServerStatement.setDouble(++i, rset.getDouble("curCp"));
					newServerStatement.setInt(++i, rset.getInt("maxHp"));
					newServerStatement.setInt(++i, rset.getInt("maxMp"));
					newServerStatement.setInt(++i, rset.getInt("maxCp"));
					newServerStatement.setInt(++i, rset.getInt("active"));
					newServerStatement.setInt(++i, rset.getInt("isBase"));
					newServerStatement.setInt(++i, rset.getInt("death_penalty"));
					newServerStatement.setInt(++i, rset.getInt("certification"));
					newServerStatement.executeUpdate();
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while merging character_subclasses. Old Char Id: " + oldCharId, e);
		}
	}

	private static void mergeHennasTable(int oldCharId, Connection oldServerCon, int newCharId, Connection newServerCon)
	{
		try (PreparedStatement oldServerStatement = oldServerCon.prepareStatement("SELECT symbol_id, slot, class_index FROM character_hennas WHERE char_obj_id = ?");
					PreparedStatement newServerStatement = newServerCon.prepareStatement("INSERT INTO character_hennas (char_obj_id, symbol_id, slot, class_index) VALUES (?,?,?,?)"))
		{
			oldServerStatement.setInt(1, oldCharId);
			try (ResultSet rset = oldServerStatement.executeQuery())
			{
				while (rset.next())
				{
					int i = 0;
					newServerStatement.setInt(++i, newCharId);
					newServerStatement.setInt(++i, rset.getInt("symbol_id"));
					newServerStatement.setInt(++i, rset.getInt("slot"));
					newServerStatement.setInt(++i, rset.getInt("class_index"));
					newServerStatement.executeUpdate();
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while merging character_hennas. Old Char Id: " + oldCharId, e);
		}
	}

	private static void mergeMacrosesTable(int oldCharId, Connection oldServerCon, int newCharId, Connection newServerCon)
	{
		try (PreparedStatement oldServerStatement = oldServerCon.prepareStatement("SELECT id, icon, name, descr, acronym, commands FROM character_macroses WHERE char_obj_id = ?");
					PreparedStatement newServerStatement = newServerCon.prepareStatement("INSERT INTO character_macroses (char_obj_id, id, icon, name, descr, acronym, commands) VALUES (?,?,?,?,?,?,?)"))
		{
			oldServerStatement.setInt(1, oldCharId);
			try (ResultSet rset = oldServerStatement.executeQuery())
			{
				while (rset.next())
				{
					int i = 0;
					newServerStatement.setInt(++i, newCharId);
					newServerStatement.setInt(++i, rset.getInt("id"));
					newServerStatement.setInt(++i, rset.getInt("icon"));
					newServerStatement.setString(++i, rset.getString("name"));
					newServerStatement.setString(++i, rset.getString("descr"));
					newServerStatement.setString(++i, rset.getString("acronym"));
					newServerStatement.setString(++i, rset.getString("commands"));
					newServerStatement.executeUpdate();
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while merging character_macroses. Old Char Id: " + oldCharId, e);
		}
	}

	private static void mergeQuestsTable(int oldCharId, Connection oldServerCon, int newCharId, Connection newServerCon)
	{
		try (PreparedStatement oldServerStatement = oldServerCon.prepareStatement("SELECT name, var, value FROM character_quests WHERE char_id = ?");
					PreparedStatement newServerStatement = newServerCon.prepareStatement("INSERT INTO character_quests (char_id, name, var, value) VALUES (?,?,?,?)"))
		{
			oldServerStatement.setInt(1, oldCharId);
			try (ResultSet rset = oldServerStatement.executeQuery())
			{
				while (rset.next())
				{
					int i = 0;
					newServerStatement.setInt(++i, newCharId);
					newServerStatement.setString(++i, rset.getString("name"));
					newServerStatement.setString(++i, rset.getString("var"));
					newServerStatement.setString(++i, rset.getString("value"));
					newServerStatement.executeUpdate();
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while merging character_quests. Old Char Id: " + oldCharId, e);
		}
	}

	private static void mergeShortcutsTable(int oldCharId, Connection oldServerCon, int newCharId, Connection newServerCon)
	{
		try (PreparedStatement oldServerStatement = oldServerCon.prepareStatement("SELECT slot, page, type, shortcut_id, level, class_index, character_type FROM character_shortcuts WHERE object_id = ?");
					PreparedStatement newServerStatement = newServerCon
								.prepareStatement("INSERT INTO character_shortcuts (object_id, slot, page, type, shortcut_id, level, class_index, character_type) VALUES (?,?,?,?,?,?,?,?)"))
		{
			oldServerStatement.setInt(1, oldCharId);
			try (ResultSet rset = oldServerStatement.executeQuery())
			{
				while (rset.next())
				{
					int i = 0;
					newServerStatement.setInt(++i, newCharId);
					newServerStatement.setInt(++i, rset.getInt("slot"));
					newServerStatement.setInt(++i, rset.getInt("page"));
					newServerStatement.setInt(++i, rset.getInt("type"));
					newServerStatement.setInt(++i, rset.getInt("shortcut_id"));
					newServerStatement.setInt(++i, rset.getInt("level"));
					newServerStatement.setInt(++i, rset.getInt("class_index"));
					newServerStatement.setInt(++i, rset.getInt("character_type"));
					newServerStatement.executeUpdate();
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while merging character_shortcuts. Old Char Id: " + oldCharId, e);
		}
	}

	private static void mergeSkillsTable(int oldCharId, Connection oldServerCon, int newCharId, Connection newServerCon)
	{
		try (PreparedStatement oldServerStatement = oldServerCon.prepareStatement("SELECT skill_id, skill_level, class_index FROM character_skills WHERE char_obj_id = ?");
					PreparedStatement newServerStatement = newServerCon.prepareStatement("INSERT INTO character_skills (char_obj_id, skill_id, skill_level, class_index) VALUES (?,?,?,?)"))
		{
			oldServerStatement.setInt(1, oldCharId);
			try (ResultSet rset = oldServerStatement.executeQuery())
			{
				while (rset.next())
				{
					int i = 0;
					newServerStatement.setInt(++i, newCharId);
					newServerStatement.setInt(++i, rset.getInt("skill_id"));
					newServerStatement.setInt(++i, rset.getInt("skill_level"));
					newServerStatement.setInt(++i, rset.getInt("class_index"));
					newServerStatement.executeUpdate();
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while merging character_skills. Old Char Id: " + oldCharId, e);
		}
	}

	private static void mergeVariablesTable(int oldCharId, Connection oldServerCon, int newCharId, Connection newServerCon)
	{
		try (PreparedStatement oldServerStatement = oldServerCon.prepareStatement("SELECT type, name, value, expire_time FROM character_variables WHERE obj_id = ?");
					PreparedStatement newServerStatement = newServerCon.prepareStatement("INSERT INTO character_variables (obj_id, type, name, value, expire_time) VALUES (?,?,?,?,?)"))
		{
			oldServerStatement.setInt(1, oldCharId);
			try (ResultSet rset = oldServerStatement.executeQuery())
			{
				while (rset.next())
				{
					int i = 0;
					newServerStatement.setInt(++i, newCharId);
					newServerStatement.setString(++i, rset.getString("type"));
					newServerStatement.setString(++i, rset.getString("name"));
					newServerStatement.setString(++i, rset.getString("value"));
					newServerStatement.setLong(++i, rset.getLong("expire_time"));
					newServerStatement.executeUpdate();
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while merging character_variables. Old Char Id: " + oldCharId, e);
		}
	}

	private static void mergeOlympiadNoblessTable(int oldCharId, Connection oldServerCon, int newCharId, Connection newServerCon)
	{
		boolean isNoble = false;
		try (PreparedStatement oldServerStatement = oldServerCon.prepareStatement(
					"SELECT class_id, olympiad_points, olympiad_points_past, olympiad_points_past_static, competitions_done, competitions_win, competitions_loose, game_classes_count, game_noclasses_count, game_team_count FROM olympiad_nobles WHERE char_id = ?");
					PreparedStatement newServerStatement = newServerCon.prepareStatement(
								"INSERT INTO olympiad_nobles (char_id, class_id, olympiad_points, olympiad_points_past, olympiad_points_past_static, competitions_done, competitions_win, competitions_loose, game_classes_count, game_noclasses_count, game_team_count) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)"))
		{
			oldServerStatement.setInt(1, oldCharId);
			try (ResultSet rset = oldServerStatement.executeQuery())
			{
				while (rset.next())
				{
					int i = 0;
					newServerStatement.setInt(++i, newCharId);
					newServerStatement.setInt(++i, rset.getInt("class_id"));
					newServerStatement.setInt(++i, rset.getInt("olympiad_points"));
					newServerStatement.setInt(++i, rset.getInt("olympiad_points_past"));
					newServerStatement.setInt(++i, rset.getInt("olympiad_points_past_static"));
					newServerStatement.setInt(++i, rset.getInt("competitions_done"));
					newServerStatement.setInt(++i, rset.getInt("competitions_win"));
					newServerStatement.setInt(++i, rset.getInt("competitions_loose"));
					newServerStatement.setInt(++i, rset.getInt("game_classes_count"));
					newServerStatement.setInt(++i, rset.getInt("game_noclasses_count"));
					newServerStatement.setInt(++i, rset.getInt("game_team_count"));
					newServerStatement.executeUpdate();
					isNoble = true;
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while merging olympiad_nobles. Old Char Id: " + oldCharId, e);
		}
		if (isNoble)
		{
			OlympiadNobleDAO.load(newCharId);
		}
	}

	private static void mergeHeroesTable(int oldCharId, Connection oldServerCon, int newCharId, Connection newServerCon)
	{
		if (!ConfigHolder.getBool("MergeHeroStatus"))
		{
			return;
		}

		int count = -1;
		int active = -1;
		int played = -1;
		try (PreparedStatement oldServerStatement = oldServerCon.prepareStatement("SELECT count, active, played, message FROM heroes WHERE char_id = ?");
					PreparedStatement newServerStatement = newServerCon.prepareStatement("INSERT INTO heroes (char_id, count, active, played, message) VALUES (?,?,?,?,?)"))
		{
			oldServerStatement.setInt(1, oldCharId);
			try (ResultSet rset = oldServerStatement.executeQuery())
			{
				while (rset.next())
				{
					count = rset.getInt("count");
					active = rset.getInt("active");
					played = rset.getInt("played");
					int i = 0;
					newServerStatement.setInt(++i, newCharId);
					newServerStatement.setInt(++i, count);
					newServerStatement.setInt(++i, active);
					newServerStatement.setInt(++i, played);
					newServerStatement.setString(++i, rset.getString("message"));
					newServerStatement.executeUpdate();
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while merging heroes. Old Char Id: " + oldCharId, e);
		}
		if (played >= 1)
		{
			Hero.getInstance().activateHero(newCharId, count, played, active);
		}
	}

	protected static void mergeItemsTable(int oldOwnerId, Connection oldServerCon, int newOwnerId, Connection newServerCon)
	{
		try (PreparedStatement oldServerStatement = oldServerCon.prepareStatement(
					"SELECT item_id, count, enchant_level, loc, loc_data, life_time, augmentation_id, attribute_fire, attribute_water, attribute_wind, attribute_earth, attribute_holy, attribute_unholy, custom_type1, custom_type2, custom_flags, agathion_energy, visual_item_id FROM items WHERE owner_id = ?");
					PreparedStatement newServerStatement = newServerCon.prepareStatement(
								"INSERT INTO items (object_id, owner_id, item_id, count, enchant_level, loc, loc_data, life_time, augmentation_id, attribute_fire, attribute_water, attribute_wind, attribute_earth, attribute_holy, attribute_unholy, custom_type1, custom_type2, custom_flags, agathion_energy, visual_item_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"))
		{
			oldServerStatement.setInt(1, oldOwnerId);
			try (ResultSet rset = oldServerStatement.executeQuery())
			{
				while (rset.next())
				{
					final int itemId = checkItemToSwap(rset.getInt("item_id"));
					if (ArrayUtils.contains(ConfigHolder.getIntArray("MergeNotTransferItemIds"), itemId))
					{
						continue;
					}

					final int enchantLevel = ConfigHolder.getInt("MergeItemsMaxEnchant") >= 0 ? Math.min(rset.getInt("enchant_level"), ConfigHolder.getInt("MergeItemsMaxEnchant"))
								: rset.getInt("enchant_level");
					final long count = checkCountMultiply(itemId, rset.getLong("count"));
					if (count <= 0L)
					{
						continue;
					}

					String location = rset.getString("loc");
					if (location.equals("AUCTION") || location.equals("MAIL"))
					{
						location = "WAREHOUSE";
					}
					int visualItemId = rset.getInt("visual_item_id");
					if (!ItemHolder.getInstance().checkTemplateExists(itemId))
					{
						continue;
					}

					if (!ItemHolder.getInstance().checkTemplateExists(visualItemId))
					{
						visualItemId = 0;
					}
					int i = 0;
					final int itemObjectId = IdFactory.getInstance().getNextId();
					newServerStatement.setInt(++i, itemObjectId);
					newServerStatement.setInt(++i, newOwnerId);
					newServerStatement.setInt(++i, itemId);
					newServerStatement.setLong(++i, count);
					newServerStatement.setInt(++i, enchantLevel);
					newServerStatement.setString(++i, location);
					newServerStatement.setInt(++i, rset.getInt("loc_data"));
					newServerStatement.setInt(++i, rset.getInt("life_time"));
					newServerStatement.setInt(++i, rset.getInt("augmentation_id"));
					newServerStatement.setInt(++i, rset.getInt("attribute_fire"));
					newServerStatement.setInt(++i, rset.getInt("attribute_water"));
					newServerStatement.setInt(++i, rset.getInt("attribute_wind"));
					newServerStatement.setInt(++i, rset.getInt("attribute_earth"));
					newServerStatement.setInt(++i, rset.getInt("attribute_holy"));
					newServerStatement.setInt(++i, rset.getInt("attribute_unholy"));
					newServerStatement.setInt(++i, rset.getInt("custom_type1"));
					newServerStatement.setInt(++i, rset.getInt("custom_type2"));
					newServerStatement.setInt(++i, rset.getInt("custom_flags"));
					newServerStatement.setInt(++i, rset.getInt("agathion_energy"));
					newServerStatement.setInt(++i, visualItemId);
					newServerStatement.executeUpdate();
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while merging items. Old Owner Id: " + oldOwnerId, e);
		}
	}

	private static int checkItemToSwap(int itemId)
	{
		for (int[] ids : ConfigHolder.getMultiIntArray("MergeItemsIdsSwap"))
		{
			if (ids[0] == itemId)
			{
				return ids[1];
			}
		}
		return itemId;
	}

	private static long checkCountMultiply(int itemId, long count)
	{
		for (double[] idAmount : ConfigHolder.getMultiDoubleArray("MergeItemsAmountToMultiply"))
		{
			if ((int) idAmount[0] == itemId)
			{
				return (long) (idAmount[1] * count);
			}
		}
		return count;
	}

	public static DataMerge getInstance()
	{
		return SingletonHolder.instance;
	}

	private static class SingletonHolder
	{
		private static final DataMerge instance = new DataMerge();
	}
}
