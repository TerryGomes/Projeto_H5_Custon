package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.matching.MatchingRoom;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

/**
 * @author VISTALL
 */
public class RequestExManageMpccRoom extends L2GameClientPacket
{
	private int _id;
	private int _memberSize;
	private int _minLevel;
	private int _maxLevel;
	private String _topic;

	@Override
	protected void readImpl()
	{
		this._id = this.readD(); // id
		this._memberSize = this.readD(); // member size
		this._minLevel = this.readD(); // min level
		this._maxLevel = this.readD(); // max level
		this.readD(); // lootType
		this._topic = this.readS(); // topic
	}

	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		MatchingRoom room = player.getMatchingRoom();
		if (room == null || room.getId() != this._id || room.getType() != MatchingRoom.CC_MATCHING || (room.getLeader() != player))
		{
			return;
		}

		room.setTopic(this._topic);
		room.setMaxMemberSize(this._memberSize);
		room.setMinLevel(this._minLevel);
		room.setMaxLevel(this._maxLevel);
		room.sendPacket(room.infoRoomPacket());

		player.sendPacket(SystemMsg.THE_COMMAND_CHANNEL_MATCHING_ROOM_INFORMATION_WAS_EDITED);
	}
}