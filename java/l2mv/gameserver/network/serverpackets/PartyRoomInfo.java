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
		this._id = room.getId();
		this._minLevel = room.getMinLevel();
		this._maxLevel = room.getMaxLevel();
		this._lootDist = room.getLootType();
		this._maxMembers = room.getMaxMembersSize();
		this._location = room.getLocationId();
		this._title = room.getTopic();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x9d);
		this.writeD(this._id); // room id
		this.writeD(this._maxMembers); // max members
		this.writeD(this._minLevel); // min level
		this.writeD(this._maxLevel); // max level
		this.writeD(this._lootDist); // loot distribution 1-Random 2-Random includ. etc
		this.writeD(this._location); // location
		this.writeS(this._title); // room name
	}
}