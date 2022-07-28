package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.ExListMpccWaiting;

/**
 * @author VISTALL
 */
public class RequestExListMpccWaiting extends L2GameClientPacket
{
	private int _listId;
	private int _locationId;
	private boolean _allLevels;

	@Override
	protected void readImpl()
	{
		this._listId = this.readD();
		this._locationId = this.readD();
		this._allLevels = this.readD() == 1;
	}

	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		player.sendPacket(new ExListMpccWaiting(player, this._listId, this._locationId, this._allLevels));
	}
}