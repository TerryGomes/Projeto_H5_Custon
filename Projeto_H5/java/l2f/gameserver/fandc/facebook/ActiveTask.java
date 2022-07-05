package l2f.gameserver.fandc.facebook;

import java.util.concurrent.TimeUnit;

import l2f.commons.annotations.NotNull;
import l2f.commons.annotations.Nullable;
import l2f.gameserver.ConfigHolder;

public class ActiveTask
{
	private final int playerId;
	private final FacebookIdentityType identityType;
	private final String identityValue;
	private final FacebookActionType actionType;
	private final OfficialPost father;
	private final String requestedMessage;
	private final long takenDate;

	public ActiveTask(int playerId, FacebookIdentityType identityType, String identityValue, FacebookActionType actionType, @Nullable final OfficialPost father, @NotNull final String requestedMessage, long takenDate)
	{
		this.playerId = playerId;
		this.identityType = identityType;
		this.identityValue = identityValue;
		this.actionType = actionType;
		this.father = father;
		this.requestedMessage = requestedMessage;
		this.takenDate = takenDate;
	}

	public int getPlayerId()
	{
		return playerId;
	}

	public FacebookIdentityType getIdentityType()
	{
		return identityType;
	}

	public String getIdentityValue()
	{
		return identityValue;
	}

	public FacebookActionType getActionType()
	{
		return actionType;
	}

	public OfficialPost getFather()
	{
		return father;
	}

	@NotNull
	public String getRequestedMessage()
	{
		return requestedMessage;
	}

	public long getTakenDate()
	{
		return takenDate;
	}

	public long getTimeLimitDate()
	{
		return takenDate + ConfigHolder.getMillis("FacebookTimeLimit", TimeUnit.SECONDS);
	}

	public String getLinkToAction()
	{
		if (father == null)
		{
			return "https://www.facebook.com/" + ConfigHolder.getString("FacebookPageName");
		}
		final String[] ids = father.getId().split("_");
		return "https://www.facebook.com/" + ids[0] + "/posts/" + ids[1];
	}

	@Override
	public String toString()
	{
		return "ActiveTask{playerId=" + playerId + ", identityType=" + identityType + ", identityValue='" + identityValue + '\'' + ", actionType=" + actionType + ", father=" + father + ", requestedMessage='" + requestedMessage + '\'' + ", takenDate=" + takenDate + '}';
	}
}
