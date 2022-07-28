package l2mv.gameserver.network.serverpackets;

import java.util.HashMap;
import java.util.Map;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.utils.Location;

public class PartyMemberPosition extends L2GameServerPacket
{
	private final Map<Integer, Location> positions = new HashMap<Integer, Location>();

	public PartyMemberPosition add(Player actor)
	{
		this.positions.put(actor.getObjectId(), actor.getLoc());
		return this;
	}

	public int size()
	{
		return this.positions.size();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xba);
		this.writeD(this.positions.size());
		for (Map.Entry<Integer, Location> e : this.positions.entrySet())
		{
			this.writeD(e.getKey());
			this.writeD(e.getValue().x);
			this.writeD(e.getValue().y);
			this.writeD(e.getValue().z);
		}
	}
}