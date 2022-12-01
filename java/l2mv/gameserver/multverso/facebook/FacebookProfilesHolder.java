package l2mv.gameserver.multverso.facebook;

import java.util.concurrent.CopyOnWriteArrayList;

import l2mv.commons.annotations.Nullable;

public class FacebookProfilesHolder
{
	private final CopyOnWriteArrayList<FacebookProfile> _profiles;

	private FacebookProfilesHolder()
	{
		_profiles = new CopyOnWriteArrayList<FacebookProfile>(FacebookDatabaseHandler.loadFacebookProfiles());
	}

	public FacebookProfile loadOrCreateProfile(String facebookId, String facebookName)
	{
		final FacebookProfile loadedProfile = getProfileById(facebookId);
		if (loadedProfile != null)
		{
			return loadedProfile;
		}
		final FacebookProfile createdProfile = new FacebookProfile(facebookId, facebookName);
		addNewProfile(createdProfile, true);
		return createdProfile;
	}

	public void addNewProfile(FacebookProfile profile, boolean saveInDatabase)
	{
		_profiles.add(profile);
		if (saveInDatabase)
		{
			FacebookDatabaseHandler.replaceFacebookProfile(profile);
		}
	}

	@Nullable
	public FacebookProfile getProfileById(@Nullable final String facebookId)
	{
		if (facebookId == null || facebookId.isEmpty())
		{
			return null;
		}
		for (FacebookProfile profile : _profiles)
		{
			if (profile.getId().equals(facebookId))
			{
				return profile;
			}
		}
		return null;
	}

	@Nullable
	public FacebookProfile getProfileByName(@Nullable final String facebookName, boolean ignoreSpaces, boolean ignoreCase)
	{
		if (facebookName == null || facebookName.isEmpty())
		{
			return null;
		}
		String nameToCompare = facebookName;
		if (ignoreSpaces)
		{
			nameToCompare = nameToCompare.replace(" ", "");
		}
		if (ignoreCase)
		{
			nameToCompare = nameToCompare.toLowerCase();
		}
		for (FacebookProfile profile : _profiles)
		{
			String profileNameToCompare = profile.getName();
			if (ignoreSpaces)
			{
				profileNameToCompare = profileNameToCompare.replace(" ", "");
			}
			if (ignoreCase)
			{
				profileNameToCompare = profileNameToCompare.toLowerCase();
			}
			if (profileNameToCompare.equals(nameToCompare))
			{
				return profile;
			}
		}
		return null;
	}

	@Override
	public String toString()
	{
		return "FacebookProfilesHolder{profiles=" + _profiles + '}';
	}

	public static FacebookProfilesHolder getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		private static final FacebookProfilesHolder INSTANCE = new FacebookProfilesHolder();
	}
}
