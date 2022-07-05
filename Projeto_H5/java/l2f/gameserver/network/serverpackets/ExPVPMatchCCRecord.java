package l2f.gameserver.network.serverpackets;

import java.util.Map;

/**
 * @author VISTALL
 */
public class ExPVPMatchCCRecord extends L2GameServerPacket
{
	private final Map<String, Integer> _scores;

	public ExPVPMatchCCRecord(Map<String, Integer> scores)
	{
		_scores = scores;
	}

	@Override
	public void writeImpl()
	{
		writeEx(0x89);
		writeD(0x00); // Open/Dont Open
		writeD(_scores.size());
		for (Map.Entry<String, Integer> p : _scores.entrySet())
		{
			writeS(p.getKey());
			writeD(p.getValue().intValue());
		}
	}
}