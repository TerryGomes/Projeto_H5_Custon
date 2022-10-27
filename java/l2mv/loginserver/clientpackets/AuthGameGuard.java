package l2mv.loginserver.clientpackets;

import l2mv.loginserver.L2LoginClient;
import l2mv.loginserver.L2LoginClient.LoginClientState;
import l2mv.loginserver.serverpackets.GGAuth;
import l2mv.loginserver.serverpackets.LoginFail;

/**
 * @author -Wooden-
 * Format: ddddd
 *
 */
public class AuthGameGuard extends L2LoginClientPacket
{
	private int _sessionId;
	@SuppressWarnings("unused")
	private int _data1;
	@SuppressWarnings("unused")
	private int _data2;
	@SuppressWarnings("unused")
	private int _data3;
	@SuppressWarnings("unused")
	private int _data4;

	@Override
	protected void readImpl()
	{
		_sessionId = readD();
		_data1 = readD();
		_data2 = readD();
		_data3 = readD();
		_data4 = readD();
	}

	@Override
	protected void runImpl()
	{
		L2LoginClient client = getClient();

		if (_sessionId == 0 || _sessionId == client.getSessionId())
		{
			client.setState(LoginClientState.AUTHED_GG);
			client.sendPacket(new GGAuth(client.getSessionId()));
		}
		else
		{
			client.close(LoginFail.LoginFailReason.REASON_ACCESS_FAILED);
		}
	}
}
