package l2mv.gameserver.network.serverpackets;

public class SpecialCamera extends L2GameServerPacket
{
	private int _id;
	private int _dist;
	private int _yaw;
	private int _pitch;
	private int _time;
	private int _duration;
	private final int _turn;
	private final int _rise;
	private final int _widescreen;
	private final int _unknown;

	public SpecialCamera(int id, int dist, int yaw, int pitch, int time, int duration)
	{
		this._id = id;
		this._dist = dist;
		this._yaw = yaw;
		this._pitch = pitch;
		this._time = time;
		this._duration = duration;
		this._turn = 0;
		this._rise = 0;
		this._widescreen = 0;
		this._unknown = 0;
	}

	public SpecialCamera(int id, int dist, int yaw, int pitch, int time, int duration, int turn, int rise, int widescreen, int unk)
	{
		this._id = id;
		this._dist = dist;
		this._yaw = yaw;
		this._pitch = pitch;
		this._time = time;
		this._duration = duration;
		this._turn = turn;
		this._rise = rise;
		this._widescreen = widescreen;
		this._unknown = unk;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xd6);
		// ddddddddddd
		this.writeD(this._id); // object id
		this.writeD(this._dist); // расстояние до объекта
		this.writeD(this._yaw); // North=90, south=270, east=0, west=180
		this.writeD(this._pitch); // > 0:looks up,pitch < 0:looks down (угол наклона)
		this.writeD(this._time); // faster that small value is
		this.writeD(this._duration); // время анимации
		this.writeD(this._turn);
		this.writeD(this._rise);
		this.writeD(this._widescreen);
		this.writeD(this._unknown);
	}
}