package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Request;
import l2mv.gameserver.model.Request.L2RequestType;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

public class RequestAnswerJoinPledge extends L2GameClientPacket
{
	private int _response;

	@Override
	protected void readImpl()
	{
		_response = _buf.hasRemaining() ? readD() : 0;
	}

	@Override
	protected void runImpl()
	{
		final Player player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		Request request = player.getRequest();
		if (request == null || !request.isTypeOf(L2RequestType.CLAN))
		{
			return;
		}

		// Already in clan
		if (!request.isInProgress() || player.isOutOfControl() || (player.getClan() != null))
		{
			request.cancel();
			player.sendActionFailed();
			return;
		}

		final Player requestor = request.getRequestor();
		if (requestor == null)
		{
			request.cancel();
			player.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
			player.sendActionFailed();
			return;
		}

		if (requestor.getRequest() != request)
		{
			request.cancel();
			player.sendActionFailed();
			return;
		}

		final Clan clan = requestor.getClan();
		if (clan == null)
		{
			request.cancel();
			player.sendActionFailed();
			return;
		}

		if (_response == 0)
		{
			request.cancel();
			requestor.sendPacket(new SystemMessage2(SystemMsg.S1_DECLINED_YOUR_CLAN_INVITATION).addName(player));
			return;
		}

		if (!player.canJoinClan())
		{
			request.cancel();
			player.sendPacket(SystemMsg.AFTER_LEAVING_OR_HAVING_BEEN_DISMISSED_FROM_A_CLAN_YOU_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_JOINING_ANOTHER_CLAN);
			return;
		}

		try
		{
			clan.addMember(player, request.getInteger("pledgeType"));
		}
		finally
		{
			request.done();
		}
	}
}