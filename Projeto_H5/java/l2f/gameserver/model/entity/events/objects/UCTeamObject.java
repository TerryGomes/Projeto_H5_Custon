package l2f.gameserver.model.entity.events.objects;

import java.io.Serializable;
import java.util.Iterator;

import l2f.commons.listener.Listener;
import l2f.gameserver.model.Party;
import l2f.gameserver.model.Player;

/**
 * @author VISTALL
 * @date 3:51/26.08.2011
 */
public class UCTeamObject implements Serializable, Iterable<UCMemberObject>
{
	private class ArrayIterator<E> implements Iterator<E>
	{
		final E[] objects;
		int cursor = 0;

		public ArrayIterator(final E[] objects)
		{
			this.objects = objects;
		}

		@Override
		public boolean hasNext()
		{
			return cursor < objects.length;
		}

		@Override
		public E next()
		{
			return objects[cursor++];
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}

	private final Party _party;
	private final Player _leader;
	private final long _registerTime;

	private int _kills;
	private int _deaths;

	private UCMemberObject[] _members = new UCMemberObject[9];

	@SuppressWarnings("unchecked")
	public UCTeamObject(Player leader, Listener listener)
	{
		_leader = leader;
		_party = leader.getParty();
		_registerTime = System.currentTimeMillis();

		int i = 0;
		for (Player player : _party)
		{
			player.addListener(listener);

			_members[i++] = new UCMemberObject(player);
		}
	}

	public Party getParty()
	{
		return _party;
	}

	public Player getLeader()
	{
		return _leader;
	}

	public long getRegisterTime()
	{
		return _registerTime;
	}

	public int getKills()
	{
		return _kills;
	}

	public void incKills()
	{
		_kills++;
	}

	public int getDeaths()
	{
		return _deaths;
	}

	public void incDeaths()
	{
		_deaths++;
	}

	public UCMemberObject[] getMembers()
	{
		return _members;
	}

	@Override
	public Iterator<UCMemberObject> iterator()
	{
		return new ArrayIterator<UCMemberObject>(_members);
	}
}
