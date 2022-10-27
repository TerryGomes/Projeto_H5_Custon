package l2mv.gameserver.network.serverpackets;

import java.util.Collections;
import java.util.Map;

import l2mv.gameserver.model.Player;

/**
 * @author VISTALL
 * @date 20:24/16.05.2011
 */
public class PackageToList extends L2GameServerPacket
{
	private Map<Integer, String> _characters = Collections.emptyMap();

	public PackageToList(Player player)
	{
		this._characters = player.getAccountChars();
	}

	@Override
	protected void writeImpl()
	{
		this.writeC(0xC8);
		this.writeD(this._characters.size());
		for (Map.Entry<Integer, String> entry : this._characters.entrySet())
		{
			this.writeD(entry.getKey());
			this.writeS(entry.getValue());
		}
	}
}
