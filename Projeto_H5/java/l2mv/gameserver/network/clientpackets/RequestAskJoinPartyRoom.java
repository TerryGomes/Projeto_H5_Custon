package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Request;
import l2mv.gameserver.model.Request.L2RequestType;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.matching.MatchingRoom;
import l2mv.gameserver.network.serverpackets.ExAskJoinPartyRoom;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

/**
 * format: (ch)S
 */
public class RequestAskJoinPartyRoom extends L2GameClientPacket
{
	private String _name; // not tested, just guessed

	@Override
	protected void readImpl()
	{
		_name = readS(16);
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		player.isntAfk();

		Player targetPlayer = World.getPlayer(_name);

		if (targetPlayer == null || targetPlayer == player)
		{
			player.sendActionFailed();
			return;
		}

		if (player.isProcessingRequest())
		{
			player.sendPacket(SystemMsg.WAITING_FOR_ANOTHER_REPLY);
			return;
		}

		if (targetPlayer.isProcessingRequest())
		{
			player.sendPacket(new SystemMessage2(SystemMsg.C1_IS_ON_ANOTHER_TASK).addName(targetPlayer));
			return;
		}

		if (player.isInFightClub() && !player.getFightClubEvent().canJoinParty(player, targetPlayer))
		{
			player.sendMessage("You cannot do that on Fight Club!");
			return;
		}

		if (targetPlayer.getMatchingRoom() != null)
		{
			return;
		}

		MatchingRoom room = player.getMatchingRoom();
		if (room == null || room.getType() != MatchingRoom.PARTY_MATCHING)
		{
			return;
		}

		if (room.getLeader() != player)
		{
			player.sendPacket(SystemMsg.ONLY_A_ROOM_LEADER_MAY_INVITE_OTHERS_TO_A_PARTY_ROOM);
			return;
		}

		if (room.getPlayers().size() >= room.getMaxMembersSize())
		{
			player.sendPacket(SystemMsg.THE_PARTY_ROOM_IS_FULL);
			return;
		}

		new Request(L2RequestType.PARTY_ROOM, player, targetPlayer).setTimeout(10000L);

		targetPlayer.sendPacket(new ExAskJoinPartyRoom(player.getName(), room.getTopic()));

		player.sendPacket(new SystemMessage2(SystemMsg.S1_HAS_SENT_AN_INVITATION_TO_ROOM_S2).addName(player).addString(room.getTopic()));
		targetPlayer.sendPacket(new SystemMessage2(SystemMsg.S1_HAS_SENT_AN_INVITATION_TO_ROOM_S2).addName(player).addString(room.getTopic()));
	}
}