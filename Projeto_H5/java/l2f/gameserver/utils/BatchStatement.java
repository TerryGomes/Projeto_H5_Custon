package l2f.gameserver.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Makes creation of sql batchs easier, especially with the setAutoCommit false
 * It uses preparedstatement
 *
 * @author Prims
 */
public class BatchStatement
{
	public static PreparedStatement createPreparedStatement(Connection con, String query) throws SQLException
	{
		con.setAutoCommit(false);
		return con.prepareStatement(query);
	}
}