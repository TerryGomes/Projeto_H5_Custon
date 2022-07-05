package l2f.gameserver.network.serverpackets;

public class Snoop extends L2GameServerPacket
{
	private int _convoID;
	private String _name;
	private int _type;
	private String _speaker;
	private String _msg;

	public Snoop(int id, String name, int type, String speaker, String msg)
	{
		_convoID = id;
		_name = name;
		_type = type;
		_speaker = speaker;
		_msg = msg;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xdb);

		writeD(_convoID);
		writeS(_name);
		writeD(0x00);
		writeD(_type);
		writeS(_speaker);
		writeS(_msg);
	}
}