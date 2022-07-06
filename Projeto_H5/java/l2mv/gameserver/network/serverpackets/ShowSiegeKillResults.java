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
		_clans = bestClans;
	}

	@Override
	public void writeImpl()
	{
		writeEx(0x89);
		writeD(0x00); // Open/Dont Open
		writeD(_clans.length);
		for (Clan c : _clans)
		{
			writeS(c == null ? "" : c.getName());
			writeD(c == null ? 0 : c.getSiegeKills());
		}
	}
}