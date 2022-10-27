package l2mv.gameserver.network.serverpackets;

/**
 * @author VISTALL
 * @date 23:13/21.03.2011
 */
public class ExConfirmAddingPostFriend extends L2GameServerPacket
{
	public static int NAME_IS_NOT_EXISTS = 0;
	public static int SUCCESS = 1;
	public static int PREVIOS_NAME_IS_BEEN_REGISTERED = -1; // The previous name is being registered. Please try again later.
	public static int NAME_IS_NOT_EXISTS2 = -2;
	public static int LIST_IS_FULL = -3;
	public static int ALREADY_ADDED = -4;
	public static int NAME_IS_NOT_REGISTERED = -4;

	private String _name;
	private int _result;

	public ExConfirmAddingPostFriend(String name, int s)
	{
		this._name = name;
		this._result = s;
	}

	@Override
	public void writeImpl()
	{
		this.writeEx(0xD2);
		this.writeS(this._name);
		this.writeD(this._result);
	}
}
