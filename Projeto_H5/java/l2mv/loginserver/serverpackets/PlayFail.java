package l2mv.loginserver.serverpackets;

public final class PlayFail extends L2LoginServerPacket
{
	public static final int REASON_SYSTEM_ERROR = 1; // This Account is already in use. Access denied.
	public static final int REASON_ACCESS_FAILED_1 = 2; // Access failed. Please try again later.
	public static final int REASON_ACCOUNT_INFO_INCORRECT = 3; // Your account information is incorrect. For more details please contact our Support Center at http://support.plaync.com
	public static final int REASON_PASSWORD_INCORRECT_1 = 4; // Password does not match this account. Confirm your account information and log in again later.
	public static final int REASON_PASSWORD_INCORRECT_2 = 5; // Password does not match this account. Confirm your account information and log in again later.
	public static final int REASON_NO_REASON = 6;
	public static final int REASON_SYS_ERROR = 7; // System error, please log in again later.
	public static final int REASON_ACCESS_FAILED_2 = 8; // Access failed. Please try again later.
	public static final int REASON_HIGH_SERVER_TRAFFIC = 9; // Due to high server traffic, your login attempt has failed. Please try again soon.
	public static final int REASON_MIN_AGE = 10; // Lineage II game service may used by individuals 15 years of age or older except for PvP server, which may only be used by adults 18
													// years of age and older. (Korea Only)

	private final int _reason;

	public PlayFail(int reason)
	{
		_reason = reason;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x06);
		writeC(_reason);
	}
}
