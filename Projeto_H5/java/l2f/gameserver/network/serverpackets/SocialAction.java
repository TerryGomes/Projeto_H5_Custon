package l2f.gameserver.network.serverpackets;

public class SocialAction extends L2GameServerPacket
{
	private int _playerId;
	private int _actionId;

	// Это для фрея.
	public static final int GREETING = 2;
	public static final int VICTORY = 3;
	public static final int ADVANCE = 4;
	public static final int NO = 5;
	public static final int YES = 6;
	public static final int BOW = 7;
	public static final int UNAWARE = 8;
	public static final int WAITING = 9;
	public static final int LAUGH = 10;
	public static final int APPLAUD = 11;
	public static final int DANCE = 12;
	public static final int SORROW = 13;
	public static final int CHARM = 14;
	public static final int SHYNESS = 15;
	public static final int COUPLE_BOW = 16;
	public static final int COUPLE_HIGH_FIVE = 17;
	public static final int COUPLE_DANCE = 18;
	public static final int GIVE_HERO = 20016;
	public static final int CURSED_WEAPON_LEVEL_UP = 20017;
	public static final int LEVEL_UP = 2122;

	public SocialAction(int playerId, int actionId)
	{
		_playerId = playerId;
		_actionId = actionId;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x27);
		writeD(_playerId);
		writeD(_actionId);
	}
}