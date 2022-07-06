package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.utils.Location;

public class PlaySound extends L2GameServerPacket
{
	public static final L2GameServerPacket SIEGE_VICTORY = new PlaySound("Siege_Victory");
	public static final L2GameServerPacket B04_S01 = new PlaySound("B04_S01");
	public static final L2GameServerPacket HB01 = new PlaySound(PlaySound.Type.MUSIC, "HB01", 0, 0, 0, 0, 0);

	public enum Type
	{
		SOUND, MUSIC, VOICE
	}

	private Type _type;
	private String _soundFile;
	private int _hasCenterObject;
	private int _objectId;
	private int _x, _y, _z;

	public PlaySound(String soundFile)
	{
		this(Type.SOUND, soundFile, 0, 0, 0, 0, 0);
	}

	public PlaySound(Type type, String soundFile, int c, int objectId, Location loc)
	{
		this(type, soundFile, c, objectId, loc == null ? 0 : loc.x, loc == null ? 0 : loc.y, loc == null ? 0 : loc.z);
	}

	public PlaySound(Type type, String soundFile, int c, int objectId, int x, int y, int z)
	{
		_type = type;
		_soundFile = soundFile;
		_hasCenterObject = c;
		_objectId = objectId;
		_x = x;
		_y = y;
		_z = z;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x9e);
		// dSdddddd
		writeD(_type.ordinal()); // 0 for quest and ship, c4 toturial = 2
		writeS(_soundFile);
		writeD(_hasCenterObject); // 0 for quest; 1 for ship;
		writeD(_objectId); // 0 for quest; objectId of ship
		writeD(_x); // x
		writeD(_y); // y
		writeD(_z); // z
	}
}