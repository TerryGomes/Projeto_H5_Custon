package l2f.gameserver.network.serverpackets;

import java.util.HashMap;
import java.util.Map;

import l2f.gameserver.model.Player;
import l2f.gameserver.utils.Location;

public class PartyMemberPosition extends L2GameServerPacket
{
	private final Map<Integer, Location> positions = new HashMap<Integer, Location>();

	public PartyMemberPosition add(Player actor)
	{
		positions.put(actor.getObjectId(), actor.getLoc());
		return this;
	}

	public int size()
	{
		return positions.size();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xba);
		writeD(positions.size());
		for (Map.Entry<Integer, Location> e : positions.entrySet())
		{
			writeD(e.getKey());
			writeD(e.getValue().x);
			writeD(e.getValue().y);
			writeD(e.getValue().z);
		}
	}
}