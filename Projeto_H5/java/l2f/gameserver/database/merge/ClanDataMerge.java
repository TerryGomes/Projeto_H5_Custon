package l2f.gameserver.database.merge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.ConfigHolder;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.idfactory.IdFactory;
import l2f.gameserver.instancemanager.ServerVariables;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.model.pledge.SubUnit;
import l2f.gameserver.model.pledge.UnitMember;
import l2f.gameserver.tables.ClanTable;

public class ClanDataMerge
{
	private static final Logger LOG = LoggerFactory.getLogger(ClanDataMerge.class);
	public static final int NOT_MERGED_CLAN_LEADER_ID = -2;

	private final Map<Integer, Integer> oldClanIdNewClanId;

	public ClanDataMerge()
	{
		oldClanIdNewClanId = new HashMap<Integer, Integer>();
		if (ConfigHolder.getBool("EnableMerge") && ConfigHolder.getBool("EnableClansMerge"))
		{
			if (ServerVariables.getString(ConfigHolder.getString("MergeClansOverVariable"), null) == null)
			{
				mergeClans();
			}
			else
			{
				loadMergedClanIds();
			}
		}
	}

	public int getNewClanId(int oldClanId, int defaultValue)
	{
		if (!ConfigHolder.getBool("EnableClansMerge"))
		{
			return defaultValue;
		}
		final int newClanId = oldClanIdNewClanId.getOrDefault(oldClanId, defaultValue);
		if ((newClanId <= 0) || (ClanTable.getInstance().getClan(newClanId) == null))
		{
			return defaultValue;
		}
		return newClanId;
	}

	public void onCharacterMerged(Connection oldServerCon, int oldCharId, Connection newServerCon, int newCharId)
	{
		if (!ConfigHolder.getBool("EnableClansMerge"))
		{
			return;
		}

		Clan.restoreSingleMember(newCharId);
		try (PreparedStatement statement = oldServerCon.prepareStatement("SELECT clan_id, type FROM clan_subpledges WHERE leader_id = ?"))
		{
			statement.setInt(1, oldCharId);
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					final int oldClanId = rset.getInt("clan_id");
					final int newClanId = oldClanIdNewClanId.getOrDefault(oldClanId, -1);
					if (newCharId <= 0)
					{
						return;
					}
					final Clan clan = ClanTable.getInstance().getClan(newClanId);
					if (clan == null)
					{
						return;
					}
					final int type = rset.getInt("type");
					final SubUnit unit = clan.getSubUnit(type);
					if (unit.getLeaderObjectId() > 0)
					{
						return;
					}
					final UnitMember leaderMember = unit.getUnitMember(newCharId);
					unit.setLeader(leaderMember, true);
					if (type == 0 && unit.getName().endsWith(ConfigHolder.getString("MergeClansAddCharOnSameName")))
					{
						updateClanName(oldServerCon, oldClanId, unit);
					}
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while fixing Leader Id of Old Char Id: " + oldCharId + ", New Char Id: " + newCharId, e);
		}
	}

	private void loadMergedClanIds()
	{
		try (Connection oldServerCon = MergeDatabaseFactory.getInstance().getConnection(); PreparedStatement statement = oldServerCon.prepareStatement("SELECT old_clan_id, new_clan_id FROM merge_data_clan"); ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				oldClanIdNewClanId.put(rset.getInt("old_clan_id"), rset.getInt("new_clan_id"));
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while loading Merged Clan Ids!", e);
		}
	}

	private void mergeClans()
	{
		LOG.info("Starting Clans Merge!");
		try (Connection oldServerCon = MergeDatabaseFactory.getInstance().getConnection(); Connection newServerCon = DatabaseFactory.getInstance().getConnection())
		{
			newServerCon.setAutoCommit(false);
			mergeClanData(oldServerCon, newServerCon);
			mergeClanPrivs(oldServerCon, newServerCon);
			mergeClanSkills(oldServerCon, newServerCon);
			mergeClanSubpledges(oldServerCon, newServerCon);
			mergeClanSubpledgesSkills(oldServerCon, newServerCon);
			mergeClanItems(oldServerCon, newServerCon);
			updateClanTable();
			saveMergedClanIds(oldServerCon);
			ServerVariables.set(ConfigHolder.getString("MergeClansOverVariable"), "Success");
			LOG.info(oldClanIdNewClanId + " Clans successfully Merged!");
		}
		catch (SQLException e)
		{
			LOG.error("Error while merging Clans!", e);
			ServerVariables.set(ConfigHolder.getString("MergeClansOverVariable"), "Error");
		}
	}

