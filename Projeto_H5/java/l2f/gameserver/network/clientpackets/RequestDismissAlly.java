package l2f.gameserver.network.clientpackets;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.pledge.Alliance;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.tables.ClanTable;

public class RequestDismissAlly extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		Clan clan = activeChar.getClan();
		if (clan == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		Alliance alliance = clan.getAlliance();
		if (alliance == null)
		{
			activeChar.sendPacket(SystemMsg.YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS);
			return;
		}

		if (!activeChar.isAllyLeader())
		{
			activeChar.sendPacket(SystemMsg.THIS_FEATURE_IS_ONLY_AVAILABLE_TO_ALLIANCE_LEADERS);
			return;
		}

		if (alliance.getMembersCount() > 1)
		{
			activeChar.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_DISSOLVE_THE_ALLIANCE);
			return;
		}

		ClanTable.getInstance().dissolveAlly(activeChar);
	}
}