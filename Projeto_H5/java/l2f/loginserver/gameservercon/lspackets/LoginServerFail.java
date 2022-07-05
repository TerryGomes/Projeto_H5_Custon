package l2f.loginserver.gameservercon.lspackets;

import l2f.loginserver.gameservercon.SendablePacket;

public class LoginServerFail extends SendablePacket
{
	public static final int REASON_IP_BANNED = 1;
	public static final int REASON_IP_RESERVED = 2;
	public static final int REASON_WRONG_HEXID = 3;
	public static final int REASON_ID_RESERVED = 4;
	public static final int REASON_NO_FREE_ID = 5;
	public static final int NOT_AUTHED = 6;
	public static final int REASON_ALREADY_LOGGED_IN = 7;
	private final int reason;

	public LoginServerFail(int reason)
	{
		this.reason = reason;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x01);
		writeC(reason);
	}
}