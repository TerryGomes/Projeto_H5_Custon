package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.instancemanager.MatchingRoomManager;
import l2mv.gameserver.model.CommandChannel;
import l2mv.gameserver.model.Party;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.matching.CCMatchingRoom;
import l2mv.gameserver.model.matching.MatchingRoom;
import l2mv.gameserver.network.serverpackets.ListPartyWaiting;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

public class RequestPartyMatchConfig extends L2GameClientPacket
{
	private int _page;
	private int _region;
	private int _allLevels;

	/**
	 * Format: ddd
	 */
	@Override
	protected void readImpl()
	{
		this._page = this.readD();
		this._region = this.readD(); // 0 to 15, or -1
		this._allLevels = this.readD(); // 1 -> all levels, 0 -> only levels matching my level
	}

	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		final Party party = player.getParty();
		final CommandChannel channel = party != null ? party.getCommandChannel() : null;
		if (channel != null && channel.getLeader() == player)
		{
			if (channel.getMatchingRoom() == null)
			{
				final CCMatchingRoom room = new CCMatchingRoom(player, 1, player.getLevel(), 50, party.getLootDistribution(), player.getName());
				channel.setMatchingRoom(room);
			}
		}
		else if (channel != null && !channel.getParties().contains(party))
		{
			player.sendPacket(SystemMsg.THE_COMMAND_CHANNEL_AFFILIATED_PARTYS_PARTY_MEMBER_CANNOT_USE_THE_MATCHING_SCREEN);
		}
		else if (party != null && !party.isLeader(player))
		{
			final MatchingRoom room = player.getMatchingRoom();
			if (room != null && room.getType() == MatchingRoom.PARTY_MATCHING)
			{
				player.setMatchingRoomWindowOpened(true);
				player.sendPacket(room.infoRoomPacket(), room.membersPacket(player));
			}
			else
			{
				player.sendPacket(SystemMsg.THE_LIST_OF_PARTY_ROOMS_CAN_ONLY_BE_VIEWED_BY_A_PERSON_WHO_IS_NOT_PART_OF_A_PARTY);
			}
		}
		else
		{
			if (party == null)
			{
				MatchingRoomManager.getInstance().addToWaitingList(player);
			}
			player.sendPacket(new ListPartyWaiting(this._region, this._allLevels == 1, this._page, player));
		}
	}
}