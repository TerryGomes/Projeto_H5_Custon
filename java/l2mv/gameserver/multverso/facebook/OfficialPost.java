package l2mv.gameserver.multverso.facebook;

import java.util.EnumSet;

import l2mv.gameserver.multverso.facebook.action.Post;

public class OfficialPost extends Post
{
	private final EnumSet<FacebookActionType> _rewardedActions;

	public OfficialPost(String id, FacebookProfile executor, String message, long createdTime, long extractionDate, EnumSet<FacebookActionType> rewardedActions)
	{
		super(id, executor, message, createdTime, extractionDate);
		_rewardedActions = rewardedActions;
	}

	public OfficialPost(String id, FacebookProfile executor, String message, long createdTime, long extractionDate)
	{
		super(id, executor, message, createdTime, extractionDate);
		_rewardedActions = EnumSet.noneOf(FacebookActionType.class);
	}

	public boolean isActionTypeRewarded(FacebookActionType type)
	{
		return _rewardedActions.contains(type);
	}

	public boolean isAnyActionTypeRewarded()
	{
		return !_rewardedActions.isEmpty();
	}

	public EnumSet<FacebookActionType> getRewardedActionsForIterate()
	{
		return _rewardedActions;
	}

	public void setRewardedActions(EnumSet<FacebookActionType> rewardedActions)
	{
		_rewardedActions.clear();
		_rewardedActions.addAll(rewardedActions);
	}

	@Override
	public boolean canBeRemoved()
	{
		return false;
	}

	@Override
	public void remove()
	{
	}

	@Override
	public String toString()
	{
		return "OfficialPost{id='" + getId() + '\'' + ", executor=" + getExecutor() + ", message='" + getMessage() + '\'' + ", createdTime=" + getCreatedDate() + ", extractionDate=" + getExtractionDate() + ", rewardedActions=" + _rewardedActions + '}';
	}
}
