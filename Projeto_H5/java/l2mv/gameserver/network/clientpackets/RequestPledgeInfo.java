package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.network.serverpackets.PledgeInfo;
import l2mv.gameserver.tables.ClanTable;

public class RequestPledgeInfo extends L2GameClientPacket
{
	private int _clanId;

	@Override
	protected void readImpl()
	{
		_clanId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		if (_clanId < 10000000)
		{
			activeChar.sendActionFailed();
			return;
		}
		Clan clan = ClanTable.getInstance().getClan(_clanId);
		if (clan == null)
		{
			// Util.handleIllegalPlayerAction(activeChar, "RequestPledgeInfo[40]", "Clan data for clanId " + _clanId + " is missing", 1);
			// _log.warn("Host " + getClient().getIpAddr() + " possibly sends fake packets. activeChar: " + activeChar);
			activeChar.sendActionFailed();
			return;
		}

		activeChar.sendPacket(new PledgeInfo(clan));
	}
}