package l2f.gameserver.network.serverpackets;

import java.util.Collections;
import java.util.Map;

import l2f.gameserver.model.Player;

/**
 * @author VISTALL
 * @date 20:24/16.05.2011
 */
public class PackageToList extends L2GameServerPacket
{
	private Map<Integer, String> _characters = Collections.emptyMap();

	public PackageToList(Player player)
	{
		_characters = player.getAccountChars();
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xC8);
		writeD(_characters.size());
		for (Map.Entry<Integer, String> entry : _characters.entrySet())
		{
			writeD(entry.getKey());
			writeS(entry.getValue());
		}
	}
}
