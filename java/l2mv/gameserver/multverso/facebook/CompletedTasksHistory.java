package l2mv.gameserver.multverso.facebook;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2mv.commons.annotations.Nullable;

public final class CompletedTasksHistory
{
	private final Map<FacebookProfile, ArrayList<CompletedTask>> _completedTasks;

	private CompletedTasksHistory()
	{
		final ArrayList<CompletedTask> loadedCompletedTasks = FacebookDatabaseHandler.loadCompletedTasks();
		_completedTasks = new ConcurrentHashMap<FacebookProfile, ArrayList<CompletedTask>>();
		for (CompletedTask task : loadedCompletedTasks)
		{
			ArrayList<CompletedTask> tasksPerProfile = _completedTasks.get(task.getExecutor());
			if (tasksPerProfile == null)
			{
				tasksPerProfile = new ArrayList<CompletedTask>();
				tasksPerProfile.add(task);
				_completedTasks.put(task.getExecutor(), tasksPerProfile);
			}
			else
			{
				tasksPerProfile.add(task);
			}
		}
	}

	public void addCompletedTask(ActiveTask task, FacebookAction action, CompletedTask.CommentApprovalType commentApprovalType, boolean isRewarded, boolean saveNewDelay, boolean saveInDatabase)
	{
		final List<CompletedTask> existingTasks = _completedTasks.get(action.getExecutor());
		final CompletedTask completedTask = new CompletedTask(task, action, commentApprovalType, isRewarded);
		if (existingTasks != null)
		{
			existingTasks.add(completedTask);
		}
		else
		{
			final ArrayList<CompletedTask> newTasksList = new ArrayList<CompletedTask>(3);
			newTasksList.add(completedTask);
			_completedTasks.put(action.getExecutor(), newTasksList);
		}
		if (saveNewDelay)
		{
			action.getExecutor().setLastCompletedTaskDate(System.currentTimeMillis());
			if (saveInDatabase)
			{
				FacebookDatabaseHandler.replaceFacebookProfile(action.getExecutor());
			}
		}
		if (saveInDatabase)
		{
			FacebookDatabaseHandler.replaceCompletedTask(completedTask);
		}
	}

	public void removeCompletedTask(FacebookAction action, boolean removeFromDatabase)
	{
		final List<CompletedTask> existingTasks = _completedTasks.get(action.getExecutor());
		if (existingTasks != null)
		{
			for (CompletedTask existingTask : existingTasks)
			{
				if (existingTask.equals(action) || existingTask.getAction().equals(action))
				{
					existingTasks.remove(existingTask);
					if (removeFromDatabase)
					{
						FacebookDatabaseHandler.deleteCompletedTask(existingTask);
					}
				}
			}
		}
	}

	public ArrayList<CompletedTask> getCompletedTasksForIterate(FacebookProfile facebookProfile)
	{
		final ArrayList<CompletedTask> tasks = _completedTasks.get(facebookProfile);
		if (tasks == null)
		{
			return new ArrayList<CompletedTask>(0);
		}
		return tasks;
	}

	public ArrayList<CompletedTask> getCompletedTasksCopy(FacebookProfile facebookProfile)
	{
		final ArrayList<CompletedTask> tasks = _completedTasks.get(facebookProfile);
		if (tasks == null)
		{
			return new ArrayList<CompletedTask>(0);
		}
		return new ArrayList<CompletedTask>(tasks);
	}

	public CompletedTask getCompletedTask(FacebookAction action)
	{
		final ArrayList<CompletedTask> tasks = _completedTasks.get(action.getExecutor());
		if (tasks == null)
		{
			return null;
		}
		for (CompletedTask task : tasks)
		{
			if (task.equals(action) || task.getAction().equals(action))
			{
				return task;
			}
		}
		return null;
	}

	public CompletedTask getCompletedTask(String actionId)
	{
		for (ArrayList<CompletedTask> tasks : _completedTasks.values())
		{
			for (CompletedTask task : tasks)
			{
				if (actionId.equals(task.getId()))
				{
					return task;
				}
			}
		}
		return null;
	}

	public ArrayList<FacebookAction> getCompletedTasks(OfficialPost post, FacebookActionType actionType)
	{
		final ArrayList<FacebookAction> results = new ArrayList<FacebookAction>();
		if (post == null)
		{
			for (ArrayList<CompletedTask> actionsPerProfile : _completedTasks.values())
			{
				for (FacebookAction action : actionsPerProfile)
				{
					if (action.getActionType() == actionType && action.getFather() == null)
					{
						results.add(action);
					}
				}
			}
		}
		else
		{
			for (ArrayList<CompletedTask> actionsPerProfile : _completedTasks.values())
			{
				for (FacebookAction action : actionsPerProfile)
				{
					if (action.getActionType() == actionType && post.equals(action.getFather()))
					{
						results.add(action);
					}
				}
			}
		}
		return results;
	}

	public ArrayList<CompletedTask> getTasksThatNeedsApproval()
	{
		final ArrayList<CompletedTask> result = new ArrayList<CompletedTask>(10);
		for (ArrayList<CompletedTask> taskList : _completedTasks.values())
		{
			for (CompletedTask task : taskList)
			{
				if (task.getCommentApprovalType() == CompletedTask.CommentApprovalType.NOT_YET_CHECKED)
				{
					result.add(task);
				}
			}
		}
		return result;
	}

	public int countTasksThatNeedsApproval()
	{
		int count = 0;
		for (ArrayList<CompletedTask> taskList : _completedTasks.values())
		{
			for (CompletedTask task : taskList)
			{
				if (task.getCommentApprovalType() == CompletedTask.CommentApprovalType.NOT_YET_CHECKED)
				{
					++count;
				}
			}
		}
		return count;
	}

