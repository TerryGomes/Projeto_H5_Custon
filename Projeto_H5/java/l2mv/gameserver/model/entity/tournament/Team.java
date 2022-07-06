package l2mv.gameserver.model.entity.tournament;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import l2mv.gameserver.dao.CharacterDAO;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;

public class Team
{
	private final int _id;
	private final int[] _playerIds;
	private int _finalPosition;
	private final TIntList _roundsLost;

	protected Team(int id, int[] playerIds)
	{
		_id = id;
		_playerIds = playerIds;
		_finalPosition = -1;
		_roundsLost = new TIntArrayList(2);
	}

	protected Team(int id, int[] playerIds, int finalPosition)
	{
		_id = id;
		_playerIds = playerIds;
		_finalPosition = finalPosition;
		_roundsLost = new TIntArrayList(2);
	}

	public int getId()
	{
		return _id;
	}

	public int[] getPlayerIdsForIterate()
	{
		return _playerIds;
	}

	public int[] getPlayerIdsCopy()
	{
		final int[] copy = new int[_playerIds.length];
		System.arraycopy(_playerIds, 0, copy, 0, _playerIds.length);
		return copy;
	}

	public List<Player> getOnlinePlayers()
	{
		final List<Player> list = new ArrayList<Player>(_playerIds.length);
		for (int playerId : _playerIds)
		{
			final Player player = GameObjectsStorage.getPlayer(playerId);
			if (player != null)
			{
				list.add(player);
			}
		}
		return list;
	}

	public List<String> getPlayerNames(boolean justOnline)
	{
		final List<String> names = new ArrayList<String>(_playerIds.length);
		for (int playerId : _playerIds)
		{
			final Player player = GameObjectsStorage.getPlayer(playerId);
			if (player != null)
			{
				names.add(player.getName());
			}
			else if (!justOnline)
			{
				final String name = CharacterDAO.getNameByObjectId(playerId);
				if (!name.equals(""))
				{
					names.add(name);
				}
			}
		}
		return names;
	}

	public boolean isMember(int objectId)
	{
		return ArrayUtils.contains(_playerIds, objectId);
	}

	public void setFinalPosition(int finalPosition)
	{
		_finalPosition = finalPosition;
	}

	public boolean hasFinalPosition()
	{
		return _finalPosition != -1;
	}

	public int getFinalPosition()
	{
		return _finalPosition;
	}

	public void addLostRound(int roundIndex)
	{
		if (!_roundsLost.contains(roundIndex))
		{
			_roundsLost.add(roundIndex);
		}
	}

	public boolean lostAnyRound()
	{
		return !_roundsLost.isEmpty();
	}

	public int getOldestLostRoundIndex()
	{
		if (_roundsLost.isEmpty())
		{
			return Integer.MAX_VALUE;
		}
		return _roundsLost.min();
	}

	public TIntList getRoundsLost()
	{
		return _roundsLost;
	}

	public void update()
	{
		TournamentTeamsManager.saveInDatabase(this);
	}

	@Override
	public String toString()
	{
		return "Team{id=" + _id + ", playerIds=" + Arrays.toString(_playerIds) + ", finalPosition=" + _finalPosition + ", roundsLost=" + _roundsLost + '}';
	}
}
