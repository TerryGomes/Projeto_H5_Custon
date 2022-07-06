package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.matching.MatchingRoom;

public class PartyRoomInfo extends L2GameServerPacket
{
	private int _id;
	private int _minLevel;
	private int _maxLevel;
	private int _lootDist;
	private int _maxMembers;
	private int _location;
	private String _title;

	public PartyRoomInfo(MatchingRoom room)
	{
		_id = room.getId();
		_minLevel = room.getMinLevel();
		_maxLevel = room.getMaxLevel();
		_lootDist = room.getLootType();
		_maxMembers = room.getMaxMembersSize();
		_location = room.getLocationId();
		_title = room.getTopic();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x9d);
		writeD(_id); // room id
		writeD(_maxMembers); // max members
		writeD(_minLevel); // min level
		writeD(_maxLevel); // max level
		writeD(_lootDist); // loot distribution 1-Random 2-Random includ. etc
		writeD(_location); // location
		writeS(_title); // room name
	}
}