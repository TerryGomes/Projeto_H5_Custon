package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.data.xml.holder.HennaHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.HennaUnequipInfo;
import l2mv.gameserver.templates.Henna;

public class RequestHennaUnequipInfo extends L2GameClientPacket
{
	private int _symbolId;

	/**
	 * format: d
	 */
	@Override
	protected void readImpl()
	{
		this._symbolId = this.readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		Henna henna = HennaHolder.getInstance().getHenna(this._symbolId);
		if (henna != null)
		{
			player.sendPacket(new HennaUnequipInfo(henna, player));
		}
	}
}