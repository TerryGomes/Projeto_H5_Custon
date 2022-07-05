package l2f.loginserver.accounts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.dbutils.DbUtils;
import l2f.commons.net.utils.Net;
import l2f.commons.net.utils.NetList;
import l2f.loginserver.database.L2DatabaseFactory;

public class Account
{
	private final static Logger _log = LoggerFactory.getLogger(Account.class);

	private final String login;

	private String passwordHash;
	private String allowedIP;
	private String allowedHwid;
	private final NetList allowedIpList = new NetList();
	private int accessLevel;

	private int banExpire;

	private double bonus;
	private int bonusExpire;

	private String lastIP;
	private int lastAccess;
	private int lastServer;

	private final IntObjectMap<Pair<Integer, int[]>> _serversInfo = new HashIntObjectMap<Pair<Integer, int[]>>(2);

	public Account(String login)
	{
		this.login = login;
	}

	public String getLogin()
	{
		return login;
	}

	public String getPasswordHash()
	{
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash)
	{
		this.passwordHash = passwordHash;
	}

	public String getAllowedIP()
	{
		return allowedIP;
	}

	public String getAllowedHwid()
	{
		return allowedHwid;
	}

	public void setAllowedHwid(String allowedHwid)
	{
		this.allowedHwid = allowedHwid;
	}

	public boolean isAllowedIP(String ip)
	{
		return allowedIpList.isEmpty() || allowedIpList.isInRange(ip);
	}

	public void setAllowedIP(String allowedIP)
	{
		if (allowedIP == null)
		{
			return;
		}
		allowedIpList.clear();
		this.allowedIP = allowedIP;

		if (this.allowedIP.isEmpty())
		{
			return;
		}

		String[] masks = this.allowedIP.split("[\\s,;]+");
		for (String mask : masks)
		{
			allowedIpList.add(Net.valueOf(mask));
		}
	}

	public int getAccessLevel()
	{
		return accessLevel;
	}

	public void setAccessLevel(int accessLevel)
	{
		this.accessLevel = accessLevel;
	}

	public double getBonus()
	{
		return bonus;
	}

	public void setBonus(double bonus)
	{
		this.bonus = bonus;
	}

	public int getBonusExpire()
	{
		return bonusExpire;
	}

	public void setBonusExpire(int bonusExpire)
	{
		this.bonusExpire = bonusExpire;
	}

	public int getBanExpire()
	{
		return banExpire;
	}

	public void setBanExpire(int banExpire)
	{
		this.banExpire = banExpire;
	}

	public void setLastIP(String lastIP)
	{
		this.lastIP = lastIP;
	}

	public String getLastIP()
	{
		return lastIP;
	}

	public int getLastAccess()
	{
		return lastAccess;
	}

	public void setLastAccess(int lastAccess)
	{
		this.lastAccess = lastAccess;
	}

	public int getLastServer()
	{
		return lastServer;
	}

	public void setLastServer(int lastServer)
	{
		this.lastServer = lastServer;
	}

	public void addAccountInfo(int serverId, int size, int[] deleteChars)
	{
		_serversInfo.put(serverId, new ImmutablePair<Integer, int[]>(size, deleteChars));
	}

	public Pair<Integer, int[]> getAccountInfo(int serverId)
	{
		return _serversInfo.get(serverId);
	}

	@Override
	public String toString()
	{
		return login;
	}

	public void restore()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT password, access_level, ban_expire, allow_ip, allow_hwid, bonus, bonus_expire, last_server, last_ip, last_access FROM accounts WHERE login = ?");
			statement.setString(1, login);
			rset = statement.executeQuery();

			if (rset.next())
			{
				setPasswordHash(rset.getString("password"));
				setAccessLevel(rset.getInt("access_level"));
				setBanExpire(rset.getInt("ban_expire"));
				setAllowedIP(rset.getString("allow_ip"));
				setAllowedHwid(rset.getString("allow_hwid"));
				setBonus(rset.getDouble("bonus"));
				setBonusExpire(rset.getInt("bonus_expire"));
				setLastServer(rset.getInt("last_server"));
				setLastIP(rset.getString("last_ip"));
				setLastAccess(rset.getInt("last_access"));
			}
		}
		catch (Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public void save()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			save(con);
		}
		catch (SQLException e)
		{
			Account._log.error("Error while saving Account " + login, e);
		}
	}

	public void save(Connection con) throws SQLException
	{
		try (PreparedStatement statement = con.prepareStatement("INSERT INTO accounts (login, password) VALUES(?,?)"))
		{
			statement.setString(1, login);
			statement.setString(2, passwordHash);
			statement.execute();
		}
	}

	public void update()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			update(con);
		}
		catch (SQLException e)
		{
			_log.error("Error while updating Account " + login, e);
		}
	}

	public void update(Connection con) throws SQLException
	{
		try (PreparedStatement statement = con.prepareStatement("UPDATE accounts SET password = ?, access_level = ?, ban_expire = ?, allow_ip = ?, allow_hwid=?, bonus = ?, bonus_expire = ?, last_server = ?, last_ip = ?, last_access = ? WHERE login = ?"))
		{
			statement.setString(1, getPasswordHash());
			statement.setInt(2, getAccessLevel());
			statement.setInt(3, getBanExpire());
			statement.setString(4, getAllowedIP());
			statement.setString(5, getAllowedHwid());
			statement.setDouble(6, getBonus());
			statement.setInt(7, getBonusExpire());
			statement.setInt(8, getLastServer());
			statement.setString(9, getLastIP());
			statement.setInt(10, getLastAccess());
			statement.setString(11, getLogin());
			statement.execute();
		}
	}
}
