package l2f.gameserver.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import l2f.gameserver.database.DatabaseFactory;

public class FakePlayersEngine
{
	private static List<String> chosenAccountNames = new ArrayList<>();

	public static class FakePlayer
	{
		private final String accountName;
		private final int objectId;
		private final int clanId;

		private FakePlayer(String accountName, int objectId, int clanId)
		{
			this.accountName = accountName;
			this.objectId = objectId;
			this.clanId = clanId;
		}

		public String getAccountName()
		{
			return accountName;
		}

		public int getObjectId()
		{
			return objectId;
		}

		public int getClanId()
		{
			return clanId;
		}
	}

	public static FakePlayer getNewFakePlayer()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT account_name, obj_Id, clanid FROM characters WHERE pvpkills > 0 AND obj_Id != 268480334 ORDER BY pvpkills DESC"); ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				String accountName = rset.getString("account_name");
				if (!chosenAccountNames.contains(accountName))
				{
					chosenAccountNames.add(accountName);
					int objId = rset.getInt("obj_Id");
					int clanId = rset.getInt("clanid");

					return new FakePlayer(accountName, objId, clanId);
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
