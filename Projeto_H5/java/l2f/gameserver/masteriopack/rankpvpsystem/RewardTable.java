/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2f.gameserver.masteriopack.rankpvpsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.tables.SkillTable;

/**
 * This class contains informations about Rewards for reach a Rank.<br>
 * Each reward is defined in database and it is static table in game.
 * @author Masterio
 */
public class RewardTable
{
	public static final Logger log = Logger.getLogger(RewardTable.class.getName());

	private static RewardTable _instance = null;

	/** [RankID, RankReward] */
	private Map<Integer, RankReward> _rankRewardList = new HashMap<>();

	private RewardTable()
	{
		long startTime = Calendar.getInstance().getTimeInMillis();

		load();

		long endTime = Calendar.getInstance().getTimeInMillis();

		log.info(" - RewardTable: Data loaded. " + _rankRewardList.size() + " objects in " + (endTime - startTime) + " ms.");
	}

	public static RewardTable getInstance()
	{
		if (_instance == null)
		{
			_instance = new RewardTable();
		}

		return _instance;
	}

	/**
	 * [Reward ID, RankReward object]
	 * @return
	 */
	public Map<Integer, RankReward> getRankRewardList()
	{
		return _rankRewardList;
	}

	/**
	 * [Reward ID, RankReward object]
	 * @param rankRewardList
	 */
	public void setRankRewardList(Map<Integer, RankReward> rankRewardList)
	{
		_rankRewardList = rankRewardList;
	}

	/**
	 * Returns the list of rewards by player rank.
	 * @param player
	 * @return
	 */
	public Map<Integer, Reward> getRankPvpRewardList(Player player)
	{
		int rankId = PvpTable.getInstance().getRankId(player.getObjectId());

		RankReward rr = _rankRewardList.get(rankId);

		if (rr != null)
		{
			return rr.getRankPvpRewardList();
		}

		return new HashMap<>();
	}

	/**
	 * Returns the list of rewards by player rank.
	 * @param rankId
	 * @return
	 */
	public Map<Integer, Reward> getRankPvpRewardList(int rankId)
	{
		RankReward rr = _rankRewardList.get(rankId);

		if (rr != null)
		{
			return rr.getRankPvpRewardList();
		}

		return new HashMap<>();
	}

	/**
	 * Gives the all new Rank Rewards for player (inventory overload possible).
	 * @param player
	 * @param newRankId
	 */
	public void giveRankLevelRewards(Player player, int newRankId)
	{
		if (player == null)
		{
			return;
		}

		// overloads the inventory, there is no repeat method for this action. Reward is given only once (in rank level time).

		// add items into player's inventory:
		RankReward rr = _rankRewardList.get(newRankId);

		if (rr == null)
		{
			return;
		}

		for (Map.Entry<Integer, Reward> e : rr.getRankLevelRewardList().entrySet())
		{
			Reward reward = e.getValue();

			if (reward != null)
			{
				player.getInventory().addItem(reward.getItemId(), reward.getItemAmount(), "rank reward");
			}
		}
	}

	/**
	 * Gives Rank Rewards for kill the Victim with RankId (inventory overload possible).
	 * @param player
	 * @param victimRankId
	 */
	public void giveRankPvpRewards(Player player, int victimRankId)
	{
		if (player == null)
		{
			return;
		}

		// overloads inventory, there is no repeat method for this action. Reward is given only in rank level time.

		// add items into player's inventory:
		RankReward rr = _rankRewardList.get(victimRankId);

		if (rr == null)
		{
			return;
		}

		for (Map.Entry<Integer, Reward> e : rr.getRankPvpRewardList().entrySet())
		{
			Reward reward = e.getValue();

			if (reward != null)
			{
				player.getInventory().addItem(reward.getItemId(), reward.getItemAmount(), "rank reward");
			}
		}
	}

	/**
	 * Gives the all new Rank Skills for player.
	 * @param player
	 * @param newRankId
	 */
	public void giveRankSkillRewards(Player player, int newRankId)
	{
		if (player == null)
		{
			return;
		}

		// add items into player's inventory:
		RankReward rr = _rankRewardList.get(newRankId);

		if (rr == null)
		{
			return;
		}

		for (Map.Entry<Integer, SkillReward> e : rr.getRankSkillRewardList().entrySet())
		{
			SkillReward skillReward = e.getValue();

			if (skillReward != null)
			{
				Skill skill = SkillTable.getInstance().getInfo(skillReward.getSkillId(), skillReward.getSkillLevel());
				if (skill != null)
				{
					player.addSkill(skill, true);
					// TODO check the skill list update.
				}
			}
		}
	}

	/**
	 * Give the item for the Player.
	 * @param player
	 */
	public void giveReward(Player player)
	{
		if (player == null || RPSConfig.PVP_REWARD_ID <= 0 || RPSConfig.PVP_REWARD_AMOUNT <= 0)
		{
			return;
		}

		// overloads inventory, there is no repeat method for this action. Reward is given only in rank level time.

		player.getInventory().addItem(RPSConfig.PVP_REWARD_ID, RPSConfig.PVP_REWARD_AMOUNT, "rank reward");
	}

