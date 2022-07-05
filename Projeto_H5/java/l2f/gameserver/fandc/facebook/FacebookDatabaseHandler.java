package l2f.gameserver.fandc.facebook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.trove.map.hash.TObjectIntHashMap;
import l2f.gameserver.database.DatabaseFactory;

public final class FacebookDatabaseHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(FacebookDatabaseHandler.class);

	public static void replaceCompletedTask(CompletedTask task)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("REPLACE INTO facebook_completed_tasks VALUES (?,?,?,?,?,?,?,?,?,?,?)"))
		{
			statement.setInt(1, task.getPlayerId());
			statement.setLong(2, task.getTakenDate());
			statement.setInt(3, task.getCommentApprovalType().ordinal());
			statement.setInt(4, task.isRewarded() ? 1 : 0);
			statement.setString(5, task.getId() == null ? "" : task.getId());
			statement.setString(6, task.getActionType().toString());
			statement.setString(7, task.getExecutor().getId());
			statement.setLong(8, task.getCreatedDate());
			statement.setLong(9, task.getExtractionDate());
			statement.setString(10, task.getMessage() == null ? "" : task.getMessage());
			statement.setString(11, task.getFather() == null ? "" : task.getFather().getId());
			statement.executeUpdate();
		}
		catch (SQLException e)
		{
			LOG.error("Error while replaceCompletedTask(" + task + ")", e);
		}
	}

	public static void deleteCompletedTask(CompletedTask task)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection();
					PreparedStatement statement = con.prepareStatement("DELETE FROM facebook_completed_tasks WHERE player_id = ? AND action_id = ? AND action_type_name = ? AND father_id = ?"))
		{
			statement.setInt(1, task.getPlayerId());
			statement.setString(2, task.getId() == null ? "" : task.getId());
			statement.setString(3, task.getActionType().toString());
			statement.setString(4, task.getFather() == null ? "" : task.getFather().getId());
			statement.executeUpdate();
		}
		catch (SQLException e)
		{
			LOG.error("Error while deleteCompletedTask(" + task + ")", e);
		}
	}

	public static ArrayList<CompletedTask> loadCompletedTasks()
	{
		final ArrayList<CompletedTask> tasks = new ArrayList<CompletedTask>();
		try (Connection con = DatabaseFactory.getInstance().getConnection();
					PreparedStatement statement = con.prepareStatement("SELECT * FROM facebook_completed_tasks");
					ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				final FacebookProfile executor = FacebookProfilesHolder.getInstance().getProfileById(rset.getString("executor_id"));
				if (executor != null)
				{
					final FacebookActionType actionType = FacebookActionType.valueOf(rset.getString("action_type_name"));
					final FacebookAction action = actionType.createInstance(rset);
					final int playerId = rset.getInt("player_id");
					final long takenDate = rset.getLong("taken_date");
					final CompletedTask.CommentApprovalType approvalType = CompletedTask.CommentApprovalType.values()[rset.getInt("comment_approved")];
					final boolean isRewarded = rset.getInt("rewarded") == 1;
					tasks.add(new CompletedTask(playerId, takenDate, action, approvalType, isRewarded));
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while loading Completed Tasks", e);
		}
		return tasks;
	}

	public static void replaceFacebookProfile(FacebookProfile profile)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("REPLACE INTO facebook_profiles VALUES(?,?,?,?,?,?,?,?,?,?,?)"))
		{
			statement.setString(1, profile.getId());
			statement.setString(2, profile.getName());
			statement.setLong(3, profile.getLastCompletedTaskDate());
			statement.setInt(4, profile.getPositivePoints(FacebookActionType.LIKE));
			statement.setInt(5, profile.getPositivePoints(FacebookActionType.COMMENT));
			statement.setInt(6, profile.getPositivePoints(FacebookActionType.POST));
			statement.setInt(7, profile.getPositivePoints(FacebookActionType.SHARE));
			statement.setInt(8, profile.getNegativePoints(FacebookActionType.LIKE));
			statement.setInt(9, profile.getNegativePoints(FacebookActionType.COMMENT));
			statement.setInt(10, profile.getNegativePoints(FacebookActionType.POST));
			statement.setInt(11, profile.getNegativePoints(FacebookActionType.SHARE));
			statement.executeUpdate();
		}
		catch (SQLException e)
		{
			LOG.error("Error while replaceFacebookProfile(" + profile + ")", e);
		}
	}

	public static ArrayList<FacebookProfile> loadFacebookProfiles()
	{
		final ArrayList<FacebookProfile> profiles = new ArrayList<FacebookProfile>();
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT * FROM facebook_profiles"); ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				final String id = rset.getString("id");
				final String name = rset.getString("name");
				final long lastCompletedTaskDate = rset.getLong("last_completed_task_date");
				final TObjectIntHashMap<FacebookActionType> positivePoints = new TObjectIntHashMap<>(0);
				if (rset.getInt("positive_points_like") > 0)
				{
					positivePoints.put(FacebookActionType.LIKE, rset.getInt("positive_points_like"));
				}
				if (rset.getInt("positive_points_comment") > 0)
				{
					positivePoints.put(FacebookActionType.COMMENT, rset.getInt("positive_points_comment"));
				}
				if (rset.getInt("positive_points_post") > 0)
				{
					positivePoints.put(FacebookActionType.POST, rset.getInt("positive_points_post"));
				}
				if (rset.getInt("positive_points_share") > 0)
				{
					positivePoints.put(FacebookActionType.SHARE, rset.getInt("positive_points_share"));
				}
				final TObjectIntHashMap<FacebookActionType> negativePoints = new TObjectIntHashMap<>(0);
				if (rset.getInt("negative_points_like") > 0)
				{
					negativePoints.put(FacebookActionType.LIKE, rset.getInt("negative_points_like"));
				}
				if (rset.getInt("negative_points_comment") > 0)
				{
					negativePoints.put(FacebookActionType.COMMENT, rset.getInt("negative_points_comment"));
				}
				if (rset.getInt("negative_points_post") > 0)
				{
					negativePoints.put(FacebookActionType.POST, rset.getInt("negative_points_post"));
				}
				if (rset.getInt("negative_points_share") > 0)
				{
					negativePoints.put(FacebookActionType.SHARE, rset.getInt("negative_points_share"));
				}
				profiles.add(new FacebookProfile(id, name, lastCompletedTaskDate, positivePoints, negativePoints));
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while loading Facebook Profiles", e);
		}
		return profiles;
	}

	public static void replaceOfficialPost(OfficialPost post)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("REPLACE INTO facebook_official_posts VALUES(?,?,?,?,?)"))
		{
			statement.setString(1, post.getId());
			statement.setInt(2, post.isActionTypeRewarded(FacebookActionType.LIKE) ? 1 : 0);
			statement.setInt(3, post.isActionTypeRewarded(FacebookActionType.COMMENT) ? 1 : 0);
			statement.setInt(4, post.isActionTypeRewarded(FacebookActionType.POST) ? 1 : 0);
			statement.setInt(5, post.isActionTypeRewarded(FacebookActionType.SHARE) ? 1 : 0);
			statement.executeUpdate();
		}
		catch (SQLException e)
		{
			LOG.error("Error while replaceOfficialPost(" + post + ")", e);
		}
	}

	public static void loadOfficialPostData(OfficialPost post)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT * FROM facebook_official_posts WHERE post_id = ?"))
		{
			statement.setString(1, post.getId());
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					final EnumSet<FacebookActionType> rewardedActions = EnumSet.noneOf(FacebookActionType.class);
					if (rset.getInt("rewards_like") == 1)
					{
						rewardedActions.add(FacebookActionType.LIKE);
					}
					if (rset.getInt("rewards_comment") == 1)
					{
						rewardedActions.add(FacebookActionType.COMMENT);
					}
					if (rset.getInt("rewards_post") == 1)
					{
						rewardedActions.add(FacebookActionType.POST);
					}
					if (rset.getInt("rewards_share") == 1)
					{
						rewardedActions.add(FacebookActionType.SHARE);
					}
					post.setRewardedActions(rewardedActions);
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while loadOfficialPostData(" + post + ")", e);
		}
	}
}
