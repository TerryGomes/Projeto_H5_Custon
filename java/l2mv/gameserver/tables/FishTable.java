package l2mv.gameserver.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.trove.map.hash.TIntObjectHashMap;
import l2mv.commons.dbutils.DbUtils;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.reward.RewardData;
import l2mv.gameserver.templates.FishTemplate;

public class FishTable
{
	private static final Logger _log = LoggerFactory.getLogger(FishTable.class);

	private static final FishTable _instance = new FishTable();

	public static final FishTable getInstance()
	{
		return _instance;
	}

	private TIntObjectHashMap<List<FishTemplate>> _fishes;
	private TIntObjectHashMap<List<RewardData>> _fishRewards;

	private FishTable()
	{
		load();
	}

	public void reload()
	{
		load();
	}

	private void load()
	{
		_fishes = new TIntObjectHashMap<List<FishTemplate>>();
		_fishRewards = new TIntObjectHashMap<List<RewardData>>();

		int count = 0;
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT id, level, name, hp, hpregen, fish_type, fish_group, fish_guts, guts_check_time, wait_time, combat_time FROM fish ORDER BY id");
			resultSet = statement.executeQuery();

			FishTemplate fish;
			List<FishTemplate> fishes;
			while (resultSet.next())
			{
				int id = resultSet.getInt("id");
				int lvl = resultSet.getInt("level");
				String name = resultSet.getString("name");
				int hp = resultSet.getInt("hp");
				int hpreg = resultSet.getInt("hpregen");
				int type = resultSet.getInt("fish_type");
				int group = resultSet.getInt("fish_group");
				int fish_guts = resultSet.getInt("fish_guts");
				int guts_check_time = resultSet.getInt("guts_check_time");
				int wait_time = resultSet.getInt("wait_time");
				int combat_time = resultSet.getInt("combat_time");

				fish = new FishTemplate(id, lvl, name, hp, hpreg, type, group, fish_guts, guts_check_time, wait_time, combat_time);
				if ((fishes = _fishes.get(group)) == null)
				{
					_fishes.put(group, fishes = new ArrayList<FishTemplate>());
				}
				fishes.add(fish);
				count++;
			}

			DbUtils.close(statement, resultSet);

			_log.info("FishTable: Loaded " + count + " fishes.");

			count = 0;

			statement = con.prepareStatement("SELECT fishid, rewardid, min, max, chance FROM fishreward ORDER BY fishid");
			resultSet = statement.executeQuery();

			RewardData reward;
			List<RewardData> rewards;
			while (resultSet.next())
			{
				int fishid = resultSet.getInt("fishid");
				int rewardid = resultSet.getInt("rewardid");
				int mindrop = resultSet.getInt("min");
				int maxdrop = resultSet.getInt("max");
				int chance = resultSet.getInt("chance");

				reward = new RewardData(rewardid, mindrop, maxdrop, chance * 10000.);
				if ((rewards = _fishRewards.get(fishid)) == null)
				{
					_fishRewards.put(fishid, rewards = new ArrayList<RewardData>());
				}

				rewards.add(reward);
				count++;
			}

			_log.info("FishTable: Loaded " + count + " fish rewards.");
		}
		catch (SQLException e)
		{
			_log.error("Error while loading Fishes", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, resultSet);
		}
	}

	public int[] getFishIds()
	{
		return _fishRewards.keys();
	}

	public List<FishTemplate> getFish(int group, int type, int lvl)
	{
		List<FishTemplate> result = new ArrayList<FishTemplate>();

		List<FishTemplate> fishs = _fishes.get(group);
		if (fishs == null)
		{
			_log.warn("No fishes defined for group : " + group + "!");
			return null;
		}

		for (FishTemplate f : fishs)
		{
			if ((f.getType() != type) || (f.getLevel() != lvl))
			{
				continue;
			}

			result.add(f);
		}

		if (result.isEmpty())
		{
			_log.warn("No fishes for group : " + group + " type: " + type + " level: " + lvl + "!");
		}

		return result;
	}

	public List<RewardData> getFishReward(int fishid)
	{
		List<RewardData> result = _fishRewards.get(fishid);
		if (_fishRewards == null)
		{
			_log.warn("No fish rewards defined for fish id: " + fishid + "!");
			return null;
		}

		if (result.isEmpty())
		{
			_log.warn("No fish rewards for fish id: " + fishid + "!");
		}

		return result;
	}
}