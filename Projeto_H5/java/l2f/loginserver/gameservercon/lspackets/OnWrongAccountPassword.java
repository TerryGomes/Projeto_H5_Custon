package l2f.loginserver.gameservercon.lspackets;

import l2f.loginserver.gameservercon.SendablePacket;

public class OnWrongAccountPassword extends SendablePacket
{
	private final String accountName;
	private final String wrotePassword;

	public OnWrongAccountPassword(String accountName, String wrotePassword)
	{
		super();
		this.accountName = accountName;
		this.wrotePassword = wrotePassword;
	}

	@Override
	protected void writeImpl()
	{
		writeC(7);
		writeS(accountName);
		writeS(wrotePassword);
	}
}