	private void load()
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();

			// PvP + Level Rewards
			PreparedStatement statement = con.prepareStatement("SELECT * FROM rank_pvp_system_rank_reward ORDER BY id ASC");
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				Reward r = new Reward();

				r.setId(rset.getInt("id"));
				r.setItemId(rset.getInt("item_id"));
				r.setItemAmount(rset.getLong("item_amount"));
				r.setRankId(rset.getInt("rank_id"));

				if (RPSConfig.RANK_PVP_REWARD_ENABLED && rset.getBoolean("is_pvp"))
				{
					RankReward rr = _rankRewardList.get(r.getRankId());

					if (rr == null)
					{
						rr = new RankReward();
						_rankRewardList.put(r.getRankId(), rr);
						rr.addRankPvpReward(r);
					}
					else
					{
						rr.addRankPvpReward(r);
					}
				}
				if (RPSConfig.RANK_LEVEL_REWARD_ENABLED && rset.getBoolean("is_level"))
				{
					RankReward rr = _rankRewardList.get(r.getRankId());

					if (rr == null)
					{
						rr = new RankReward();
						_rankRewardList.put(r.getRankId(), rr);
						rr.addRankLevelReward(r);
					}
					else
					{
						rr.addRankLevelReward(r);
					}
				}
			}

			rset.close();
			statement.close();

			// Skill Rewards
			if (RPSConfig.RANK_SKILL_REWARD_ENABLED)
			{
				statement = con.prepareStatement("SELECT * FROM rank_pvp_system_rank_skill ORDER BY id ASC");
				rset = statement.executeQuery();

				while (rset.next())
				{
					SkillReward r = new SkillReward();
					r.setId(rset.getInt("id"));
					r.setSkillId(rset.getInt("skill_id"));
					r.setSkillLevel(rset.getInt("skill_level"));
					r.setRankId(rset.getInt("rank_id"));

					RankReward rr = _rankRewardList.get(r.getRankId());

					if (rr == null)
					{
						rr = new RankReward();
						_rankRewardList.put(r.getRankId(), rr);
						rr.addRankSkillReward(r);
					}
					else
					{
						rr.addRankSkillReward(r);
					}
				}

				rset.close();
				statement.close();
			}
		}
		catch (SQLException e)
		{
			log.info(e.getMessage());
		}
		finally
		{
			try
			{
				if (con != null)
				{
					con.close();
				}
			}
			catch (Exception e)
			{
				log.info(e.getMessage());
			}
		}
	}

	protected class RankReward
	{
		// [RewardId, Object]
		private Map<Integer, Reward> _rankLevelRewardList = new HashMap<>();
		private Map<Integer, Reward> _rankPvpRewardList = new HashMap<>();
		private Map<Integer, SkillReward> _rankSkillRewardList = new HashMap<>();

		public void addRankLevelReward(Reward reward)
		{
			_rankLevelRewardList.put(reward.getId(), reward);
		}

		public void addRankPvpReward(Reward reward)
		{
			_rankPvpRewardList.put(reward.getId(), reward);
		}

		public void addRankSkillReward(SkillReward skillReward)
		{
			_rankSkillRewardList.put(skillReward.getId(), skillReward);
		}

		public Map<Integer, Reward> getRankLevelRewardList()
		{
			return _rankLevelRewardList;
		}

		public Map<Integer, Reward> getRankPvpRewardList()
		{
			return _rankPvpRewardList;
		}

		public Map<Integer, SkillReward> getRankSkillRewardList()
		{
			return _rankSkillRewardList;
		}
	}

	protected class Reward
	{
		private int _id = 0; // reward id
		private int _itemId = 0; // game item id
		private long _itemAmount = 0; // amount of the game item
		private int _rankId = 0; // required rank id

		public int getId()
		{
			return _id;
		}

		public void setId(int id)
		{
			_id = id;
		}

		public int getItemId()
		{
			return _itemId;
		}

		public void setItemId(int itemId)
		{
			_itemId = itemId;
		}

		public long getItemAmount()
		{
			return _itemAmount;
		}

		public void setItemAmount(long itemAmount)
		{
			_itemAmount = itemAmount;
		}

		public int getRankId()
		{
			return _rankId;
		}

		public void setRankId(int rankId)
		{
			_rankId = rankId;
		}
	}

	protected class SkillReward
	{
		private int _id = 0; // reward id
		private int _skillId = 0; // skill id
		private int _skillLevel = 0; // skill level
		private int _rankId = 0; // required rank id

		public int getId()
		{
			return _id;
		}

		public void setId(int id)
		{
			_id = id;
		}

		public int getSkillId()
		{
			return _skillId;
		}

		public void setSkillId(int skillId)
		{
			_skillId = skillId;
		}

		public int getSkillLevel()
		{
			return _skillLevel;
		}

		public void setSkillLevel(int skillLevel)
		{
			_skillLevel = skillLevel;
		}

		public int getRankId()
		{
			return _rankId;
		}

		public void setRankId(int rankId)
		{
			_rankId = rankId;
		}
	}
}
