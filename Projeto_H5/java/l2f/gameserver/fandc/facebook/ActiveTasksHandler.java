package l2f.gameserver.fandc.facebook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ConfigHolder;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.dao.CharacterDAO;
import l2f.gameserver.data.StringHolder;
import l2f.gameserver.data.xml.holder.FacebookCommentsHolder;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.HideBoard;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.utils.Language;
import l2f.gameserver.utils.Log;

public final class ActiveTasksHandler
{
	private final CopyOnWriteArrayList<ActiveTask> _activeTasks;
	private final ScheduledFuture<?> _expiredTimeThread;

	private ActiveTasksHandler()
	{
		_activeTasks = new CopyOnWriteArrayList<ActiveTask>();
		final long delay = ConfigHolder.getLong("FacebookTimeLimitThreadDelay");
		_expiredTimeThread = ThreadPoolManager.getInstance().scheduleAtFixedDelay(new TimeExpiredThread(), delay, delay);
	}

	public ActiveTask createActiveTask(Player player, FacebookIdentityType identityType, String identityValue, FacebookActionType actionType) throws TaskNoAvailableException
	{
		FacebookProfile profile = null;
		OfficialPost father = null;
		if (actionType.haveFather())
		{
			if (identityType == FacebookIdentityType.ID)
			{
				profile = FacebookProfilesHolder.getInstance().getProfileById(identityValue);
				if ((profile == null) || !profile.equals(player.getFacebookProfile()))
				{
					return null;
				}
			}
			father = CompletedTasksHistory.getInstance().getFatherAction(profile, actionType);
			if (father == null)
			{
				throw new TaskNoAvailableException("Couldn't find Father for " + identityType + ", " + identityValue + ", " + actionType + ", " + profile);
			}
		}
		String message = "";
		if (actionType.haveCommentMessage())
		{
			message = FacebookCommentsHolder.getInstance().getCommentToWrite(father, identityType, identityValue);
		}
		final ActiveTask activeTask = new ActiveTask(player.getObjectId(), identityType, identityValue, actionType, father, message, System.currentTimeMillis());
		addNewActiveTask(activeTask);
		final boolean completed = this.checkTaskCompleted(activeTask);
		Log.logFacebook("Creating new Active Task " + activeTask + ". Profile: " + profile + ". Completed: " + completed);
		if (completed)
		{
			return null;
		}
		return activeTask;
	}

	public boolean checkTaskCompleted(FacebookAction action)
	{
		for (ActiveTask task : _activeTasks)
		{
			if (isWantedAction(task, action))
			{
				final boolean continueLooking = onWantedActionFound(task, action);
				if (!continueLooking)
				{
					return true;
				}
				continue;
			}
		}
		return false;
	}

	public boolean checkTaskCompleted(ActiveTask task)
	{
		final List<FacebookAction> allActionsOfFather = ActionsAwaitingOwner.getInstance().getActionsForIterate(task.getFather(), task.getActionType());
		for (FacebookAction action : allActionsOfFather)
		{
			if (isWantedAction(task, action))
			{
				final boolean continueLooking = onWantedActionFound(task, action);
				if (!continueLooking)
				{
					return true;
				}
				continue;
			}
		}
		return false;
	}

	private boolean onWantedActionFound(ActiveTask task, FacebookAction action)
	{
		if (CompletedTasksHistory.getInstance().getCompletedTask(action.getExecutor(), task.getFather(), task.getActionType()) != null)
		{
			abortTask(task, action, AbortTaskReason.PROFILE_DONE_SIMILAR_TASK);
			return false;
		}
		final FacebookCommentsHolder.CommentMatchType commentMatchType = FacebookCommentsHolder.getInstance().checkCommentMatches(task, action);
		if (commentMatchType == FacebookCommentsHolder.CommentMatchType.FULL_MATCH)
		{
			onTaskCompleted(task, action, true);
			return false;
		}
		if (commentMatchType == FacebookCommentsHolder.CommentMatchType.COMMENT_NOT_MATCHES && (task.getIdentityType() == FacebookIdentityType.ID || !ConfigHolder.getBool("FacebookRegistrationOnlyExactComment")))
		{
			onTaskCompleted(task, action, false);
			return false;
		}
		return true;
	}

