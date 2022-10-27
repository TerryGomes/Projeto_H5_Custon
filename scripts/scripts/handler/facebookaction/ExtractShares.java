package handler.facebookaction;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import l2mv.commons.annotations.Nullable;
import l2mv.gameserver.fandc.facebook.ActionsAwaitingOwner;
import l2mv.gameserver.fandc.facebook.ActionsExtractingManager;
import l2mv.gameserver.fandc.facebook.ActionsExtractor;
import l2mv.gameserver.fandc.facebook.CompletedTasksHistory;
import l2mv.gameserver.fandc.facebook.FacebookAction;
import l2mv.gameserver.fandc.facebook.FacebookActionType;
import l2mv.gameserver.fandc.facebook.FacebookProfile;
import l2mv.gameserver.fandc.facebook.FacebookProfilesHolder;
import l2mv.gameserver.fandc.facebook.OfficialPost;
import l2mv.gameserver.fandc.facebook.OfficialPostsHolder;
import l2mv.gameserver.fandc.facebook.action.Share;
import l2mv.gameserver.scripts.ScriptFile;

public class ExtractShares implements ScriptFile, ActionsExtractor
{
	@Override
	public void onLoad()
	{
		ActionsExtractingManager.getInstance().addExtractor(this);
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}

	@Override
	public void extractData(String token) throws IOException
	{
		long currentTime = -1L;
		final List<OfficialPost> activePosts = OfficialPostsHolder.getInstance().getActivePostsForIterate(FacebookActionType.SHARE);
		for (OfficialPost activePost : activePosts)
		{
			final URL apiCallURL = prepareAPICall(activePost.getId(), token);
			final JSONObject extractionResult = call(apiCallURL);
			final JSONArray share = extractionResult.getJSONArray("data");
			final ArrayList<FacebookAction> finishedActions = CompletedTasksHistory.getInstance().getCompletedTasks(activePost, FacebookActionType.SHARE);
			final ArrayList<FacebookAction> awaitingActions = ActionsAwaitingOwner.getInstance().getActionsCopy(activePost, FacebookActionType.SHARE);
			for (int i = 0; i < share.length(); ++i)
			{
				final JSONObject shareData = share.getJSONObject(i);
				final String executorId = shareData.getString("id");
				FacebookAction existingAction = findExistingAction(finishedActions, executorId);
				if (existingAction == null)
				{
					existingAction = findExistingAction(awaitingActions, executorId);
					if (existingAction != null)
					{
						awaitingActions.remove(existingAction);
					}
				}
				else
				{
					finishedActions.remove(existingAction);
				}
				if (existingAction == null)
				{
					if (currentTime < 0L)
					{
						currentTime = System.currentTimeMillis();
					}
					final FacebookProfile executor = FacebookProfilesHolder.getInstance().loadOrCreateProfile(executorId, shareData.getString("name"));
					ActionsExtractingManager.getInstance().onActionExtracted(new Share(executor, currentTime, activePost));
				}
			}
			for (FacebookAction removedAction : finishedActions)
			{
				ActionsExtractingManager.onActionDisappeared(removedAction, true);
			}
			for (FacebookAction removedAction : awaitingActions)
			{
				ActionsExtractingManager.onActionDisappeared(removedAction, false);
			}
		}
	}

	@Nullable
	private static FacebookAction findExistingAction(Iterable<FacebookAction> list, String executorId)
	{
		for (FacebookAction action : list)
		{
			if (action.getExecutor().getId().equals(executorId))
			{
				return action;
			}
		}
		return null;
	}

	private static URL prepareAPICall(String postId, String token) throws MalformedURLException
	{
		return new URL("https://graph.facebook.com/" + postId + "/sharedposts?fields=name,id&limit=1000&access_token=" + token);
	}
}
