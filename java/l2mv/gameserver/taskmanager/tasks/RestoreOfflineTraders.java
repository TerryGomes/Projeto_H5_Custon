package l2mv.gameserver.taskmanager.tasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.Config;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;

public class RestoreOfflineTraders extends RunnableImpl
{
	private static final Logger LOG = LoggerFactory.getLogger(RestoreOfflineTraders.class);

	@Override
	public void runImpl() throws Exception
	{
		int count = 0;

		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{

			// Убираем просроченных
			if (Config.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK > 0)
			{
				int expireTimeSecs = (int) (System.currentTimeMillis() / 1000L - Config.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK);

				try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_variables WHERE name = 'offline' AND value < ?"))
				{
					statement.setLong(1, expireTimeSecs);
					statement.executeUpdate();
				}
			}

			// Убираем забаненных
			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_variables WHERE name = 'offline' AND obj_id IN (SELECT obj_id FROM characters WHERE accessLevel < 0)"))
			{
				statement.executeUpdate();
			}

			try (PreparedStatement statement = con.prepareStatement("SELECT obj_id, value FROM character_variables WHERE name = 'offline'"); ResultSet rset = statement.executeQuery())
			{
				int objectId;
				int expireTimeSecs;
				Player p;

				while (rset.next())
				{
					objectId = rset.getInt("obj_id");
					expireTimeSecs = rset.getInt("value");

					p = Player.restore(objectId);
					if (p == null)
					{
						continue;
					}

					if (p.isDead())
					{
						p.kick();
						continue;
					}

					p.setNameColor(Config.SERVICES_OFFLINE_TRADE_NAME_COLOR);
					p.setOfflineMode(true);
					p.setIsOnline(true);
					p.updateOnlineStatus();

					p.spawnMe();

					if (p.getClan() != null && p.getClan().getAnyMember(p.getObjectId()) != null)
					{
						p.getClan().getAnyMember(p.getObjectId()).setPlayerInstance(p, false);
					}

					// Если кто-то успел сесть рядом с оффлайн торговцем до его прогрузки - снимаем его с торга.
					if (Config.SERVICES_TRADE_ONLY_FAR)
					{
						for (Player player : World.getAroundPlayers(p, Config.SERVICES_TRADE_RADIUS, 200))
						{
							if (player.isInStoreMode())
							{
								if (player.isInOfflineMode())
								{
									player.setOfflineMode(false);
									player.kick();
									LOG.warn("Offline trader: " + player + " kicked.");
								}
								else
								{
									player.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
									player.standUp();
									player.broadcastCharInfo();
								}
							}
						}
					}

					count++;
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while restoring offline traders!", e);
		}

		LOG.info("Restored " + count + " offline traders");
	}
}
