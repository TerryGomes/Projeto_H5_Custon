package l2f.gameserver.network.loginservercon.gspackets;

import l2f.gameserver.network.GameClient;
import l2f.gameserver.network.loginservercon.SendablePacket;

public class PlayerAuthRequest extends SendablePacket
{
	private final String account;
	private final int playOkID1;
	private final int playOkID2;
	private final int loginOkID1;
	private final int loginOkID2;

	public PlayerAuthRequest(GameClient client)
	{
		account = client.getLogin();
		playOkID1 = client.getSessionKey().playOkID1;
		playOkID2 = client.getSessionKey().playOkID2;
		loginOkID1 = client.getSessionKey().loginOkID1;
		loginOkID2 = client.getSessionKey().loginOkID2;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x02);
		writeS(account);
		writeD(playOkID1);
		writeD(playOkID2);
		writeD(loginOkID1);
		writeD(loginOkID2);
	}
}