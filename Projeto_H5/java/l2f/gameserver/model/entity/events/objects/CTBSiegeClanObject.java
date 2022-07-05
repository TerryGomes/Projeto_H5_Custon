package l2f.gameserver.model.entity.events.objects;

import java.util.ArrayList;
import java.util.List;

import l2f.gameserver.dao.SiegePlayerDAO;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.events.impl.SiegeEvent;
import l2f.gameserver.model.entity.residence.Residence;
import l2f.gameserver.model.pledge.Clan;

public class CTBSiegeClanObject extends SiegeClanObject
{
	private final List<Integer> _players = new ArrayList<Integer>();
	private long _npcId;

	public CTBSiegeClanObject(String type, Clan clan, long param, long date)
	{
		super(type, clan, param, date);
		_npcId = param;
	}

	public CTBSiegeClanObject(String type, Clan clan, long param)
	{
		this(type, clan, param, System.currentTimeMillis());
	}

	public void select(Residence r)
	{
		_players.addAll(SiegePlayerDAO.getInstance().select(r, getObjectId()));
	}

	public List<Integer> getPlayers()
	{
		return _players;
	}

	@Override
	public void setEvent(boolean start, SiegeEvent event)
	{
		for (int i : getPlayers())
		{
			final Player player = GameObjectsStorage.getPlayer(i);
			if (player != null)
			{
				if (start)
				{
					player.addEvent(event);
				}
				else
				{
					player.removeEvent(event);
				}
				player.broadcastCharInfo();
			}
		}
	}

	@Override
	public boolean isParticle(Player player)
	{
		return _players.contains(player.getObjectId());
	}

	@Override
	public long getParam()
	{
		return _npcId;
	}

	public void setParam(int npcId)
	{
		_npcId = npcId;
	}
}
