package l2mv.gameserver.network.serverpackets;

/**
 * Reworked: VISTALL
 */
public class AcquireSkillDone extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new AcquireSkillDone();

	@Override
	protected void writeImpl()
	{
		this.writeC(0x94);
	}
}