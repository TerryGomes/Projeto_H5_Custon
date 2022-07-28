package l2mv.gameserver.network.loginservercon.gspackets;

import l2mv.gameserver.network.GameClient;
import l2mv.gameserver.network.loginservercon.SendablePacket;

public class PlayerAuthRequest extends SendablePacket
{
	private final String account;
	private final int playOkID1;
	private final int playOkID2;
	private final int loginOkID1;
	private final int loginOkID2;

	public PlayerAuthRequest(GameClient client)
	{
		this.account = client.getLogin();
		this.playOkID1 = client.getSessionKey().playOkID1;
		this.playOkID2 = client.getSessionKey().playOkID2;
		this.loginOkID1 = client.getSessionKey().loginOkID1;
		this.loginOkID2 = client.getSessionKey().loginOkID2;
	}

	@Override
	protected void writeImpl()
	{
		this.writeC(0x02);
		this.writeS(this.account);
		this.writeD(this.playOkID1);
		this.writeD(this.playOkID2);
		this.writeD(this.loginOkID1);
		this.writeD(this.loginOkID2);
	}
}