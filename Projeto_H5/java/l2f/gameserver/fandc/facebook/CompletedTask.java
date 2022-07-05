package l2f.gameserver.fandc.facebook;

public class CompletedTask implements FacebookAction
{
	private final int playerId;
	private final long takenDate;
	private final FacebookAction action;
	private CommentApprovalType commentApprovalType;
	private boolean isRewarded;

	public CompletedTask(ActiveTask task, FacebookAction action, CommentApprovalType commentApprovalType, boolean isRewarded)
	{
		playerId = task.getPlayerId();
		takenDate = task.getTakenDate();
		this.action = action;
		this.commentApprovalType = commentApprovalType;
		this.isRewarded = isRewarded;
	}

	public CompletedTask(int playerId, long takenDate, FacebookAction action, CommentApprovalType commentApprovalType, boolean isRewarded)
	{
		this.playerId = playerId;
		this.takenDate = takenDate;
		this.action = action;
		this.commentApprovalType = commentApprovalType;
		this.isRewarded = isRewarded;
	}

	public int getPlayerId()
	{
		return playerId;
	}

	public long getTakenDate()
	{
		return takenDate;
	}

	public FacebookAction getAction()
	{
		return action;
	}

	public void setCommentApproved(CommentApprovalType commentApprovalType)
	{
		this.commentApprovalType = commentApprovalType;
	}

	public CommentApprovalType getCommentApprovalType()
	{
		return commentApprovalType;
	}

	public void setRewarded(boolean isRewarded)
	{
		this.isRewarded = isRewarded;
	}

	public boolean isRewarded()
	{
		return isRewarded;
	}

	@Override
	public String getId()
	{
		return action.getId();
	}

	@Override
	public FacebookActionType getActionType()
	{
		return action.getActionType();
	}

	@Override
	public FacebookProfile getExecutor()
	{
		return action.getExecutor();
	}

	@Override
	public long getCreatedDate()
	{
		return action.getCreatedDate();
	}

	@Override
	public long getExtractionDate()
	{
		return action.getExtractionDate();
	}

	@Override
	public String getMessage()
	{
		return action.getMessage();
	}

	@Override
	public void changeMessage(String newMessage)
	{
		action.changeMessage(newMessage);
	}

	@Override
	public FacebookAction getFather()
	{
		return action.getFather();
	}

	@Override
	public boolean canBeRemoved()
	{
		return action.canBeRemoved();
	}

	@Override
	public void remove()
	{
		action.remove();
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof CompletedTask && playerId == ((CompletedTask) obj).playerId && action.equals(((CompletedTask) obj).action);
	}

	@Override
	public int hashCode()
	{
		int result = playerId;
		result = 31 * result + action.hashCode();
		return result;
	}

	@Override
	public String toString()
	{
		return "CompletedTask{playerId=" + playerId + ", takenDate=" + takenDate + ", action=" + action + ", commentApprovalType=" + commentApprovalType + ", isRewarded=" + isRewarded + '}';
	}

	public enum CommentApprovalType
	{
		NOT_YET_CHECKED, NOT_APPROVED, APPROVED;
	}
}
