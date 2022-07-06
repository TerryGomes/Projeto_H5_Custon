package l2mv.loginserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.dbutils.DbUtils;
import l2mv.loginserver.database.L2DatabaseFactory;

public class GameServerRegister
{
	private final static Logger _log = LoggerFactory.getLogger(GameServerRegister.class);

	public static void main(String[] paramArrayOfString)
	{
		Config.load();

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;

		int i = 0;
		while (i == 0)
		{
			System.out.println();
			System.out.println("1. List GameServers");
			System.out.println("2. Add GameServer");
			System.out.println("3. Del GameServer");
			System.out.println("4. Exit");
			System.out.print("Enter: ");
			try
			{
				InputStreamReader localInputStreamReader1 = new InputStreamReader(System.in);
				BufferedReader localBufferedReader1 = new BufferedReader(localInputStreamReader1);
				con = L2DatabaseFactory.getInstance().getConnection();
				int j = Integer.parseInt(localBufferedReader1.readLine());
				switch (j)
				{
				case 1:
					System.out.println("\n=========== Registered GameServers =============\n");
					statement = con.prepareStatement("SELECT * FROM gameservers");
					rset = statement.executeQuery();
					while (rset.next())
					{
						System.out.println("ID: " + rset.getInt("server_id"));
						System.out.println("IP: " + rset.getString("host"));
						System.out.println();
					}
					System.out.println("================================================");
					break;
				case 2:
					System.out.println("\n============= Register GameServer ==============");
					System.out.print("Enter GameServer Id: ");
					InputStreamReader localInputStreamReader2 = new InputStreamReader(System.in);
					BufferedReader localBufferedReader2 = new BufferedReader(localInputStreamReader2);
					int regId = Integer.parseInt(localBufferedReader2.readLine());
					System.out.print("Enter GameServer IP: ");
					localInputStreamReader2 = new InputStreamReader(System.in);
					localBufferedReader2 = new BufferedReader(localInputStreamReader2);
					String regIp = localBufferedReader2.readLine();
					statement = con.prepareStatement("REPLACE INTO gameservers VALUES (?, ?)");
					statement.setInt(1, regId);
					statement.setString(2, regIp);
					statement.execute();
					System.out.println("GameServer registered");
					System.out.println("================================================");
					break;
				case 3:
					System.out.println("\n============= Deleted GameServer ===============");
					System.out.print("Enter GameServer Id: ");
					InputStreamReader localInputStreamReader3 = new InputStreamReader(System.in);
					BufferedReader localBufferedReader3 = new BufferedReader(localInputStreamReader3);
					int delId = Integer.parseInt(localBufferedReader3.readLine());
					statement = con.prepareStatement("DELETE FROM gameservers WHERE server_id = ?");
					statement.setInt(1, delId);
					statement.execute();
					System.out.println("GameServer ID:" + delId + " deleted");
					System.out.println("================================================");
					break;
				case 4:
					i = 1;
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
	}
}