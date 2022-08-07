package l2mv.gameserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Request;
import l2mv.gameserver.model.Request.L2RequestType;
import l2mv.gameserver.model.pledge.ClanWar;
import l2mv.gameserver.model.pledge.ClanWar.ClanWarPeriod;
import l2mv.gameserver.network.serverpackets.SystemMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

/**
 * @author GodWorld & reworked by Bonux
**/
public final class RequestReplyStartPledgeWar extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestReplyStartPledgeWar.class);

	private int _answer;

	@Override
	protected void readImpl()
	{
		/* String _reqName = */this.readS();
		this._answer = this.readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		Request request = activeChar.getRequest();
		if (request == null || !request.isTypeOf(L2RequestType.CLAN_WAR_START))
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
		if ((requestor == null) || (requestor.getRequest() != request))
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}

		if (this._answer == 1)
		{
			try
			{
				ClanWar war = activeChar.getClan().getClanWar(requestor.getClan());
				if (war == null)
				{
					_log.warn(this.getClass().getSimpleName() + ": Opponent clan war object not found!");

					request.cancel();
					activeChar.sendActionFailed();
					return;
				}

				if (war.getPeriod() != ClanWarPeriod.PREPARATION)
				{
					request.cancel();
					activeChar.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_ALREADY_BEEN_AT_WAR_WITH_THE_S1_CLAN_5_DAYS_MUST_PASS_BEFORE_YOU_CAN_DECLARE_WAR_AGAIN).addString(requestor.getClan().getName()));
					return;
				}

				war.setPeriod(ClanWarPeriod.MUTUAL);
			}
			finally
			{
				request.done();
			}
		}
		else
		{
			requestor.sendPacket(SystemMsg.THE_S1_CLAN_DID_NOT_RESPOND_WAR_PROCLAMATION_HAS_BEEN_REFUSED);
			request.cancel();
		}
	}
}