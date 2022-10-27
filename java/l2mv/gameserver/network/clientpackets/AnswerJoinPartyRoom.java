package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Request;
import l2mv.gameserver.model.Request.L2RequestType;
import l2mv.gameserver.model.matching.MatchingRoom;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

/**
 * format: (ch)d
 */
public class AnswerJoinPartyRoom extends L2GameClientPacket
{
	private int _response;

	@Override
	protected void readImpl()
	{
		if (this._buf.hasRemaining())
		{
			this._response = this.readD();
		}
		else
		{
			this._response = 0;
		}
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		activeChar.isntAfk();

		Request request = activeChar.getRequest();
		if (request == null || !request.isTypeOf(L2RequestType.PARTY_ROOM))
		{
			return;
		}

		if (!request.isInProgress() || activeChar.isOutOfControl())
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}

		Player requestor = request.getRequestor();
		if (requestor == null)
		{
			request.cancel();
			activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
			activeChar.sendActionFailed();
			return;
		}

		if (requestor.getRequest() != request)
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}

		// отказ
		if (this._response == 0)
		{
			request.cancel();
			requestor.sendPacket(SystemMsg.THE_PLAYER_DECLINED_TO_JOIN_YOUR_PARTY);
			return;
		}

		if (activeChar.getMatchingRoom() != null)
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}

		try
		{
			MatchingRoom room = requestor.getMatchingRoom();
			if (room == null || room.getType() != MatchingRoom.PARTY_MATCHING)
			{
				return;
			}

			room.addMember(activeChar);
		}
		finally
		{
			request.done();
		}
	}
}