package l2mv.gameserver.kara.vote;

public class VoteStatus
{
	Site site;
	long lastReward;

	public VoteStatus(Site site, long lastReward)
	{
		this.site = site;
		this.lastReward = lastReward;
	}

	public Site getSite()
	{
		return site;
	}

	public long getLastReward()
	{
		return lastReward;
	}
}
