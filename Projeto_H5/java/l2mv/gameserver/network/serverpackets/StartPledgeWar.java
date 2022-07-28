package l2mv.gameserver.network.serverpackets;

public class StartPledgeWar extends L2GameServerPacket
{
	private String _pledgeName;
	private String _char;

	public StartPledgeWar(String pledge, String charName)
	{
		this._pledgeName = pledge;
		this._char = charName;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x63);
		this.writeS(this._char);
		this.writeS(this._pledgeName);
	}
}