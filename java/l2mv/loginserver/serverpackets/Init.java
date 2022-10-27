package l2mv.loginserver.serverpackets;

import l2mv.loginserver.L2LoginClient;

public final class Init extends L2LoginServerPacket
{
	private int _sessionId;

	private byte[] _publicKey;
	private byte[] _blowfishKey;

	public Init(L2LoginClient client)
	{
		this(client.getScrambledModulus(), client.getBlowfishKey(), client.getSessionId());
	}

	public Init(byte[] publickey, byte[] blowfishkey, int sessionId)
	{
		_sessionId = sessionId;
		_publicKey = publickey;
		_blowfishKey = blowfishkey;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x00); // init packet id

		writeD(_sessionId); // session id
		writeD(0x0000c621); // protocol revision

		writeB(_publicKey); // RSA Public Key

		// unk GG related?
		writeD(0x29DD954E);
		writeD(0x77C39CFC);
		writeD(0x97ADB620);
		writeD(0x07BDE0F7);

		writeB(_blowfishKey); // BlowFish key
		writeC(0x00); // null termination ;)
	}
}