package l2mv.gameserver.network.loginservercon.lspackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.napile.primitive.Containers;
import org.napile.primitive.lists.IntList;
import org.napile.primitive.lists.impl.ArrayIntList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.network.loginservercon.AuthServerCommunication;
import l2mv.gameserver.network.loginservercon.ReceivablePacket;
import l2mv.gameserver.network.loginservercon.gspackets.SetAccountInfo;

public class GetAccountInfo extends ReceivablePacket
{
	private static final Logger _log = LoggerFactory.getLogger(GetAccountInfo.class);
	private String _account;

	@Override
	protected void readImpl()
	{
		_account = readS();
	}

	@Override
	protected void runImpl()
	{
		int playerSize = 0;
		IntList deleteChars = Containers.EMPTY_INT_LIST;

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT deletetime FROM characters WHERE account_name=?"))
		{
			statement.setString(1, _account);

			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					playerSize++;
					int d = rset.getInt("deletetime");
					if (d > 0)
					{
						if (deleteChars.isEmpty())
						{
							deleteChars = new ArrayIntList(3);
						}

						deleteChars.add(d + Config.DELETE_DAYS * 24 * 60 * 60);
					}
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("GetAccountInfo:runImpl() ", e);
		}

		AuthServerCommunication.getInstance().sendPacket(new SetAccountInfo(_account, playerSize, deleteChars.toArray()));
	}
}
