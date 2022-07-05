package l2f.gameserver.model.entity.events.objects;

import java.io.Serializable;
import java.util.Comparator;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.entity.events.impl.SiegeEvent;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.network.serverpackets.L2GameServerPacket;
import l2f.gameserver.network.serverpackets.components.IStaticPacket;

public class SiegeClanObject implements Serializable
{
	public static class SiegeClanComparatorImpl implements Comparator<SiegeClanObject>
	{
		private static final SiegeClanComparatorImpl _instance = new SiegeClanComparatorImpl();

		public static SiegeClanComparatorImpl getInstance()
		{
			return _instance;
		}

		@Override
		public int compare(SiegeClanObject o1, SiegeClanObject o2)
		{
			return (o2.getParam() < o1.getParam()) ? -1 : ((o2.getParam() == o1.getParam()) ? 0 : 1);
		}
	}

	private String _type;
	private final Clan _clan;
	private NpcInstance _flag;
	private final long _date;

	public SiegeClanObject(String type, Clan clan, long param)
	{
		this(type, clan, 0, System.currentTimeMillis());
	}

	public SiegeClanObject(String type, Clan clan, long param, long date)
	{
		_type = type;
		_clan = clan;
		_date = date;
	}

	public int getObjectId()
	{
		return _clan.getClanId();
	}

	public Clan getClan()
	{
		return _clan;
	}

	public NpcInstance getFlag()
	{
		return _flag;
	}

	public void deleteFlag()
	{
		if (_flag != null)
		{
			_flag.deleteMe();
			_flag = null;
		}
	}

	public void setFlag(NpcInstance npc)
	{
		_flag = npc;
	}

	public void setType(String type)
	{
		_type = type;
	}

	public String getType()
	{
		return _type;
	}

	public void broadcast(IStaticPacket... packet)
	{
		getClan().broadcastToOnlineMembers(packet);
	}

	public void broadcast(L2GameServerPacket... packet)
	{
		getClan().broadcastToOnlineMembers(packet);
	}

	public void setEvent(boolean start, SiegeEvent event)
	{
		if (start)
		{
			for (Player player : _clan.getOnlineMembers(0))
			{
				player.addEvent(event);
				player.broadcastUserInfo(true);
			}
		}
		else
		{
			for (Player player : _clan.getOnlineMembers(0))
			{
				player.removeEvent(event);
				player.getEffectList().stopEffect(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME);
				player.broadcastUserInfo(true);
			}
		}
	}

	public boolean isParticle(Player player)
	{
		return true;
	}

	public long getParam()
	{
		return 0;
	}

	public long getDate()
	{
		return _date;
	}
}
