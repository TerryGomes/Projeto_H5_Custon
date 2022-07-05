package l2f.gameserver.fandc.facebook;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ConfigHolder;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.data.StringHolder;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.components.ChatType;

public final class FacebookAutoAnnouncement extends RunnableImpl
{
	private static final long SAFE_MIN_DELAY = 1000L;
	private static final long DELAY_WHILE_SYSTEM_DISABLED = 30000L;
	private static final String NOT_CONNECTED_MSG_ADDRESS = "Facebook.AutoAnnounce.NotConnected";
	private static final String CONNECTED_MSG_ADDRESS = "Facebook.AutoAnnounce.NextTask";
	private static final String NEGATIVE_POINTS_MSG_ADDRESS = "Facebook.AutoAnnounce.NegativePoints";

	private long _lastNotConnectedMsgDate;
	private long _lastConnectedMsgDate;
	private long _lastNegativePointsMsgDate;
	private static ScheduledFuture<?> _runningThread;

	private FacebookAutoAnnouncement()
	{
		_lastNotConnectedMsgDate = 0L;
		_lastConnectedMsgDate = 0L;
		_lastNegativePointsMsgDate = 0L;
	}

	public static void load()
	{
		ThreadPoolManager.getInstance().execute(new FacebookAutoAnnouncement());
	}

	@Override
	public void runImpl()
	{
		final long currentDate = System.currentTimeMillis();
		if (ConfigHolder.getBool("AllowFacebookRewardSystem"))
		{
			if (ConfigHolder.getBool("FacebookAllowAutoAnnouncementNotConnected")
						&& _lastNotConnectedMsgDate + ConfigHolder.getMillis("FacebookAutoAnnouncementNotConnectedDelay", TimeUnit.SECONDS) < currentDate)
			{
				_lastNotConnectedMsgDate = currentDate;
				announceNotConnectedMsg();
			}
			if (ConfigHolder.getBool("FacebookAllowAutoAnnouncementConnected") && _lastConnectedMsgDate + ConfigHolder.getMillis("FacebookAutoAnnouncementConnectedDelay", TimeUnit.SECONDS) < currentDate)
			{
				_lastConnectedMsgDate = currentDate;
				announceConnectedMsg();
			}
			if (ConfigHolder.getBool("FacebookAllowAutoAnnounceNegativePoints")
						&& _lastNegativePointsMsgDate + ConfigHolder.getMillis("FacebookAutoAnnouncementNegativePointsDelay", TimeUnit.SECONDS) < currentDate)
			{
				_lastNegativePointsMsgDate = currentDate;
				announceNegativePointsMsg();
			}
		}
		FacebookAutoAnnouncement._runningThread = ThreadPoolManager.getInstance().schedule(this, getNextDelay(currentDate));
	}

	private static void announceNotConnectedMsg()
	{
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player.getFacebookProfile() == null)
			{
				player.sendPacket(new Say2(0, ChatType.ANNOUNCEMENT, "", StringHolder.getNotNull(player, NOT_CONNECTED_MSG_ADDRESS, player.getName())));
			}
		}
	}

	private static void announceConnectedMsg()
	{
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			final FacebookProfile fb = player.getFacebookProfile();
			if (fb != null && !fb.hasNegativePoints() && !CompletedTasksHistory.getInstance().getAvailableActionTypes(fb).isEmpty() && !fb.hasTaskDelay())
			{
				player.sendPacket(new Say2(0, ChatType.ANNOUNCEMENT, "", StringHolder.getNotNull(player, CONNECTED_MSG_ADDRESS, player.getName())));
			}
		}
	}

	private static void announceNegativePointsMsg()
	{
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player.getFacebookProfile() != null && player.getFacebookProfile().hasNegativePoints())
			{
				player.sendPacket(new Say2(0, ChatType.ANNOUNCEMENT, "", StringHolder.getNotNull(player, "Facebook.AutoAnnounce.NegativePoints", player.getName())));
			}
		}
	}

	private long getNextDelay(long currentDate)
	{
		if (!ConfigHolder.getBool("AllowFacebookRewardSystem") || !ConfigHolder.getBool("FacebookAllowAutoAnnouncementNotConnected") && !ConfigHolder.getBool("FacebookAllowAutoAnnouncementConnected")
					&& !ConfigHolder.getBool("FacebookAllowAutoAnnounceNegativePoints"))
		{
			return DELAY_WHILE_SYSTEM_DISABLED;
		}

		final long delayToNotConnected = _lastNotConnectedMsgDate + ConfigHolder.getMillis("FacebookAutoAnnouncementNotConnectedDelay", TimeUnit.SECONDS) - currentDate;
		final long delayToConnected = _lastConnectedMsgDate + ConfigHolder.getMillis("FacebookAutoAnnouncementConnectedDelay", TimeUnit.SECONDS) - currentDate;
		final long delayNegativePoints = _lastNegativePointsMsgDate + ConfigHolder.getMillis("FacebookAutoAnnouncementNegativePointsDelay", TimeUnit.SECONDS) - currentDate;
		return Math.max(Math.min(delayToNotConnected, Math.min(delayToConnected, delayNegativePoints)), SAFE_MIN_DELAY);
	}
}