	private void onTaskCompleted(ActiveTask finishedTask, FacebookAction action, boolean commentApproved)
	{
		final boolean hadNegativePoints = action.getExecutor().hasNegativePoints();
		if (commentApproved)
		{
			if (hadNegativePoints)
			{
				action.getExecutor().removeNegativePoint(action.getActionType(), true);
				sendMailNegativeRemoved(finishedTask.getPlayerId(), action);
			}
			else
			{
				action.getExecutor().addPositivePoint(action.getActionType(), true);
				sendReward(finishedTask.getPlayerId(), finishedTask.getTakenDate(), action);
			}
		}
		else
		{
			sendMailMsgToVerification(finishedTask);
			notifyGMsMsgsToVerify(true);
		}
		if (finishedTask.getIdentityType() != FacebookIdentityType.ID)
		{
			attachFacebookId(finishedTask.getPlayerId(), action.getExecutor());
		}
		ActionsAwaitingOwner.getInstance().removeAction(action);
		final CompletedTask.CommentApprovalType approvalType = commentApproved ? CompletedTask.CommentApprovalType.APPROVED : CompletedTask.CommentApprovalType.NOT_YET_CHECKED;
		CompletedTasksHistory.getInstance().addCompletedTask(finishedTask, action, approvalType, commentApproved, !hadNegativePoints, true);
		_activeTasks.remove(finishedTask);
		Log.logFacebook("Task Completed: " + finishedTask + " " + action + " Comment Approved: " + commentApproved);
	}

	public void abortTask(ActiveTask task, FacebookAction action, AbortTaskReason reason)
	{
		switch (reason)
		{
		case PROFILE_DONE_SIMILAR_TASK:
		{
			sendMailSimilarTaskDoneAleady(task.getPlayerId());
			break;
		}
		}
		_activeTasks.remove(task);
		Log.logFacebook("Task Aborted: " + task + " " + action + " Reason: " + reason);
	}

	public static void manageMessageApproval(CompletedTask task, boolean approved)
	{
		task.setCommentApproved(approved ? CompletedTask.CommentApprovalType.APPROVED : CompletedTask.CommentApprovalType.NOT_APPROVED);
		FacebookDatabaseHandler.replaceCompletedTask(task);
		Log.logFacebook("Managing Approval: " + task + " Comment Approved: " + approved);
		if (task.isRewarded())
		{
			if (!approved)
			{
				task.setRewarded(false);
				task.getExecutor().addNegativePoint(task.getActionType(), true);
			}
		}
		else if (approved)
		{
			task.setRewarded(true);
			FacebookDatabaseHandler.replaceCompletedTask(task);
			final boolean hadNegativePoints = task.getExecutor().hasNegativePoints();
			if (hadNegativePoints)
			{
				task.getExecutor().removeNegativePoint(task.getActionType(), true);
				sendMailNegativeRemoved(task.getPlayerId(), task);
			}
			else
			{
				task.getExecutor().addPositivePoint(task.getActionType(), true);
				sendReward(task.getPlayerId(), task.getTakenDate(), task);
			}
		}
		else
		{
			sendMailMsgNotApproved(task.getPlayerId());
		}
	}

	public static void notifyGMsMsgsToVerify(boolean incrementNotApprovedCountByOne)
	{
		int notApprovedMsgs = CompletedTasksHistory.getInstance().countTasksThatNeedsApproval();
		if (incrementNotApprovedCountByOne)
		{
			++notApprovedMsgs;
		}
		String msg;
		if (notApprovedMsgs == 1)
		{
			msg = StringHolder.getNotNull(Language.ENGLISH, "Facebook.GMNotification.MsgToVerify.One", new Object[0]);
		}
		else
		{
			msg = StringHolder.getNotNull(Language.ENGLISH, "Facebook.GMNotification.MsgToVerify.Multiple", notApprovedMsgs);
		}
		final Say2 gmNotification = new Say2(0, ChatType.COMMANDCHANNEL_ALL, "Facebook", msg);
		for (Player gm : GameObjectsStorage.getAllGMs())
		{
			gm.sendPacket(gmNotification);
		}
	}

