package handler.facebookaction;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.fandc.facebook.ActionsExtractingManager;
import l2mv.gameserver.fandc.facebook.ActionsExtractor;
import l2mv.gameserver.fandc.facebook.FacebookDatabaseHandler;
import l2mv.gameserver.fandc.facebook.FacebookProfile;
import l2mv.gameserver.fandc.facebook.FacebookProfilesHolder;
import l2mv.gameserver.fandc.facebook.OfficialPost;
import l2mv.gameserver.fandc.facebook.OfficialPostsHolder;
import l2mv.gameserver.scripts.ScriptFile;

public class ExtractOfficialPosts implements ScriptFile, ActionsExtractor
{
	private static final Logger LOG = LoggerFactory.getLogger(ExtractOfficialPosts.class);

	@Override
	public void onLoad()
	{
		ActionsExtractingManager.getInstance().addExtractor(this, true);
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
		final List<OfficialPost> recentPosts = OfficialPostsHolder.getInstance().getRecentOfficialPostsForIterate();
		final URL apiCallURL = prepareAPICall(token);
		final JSONObject extractionResult = call(apiCallURL);
		final JSONArray data = extractionResult.getJSONArray("data");
		for (int i = 0; i < data.length(); ++i)
		{
			final JSONObject postData = data.getJSONObject(i);
			try
			{
				final String postId = postData.getString("id");
				final Optional<OfficialPost> activePost = recentPosts.stream().filter(iPost -> iPost.getId().equals(postId)).findAny();
				if (!activePost.isPresent())
				{
					final String message = postData.getString("message");
					final long createdTime = parseFacebookDate(postData.getString("created_time"));
					final JSONObject from = postData.getJSONObject("from");
					final String executorName = from.getString("name");
					final String executorId = from.getString("id");
					final FacebookProfile profile = FacebookProfilesHolder.getInstance().loadOrCreateProfile(executorId, executorName);
					final long extractionDate = System.currentTimeMillis();
					final OfficialPost post = new OfficialPost(postId, profile, message, createdTime, extractionDate);
					FacebookDatabaseHandler.loadOfficialPostData(post);
					OfficialPostsHolder.getInstance().addNewActivePost(post, true);
				}
			}
			catch (ParseException e)
			{
				LOG.error("Error while parsing created_time of " + postData, e);
			}
		}
	}

	private static URL prepareAPICall(String token) throws MalformedURLException
	{
		final int limit = OfficialPostsHolder.getInstance().getMinimumPostsToExtract();
		return new URL("https://graph.facebook.com/me/posts?fields=id,message,created_time,from&limit=" + limit + "&access_token=" + token);
	}
}
