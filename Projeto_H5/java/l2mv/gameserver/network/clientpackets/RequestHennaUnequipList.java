package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.HennaUnequipList;

public class RequestHennaUnequipList extends L2GameClientPacket
{
	private int _symbolId;

	/**
	 * format: d
	 */
	@Override
	protected void readImpl()
	{
		this._symbolId = this.readD(); // ?
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		HennaUnequipList he = new HennaUnequipList(activeChar);
		activeChar.sendPacket(he);
	}
}