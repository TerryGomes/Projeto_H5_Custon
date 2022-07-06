package l2mv.gameserver.security;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import l2mv.gameserver.Config;

public class License
{
	public static void main(String[] args)
	{
		try
		{
			// create our mysql database connection
			String myDriver = "com.mysql.jdbc.Driver";
			String myUrl = "jdbc:mysql://217.174.152.33/fandcro_subr424";
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, "fandcro_license", "license123");
			String email = Config.MAIL_USER;
			// our SQL SELECT query.
			// if you only need a few columns, specify them by name instead
			// of using "*"
			String query = "SELECT server_ip FROM su_members where email=" + email + "";
			System.out.println(query);
			// create the java statement
			Statement st = conn.createStatement();

			// execute the query, and get a java resultset
			ResultSet rs = st.executeQuery(query);
			String server_ip = "127.0.0.1";
			// iterate through the java resultset
			while (rs.next())
			{
				// int id = rs.getInt("id");
				server_ip = rs.getString("server_ip");
				/*
				 * String lastName = rs.getString("last_name");
				 * Date dateCreated = rs.getDate("date_created");
				 * boolean isAdmin = rs.getBoolean("is_admin");
				 * int numPoints = rs.getInt("num_points");
				 */
				// print the results
				System.out.format("%s\n", server_ip);
			}
			if (server_ip != Config.GAMESERVER_HOSTNAME)
			{
				System.out.println("Please setup your ip in http://fandc.ro");
				st.close();
				return;
			}
			System.out.println("work");
			st.close();
		}
		catch (Exception e)
		{
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}
	}
}
