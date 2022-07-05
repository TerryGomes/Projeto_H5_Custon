package handler.facebookaction;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.fandc.facebook.ActionsAwaitingOwner;
import l2f.gameserver.fandc.facebook.ActionsExtractingManager;
import l2f.gameserver.fandc.facebook.ActionsExtractor;
import l2f.gameserver.fandc.facebook.CompletedTasksHistory;
import l2f.gameserver.fandc.facebook.FacebookAction;
import l2f.gameserver.fandc.facebook.FacebookActionType;
import l2f.gameserver.fandc.facebook.FacebookProfile;
import l2f.gameserver.fandc.facebook.FacebookProfilesHolder;
import l2f.gameserver.fandc.facebook.OfficialPost;
import l2f.gameserver.fandc.facebook.OfficialPostsHolder;
import l2f.gameserver.fandc.facebook.action.Comment;
import l2f.commons.annotations.Nullable;
import l2f.gameserver.scripts.ScriptFile;

public class ExtractComments implements ScriptFile, ActionsExtractor
{
	private static final Logger LOG = LoggerFactory.getLogger(ExtractComments.class);

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
		final List<OfficialPost> activePosts = OfficialPostsHolder.getInstance().getActivePostsForIterate(FacebookActionType.COMMENT);
		for (OfficialPost activePost : activePosts)
		{
			final URL apiCallURL = prepareAPICall(activePost.getId(), token);
			final JSONObject extractionResult = call(apiCallURL);
			final JSONArray comments = extractionResult.getJSONArray("data");
			final ArrayList<FacebookAction> finishedActions = CompletedTasksHistory.getInstance().getCompletedTasks(activePost, FacebookActionType.COMMENT);
			final ArrayList<FacebookAction> awaitingActions = ActionsAwaitingOwner.getInstance().getActionsCopy(activePost, FacebookActionType.COMMENT);
			for (int x = 0; x < comments.length(); ++x)
			{
				final JSONObject commentData = comments.getJSONObject(x);
				final String id = commentData.getString("id");
				FacebookAction existingAction = findExistingAction(finishedActions, id);
				boolean isCompletedTask;
				if (existingAction == null)
				{
					existingAction = findExistingAction(awaitingActions, id);
					if (existingAction != null)
					{
						awaitingActions.remove(existingAction);
					}
					isCompletedTask = false;
				}
				else
				{
					finishedActions.remove(existingAction);
					isCompletedTask = true;
				}
				if (existingAction == null)
				{
					try
					{
						if (!commentData.isNull("from"))
						{
							final String message = commentData.getString("message");
							final long createdTime = parseFacebookDate(commentData.getString("created_time"));
							final JSONObject from = commentData.getJSONObject("from");
							final String executorName = from.getString("name");
							final String executorId = from.getString("id");
							final FacebookProfile profile = FacebookProfilesHolder.getInstance().loadOrCreateProfile(executorId, executorName);
							if (currentTime < 0L)
							{
								currentTime = System.currentTimeMillis();
							}
							ActionsExtractingManager.getInstance().onActionExtracted(new Comment(id, profile, message, createdTime, currentTime, activePost));
						}
					}
					catch (ParseException e)
					{
						ExtractComments.LOG.error("Error while parsing created_time of " + commentData, e);
					}
				}
				else if (!existingAction.getMessage().equals(commentData.getString("message")) && isCompletedTask)
				{
					CompletedTasksHistory.getInstance().setMessageChanged(existingAction, commentData.getString("message"), true);
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
	private static FacebookAction findExistingAction(Iterable<FacebookAction> list, String commentId)
	{
		for (FacebookAction action : list)
		{
			if (action.getId().equals(commentId))
			{
				return action;
			}
		}
		return null;
	}

	private static URL prepareAPICall(String postId, String token) throws MalformedURLException
	{
		return new URL("https://graph.facebook.com/" + postId + "/comments?fields=id,message,created_time,from&limit=1000&access_token=" + token);
	}
}
