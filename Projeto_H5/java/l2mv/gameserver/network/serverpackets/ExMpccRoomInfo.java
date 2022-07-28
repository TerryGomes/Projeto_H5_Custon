package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.matching.MatchingRoom;

public class ExMpccRoomInfo extends L2GameServerPacket
{
	private int _index;
	private int _memberSize;
	private int _minLevel;
	private int _maxLevel;
	private int _lootType;
	private int _locationId;
	private String _topic;

	public ExMpccRoomInfo(MatchingRoom matching)
	{
		this._index = matching.getId();
		this._locationId = matching.getLocationId();
		this._topic = matching.getTopic();
		this._minLevel = matching.getMinLevel();
		this._maxLevel = matching.getMaxLevel();
		this._memberSize = matching.getMaxMembersSize();
		this._lootType = matching.getLootType();
	}

	@Override
	public void writeImpl()
	{
		this.writeEx(0x9B);
		//
		this.writeD(this._index); // index
		this.writeD(this._memberSize); // member size 1-50
		this.writeD(this._minLevel); // min level
		this.writeD(this._maxLevel); // max level
		this.writeD(this._lootType); // loot type
		this.writeD(this._locationId); // location id as party room
		this.writeS(this._topic); // topic
	}
}