package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Party;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.matching.MatchingRoom;
import l2mv.gameserver.model.matching.PartyMatchingRoom;

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
		this._roomId = this.readD();
		this._maxMembers = this.readD();
		this._minLevel = this.readD();
		this._maxLevel = this.readD();
		this._lootDist = this.readD();
		this._roomTitle = this.readS(64);
	}

	@Override
	protected void runImpl()
	{
		final Player player = this.getClient().getActiveChar();
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
			room = new PartyMatchingRoom(player, this._minLevel, this._maxLevel, this._maxMembers, this._lootDist, this._roomTitle);
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
		else if (room.getId() == this._roomId && room.getType() == MatchingRoom.PARTY_MATCHING && room.getLeader() == player)
		{
			room.setMinLevel(this._minLevel);
			room.setMaxLevel(this._maxLevel);
			room.setMaxMemberSize(this._maxMembers);
			room.setTopic(this._roomTitle);
			room.setLootType(this._lootDist);
			room.sendPacket(room.infoRoomPacket());
		}
	}
}