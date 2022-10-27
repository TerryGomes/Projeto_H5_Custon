package l2mv.gameserver.network.serverpackets;

public class Snoop extends L2GameServerPacket
{
	private int _convoID;
	private String _name;
	private int _type;
	private String _speaker;
	private String _msg;

	public Snoop(int id, String name, int type, String speaker, String msg)
	{
		this._convoID = id;
		this._name = name;
		this._type = type;
		this._speaker = speaker;
		this._msg = msg;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xdb);

		this.writeD(this._convoID);
		this.writeS(this._name);
		this.writeD(0x00);
		this.writeD(this._type);
		this.writeS(this._speaker);
		this.writeS(this._msg);
	}
}