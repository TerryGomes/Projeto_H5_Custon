package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;

public class SnoopQuit extends L2GameClientPacket
{
	private int _snoopID;

	/**
	 * format: cd
	 */
	@Override
	protected void readImpl()
	{
		this._snoopID = this.readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = (Player) GameObjectsStorage.findObject(this._snoopID);
		if (player == null)
		{
			return;
		}
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		player.removeSnooper(activeChar);
		activeChar.removeSnooped(player);
	}
}