	private static void sendReward(int playerId, long taskTakenDate, FacebookAction action)
	{
		long closestDateToCreation;
		if (action.getCreatedDate() > 0L)
		{
			closestDateToCreation = action.getCreatedDate();
		}
		else
		{
			closestDateToCreation = action.getExtractionDate();
		}
		Map<Integer, Long> reward;
		String messageBodyAddress;
		if (closestDateToCreation < taskTakenDate)
		{
			reward = action.getActionType().getRewardForNoTask();
			messageBodyAddress = "Facebook.Reward.Body.NoTask";
		}
		else
		{
			reward = action.getActionType().getRewardForTask();
			messageBodyAddress = "Facebook.Reward.Body.Task";
		}
		final String messageTitle = StringHolder.getNotNull(playerId, "Facebook.Reward.Title");
		final String messageBody = StringHolder.getNotNull(playerId, messageBodyAddress);
		final Player onlinePlayer = GameObjectsStorage.getPlayer(playerId);
		if (onlinePlayer == null)
		{
			Functions.sendSystemMail(playerId, messageTitle, messageBody, reward);
		}
		else
		{
			Functions.sendSystemMail(onlinePlayer, messageTitle, messageBody, reward);
			onlinePlayer.sendPacket(HideBoard.PACKET);
		}
	}

	private static void sendMailNegativeRemoved(int playerId, FacebookAction action)
	{
		String messageTitle;
		String messageBody;
		if (action.getExecutor().hasNegativePoints())
		{
			messageTitle = StringHolder.getNotNull(playerId, "Facebook.NegativeRemoved.StillNegativeBalance.Title");
			messageBody = StringHolder.getNotNull(playerId, "Facebook.NegativeRemoved.StillNegativeBalance.Body");
		}
		else
		{
			messageTitle = StringHolder.getNotNull(playerId, "Facebook.NegativeRemoved.ClearAccount.Title");
			messageBody = StringHolder.getNotNull(playerId, "Facebook.NegativeRemoved.ClearAccount.Body");
		}
		final Player onlinePlayer = GameObjectsStorage.getPlayer(playerId);
		if (onlinePlayer == null)
		{
			Functions.sendSystemMail(playerId, messageTitle, messageBody, Collections.emptyMap());
		}
		else
		{
			Functions.sendSystemMail(onlinePlayer, messageTitle, messageBody, Collections.emptyMap());
			onlinePlayer.sendPacket(HideBoard.PACKET);
		}
	}

	private static void sendMailMsgToVerification(ActiveTask finishedTask)
	{
		final String messageTitle = StringHolder.getNotNull(finishedTask.getPlayerId(), "Facebook.MsgToVerification.Title");
		final String messageBody = StringHolder.getNotNull(finishedTask.getPlayerId(), "Facebook.MsgToVerification.Body");
		final Player onlinePlayer = GameObjectsStorage.getPlayer(finishedTask.getPlayerId());
		if (onlinePlayer == null)
		{
			Functions.sendSystemMail(finishedTask.getPlayerId(), messageTitle, messageBody, Collections.emptyMap());
		}
		else
		{
			Functions.sendSystemMail(onlinePlayer, messageTitle, messageBody, Collections.emptyMap());
			onlinePlayer.sendPacket(HideBoard.PACKET);
		}
	}

	private static void sendMailMsgNotApproved(int playerId)
	{
		final String messageTitle = StringHolder.getNotNull(playerId, "Facebook.MsgNotApproved.Title");
		final String messageBody = StringHolder.getNotNull(playerId, "Facebook.MsgNotApproved.Body");
		final Player onlinePlayer = GameObjectsStorage.getPlayer(playerId);
		if (onlinePlayer == null)
		{
			Functions.sendSystemMail(playerId, messageTitle, messageBody, Collections.emptyMap());
		}
		else
		{
			Functions.sendSystemMail(onlinePlayer, messageTitle, messageBody, Collections.emptyMap());
			onlinePlayer.sendPacket(HideBoard.PACKET);
		}
	}

	private static void sendMailSimilarTaskDoneAleady(int playerId)
	{
		final String messageTitle = StringHolder.getNotNull(playerId, "Facebook.SimilarTaskDoneAlready.Title");
		final String messageBody = StringHolder.getNotNull(playerId, "Facebook.SimilarTaskDoneAlready.Body");
		final Player onlinePlayer = GameObjectsStorage.getPlayer(playerId);
		if (onlinePlayer == null)
		{
			Functions.sendSystemMail(playerId, messageTitle, messageBody, Collections.emptyMap());
		}
		else
		{
			Functions.sendSystemMail(onlinePlayer, messageTitle, messageBody, Collections.emptyMap());
			onlinePlayer.sendPacket(HideBoard.PACKET);
		}
	}

