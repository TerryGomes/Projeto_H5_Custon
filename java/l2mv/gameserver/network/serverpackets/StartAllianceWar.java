package l2mv.gameserver.network.serverpackets;

public class StartAllianceWar extends L2GameServerPacket
{
	private String _allianceName;
	private String _char;

	public StartAllianceWar(String alliance, String charName)
	{
		this._allianceName = alliance;
		this._char = charName;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xc2);
		this.writeS(this._char);
		this.writeS(this._allianceName);
	}
}