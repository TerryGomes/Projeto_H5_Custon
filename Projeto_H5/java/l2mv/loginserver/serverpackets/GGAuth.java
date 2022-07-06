package l2mv.loginserver.serverpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GGAuth extends L2LoginServerPacket
{
	static Logger _log = LoggerFactory.getLogger(GGAuth.class);
	public static int SKIP_GG_AUTH_REQUEST = 0x0b;

	private final int _response;

	public GGAuth(int response)
	{
		_response = response;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x0b);
		writeD(_response);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
	}
}
