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
		_id = readD(); // id
		_memberSize = readD(); // member size
		_minLevel = readD(); // min level
		_maxLevel = readD(); // max level
		readD(); // lootType
		_topic = readS(); // topic
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		MatchingRoom room = player.getMatchingRoom();
		if (room == null || room.getId() != _id || room.getType() != MatchingRoom.CC_MATCHING || (room.getLeader() != player))
		{
			return;
		}

		room.setTopic(_topic);
		room.setMaxMemberSize(_memberSize);
		room.setMinLevel(_minLevel);
		room.setMaxLevel(_maxLevel);
		room.sendPacket(room.infoRoomPacket());

		player.sendPacket(SystemMsg.THE_COMMAND_CHANNEL_MATCHING_ROOM_INFORMATION_WAS_EDITED);
	}
}