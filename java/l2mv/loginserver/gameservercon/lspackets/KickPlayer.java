package l2mv.loginserver.gameservercon.lspackets;

import l2mv.loginserver.gameservercon.SendablePacket;

public class KickPlayer extends SendablePacket
{
	private final String account;

	public KickPlayer(String login)
	{
		account = login;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x03);
		writeS(account);
	}
}