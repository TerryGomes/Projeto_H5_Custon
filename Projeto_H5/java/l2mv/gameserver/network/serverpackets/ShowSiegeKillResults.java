package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.pledge.Clan;

/**
 * @author VISTALL
 */
public class ShowSiegeKillResults extends L2GameServerPacket
{
	private final Clan[] _clans;

	public ShowSiegeKillResults(Clan[] bestClans)
	{
		this._clans = bestClans;
	}

	@Override
	public void writeImpl()
	{
		this.writeEx(0x89);
		this.writeD(0x00); // Open/Dont Open
		this.writeD(this._clans.length);
		for (Clan c : this._clans)
		{
			this.writeS(c == null ? "" : c.getName());
			this.writeD(c == null ? 0 : c.getSiegeKills());
		}
	}
}