package l2f.loginserver.merge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.loginserver.Config;
import l2f.loginserver.accounts.Account;
import l2f.loginserver.database.L2DatabaseFactory;

public class AccountMerge
{
	private static final Logger LOG = LoggerFactory.getLogger(AccountMerge.class);

	private final Set<String> mergedAccounts = new HashSet<String>();
	private final Object lock = new Object();

	public Account tryMergeAccount(String newLogin)
	{
		synchronized (lock)
		{
			if (mergedAccounts.contains(newLogin))
			{
				return null;
			}
			mergedAccounts.add(newLogin);
		}

		final String oldLogin = getAccountToMerge(newLogin);
		if (oldLogin.isEmpty())
		{
			return null;
		}

		Account account = null;
		final long currentTime = System.currentTimeMillis();
		try (Connection oldDatabaseCon = MergeDatabaseFactory.getInstance().getConnection(); Connection newDatabaseCon = L2DatabaseFactory.getInstance().getConnection())
		{
			try (PreparedStatement statement = oldDatabaseCon.prepareStatement("SELECT * FROM accounts WHERE login=? AND access_level >= 0"))
			{
				statement.setString(1, oldLogin);
				try (ResultSet rset = statement.executeQuery())
				{
					if (rset.next())
					{
						account = new Account(newLogin);
						account.setPasswordHash(rset.getString("password"));
						account.setAccessLevel(rset.getInt("access_level"));
						account.setBanExpire(rset.getInt("ban_expire"));
						account.setAllowedIP(rset.getString("allow_ip"));
						account.setAllowedHwid(rset.getString("allow_hwid"));
						account.setBonus(rset.getDouble("bonus"));
						account.setBonusExpire(rset.getInt("bonus_expire"));
						account.setLastServer(rset.getInt("last_server"));
						account.setLastIP(rset.getString("last_ip"));
						account.setLastAccess(rset.getInt("last_access"));
					}
				}
			}

			if (account == null)
			{
				return null;
			}

			LOG.info("Merging Account " + oldLogin + " into " + newLogin);
			account.save(newDatabaseCon);
			account.update(newDatabaseCon);
			int premiumType = Config.MERGE_NEW_PREMIUM_BONUS_TYPE;
			long premiumToDate = -1L;

			if (Config.MERGE_PREMIUM_TABLE)
			{
				try (PreparedStatement statement2 = oldDatabaseCon.prepareStatement("SELECT * FROM account_bonus WHERE account=?"))
				{
					statement2.setString(1, oldLogin);
					try (ResultSet rset2 = statement2.executeQuery())
					{
						if (rset2.next() && rset2.getLong("bonus_expire") >= TimeUnit.MILLISECONDS.toSeconds(currentTime))
						{
							premiumType = rset2.getInt("bonus");
							premiumToDate = rset2.getLong("bonus_expire");
						}
					}
				}
			}

			if (premiumType > 0)
			{
				if (premiumToDate <= 0L)
				{
					premiumToDate = TimeUnit.MILLISECONDS.toSeconds(currentTime);
				}
				premiumToDate += TimeUnit.DAYS.toSeconds(Config.MERGE_PREMIUM_INCREASE);

				try (PreparedStatement statement2 = newDatabaseCon.prepareCall("INSERT INTO account_bonus VALUES(?,?,?)"))
				{
					statement2.setString(1, newLogin);
					statement2.setInt(2, premiumType);
					statement2.setLong(3, premiumToDate);
					statement2.executeUpdate();
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while checking if Merge Account(" + oldLogin + ") exists!", e);
		}
		return account;
	}

	private static String getAccountToMerge(String currentAccount)
	{
		try (Connection con = MergeDatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT old_login FROM merge_data WHERE new_login = ? AND finished = 0"))
		{
			statement.setString(1, currentAccount);
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					return rset.getString("old_login");
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while getting Account To Merge by Account Name: " + currentAccount, e);
		}
		return "";
	}

	public static AccountMerge getInstance()
	{
		return SingletonHolder.instance;
	}

	private static class SingletonHolder
	{
		private static final AccountMerge instance = new AccountMerge();
	}
}
