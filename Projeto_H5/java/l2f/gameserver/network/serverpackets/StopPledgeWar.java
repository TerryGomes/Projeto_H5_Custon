package l2f.gameserver.network.serverpackets;

public class StopPledgeWar extends L2GameServerPacket
{
	private String _pledgeName;
	private String _char;

	public StopPledgeWar(String pledge, String charName)
	{
		_pledgeName = pledge;
		_char = charName;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x65);
		writeS(_pledgeName);
		writeS(_char);
	}
}