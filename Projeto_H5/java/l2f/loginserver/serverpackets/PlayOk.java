package l2f.loginserver.serverpackets;

import l2f.loginserver.SessionKey;

public final class PlayOk extends L2LoginServerPacket
{
	private int _playOk1, _playOk2;

	public PlayOk(SessionKey sessionKey)
	{
		_playOk1 = sessionKey.playOkID1;
		_playOk2 = sessionKey.playOkID2;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x07);
		writeD(_playOk1);
		writeD(_playOk2);
	}
}
