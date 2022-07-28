package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.actor.instances.player.ShortCut;

public class ShortCutRegister extends ShortCutPacket
{
	private ShortcutInfo _shortcutInfo;

	public ShortCutRegister(Player player, ShortCut sc)
	{
		this._shortcutInfo = convert(player, sc);
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x44);

		this._shortcutInfo.write(this);
	}
}