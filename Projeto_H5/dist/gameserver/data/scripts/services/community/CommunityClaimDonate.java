package services.community;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.concurrent.locks.ReentrantLock;

import l2f.commons.dbutils.DbUtils;
import l2f.gameserver.Config;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.handler.bbs.CommunityBoardManager;
import l2f.gameserver.handler.bbs.ICommunityBoardHandler;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.ShowBoard;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.utils.BbsUtil;

/**
 *
 * @author claww
 */
public class CommunityClaimDonate implements ICommunityBoardHandler, ScriptFile
{

	private static final int ITEM_ID = Config.DONATE_ID;
	private static final String BUTTON = "<button value=\"Get\" action=\"bypass _bbsdonate:get\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\">";
	private static final ReentrantLock lock = new ReentrantLock();

	@Override
	public String[] getBypassCommands()
	{
		return new String[]
		{
			"_bbsdonate"
		};
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		StringTokenizer st = new StringTokenizer(bypass, ":");
		String cmd = st.nextToken();
		if (cmd.equals("_bbsdonate"))
		{
			String next = st.hasMoreTokens() ? st.nextToken() : "info";
			if ("info".equals(next))
			{
				String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_donate.htm", player);
				int count = getCoinCount(player.getObjectId());
				html = html.replace("%coins%", String.valueOf(count));
				html = html.replace("%button%", count > 0 ? BUTTON : "No coins");
				html = BbsUtil.htmlAll(html, player);
				ShowBoard.separateAndSend(html, player);
				return;
			}
			else if ("get".equals(next))
			{
				final int count = getCoinCount(player.getObjectId());
				if (count > 0)
				{
					int can = 0;
					Connection con = null;
					PreparedStatement ps = null;
					try
					{
						con = DatabaseFactory.getInstance().getConnection();
						ps = con.prepareStatement("UPDATE remdev_payments SET status=2 WHERE obj_id = ? AND status = 1");
						ps.setInt(1, player.getObjectId());
						ps.executeUpdate();
						can = ps.getUpdateCount();
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
						can = 0;
					}
					finally
					{
						DbUtils.closeQuietly(con, ps);
					}
					if (can > 0)
					{
						lock.lock();
						try
						{
							Functions.addItem(player, ITEM_ID, count, "AutoDonate");
						}
						finally
						{
							lock.unlock();
						}
					}
				}
			}
			onBypassCommand(player, "_bbsdonate:info");
		}
	}

	private int getCoinCount(final int obj_id)
	{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int count = 0;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			ps = con.prepareStatement("SELECT * FROM remdev_payments WHERE obj_id = ? AND status = 1");
			ps.setInt(1, obj_id);
			rs = ps.executeQuery();
			while (rs.next())
			{
				count += rs.getInt("amount");
			}

		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con, ps, rs);
		}
		return count;
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{

	}

	@Override
	public void onLoad()
	{
		if (Config.COMMUNITYBOARD_ENABLED)
		{
			CommunityBoardManager.getInstance().registerHandler(this);
		}
	}

	@Override
	public void onReload()
	{
		if (Config.COMMUNITYBOARD_ENABLED)
		{
			CommunityBoardManager.getInstance().removeHandler(this);
		}
	}

	@Override
	public void onShutdown()
	{

	}

}
