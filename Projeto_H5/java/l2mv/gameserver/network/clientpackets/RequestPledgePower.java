package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.network.serverpackets.ManagePledgePower;

public class RequestPledgePower extends L2GameClientPacket
{
	private int _rank;
	private int _action;
	private int _privs;

	@Override
	protected void readImpl()
	{
		this._rank = this.readD();
		this._action = this.readD();
		if (this._action == 2)
		{
			this._privs = this.readD();
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
		if (this._action == 2)
		{
			if (this._rank < Clan.RANK_FIRST || this._rank > Clan.RANK_LAST)
			{
				return;
			}
			if (activeChar.getClan() != null && (activeChar.getClanPrivileges() & Clan.CP_CL_MANAGE_RANKS) == Clan.CP_CL_MANAGE_RANKS)
			{
				if (this._rank == 9) // Академикам оставляем только перечисленные ниже права
				{
					this._privs = (this._privs & Clan.CP_CL_WAREHOUSE_SEARCH) + (this._privs & Clan.CP_CH_ENTRY_EXIT) + (this._privs & Clan.CP_CS_ENTRY_EXIT) + (this._privs & Clan.CP_CH_USE_FUNCTIONS) + (this._privs & Clan.CP_CS_USE_FUNCTIONS);
				}
				activeChar.getClan().setRankPrivs(this._rank, this._privs);
				activeChar.getClan().updatePrivsForRank(this._rank);
			}
		}
		else if (activeChar.getClan() != null)
		{
			activeChar.sendPacket(new ManagePledgePower(activeChar, this._action, this._rank));
		}
		else
		{
			activeChar.sendActionFailed();
		}
	}
}