package l2f.gameserver.taskmanager.actionrunner.tasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.database.mysql;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.utils.Strings;

public class DeleteExpiredVarsTask extends AutomaticTask
{
	private static final Logger LOG = Logger.getLogger(DeleteExpiredVarsTask.class);

	public DeleteExpiredVarsTask()
	{
		super();
	}

	@Override
	public void doTask() throws Exception
	{
		Map<Integer, String> varMap = new HashMap<Integer, String>();

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement query = con.prepareStatement("SELECT obj_id, name FROM character_variables WHERE expire_time > 0 AND expire_time < ?"))
		{
			query.setLong(1, System.currentTimeMillis());
			try (ResultSet rs = query.executeQuery())
			{
				while (rs.next())
				{
					String name = rs.getString("name");
					String obj_id = Strings.stripSlashes(rs.getString("obj_id"));
					varMap.put(Integer.parseInt(obj_id), name);
				}
			}
		}
		catch (NumberFormatException | SQLException e)
		{
			LOG.error("Error while Selecting Expired Character Variables", e);
		}

		if (!varMap.isEmpty())
		{
			for (Map.Entry<Integer, String> entry : varMap.entrySet())
			{
				Player player = GameObjectsStorage.getPlayer(entry.getKey());
				if (player != null && player.isOnline())
				{
					player.unsetVar(entry.getValue());
				}
				else
				{
					mysql.set("DELETE FROM `character_variables` WHERE `obj_id`=? AND `type`='user-var' AND `name`=? LIMIT 1", entry.getKey(), entry.getValue());
				}
			}
		}
	}

	@Override
	public long reCalcTime(boolean start)
	{
		return System.currentTimeMillis() + 600000L;
	}
}
