package l2mv.gameserver.model.pledge;

public class ClanChangeLeaderRequest
{
	private int _clanId;
	private int _newLeaderId;
	private long _time;

	public ClanChangeLeaderRequest(int clanId, int newLeaderId, long time)
	{
		_clanId = clanId;
		_newLeaderId = newLeaderId;
		_time = time;
	}

	public int getClanId()
	{
		return _clanId;
	}

	public int getNewLeaderId()
	{
		return _newLeaderId;
	}

	public long getTime()
	{
		return _time;
	}
}
