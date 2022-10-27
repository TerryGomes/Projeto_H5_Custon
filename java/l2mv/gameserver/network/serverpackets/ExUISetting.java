package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

public class ExUISetting extends L2GameServerPacket
{
	private final byte data[];

	public ExUISetting(Player player)
	{
		this.data = player.getKeyBindings();
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x70);
		this.writeD(this.data.length);
		this.writeB(this.data);
	}
}
