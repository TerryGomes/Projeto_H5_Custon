package l2mv.gameserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Request;
import l2mv.gameserver.model.Request.L2RequestType;
import l2mv.gameserver.model.pledge.ClanWar;
import l2mv.gameserver.model.pledge.ClanWar.ClanWarPeriod;

/**
 * @author GodWorld & reworked by Bonux
**/
public final class RequestReplySurrenderPledgeWar extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestReplySurrenderPledgeWar.class);

	private String _reqName;
	private int _answer;

	@Override
	protected void readImpl()
	{
		this._reqName = this.readS();
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
		if (request == null || !request.isTypeOf(L2RequestType.CLAN_WAR_SURRENDER))
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
				// requestor.deathPenalty(false, false, false);

				ClanWar war = requestor.getClan().getClanWar(activeChar.getClan());
				if (war != null)
				{
					war.setPeriod(ClanWarPeriod.PEACE);
				}
			}
			finally
			{
				request.done();
			}
		}
		else
		{
			_log.warn(this.getClass().getSimpleName() + ": Missing implementation for answer: " + this._answer + " and name: " + this._reqName + "!");
			request.cancel();
		}
	}
}