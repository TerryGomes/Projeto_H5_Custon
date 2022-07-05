package l2f.gameserver.vote;

import l2f.commons.annotations.Nullable;

public class RuVote
{
	private final String nickname;
	private final String ip;
	private String hwid;
	private final int voteType;

	protected RuVote(String nickname, String ip, int voteType)
	{
		this.nickname = nickname;
		this.ip = ip;
		this.voteType = voteType;
	}

	public String getNickname()
	{
		return nickname;
	}

	public String getIp()
	{
		return ip;
	}

	public void setHwid(String hwid)
	{
		this.hwid = hwid;
	}

	@Nullable
	public String getHwid()
	{
		return hwid;
	}

	public int getVoteType()
	{
		return voteType;
	}

	public String getValue(RuVoteProtectionType type)
	{
		switch (type)
		{
		case IP:
		{
			return ip;
		}
		case PLAYER_NAME:
		{
			return nickname;
		}
		case HWID:
		{
			return hwid;
		}
		default:
		{
			return "";
		}
		}
	}
}