	private static void attachFacebookId(int playerId, FacebookProfile profile)
	{
		final Player player = GameObjectsStorage.getPlayer(playerId);
		if (player == null)
		{
			CharacterDAO.setFacebookId(playerId, profile.getId());
		}
		else
		{
			player.setFacebookProfile(profile);
		}
	}

	public void addNewActiveTask(ActiveTask task)
	{
		_activeTasks.add(task);
	}

	public List<ActiveTask> getActiveTasksForIterate()
	{
		return _activeTasks;
	}

	public List<ActiveTask> getActiveTasksCopy(boolean concurrent)
	{
		return (concurrent ? new ArrayList<ActiveTask>(_activeTasks) : new CopyOnWriteArrayList<ActiveTask>(_activeTasks));
	}

	public ActiveTask getActiveTaskByPlayer(Player player)
	{
		for (ActiveTask task : _activeTasks)
		{
			if (task.getPlayerId() == player.getObjectId())
			{
				return task;
			}
		}
		return null;
	}

	public ActiveTask getActiveTaskByFacebookId(String facebookId)
	{
		for (ActiveTask task : _activeTasks)
		{
			if (task.getIdentityType() == FacebookIdentityType.ID && task.getIdentityValue().equals(facebookId))
			{
				return task;
			}
		}
		return null;
	}

	public ActiveTask getActiveTask(FacebookIdentityType identityType, String identityValue)
	{
		if (identityType == FacebookIdentityType.NAME_IN_COMMENT)
		{
			return null;
		}
		String identityToCompare = identityValue;
		if (identityType == FacebookIdentityType.NAME)
		{
			identityToCompare = identityToCompare.toLowerCase().replace(" ", "");
		}
		for (ActiveTask task : _activeTasks)
		{
			if (task.getIdentityType() == FacebookIdentityType.ID)
			{
				if (task.getIdentityValue().equals(identityToCompare))
				{
					return task;
				}
				continue;
			}
			else
			{
				if (task.getIdentityType() == FacebookIdentityType.NAME && task.getIdentityValue().toLowerCase().replace(" ", "").equals(identityToCompare))
				{
					return task;
				}
				continue;
			}
		}
		return null;
	}

	private static boolean isWantedAction(ActiveTask active, FacebookAction action)
	{
		if (active.getActionType() != action.getActionType())
		{
			return false;
		}
		switch (active.getIdentityType())
		{
		case NAME:
		{
			if (!compareFacebookNames(active.getIdentityValue(), action.getExecutor().getName()))
			{
				return false;
			}
			break;
		}
		case ID:
		{
			if (!active.getIdentityValue().equals(action.getExecutor().getId()))
			{
				return false;
			}
			break;
		}
		}
		return (active.getFather() != null || action.getFather() == null) && (active.getFather() == null || active.getFather().equals(action.getFather()));
	}

	private static boolean compareFacebookNames(String nameInAction, String nameWroteByPlayer)
	{
		return nameInAction.replace(" ", "").equalsIgnoreCase(nameWroteByPlayer.replace(" ", ""));
	}

	public void forceExpireTask(ActiveTask task)
	{
		onTaskExpired(task);
	}

	private void onTaskExpired(ActiveTask task)
	{
		final String title = StringHolder.getNotNull(task.getPlayerId(), "Facebook.TaskExpired.Title");
		final String body = StringHolder.getNotNull(task.getPlayerId(), "Facebook.TaskExpired.Body");
		Functions.sendSystemMail(task.getPlayerId(), title, body, Collections.emptyMap());
		_activeTasks.remove(task);
		Log.logFacebook("Task Expired: " + task);
	}

	private void checkForExpiredTasks()
	{
		final long currentDate = System.currentTimeMillis();
		for (ActiveTask task : _activeTasks)
		{
			if (task.getTimeLimitDate() < currentDate)
			{
				onTaskExpired(task);
			}
		}
	}

	@Override
	public String toString()
	{
		return "ActiveTasksHandler{activeTasks=" + _activeTasks + '}';
	}

	private enum AbortTaskReason
	{
		PROFILE_DONE_SIMILAR_TASK;
	}

	private static class TimeExpiredThread extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			ActiveTasksHandler.getInstance().checkForExpiredTasks();
		}
	}

	public static ActiveTasksHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		private static final ActiveTasksHandler INSTANCE = new ActiveTasksHandler();
	}
}
