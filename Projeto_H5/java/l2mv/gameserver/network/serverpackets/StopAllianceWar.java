package l2mv.gameserver.network.serverpackets;

public class StopAllianceWar extends L2GameServerPacket
{
	private String _allianceName;
	private String _char;

	public StopAllianceWar(String alliance, String charName)
	{
		this._allianceName = alliance;
		this._char = charName;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xc4);
		this.writeS(this._allianceName);
		this.writeS(this._char);
	}
}