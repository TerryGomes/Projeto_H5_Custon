package l2mv.gameserver.network.serverpackets;

import java.util.Collections;
import java.util.Set;

/**
 * @author VISTALL
 * @date 6:22/12.06.2011
 */
public class ExMpccPartymasterList extends L2GameServerPacket
{
	private Set<String> _members = Collections.emptySet();

	public ExMpccPartymasterList(Set<String> s)
	{
		this._members = s;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xA2);
		this.writeD(this._members.size());
		for (String t : this._members)
		{
			this.writeS(t);
		}
	}
}
