package l2mv.gameserver.network.serverpackets;

import java.util.Map;

/**
 * @author VISTALL
 */
public class ExPVPMatchCCRecord extends L2GameServerPacket
{
	private final Map<String, Integer> _scores;

	public ExPVPMatchCCRecord(Map<String, Integer> scores)
	{
		this._scores = scores;
	}

	@Override
	public void writeImpl()
	{
		this.writeEx(0x89);
		this.writeD(0x00); // Open/Dont Open
		this.writeD(this._scores.size());
		for (Map.Entry<String, Integer> p : this._scores.entrySet())
		{
			this.writeS(p.getKey());
			this.writeD(p.getValue().intValue());
		}
	}
}