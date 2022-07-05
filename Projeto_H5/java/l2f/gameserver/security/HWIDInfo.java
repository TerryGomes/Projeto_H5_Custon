package l2f.gameserver.security;

public class HWIDInfo
{
	private final int _id;
	private String HWID;
	private int playerID;
	private String login;

	public HWIDInfo(int id)
	{
		_id = id;
	}

	public int get_id()
	{
		return _id;
	}

	public void setHwids(String hwid)
	{
		HWID = hwid;
	}

	public String getHWID()
	{
		return HWID;
	}

	public void setHWID(String HWID)
	{
		this.HWID = HWID;
	}

	public int getPlayerID()
	{
		return playerID;
	}

	public void setPlayerID(int playerID)
	{
		this.playerID = playerID;
	}

	public String getLogin()
	{
		return login;
	}

	public void setLogin(String login)
	{
		this.login = login;
	}
}