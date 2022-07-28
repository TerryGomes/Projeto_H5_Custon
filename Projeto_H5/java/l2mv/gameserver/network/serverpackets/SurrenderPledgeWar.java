package l2mv.gameserver.network.serverpackets;

public class SurrenderPledgeWar extends L2GameServerPacket
{
	private String _pledgeName;
	private String _char;

	public SurrenderPledgeWar(String pledge, String charName)
	{
		this._pledgeName = pledge;
		this._char = charName;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x67);
		this.writeS(this._pledgeName);
		this.writeS(this._char);
	}
}