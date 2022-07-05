package l2f.gameserver.model.entity.forum;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.lang.ArrayUtils;
import l2f.gameserver.ConfigHolder;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.utils.BatchStatement;

public class ForumMembersHolder
{
	private static final Logger LOG = LoggerFactory.getLogger(ForumMembersHolder.class);

	private final Map<Integer, ForumMember> forumMembers = new ConcurrentHashMap<Integer, ForumMember>();
	private final List<ForumMember> membersToSave = new CopyOnWriteArrayList<ForumMember>();
	private int lastLoadedMemberId = -1;
	private int biggestMemberId = 1;
	private final ForumMember deletedMember = new ForumMember(-1, "Account Deleted", "", ForumMemberGroup.DELETED, "", 0);

	public ForumMember getDeletedMember()
	{
		return deletedMember;
	}

	public ForumMember getMemberById(int memberId)
	{
		final ForumMember member = forumMembers.get(memberId);
		if (member != null)
		{
			return member;
		}
		return deletedMember;
	}

	public ForumMember getMemberByName(String memberName)
	{
		return getMemberByName(memberName, deletedMember);
	}

	public ForumMember getMemberByName(String memberName, ForumMember defaultValue)
	{
		for (ForumMember member : forumMembers.values())
		{
			if (member.getMemberName().equalsIgnoreCase(memberName))
			{
				return member;
			}
		}
		return defaultValue;
	}

	public boolean containsName(String memberName)
	{
		for (ForumMember member : forumMembers.values())
		{
			if (member.getMemberName().equalsIgnoreCase(memberName))
			{
				return true;
			}
		}
		return false;
	}

	public int getMembersCount(ForumMemberGroup... groups)
	{
		int count = 0;
		for (ForumMember member : forumMembers.values())
		{
			if (ArrayUtils.contains(groups, member.getMemberGroup()))
			{
				++count;
			}
		}
		return count;
	}

	public void synchronizeMembers(Connection con)
	{
		final Collection<Integer> allMembersIds = new HashSet<Integer>();
		try (PreparedStatement statement = con.prepareStatement("SELECT id_member, real_name, passwd, email_address, id_group, posts, warning FROM smf_members WHERE posts > 0");
					ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				final int memberId = rset.getInt("id_member");
				final int warningLevel = rset.getInt("warning");
				final int posts = rset.getInt("posts");
				if (!forumMembers.containsKey(memberId))
				{
					final String memberName = rset.getString("real_name");
					final String passwordHash = rset.getString("passwd");
					final String email = rset.getString("email_address");
					final int idGroup = rset.getInt("id_group");
					final ForumMember member = new ForumMember(memberId, memberName, passwordHash, ForumMemberGroup.findGroup(idGroup), email, warningLevel);
					forumMembers.put(memberId, member);
					connectPlayersWithMember(memberName, member);
				}
				else
				{
					final ForumMember member2 = forumMembers.get(memberId);
					if (member2.getWarningLevel() < warningLevel)
					{
						member2.setWarningLevel(warningLevel);
						onMemberWarned(member2);
					}
					else if (member2.getPostCount() < posts)
					{
						member2.setPostCount(posts);
					}
				}
				if (biggestMemberId < memberId)
				{
					biggestMemberId = memberId;
				}
				if (lastLoadedMemberId < memberId)
				{
					lastLoadedMemberId = memberId;
				}
				allMembersIds.add(memberId);
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while loading Members from Forum Database!", e);
			return;
		}

		if (!allMembersIds.isEmpty())
		{
			for (ForumMember member3 : forumMembers.values())
			{
				if (!allMembersIds.contains(member3.getMemberId()) && !membersToSave.contains(member3))
				{
					forumMembers.remove(member3.getMemberId());
					for (Player player : GameObjectsStorage.getAllPlayersCopy())
					{
						if (player.getForumMember() != null && player.getForumMember().getMemberId() == member3.getMemberId())
						{
							player.setForumMember(null);
							if (allMembersIds.isEmpty())
							{
								continue;
							}
							player.setForumLogin("");
						}
					}
				}
			}
		}

		try (PreparedStatement statement = BatchStatement.createPreparedStatement(con,
					"REPLACE INTO smf_members (id_member, member_name, date_registered, id_group, last_login, real_name, buddy_list, message_labels, openid_uri, passwd, email_address, signature, member_ip, member_ip2, id_post_group, password_salt, ignore_boards) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"))
		{
			final long time = System.currentTimeMillis() / 1000L;
			for (ForumMember member4 : membersToSave)
			{
				membersToSave.remove(member4);
				statement.setInt(1, member4.getMemberId());
				statement.setString(2, member4.getMemberName());
				statement.setLong(3, time);
				statement.setInt(4, member4.getMemberGroup().getGroupId());
				statement.setLong(5, time);
				statement.setString(6, member4.getMemberName());
				statement.setString(7, "");
				statement.setString(8, "");
				statement.setString(9, "");
				statement.setString(10, member4.getPasswordHash());
				statement.setString(11, member4.getEmailAddress());
				statement.setString(12, "");
				statement.setString(13, member4.getLastIp());
				statement.setString(14, member4.getLastIp());
				statement.setInt(15, 4);
				statement.setString(16, "e938");
				statement.setString(17, "");
				statement.addBatch();
			}
			statement.executeBatch();
			con.commit();
		}
		catch (SQLException e)
		{
			LOG.error("Error while Saving New Member to Forum Database!", e);
		}

		try (PreparedStatement statement = BatchStatement.createPreparedStatement(con, "UPDATE smf_members SET posts = posts + ? WHERE id_member = ?"))
		{
			for (ForumMember member5 : forumMembers.values())
			{
				if (member5.getPostCountToIncInDatabase() > 0)
				{
					statement.setInt(1, member5.getPostCountToIncInDatabase());
					statement.setInt(2, member5.getMemberId());
					statement.addBatch();
					member5.setPostsToIncInDatabase(0);
				}
			}
			statement.executeBatch();
			con.commit();
		}
		catch (SQLException e)
		{
			LOG.error("Error while Updating Posts Count in Forum Database!", e);
		}
	}

