package l2f.gameserver.network.clientpackets;

import l2f.gameserver.model.Party;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.matching.MatchingRoom;
import l2f.gameserver.model.matching.PartyMatchingRoom;

public class RequestPartyMatchList extends L2GameClientPacket
{
	private int _lootDist;
	private int _maxMembers;
	private int _minLevel;
	private int _maxLevel;
	private int _roomId;
	private String _roomTitle;

	/**
	 * Format:(ch) dddddS
	 */
	@Override
	protected void readImpl()
	{
		_roomId = readD();
		_maxMembers = readD();
		_minLevel = readD();
		_maxLevel = readD();
		_lootDist = readD();
		_roomTitle = readS(64);
	}

	@Override
	protected void runImpl()
	{
		final Player player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		final Party party = player.getParty();
		if (party != null && party.getLeader() != player)
		{
			return;
		}
		MatchingRoom room = player.getMatchingRoom();
		if (room == null)
		{
			room = new PartyMatchingRoom(player, _minLevel, _maxLevel, _maxMembers, _lootDist, _roomTitle);
			if (party != null)
			{
				for (Player member : party)
				{
					if (member != null && member != player)
					{
						room.addMemberForce(member);
					}
				}
			}
		}
		else if (room.getId() == _roomId && room.getType() == MatchingRoom.PARTY_MATCHING && room.getLeader() == player)
		{
			room.setMinLevel(_minLevel);
			room.setMaxLevel(_maxLevel);
			room.setMaxMemberSize(_maxMembers);
			room.setTopic(_roomTitle);
			room.setLootType(_lootDist);
			room.sendPacket(room.infoRoomPacket());
		}
	}
}