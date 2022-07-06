package l2mv.gameserver.vote;

import l2mv.gameserver.ConfigHolder;

public enum RuVoteProtectionType
{
	IP("RuVoteIPCheck"), PLAYER_NAME((String) null), HWID("RuVoteHwidCheck");

	private final String enableConfigName;

	private RuVoteProtectionType(String enableConfigName)
	{
		this.enableConfigName = enableConfigName;
	}

	public boolean isEnabled()
	{
		return enableConfigName == null || ConfigHolder.getBool(enableConfigName);
	}
}
