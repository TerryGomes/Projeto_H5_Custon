package l2mv.gameserver.fandc.streaming;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import l2mv.commons.annotations.Nullable;
import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;

public class Stream
{
	private final String _channelName;
	private String _streamGameName;
	private boolean _isStreamGameNameCorrect;
	private String _streamTitle;
	private boolean _isTitleCorrect;
	private int _viewersCount;
	private long _lastActiveDate;
	private int _attachedPlayerId = -1;
	private String _attachedPlayerServer = "";
	private long _notRewardedSeconds = 0L;
	private long _totalRewardedSecondsToday = 0L;
	private final List<Integer> _idsToApprove = new ArrayList<Integer>();
	private long _punishedUntilDate = -1L;

	public Stream(String channelName, String streamGameName, boolean isStreamGameNameCorrect, String streamTitle, boolean isTitleCorrect, int viewersCount, long lastActiveDate)
	{
		_channelName = channelName;
		_streamGameName = streamGameName;
		_isStreamGameNameCorrect = isStreamGameNameCorrect;
		_streamTitle = streamTitle;
		_isTitleCorrect = isTitleCorrect;
		_viewersCount = viewersCount;
		_lastActiveDate = lastActiveDate;
	}

	public String getChannelName()
	{
		return _channelName;
	}

	public void setStreamGameName(String streamGameName, boolean isStreamGameNameCorrect)
	{
		_streamGameName = streamGameName;
		_isStreamGameNameCorrect = isStreamGameNameCorrect;
	}

	public String getStreamGameName()
	{
		return _streamGameName;
	}

	public boolean isStreamGameNameCorrect()
	{
		return _isStreamGameNameCorrect;
	}

	public void setStreamTitle(String streamTitle, boolean isTitleCorrect)
	{
		_streamTitle = streamTitle;
		_isTitleCorrect = isTitleCorrect;
	}

	public String getStreamTitle()
	{
		return _streamTitle;
	}

	public boolean isTitleCorrect()
	{
		return _isTitleCorrect;
	}

	public void setViewersCount(int viewersCount)
	{
		_viewersCount = viewersCount;
	}

	public int getViewersCount()
	{
		return _viewersCount;
	}

	public void setLastActiveDate(long lastActiveDate)
	{
		_lastActiveDate = lastActiveDate;
	}

	public long getLastActiveDate()
	{
		return _lastActiveDate;
	}

	public void setAttachedPlayerId(int playerId, String playerServer)
	{
		_attachedPlayerId = playerId;
		_attachedPlayerServer = playerServer;
	}

	public int getAttachedPlayerId()
	{
		return _attachedPlayerId;
	}

	@Nullable
	public Player getStreamingPlayer()
	{
		if (_attachedPlayerId <= 0)
		{
			return null;
		}
		return GameObjectsStorage.getPlayer(_attachedPlayerId);
	}

	public String getAttachedPlayerServer()
	{
		return _attachedPlayerServer;
	}

	public void setNotRewardedSeconds(long notRewardedSeconds)
	{
		_notRewardedSeconds = notRewardedSeconds;
	}

	public long incNotRewardedSeconds(long toAdd, boolean addToTotal, boolean checkTotal)
	{
		if (!checkTotal || toAdd + _totalRewardedSecondsToday <= ConfigHolder.getLong("StreamingMaxRewardedSecondsPerDay"))
		{
			_notRewardedSeconds += toAdd;
			if (addToTotal)
			{
				_totalRewardedSecondsToday += toAdd;
			}
			return toAdd;
		}

		final long realToAdd = ConfigHolder.getLong("StreamingMaxRewardedSecondsPerDay") - _totalRewardedSecondsToday;
		if (realToAdd == 0L)
		{
			return 0L;
		}

		_notRewardedSeconds += realToAdd;
		if (addToTotal)
		{
			_totalRewardedSecondsToday += realToAdd;
		}
		return realToAdd;
	}

	public long getNotRewardedSeconds()
	{
		return _notRewardedSeconds;
	}

	public void setTotalRewardedSecondsToday(long totalRewardedSecondsToday)
	{
		_totalRewardedSecondsToday = totalRewardedSecondsToday;
	}

	public long getTotalRewardedSecondsToday()
	{
		return _totalRewardedSecondsToday;
	}

	public void addIdsToApprove(Collection<Integer> ids)
	{
		_idsToApprove.addAll(ids);
	}

	public void addIdToApprove(Integer id)
	{
		_idsToApprove.add(id);
	}

	public boolean isOnApprovalList(Integer id)
	{
		return _idsToApprove.contains(id);
	}

	public List<Integer> getIdsToApprove()
	{
		return _idsToApprove;
	}

	public List<Integer> getIdsToApproveCopy()
	{
		return new ArrayList<Integer>(_idsToApprove);
	}

	public void setPunishedUntilDate(long date)
	{
		_punishedUntilDate = date;
	}

	public long getPunishedUntilDate()
	{
		return _punishedUntilDate;
	}

	public boolean isNowPunished()
	{
		return _punishedUntilDate > System.currentTimeMillis();
	}

	public boolean isNowPunished(long currentDate)
	{
		return _punishedUntilDate > currentDate;
	}

	@Override
	public String toString()
	{
		return "Stream{channelName='" + _channelName + '\'' + ", streamGameName='" + _streamGameName + '\'' + ", streamTitle='" + _streamTitle + '\'' + ", attachedPlayerId=" + _attachedPlayerId + '}';
	}
}
