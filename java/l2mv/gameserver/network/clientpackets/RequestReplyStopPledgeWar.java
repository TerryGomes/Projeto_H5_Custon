package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Request;
import l2mv.gameserver.model.Request.L2RequestType;
import l2mv.gameserver.model.pledge.ClanWar;
import l2mv.gameserver.model.pledge.ClanWar.ClanWarPeriod;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

/**
 * @author GodWorld & reworked by Bonux
**/
public final class RequestReplyStopPledgeWar extends L2GameClientPacket
{
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
		if (request == null || !request.isTypeOf(L2RequestType.CLAN_WAR_STOP))
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
			requestor.sendPacket(SystemMsg.REQUEST_TO_END_WAR_HAS_BEEN_DENIED);
			request.cancel();
		}
	}
}