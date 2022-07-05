package l2f.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.model.Player;

public class CharacterPostFriendDAO
{
	private static final Logger _log = LoggerFactory.getLogger(CharacterPostFriendDAO.class);
	private static final CharacterPostFriendDAO _instance = new CharacterPostFriendDAO();

	private static final String SELECT_SQL_QUERY = "SELECT pf.post_friend, c.char_name FROM character_post_friends pf LEFT JOIN characters c ON pf.post_friend = c.obj_Id WHERE pf.object_id = ?";
	private static final String INSERT_SQL_QUERY = "INSERT INTO character_post_friends(object_id, post_friend) VALUES (?,?)";
	private static final String DELETE_SQL_QUERY = "DELETE FROM character_post_friends WHERE object_id=? AND post_friend=?";

	public static CharacterPostFriendDAO getInstance()
	{
		return _instance;
	}

	public static IntObjectMap<String> select(Player player, Connection con)
	{
		IntObjectMap<String> set = new CHashIntObjectMap<>();
		try (PreparedStatement statement = con.prepareStatement(SELECT_SQL_QUERY))
		{
			statement.setInt(1, player.getObjectId());
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					if ((rset.getInt(1) <= 0) || (rset.getString(2) == null))
					{
						continue;
					}
					set.put(rset.getInt(1), rset.getString(2));
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("CharacterPostFriendDAO.load(Player): " + e, e);
		}
		return set;
	}

	public void insert(Player player, int val)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(INSERT_SQL_QUERY))
		{
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, val);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("CharacterPostFriendDAO.insert(Player, int): ", e);
		}
	}

	public static void delete(Player player, int val)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(DELETE_SQL_QUERY))
		{
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, val);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("CharacterPostFriendDAO.delete(Player, int): " + e, e);
		}
	}
}
