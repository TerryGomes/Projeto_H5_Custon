package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.ai.PlayableAI;
import l2mv.gameserver.geodata.GeoEngine;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Request;
import l2mv.gameserver.model.Request.L2RequestType;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.utils.Location;

public class AnswerCoupleAction extends L2GameClientPacket
{
	private int _charObjId;
	private int _actionId;
	private int _answer;

	@Override
	protected void readImpl()
	{
		this._actionId = this.readD();
		this._answer = this.readD();
		this._charObjId = this.readD();
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
		if (request == null || !request.isTypeOf(L2RequestType.COUPLE_ACTION))
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

		if (requestor.getObjectId() != this._charObjId || requestor.getRequest() != request)
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}

		switch (this._answer)
		{
		case -1: // refused
			requestor.sendPacket(new SystemMessage2(SystemMsg.C1_IS_SET_TO_REFUSE_COUPLE_ACTIONS_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION).addName(activeChar));
			request.cancel();
			break;
		case 0: // cancel
			activeChar.sendPacket(SystemMsg.THE_COUPLE_ACTION_WAS_DENIED);
			requestor.sendPacket(SystemMsg.THE_COUPLE_ACTION_WAS_CANCELLED);
			requestor.sendActionFailed();
			request.cancel();
			break;
		case 1: // ok
			try
			{
				if (!checkCondition(activeChar, requestor) || !checkCondition(requestor, activeChar))
				{
					return;
				}

				Location loc = requestor.applyOffset(activeChar.getLoc(), 25);
				loc = GeoEngine.moveCheck(requestor.getX(), requestor.getY(), requestor.getZ(), loc.x, loc.y, requestor.getGeoIndex());
				requestor.moveToLocation(loc, 0, false);
				requestor.getAI().setNextAction(PlayableAI.nextAction.COUPLE_ACTION, activeChar, this._actionId, true, false);
			}
			finally
			{
				request.done();
			}
			break;
		}
	}

	private static boolean checkCondition(Player activeChar, Player requestor)
	{
		if (!activeChar.isInRange(requestor, 300) || activeChar.isInRange(requestor, 25) || !GeoEngine.canSeeTarget(activeChar, requestor, false))
		{
			activeChar.sendPacket(SystemMsg.THE_REQUEST_CANNOT_BE_COMPLETED_BECAUSE_THE_TARGET_DOES_NOT_MEET_LOCATION_REQUIREMENTS);
			return false;
		}
		return activeChar.checkCoupleAction(requestor);
	}
}