	private void mergeClanData(Connection oldServerCon, Connection newServerCon) throws SQLException
	{
		final boolean transferCRP = ConfigHolder.getBool("MergeClansTransferCRP");
		try (PreparedStatement oldServerStatement = oldServerCon.prepareStatement("SELECT clan_id, clan_level, reputation_score, warehouse, auction_bid_at, airship FROM clan_data");
					PreparedStatement newServerStatement = newServerCon.prepareStatement("INSERT INTO clan_data (clan_id, clan_level, hasCastle, hasFortress, hasHideout, ally_id, crest, largecrest, reputation_score, warehouse, expelled_member, leaved_ally, dissolved_ally, auction_bid_at, airship) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					ResultSet rset = oldServerStatement.executeQuery())
		{
			while (rset.next())
			{
				final int oldClanId = rset.getInt("clan_id");
				final int newClanId = IdFactory.getInstance().getNextId();
				oldClanIdNewClanId.put(oldClanId, newClanId);
				final int level = Math.min(rset.getInt("clan_level"), ConfigHolder.getInt("MergeClansMaxLevel"));
				int i = 0;
				newServerStatement.setInt(++i, newClanId);
				newServerStatement.setInt(++i, level);
				newServerStatement.setInt(++i, 0);
				newServerStatement.setInt(++i, 0);
				newServerStatement.setInt(++i, 0);
				newServerStatement.setInt(++i, 0);
				newServerStatement.setNull(++i, 2004);
				newServerStatement.setNull(++i, 2004);
				newServerStatement.setInt(++i, transferCRP ? rset.getInt("reputation_score") : 0);
				newServerStatement.setInt(++i, rset.getInt("warehouse"));
				newServerStatement.setLong(++i, 0L);
				newServerStatement.setLong(++i, 0L);
				newServerStatement.setLong(++i, 0L);
				newServerStatement.setInt(++i, rset.getInt("auction_bid_at"));
				newServerStatement.setInt(++i, rset.getInt("airship"));
				newServerStatement.addBatch();
			}
			newServerStatement.executeBatch();
			newServerCon.commit();
		}
	}

	private void mergeClanPrivs(Connection oldServerCon, Connection newServerCon) throws SQLException
	{
		try (PreparedStatement oldServerStatement = oldServerCon.prepareStatement("SELECT clan_id, rank, privilleges FROM clan_privs"); PreparedStatement newServerStatement = newServerCon.prepareStatement("INSERT INTO clan_privs (clan_id, rank, privilleges) VALUES (?,?,?)"); ResultSet rset = oldServerStatement.executeQuery())
		{
			while (rset.next())
			{
				final int oldClanId = rset.getInt("clan_id");
				final int newClanId = oldClanIdNewClanId.getOrDefault(oldClanId, -1);
				if (newClanId > 0)
				{
					int i = 0;
					newServerStatement.setInt(++i, newClanId);
					newServerStatement.setInt(++i, rset.getInt("rank"));
					newServerStatement.setInt(++i, rset.getInt("privilleges"));
					newServerStatement.addBatch();
				}
			}
			newServerStatement.executeBatch();
			newServerCon.commit();
		}
	}

	private void mergeClanSkills(Connection oldServerCon, Connection newServerCon) throws SQLException
	{
		try (PreparedStatement oldServerStatement = oldServerCon.prepareStatement("SELECT clan_id, skill_id, skill_level FROM clan_skills"); PreparedStatement newServerStatement = newServerCon.prepareStatement("INSERT INTO clan_skills (clan_id, skill_id, skill_level) VALUES (?,?,?)"); ResultSet rset = oldServerStatement.executeQuery())
		{
			while (rset.next())
			{
				final int oldClanId = rset.getInt("clan_id");
				final int newClanId = oldClanIdNewClanId.getOrDefault(oldClanId, -1);
				if (newClanId > 0)
				{
					int i = 0;
					newServerStatement.setInt(++i, newClanId);
					newServerStatement.setInt(++i, rset.getInt("skill_id"));
					newServerStatement.setInt(++i, rset.getInt("skill_level"));
					newServerStatement.addBatch();
				}
			}
			newServerStatement.executeBatch();
			newServerCon.commit();
		}
	}

	private void mergeClanSubpledges(Connection oldServerCon, Connection newServerCon) throws SQLException
	{
		final String addCharOnSameName = ConfigHolder.getString("MergeClansAddCharOnSameName");
		final ClanTable clanTable = ClanTable.getInstance();
		try (PreparedStatement oldServerStatement = oldServerCon.prepareStatement("SELECT clan_id, type, name, leader_id FROM clan_subpledges"); PreparedStatement newServerStatement = newServerCon.prepareStatement("INSERT INTO clan_subpledges (clan_id, type, name, leader_id) VALUES (?,?,?,?)"); ResultSet rset = oldServerStatement.executeQuery())
		{
			while (rset.next())
			{
				final int oldClanId = rset.getInt("clan_id");
				final int newClanId = oldClanIdNewClanId.getOrDefault(oldClanId, -1);
				if (newClanId > 0)
				{
					final int type = rset.getInt("type");
					final String oldName = rset.getString("name");
					String newName;
					if (type != 0 || clanTable.getClanByName(oldName) == null)
					{
						newName = oldName;
					}
					else
					{
						newName = oldName + addCharOnSameName;
					}
					int i = 0;
					newServerStatement.setInt(++i, newClanId);
					newServerStatement.setInt(++i, type);
					newServerStatement.setString(++i, newName);
					newServerStatement.setInt(++i, -2);
					newServerStatement.addBatch();
				}
			}
			newServerStatement.executeBatch();
			newServerCon.commit();
		}
	}

	private void mergeClanSubpledgesSkills(Connection oldServerCon, Connection newServerCon) throws SQLException
	{
		try (PreparedStatement oldServerStatement = oldServerCon.prepareStatement("SELECT clan_id, type, skill_id, skill_level FROM clan_subpledges_skills"); PreparedStatement newServerStatement = newServerCon.prepareStatement("INSERT INTO clan_subpledges_skills (clan_id, type, skill_id, skill_level) VALUES (?,?,?,?)"); ResultSet rset = oldServerStatement.executeQuery())
		{
			while (rset.next())
			{
				final int oldClanId = rset.getInt("clan_id");
				final int newClanId = oldClanIdNewClanId.getOrDefault(oldClanId, -1);
				if (newClanId > 0)
				{
					int i = 0;
					newServerStatement.setInt(++i, newClanId);
					newServerStatement.setInt(++i, rset.getInt("type"));
					newServerStatement.setInt(++i, rset.getInt("skill_id"));
					newServerStatement.setInt(++i, rset.getInt("skill_level"));
					newServerStatement.addBatch();
				}
			}
			newServerStatement.executeBatch();
			newServerCon.commit();
		}
	}

	private void mergeClanItems(Connection oldServerCon, Connection newServerCon)
	{
		for (Map.Entry<Integer, Integer> idsEntry : oldClanIdNewClanId.entrySet())
		{
			DataMerge.mergeItemsTable(idsEntry.getKey(), oldServerCon, idsEntry.getValue(), newServerCon);
		}

		try
		{
			newServerCon.commit();
		}
		catch (SQLException e)
		{
			LOG.error("Error while merging Clan Items!", e);
		}
	}

	private void updateClanTable()
	{
		for (Integer newClanId : oldClanIdNewClanId.values())
		{
			ClanTable.getInstance().restoreClan(newClanId);
		}
	}

	private void saveMergedClanIds(Connection oldServerCon) throws SQLException
	{
		try (PreparedStatement statement = oldServerCon.prepareStatement("INSERT INTO merge_data_clan VALUES (?,?,?,?)"))
		{
			for (Map.Entry<Integer, Integer> idsEntry : oldClanIdNewClanId.entrySet())
			{
				int i = 0;
				final String newClanName = ClanTable.getInstance().getClanName(idsEntry.getValue());
				statement.setInt(++i, idsEntry.getKey());
				statement.setInt(++i, idsEntry.getValue());
				statement.setString(++i, getOldClanName(oldServerCon, idsEntry.getKey()));
				statement.setString(++i, newClanName.endsWith(ConfigHolder.getString("MergeClansAddCharOnSameName")) ? "" : newClanName);
				statement.addBatch();
			}
			statement.executeBatch();
		}
	}

	private static void updateClanName(Connection oldServerCon, int oldClanId, SubUnit newUnit)
	{
		String newClanName = null;
		try (PreparedStatement statement = oldServerCon.prepareStatement("SELECT new_clan_name FROM merge_data_clan WHERE old_clan_id=?"))
		{
			statement.setInt(1, oldClanId);
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					newClanName = rset.getString("new_clan_name");
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while getting New Clan Name of Merged Clan Old Id: " + oldClanId);
		}
		if (newClanName == null || newClanName.isEmpty())
		{
			LOG.warn("New Clan Name is NULL for Old Clan Id: " + oldClanId + " Unit Type: " + newUnit.getType());
			return;
		}
		newUnit.setName(newClanName, true);
	}

	private static String getOldClanName(Connection oldServerCon, int oldClanId)
	{
		try (PreparedStatement statement = oldServerCon.prepareStatement("SELECT name FROM clan_subpledges WHERE clan_id=? AND type=0"))
		{
			statement.setInt(1, oldClanId);
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					return rset.getString("name");
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while getting Old Merged Clan Name of Clan Id: " + oldClanId, e);
		}
		return "NOT_FOUND";
	}

	public static ClanDataMerge getInstance()
	{
		return SingletonHolder.instance;
	}

	private static class SingletonHolder
	{
		private static final ClanDataMerge instance = new ClanDataMerge();
	}
}
