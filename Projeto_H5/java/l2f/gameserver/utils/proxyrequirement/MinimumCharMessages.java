package l2f.gameserver.utils.proxyrequirement;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.utils.ProxyRequirement;

public class MinimumCharMessages implements ProxyRequirement
{
	private static final Logger LOG = LoggerFactory.getLogger(MinimumCharMessages.class);

	private final Map<ChatType, Integer> requirements = new HashMap<ChatType, Integer>(8);

	public MinimumCharMessages(int all, int shout, int pm, int trade, int party, int clan, int ally, int hero)
	{
		requirements.put(ChatType.ALL, all);
		requirements.put(ChatType.SHOUT, shout);
		requirements.put(ChatType.TELL, pm);
		requirements.put(ChatType.TRADE, trade);
		requirements.put(ChatType.PARTY, party);
		requirements.put(ChatType.CLAN, clan);
		requirements.put(ChatType.ALLIANCE, ally);
		requirements.put(ChatType.HERO_VOICE, hero);
	}

	private static int getBiggestLevel(Connection con, String accountName, ChatType chatType)
	{
		final String variableName = "wroteMessages_" + chatType;
		try (PreparedStatement statement = con.prepareStatement(
					"SELECT value FROM character_variables INNER JOIN characters ON character_variables.obj_id=characters.obj_Id WHERE characters.account_name = ? AND character_variables.name = ? ORDER BY character_variables.name DESC LIMIT 1"))
		{
			statement.setString(1, accountName);
			statement.setString(2, variableName);
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					return Integer.parseInt(rset.getString("value"));
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while getting highest number of Chats with Type: " + chatType + " for Account Name: " + accountName, e);
		}
		return -1;
	}

	@Override
	public boolean matches(String accountName, InetAddress ip)
	{
		boolean atLeastOnePassed = false;
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			for (Map.Entry<ChatType, Integer> typeWithMinimum : requirements.entrySet())
			{
				if (typeWithMinimum.getValue() > 0)
				{
					if (getBiggestLevel(con, accountName, typeWithMinimum.getKey()) < typeWithMinimum.getValue())
					{
						return false;
					}
					atLeastOnePassed = true;
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while connecting to Database from " + MinimumCharMessages.class.getSimpleName(), e);
		}
		return atLeastOnePassed;
	}
}
