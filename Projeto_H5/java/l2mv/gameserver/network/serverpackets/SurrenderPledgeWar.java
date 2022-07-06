package l2mv.gameserver.network.serverpackets;

public class SurrenderPledgeWar extends L2GameServerPacket
{
	private String _pledgeName;
	private String _char;

	public SurrenderPledgeWar(String pledge, String charName)
	{
		_pledgeName = pledge;
		_char = charName;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x67);
		writeS(_pledgeName);
		writeS(_char);
	}
}