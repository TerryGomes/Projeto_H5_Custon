package l2mv.gameserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.model.pledge.ClanWar;
import l2mv.gameserver.model.pledge.ClanWar.ClanWarPeriod;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.tables.ClanTable;

/**
 * @author GodWorld & reworked by Bonux
**/
public final class RequestSurrenderPledgeWar extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestSurrenderPledgeWar.class);

	private String _pledgeName;

	@Override
	protected void readImpl()
	{
		this._pledgeName = this.readS();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		Clan clan = activeChar.getClan();
		if (clan == null)
		{
			return;
		}

		Clan targetClan = ClanTable.getInstance().getClanByName(this._pledgeName);
		if (targetClan == null)
		{
			activeChar.sendPacket(SystemMsg.THE_TARGET_FOR_DECLARATION_IS_WRONG);
			activeChar.sendActionFailed();
			return;
		}

		_log.info(this.getClass().getSimpleName() + ": by " + clan.getName() + " with " + this._pledgeName);

		if (!clan.isAtWarWith(targetClan.getClanId()))
		{
			// TODO: activeChar.sendMessage("You aren't at war with this clan.");
			activeChar.sendActionFailed();
			return;
		}

		activeChar.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_SURRENDERED_TO_THE_S1_CLAN).addString(this._pledgeName));
		// activeChar.deathPenalty(false, false, false);

		ClanWar war = clan.getClanWar(targetClan);
		if (war != null)
		{
			war.setPeriod(ClanWarPeriod.PEACE);
		}
	}
}