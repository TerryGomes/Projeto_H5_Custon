package l2f.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.actor.instances.player.Friend;

/**
 * @author VISTALL
 * @date 23:27/22.03.2011
 */
public class CharacterFriendDAO
{
	private static final Logger _log = LoggerFactory.getLogger(CharacterFriendDAO.class);

	private static final CharacterFriendDAO _instance = new CharacterFriendDAO();

	public static CharacterFriendDAO getInstance()
	{
		return _instance;
	}

	public static Map<Integer, Friend> select(Player owner, Connection con)
	{
		Map<Integer, Friend> map = new HashMap<>();

		try (PreparedStatement statement = con.prepareStatement("SELECT f.friend_id, c.char_name, s.class_id, s.level FROM character_friends f LEFT JOIN characters c ON f.friend_id = c.obj_Id LEFT JOIN character_subclasses s ON ( f.friend_id = s.char_obj_id AND s.active =1 ) WHERE f.char_id = ?"))
		{
			statement.setInt(1, owner.getObjectId());

			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					int objectId = rset.getInt("f.friend_id");
					String name = rset.getString("c.char_name");
					int classId = rset.getInt("s.class_id");
					int level = rset.getInt("s.level");

					map.put(objectId, new Friend(objectId, name, level, classId));
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("CharacterFriendDAO.load(L2Player): " + e, e);
		}

		return map;
	}

	public void insert(Player owner, Player friend)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("INSERT INTO character_friends (char_id,friend_id) VALUES(?,?)"))
		{
			statement.setInt(1, owner.getObjectId());
			statement.setInt(2, friend.getObjectId());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.warn(owner.getFriendList() + " could not add friend objectid: " + friend.getObjectId(), e);
		}
	}

	public void delete(Player owner, int friend)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("DELETE FROM character_friends WHERE (char_id=? AND friend_id=?) OR (char_id=? AND friend_id=?)"))
		{
			statement.setInt(1, owner.getObjectId());
			statement.setInt(2, friend);
			statement.setInt(3, friend);
			statement.setInt(4, owner.getObjectId());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.warn("FriendList: could not delete friend objectId: " + friend + " ownerId: " + owner.getObjectId(), e);
		}
	}

}
