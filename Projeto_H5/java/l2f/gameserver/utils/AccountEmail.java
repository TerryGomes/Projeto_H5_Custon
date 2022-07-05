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
package l2f.gameserver.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import l2f.commons.dbutils.DbUtils;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.clientpackets.EnterWorld;
import l2f.gameserver.network.serverpackets.TutorialCloseHtml;
import l2f.gameserver.network.serverpackets.TutorialShowHtml;

/**
 * @author Nik
 *
 */
public class AccountEmail
{
	private static Logger _log = Logger.getLogger(AccountEmail.class.getName());

	public static void onBypass(Player player, String fullbypass)
	{
		if (fullbypass.startsWith("setemail"))
		{
			// setemail email1 email2
			String[] split = fullbypass.split(" ");
			if (split.length < 3)
			{
				player.sendMessage("Please fill all the fields before proceeding.");
				return;
			}

			// split[1] = command
			String email1 = split[1];
			String email2 = split[2];

			setEmail(player, email1, email2);
		}
		else if (fullbypass.startsWith("verifyemail"))
		{
			String[] split = fullbypass.split(" ");
			verifyEmail(player, split.length <= 1 ? null : split[1]);
		}
	}

	public static void checkEmail(Player player)
	{
		if (getEmail(player) == null) // Player has no e-mail set.
		{
			String html = HtmCache.getInstance().getNotNull("custom/AccountEmail.htm", player);
			player.sendPacket(new TutorialShowHtml(html));
		}
	}

	public static void verifyEmail(Player player, String email)
	{
		if (email == null)
		{
			String html = HtmCache.getInstance().getNotNull("custom/VerifyEmail.htm", player);
			player.sendPacket(new TutorialShowHtml(html));
			if (!player.isBlocked())
			{
				player.block();
			}
		}
		else if (email.equalsIgnoreCase(getEmail(player)))
		{
			if (player.isBlocked())
			{
				player.unblock();
			}
			player.sendMessage("You have confirmed to be the owner of this account. You are free to go.");
			player.setVar("LastIP", player.getIP()); // Taken from EnterWorld and commented it there.
			player.sendPacket(TutorialCloseHtml.STATIC); // Close the tutorial window since you have confirmed to be the owner.
			EnterWorld.loadTutorial(player); // Secondary password, class change and other stuff here...
		}
		else
		{
			player.sendMessage("This is an incorrect e-mail address. You will be kicked.");
			player.getNetConnection().closeLater();
		}
	}

	public static void setEmail(Player player, String email)
	{
		if (player != null)
		{
			setEmail(player.getAccountName(), email);
		}
	}

	public static void setEmail(String accountName, String email)
	{
		if (accountName == null)
		{
			return;
		}

		insertAccountData(accountName, "email_addr", email);
	}

	public static String getEmail(Player player)
	{
		return player != null ? getEmail(player.getAccountName()) : null;
	}

	public static String getEmail(String accountName)
	{
		if (accountName == null)
		{
			return null;
		}

		return getAccountValue(accountName, "email_addr");
	}

	public static boolean validateEmail(String email, String email2)
	{

		if (email == null || email2 == null || email.isEmpty() || email2.isEmpty())
		{
			return false;
		}

		if (email.contains("@") && email.contains(".") && email.length() <= 50 && email.length() >= 5)
		{
			if (email.equalsIgnoreCase(email2))
			{
				return true;
			}
		}

		return false;
	}

	private static void setEmail(Player player, String email, String confirmEmail)
	{
		if (!validateEmail(email, confirmEmail))
		{
			player.sendMessage("This e-mail address is invalid. Please try again with a valid one.");
			player.sendMessage("It is important to use a valid e-mail address because it is the only thing to recognize you as the owner of this account.");
			// Tutorial window still not closed so the player can set e-mail again.
		}
		else
		{
			setEmail(player, email);
			player.sendMessage("Your e-mail has been successfully set to: " + email);
			player.sendMessage("Please remember this e-mail address, because it is used to identify you as the owner of this account.");
			player.sendPacket(TutorialCloseHtml.STATIC); // Close the tutorial window since now the e-mail is set.
			EnterWorld.loadTutorial(player); // Secondary password, class change and other stuff here...
		}
	}

	public static void insertAccountData(String accountName, String var, String value)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("REPLACE INTO account_variables VALUES (?,?,?)");
			statement.setString(1, accountName);
			statement.setString(2, var);
			statement.setString(3, value);
			statement.executeUpdate();
		}
		catch (Exception e)
		{
			_log.warn("Cannot insert account variable.", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public static String getAccountValue(String accountName, String var)
	{
		String data = null;
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT value FROM account_variables WHERE account_name=? AND var=?");
			statement.setString(1, accountName);
			statement.setString(2, var);
			rset = statement.executeQuery();
			while (rset.next())
			{
				data = rset.getString(1);
			}
		}
		catch (Exception e)
		{
			_log.warn("Cannot get account variable value.", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		return data;
	}

	public static String getAccountVar(String accountName)
	{
		String data = "";

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT var FROM account_variables WHERE account_name=?");
			statement.setString(1, accountName);
			rset = statement.executeQuery();
			while (rset.next())
			{
				data = rset.getString(1);
			}
		}
		catch (Exception e)
		{
			_log.warn("Cannot get account variable.", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		return data;
	}
}
