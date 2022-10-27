package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;

/**
 * Format: (c) S
 * S: pledge name?
 */
public class RequestPledgeExtendedInfo extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private String _name;

	@Override
	protected void readImpl()
	{
		this._name = this.readS(16);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		if (activeChar.isGM())
		{
			activeChar.sendMessage("RequestPledgeExtendedInfo");
		}

		// TODO this
	}
}