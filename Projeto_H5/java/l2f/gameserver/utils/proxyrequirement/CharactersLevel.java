package l2f.gameserver.utils.proxyrequirement;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.utils.ProxyRequirement;

public class CharactersLevel implements ProxyRequirement
{
	private static final Logger LOG = LoggerFactory.getLogger(CharactersLevel.class);

	private final int minLevel;
	private final int maxLevel;

	public CharactersLevel(int minLevel, int maxLevel)
	{
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
	}

	private static int getBiggestLevel(String accountName)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT level FROM character_subclasses INNER JOIN characters ON character_subclasses.char_obj_id=characters.obj_Id WHERE characters.account_name = ? ORDER BY level DESC LIMIT 1"))
		{
			statement.setString(1, accountName);
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					return rset.getInt("level");
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while getting biggest level for Account Name: " + accountName, e);
		}
		return -1;
	}

	@Override
	public boolean matches(String accountName, InetAddress ip)
	{
		final int biggestLevel = getBiggestLevel(accountName);
		return biggestLevel >= minLevel && biggestLevel <= maxLevel;
	}
}
