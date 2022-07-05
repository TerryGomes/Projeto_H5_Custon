package l2f.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.database.DatabaseFactory;

public class ClickersSignatureDao
{
	private static final Logger _log = LoggerFactory.getLogger(ClickersSignatureDao.class);
	private static ClickersSignatureDao _instance = null;

	private int[] _signatures = null;

	public ClickersSignatureDao()
	{
		updateSignatures();
	}

	public int[] getSignatures()
	{
		return _signatures;
	}

	public void updateSignatures()
	{
		_signatures = null;
		List<Integer> tempList = new ArrayList<>();
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT * FROM botSignatures"); ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				tempList.add(rset.getInt("signature"));
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while updatingSignatures", e);
		}

		_signatures = new int[tempList.size()];
		for (int i = 0; i < tempList.size(); i++)
		{
			_signatures[i] = tempList.get(i);
		}
	}

	public static ClickersSignatureDao getInstance()
	{
		if (_instance == null)
		{
			_instance = new ClickersSignatureDao();
		}
		return _instance;
	}
}
