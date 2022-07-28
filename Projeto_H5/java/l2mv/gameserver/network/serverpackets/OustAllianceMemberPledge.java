package l2mv.gameserver.network.serverpackets;

public class OustAllianceMemberPledge extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		this.writeC(0xAC);
		// TODO d
	}
}