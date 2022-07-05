package l2f.gameserver.fandc.facebook;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import l2f.gameserver.ConfigHolder;
import l2f.gameserver.utils.Log;

public final class OfficialPostsHolder
{
	private final List<OfficialPost> recentOfficialPosts;
	private final List<OfficialPost> activePosts;
	private final Map<FacebookActionType, ArrayList<OfficialPost>> activePostsPerActionType;

	private OfficialPostsHolder()
	{
		recentOfficialPosts = new ArrayList<OfficialPost>();
		activePostsPerActionType = new EnumMap<FacebookActionType, ArrayList<OfficialPost>>(FacebookActionType.class);
		activePosts = new ArrayList<OfficialPost>(ConfigHolder.getInt("FacebookValidPostsCount"));
	}

	public ArrayList<OfficialPost> getActivePostsForIterate(FacebookActionType type)
	{
		ArrayList<OfficialPost> posts = activePostsPerActionType.getOrDefault(type, null);
		if (posts == null)
		{
			posts = calculatePostsPerActionType(type);
		}
		return posts;
	}

	public List<OfficialPost> getActivePostsCopy(FacebookActionType type)
	{
		List<OfficialPost> posts = activePostsPerActionType.getOrDefault(type, null);
		if (posts == null)
		{
			posts = calculatePostsPerActionType(type);
		}
		return new ArrayList<OfficialPost>(posts);
	}

	public List<OfficialPost> getRecentOfficialPostsForIterate()
	{
		return recentOfficialPosts;
	}

	public List<OfficialPost> getActivePostsForIterate()
	{
		return activePosts;
	}

	public void addNewActivePost(OfficialPost post, boolean justExtracted)
	{
		Log.logFacebook("Adding new Active Post: " + post + ". Just Extracted: " + justExtracted);
		if (post.isAnyActionTypeRewarded())
		{
			activePostsPerActionType.clear();
			activePosts.add(post);
			ActionsAwaitingOwner.getInstance().addNewFather(post);
		}
		if (justExtracted)
		{
			recentOfficialPosts.add(post);
		}
		if (justExtracted)
		{
			return;
		}
	}

	public OfficialPost getOfficialPost(String postId)
	{
		for (OfficialPost post : recentOfficialPosts)
		{
			if (post.getId().equals(postId))
			{
				return post;
			}
		}
		return null;
	}

	public void addNewRewardedAction(String officialPostId, FacebookActionType actionType)
	{
		final OfficialPost post = getOfficialPost(officialPostId);
		if (post != null)
		{
			this.addNewRewardedAction(post, actionType);
		}
	}

	public void addNewRewardedAction(OfficialPost post, FacebookActionType actionType)
	{
		if (!post.isActionTypeRewarded(actionType))
		{
			final boolean newActivePost = post.getRewardedActionsForIterate().isEmpty();
			post.getRewardedActionsForIterate().add(actionType);
			activePostsPerActionType.clear();
			ActionsAwaitingOwner.getInstance().addNewFather(post);
			if (newActivePost)
			{
				if (activePosts.size() >= ConfigHolder.getInt("FacebookValidPostsCount"))
				{
					final OfficialPost officialPost = activePosts.remove(0);
					activePosts.add(post);
				}
			}
			FacebookDatabaseHandler.replaceOfficialPost(post);
		}
	}

	public void removeNewRewardedAction(String officialPostId, FacebookActionType actionType)
	{
		final OfficialPost post = getOfficialPost(officialPostId);
		if (post != null)
		{
			removeRewardedAction(post, actionType);
		}
	}

	public void removeRewardedAction(OfficialPost post, FacebookActionType actionType)
	{
		post.getRewardedActionsForIterate().remove(actionType);
		activePostsPerActionType.clear();
		FacebookDatabaseHandler.replaceOfficialPost(post);
	}

	public int getMinimumPostsToExtract()
	{
		for (int i = recentOfficialPosts.size() - 1; i > ConfigHolder.getInt("FacebookValidPostsCount"); ++i)
		{
			if (activePosts.contains(recentOfficialPosts.get(i)))
			{
				return i;
			}
		}
		return ConfigHolder.getInt("FacebookValidPostsCount");
	}

	private ArrayList<OfficialPost> calculatePostsPerActionType(FacebookActionType type)
	{
		final ArrayList<OfficialPost> posts = new ArrayList<OfficialPost>(activePosts.size());
		for (OfficialPost post : activePosts)
		{
			if (post.isActionTypeRewarded(type))
			{
				posts.add(post);
			}
		}
		posts.trimToSize();
		activePostsPerActionType.put(type, posts);
		return posts;
	}

	public static OfficialPostsHolder getInstance()
	{
		return SingletonHolder.instance;
	}

	private static class SingletonHolder
	{
		private static final OfficialPostsHolder instance = new OfficialPostsHolder();
	}
}
