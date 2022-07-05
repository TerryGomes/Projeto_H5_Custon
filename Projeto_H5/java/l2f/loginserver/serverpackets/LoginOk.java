package l2f.loginserver.serverpackets;

import l2f.loginserver.SessionKey;

public final class LoginOk extends L2LoginServerPacket
{
	private int _loginOk1, _loginOk2;

	public LoginOk(SessionKey sessionKey)
	{
		_loginOk1 = sessionKey.loginOkID1;
		_loginOk2 = sessionKey.loginOkID2;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x03);
		writeD(_loginOk1);
		writeD(_loginOk2);
		writeD(0x00);
		writeD(0x00);
		writeD(0x000003ea);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		writeB(new byte[16]);
	}
}
