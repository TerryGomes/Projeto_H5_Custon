package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;

/**
 * @author VISTALL
 * @date 23:33/23.03.2011
 */
public class RequestGoodsInventoryInfo extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{

	}

	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
			// player.sendPacket(new ExGoodsInventoryInfo(player));
		}
	}
}