	public void setMessageChanged(FacebookAction existingAction, String newMessage, boolean messageGMs)
	{
		final CompletedTask task = this.getCompletedTask(existingAction);
		if (task != null)
		{
			task.changeMessage(newMessage);
			task.setCommentApproved(CompletedTask.CommentApprovalType.NOT_YET_CHECKED);
			FacebookDatabaseHandler.replaceCompletedTask(task);
			if (messageGMs)
			{
				ActiveTasksHandler.notifyGMsMsgsToVerify(false);
			}
		}
	}

	public EnumSet<FacebookActionType> getAvailableNegativeBalanceTypes(FacebookProfile facebookProfile)
	{
		final EnumSet<FacebookActionType> result = EnumSet.noneOf(FacebookActionType.class);
		for (FacebookActionType type : facebookProfile.getNegativePointTypesForIterate())
		{
			if (type.haveFather())
			{
				if (getInstance().getFatherAction(facebookProfile, type) == null)
				{
					continue;
				}
				result.add(type);
			}
			else
			{
				result.add(type);
			}
		}
		return result;
	}

	public EnumSet<FacebookActionType> getAvailableActionTypes(FacebookProfile facebookProfile)
	{
		final List<CompletedTask> facebookIdCompletedTasks = getCompletedTasksForIterate(facebookProfile);
		final EnumSet<FacebookActionType> notAvailableTaskTypes = EnumSet.allOf(FacebookActionType.class);
		for (OfficialPost activePost : OfficialPostsHolder.getInstance().getActivePostsForIterate())
		{
			for (FacebookActionType notAvailableTaskType : notAvailableTaskTypes)
			{
				if (activePost.isActionTypeRewarded(notAvailableTaskType) && getCompletedTask(facebookIdCompletedTasks, activePost, notAvailableTaskType) == null)
				{
					notAvailableTaskTypes.remove(notAvailableTaskType);
				}
			}
		}
		return EnumSet.complementOf(notAvailableTaskTypes);
	}

	public boolean isActionTypeAvailable(FacebookProfile facebookProfile, FacebookActionType type)
	{
		final List<CompletedTask> facebookIdCompletedTasks = getCompletedTasksForIterate(facebookProfile);
		for (OfficialPost activePost : OfficialPostsHolder.getInstance().getActivePostsForIterate())
		{
			if (activePost.isActionTypeRewarded(type) && getCompletedTask(facebookIdCompletedTasks, activePost, type) == null)
			{
				return true;
			}
		}
		return false;
	}

	public OfficialPost getFatherAction(@Nullable final FacebookProfile facebookTakingAction, FacebookActionType childActionType)
	{
		if (facebookTakingAction != null)
		{
			final List<CompletedTask> facebookIdCompletedTasks = getCompletedTasksForIterate(facebookTakingAction);
			for (OfficialPost activePost : OfficialPostsHolder.getInstance().getActivePostsForIterate())
			{
				if (activePost.isActionTypeRewarded(childActionType) && getCompletedTask(facebookIdCompletedTasks, activePost, childActionType) == null)
				{
					return activePost;
				}
			}
			return null;
		}
		if (OfficialPostsHolder.getInstance().getActivePostsForIterate().isEmpty())
		{
			return null;
		}
		return OfficialPostsHolder.getInstance().getActivePostsForIterate().get(0);
	}

	private static CompletedTask getCompletedTask(List<CompletedTask> tasks, OfficialPost father, FacebookActionType type)
	{
		for (CompletedTask task : tasks)
		{
			if (task.getActionType() == type && task.hasSameFather(father))
			{
				return task;
			}
		}
		return null;
	}

	public CompletedTask getCompletedTask(FacebookProfile profile, @Nullable final OfficialPost father, FacebookActionType type)
	{
		for (CompletedTask task : getCompletedTasksForIterate(profile))
		{
			if (task.getActionType() == type && task.hasSameFather(father))
			{
				return task;
			}
		}
		return null;
	}

	public CompletedTask getCompletedTask(FacebookIdentityType identityType, String identityValue, @Nullable final OfficialPost father, FacebookActionType type, boolean skipSpacesInIdentity)
	{
		if (identityType == FacebookIdentityType.NAME_IN_COMMENT)
		{
			return null;
		}
		for (Map.Entry<FacebookProfile, ArrayList<CompletedTask>> tasksPerProfile : _completedTasks.entrySet())
		{
			if (checkIdentityMatches(tasksPerProfile.getKey(), identityType, identityValue, skipSpacesInIdentity))
			{
				for (CompletedTask task : tasksPerProfile.getValue())
				{
					if (task.getActionType() == type && task.hasSameFather(father))
					{
						return task;
					}
				}
				return null;
			}
		}
		return null;
	}

	private static boolean checkIdentityMatches(FacebookProfile profile, FacebookIdentityType identityType, String identityValue, boolean skipSpacesInIdentity)
	{
		switch (identityType)
		{
		case NAME:
		{
			if (skipSpacesInIdentity)
			{
				return profile.getName().replace(" ", "").equalsIgnoreCase(identityValue.replace(" ", ""));
			}
			return profile.getName().equalsIgnoreCase(identityValue);
		}
		case ID:
		{
			return profile.getId().equals(identityValue);
		}
		case NONE:
		case NAME_IN_COMMENT:
		{
			return false;
		}
		default:
		{
			return false;
		}
		}
	}

	@Override
	public String toString()
	{
		return "CompletedTasksHistory{completedTasks=" + _completedTasks + '}';
	}

	public static CompletedTasksHistory getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		private static final CompletedTasksHistory INSTANCE = new CompletedTasksHistory();
	}
}
