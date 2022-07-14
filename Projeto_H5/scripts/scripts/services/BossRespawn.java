package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bosses.EpicBossState.State;
import l2mv.commons.dbutils.DbUtils;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.utils.TimeUtils;

/**
 * @date 2016
 */
public class BossRespawn extends Functions
{
	private static final Logger _log = LoggerFactory.getLogger(BossRespawn.class);

	public void show()
	{
		final Player player = getSelf();
		if (player == null)
		{
			return;
		}

		final StringBuilder append = new StringBuilder();
		append.append("<html><title>Epic Boss Info</title><body><br><center>");
		append.append("<img src=\"L2UI_CH3.onscrmsg_pattern01_1\" width=290 height=32><br>");
		final String epicBoss = epicBoss();// Display epic bosses
		append.append(epicBoss);
		append.append("<br><img src=\"L2UI_CH3.onscrmsg_pattern01_2\" width=290 height=32><br>");
		append.append("</center></body></html>");
		show(append.toString(), player, null);
	}

	private String epicBoss()
	{
		long _respawnDate;
		String _name;
		String _state;
		final StringBuilder append = new StringBuilder();
		append.append("<font color=\"00C3FF\">Epic Boss State</color><br1> ");
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM epic_boss_spawn");
			rset = statement.executeQuery();
			while (rset.next())
			{
				_name = NpcHolder.getInstance().getTemplate(rset.getInt("bossId")).getName();
				append.append("<font color=\"00C3FF\">" + _name + "</color>: ");
				_respawnDate = rset.getLong("respawnDate") * 1000L;
				if (_respawnDate - System.currentTimeMillis() <= 0)
				{
					_state = "Not Spawned";
					append.append("<font color=\"FF0000\">" + _state + "</color> ");
				}
				else
				{
					final int tempState = rset.getInt("state");
					if (tempState == State.NOTSPAWN.ordinal())
					{
						_state = "Not Spawned";
						append.append("<font color=\"FF0000\">" + _state + "</color> ");
					}
					else if (tempState == State.INTERVAL.ordinal())
					{
						_state = "Next Spawn:";
						append.append("<font color=\"FFFF00\">" + _state + " </color> ");
					}
					else if (tempState == State.ALIVE.ordinal())
					{
						_state = "Is Alive";
						append.append("<font color=\"00FF00\">" + _state + "</color> ");
					}
					else if (tempState == State.DEAD.ordinal())
					{
						_state = "Is Dead";
						append.append("<font color=\"FF00FF\">" + _state + "</color> ");
					}
					else
					{
						_state = "Not Spawned";
						append.append("<font color=\"FF0000\">" + _state + "</color> ");
					}
				}
				if (_respawnDate - System.currentTimeMillis() > 0)
				{
					append.append("<font color=\"9CC300\">" + TimeUtils.toSimpleFormat(_respawnDate) + "</color>" + "<br1>");
				}
				else
				{
					append.append("<br1>");
				}
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
		return append.toString();
	}
}
