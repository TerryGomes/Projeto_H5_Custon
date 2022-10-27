package l2mv.gameserver.network.serverpackets;

public class ExBR_BroadcastEventState extends L2GameServerPacket
{
	private int _eventId;
	private int _eventState;
	private int _param0;
	private int _param1;
	private int _param2;
	private int _param3;
	private int _param4;
	private String _param5;
	private String _param6;

	public static final int APRIL_FOOLS = 20090401;
	public static final int EVAS_INFERNO = 20090801; // event state (0 - hide, 1 - show), day (1-14), percent (0-100)
	public static final int HALLOWEEN_EVENT = 20091031; // event state (0 - hide, 1 - show)
	public static final int RAISING_RUDOLPH = 20091225; // event state (0 - hide, 1 - show)
	public static final int LOVERS_JUBILEE = 20100214; // event state (0 - hide, 1 - show)
	public static final int APRIL_FOOLS_10 = 20100401; // event state (0 - hide, 1 - show)

	public ExBR_BroadcastEventState(int eventId, int eventState)
	{
		this._eventId = eventId;
		this._eventState = eventState;
	}

	public ExBR_BroadcastEventState(int eventId, int eventState, int param0, int param1, int param2, int param3, int param4, String param5, String param6)
	{
		this._eventId = eventId;
		this._eventState = eventState;
		this._param0 = param0;
		this._param1 = param1;
		this._param2 = param2;
		this._param3 = param3;
		this._param4 = param4;
		this._param5 = param5;
		this._param6 = param6;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xBC);
		this.writeD(this._eventId);
		this.writeD(this._eventState);
		this.writeD(this._param0);
		this.writeD(this._param1);
		this.writeD(this._param2);
		this.writeD(this._param3);
		this.writeD(this._param4);
		this.writeS(this._param5);
		this.writeS(this._param6);
	}
}