	public static void synchronizeOnlineStatus(Connection con)
	{
		final int currentOnline = (int) (System.currentTimeMillis() / 1000L) - ConfigHolder.getInt("ForumOnlineDateCut");
		try (PreparedStatement statement = BatchStatement.createPreparedStatement(con, "UPDATE smf_members SET last_login=? WHERE id_member=?"))
		{
			for (Player player : GameObjectsStorage.getAllPlayersForIterate())
			{
				final ForumMember member = player.getForumMember();
				if (member != null)
				{
					statement.setInt(1, currentOnline);
					statement.setInt(2, member.getMemberId());
					statement.addBatch();
				}
			}
			statement.executeBatch();
			con.commit();
		}
		catch (SQLException e)
		{
			LOG.error("Error while Saving smf_members online status!", e);
		}

		final List<Integer> idMembersViewingRealForum = new ArrayList<Integer>();
		try (PreparedStatement statement2 = con.prepareStatement("SELECT id_member FROM smf_log_online WHERE session != id_member AND id_member > 0"); ResultSet rset = statement2.executeQuery())
		{
			while (rset.next())
			{
				idMembersViewingRealForum.add(rset.getInt("id_member"));
			}
		}
		catch (SQLException e2)
		{
			LOG.error("Error while getting smf_log_online!", e2);
		}

		final List<Integer> possibleDuplicateIds = new ArrayList<Integer>(idMembersViewingRealForum.size());
		try (PreparedStatement statement3 = BatchStatement.createPreparedStatement(con, "REPLACE INTO smf_log_online(session, log_time, id_member, id_spider, ip, url) VALUES(?,?,?,?,?,?)"))
		{
			for (Player player2 : GameObjectsStorage.getAllPlayersForIterate())
			{
				final ForumMember member2 = player2.getForumMember();
				if (member2 != null)
				{
					if (idMembersViewingRealForum.contains(member2.getMemberId()))
					{
						possibleDuplicateIds.add(member2.getMemberId());
					}
					else
					{
						statement3.setString(1, String.valueOf(member2.getMemberId()));
						statement3.setInt(2, currentOnline);
						statement3.setInt(3, member2.getMemberId());
						statement3.setInt(4, 0);
						statement3.setInt(5, 0);
						statement3.setString(6, "");
						statement3.addBatch();
					}
				}
			}
			statement3.executeBatch();
			con.commit();
		}
		catch (SQLException e3)
		{
			LOG.error("Error while Saving smf_log_online!", e3);
		}

		try (PreparedStatement statement3 = BatchStatement.createPreparedStatement(con, "DELETE FROM smf_log_online WHERE session = ? AND id_member = ?"))
		{
			for (Integer memberId : possibleDuplicateIds)
			{
				statement3.setString(1, String.valueOf(memberId));
				statement3.setInt(2, memberId);
				statement3.addBatch();
			}
			statement3.executeBatch();
			con.commit();
		}
		catch (SQLException e3)
		{
			LOG.error("Error while Deleting Duplicated smf_log_online!", e3);
		}
	}

	private static void connectPlayersWithMember(String memberName, ForumMember member)
	{
		for (Player player : GameObjectsStorage.getAllPlayersCopy())
		{
			if (player.getForumLogin() != null && player.getForumLogin().equalsIgnoreCase(memberName))
			{
				player.setForumMember(member);
			}
		}
	}

	private static void onMemberWarned(ForumMember member)
	{
		for (Player player : GameObjectsStorage.getAllPlayersCopy())
		{
			if (player.getForumMember() != null && player.getForumMember().getMemberId() == member.getMemberId())
			{
				player.sendMessage("Forum Account has been warned! More info on www.lineage2tales.com/forum");
				if (member.getWarningLevel() < 100)
				{
					continue;
				}

				player.setForumLogin("");
				player.setForumMember(null);
				player.sendMessage("Forum Account is no longer attached to the Game!");
			}
		}
	}

	public boolean checkCorrectPassword(ForumMember forumMember, String password)
	{
		final String passwordHash = getPasswordHash(forumMember.getMemberName().toLowerCase(), password);
		return passwordHash.equals(forumMember.getPasswordHash());
	}

	private int getNewMemberId()
	{
		if (lastLoadedMemberId == biggestMemberId)
		{
			biggestMemberId += 3;
		}
		else
		{
			++biggestMemberId;
		}
		return biggestMemberId;
	}

	private static String getPasswordHash(String charName, String realPassword)
	{
		MessageDigest mDigest;
		try
		{
			mDigest = MessageDigest.getInstance("SHA1");
		}
		catch (NoSuchAlgorithmException e)
		{
			LOG.error("Couldn't find Algorithm while creating Password Hash!", e);
			return "";
		}

		final String passwordToHash = charName + realPassword;
		final byte[] result = mDigest.digest(passwordToHash.getBytes());
		final StringBuilder sb = new StringBuilder();
		for (byte aResult : result)
		{
			sb.append(Integer.toString((aResult & 0xFF) + 256, 16).substring(1));
		}
		return sb.toString();
	}

	public static ForumMembersHolder getInstance()
	{
		return SingletonHolder.instance;
	}

	private static class SingletonHolder
	{
		private static final ForumMembersHolder instance = new ForumMembersHolder();
	}
}
