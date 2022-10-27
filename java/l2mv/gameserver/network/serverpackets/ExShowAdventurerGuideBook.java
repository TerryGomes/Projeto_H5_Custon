package l2mv.gameserver.network.serverpackets;

/**
 * Format: ch (trigger)
 */
public class ExShowAdventurerGuideBook extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x38);
	}
}