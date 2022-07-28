package l2mv.gameserver.network.loginservercon;

public enum ServerType
{
	NORMAL, RELAX, TEST, NO_LABEL, RESTRICTED, EVENT, FREE;

	private int _mask;

	ServerType()
	{
		this._mask = 1 << this.ordinal();
	}

	public int getMask()
	{
		return this._mask;
	}
}
