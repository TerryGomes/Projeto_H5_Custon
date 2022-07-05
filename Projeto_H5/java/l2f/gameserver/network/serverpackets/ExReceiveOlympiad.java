package l2f.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2f.gameserver.Config;
import l2f.gameserver.model.base.TeamType;
import l2f.gameserver.model.entity.olympiad.Olympiad;
import l2f.gameserver.model.entity.olympiad.OlympiadGame;
import l2f.gameserver.model.entity.olympiad.OlympiadManager;
import l2f.gameserver.model.entity.olympiad.TeamMember;

public abstract class ExReceiveOlympiad extends L2GameServerPacket
{
	public static class MatchList extends ExReceiveOlympiad
	{
		private List<ArenaInfo> _arenaList = Collections.emptyList();

		public MatchList()
		{
			super(0);
			OlympiadManager manager = Olympiad._manager;
			if (manager != null)
			{
				_arenaList = new ArrayList<ArenaInfo>();
				for (int i = 0; i < Olympiad.STADIUMS.length; i++)
				{
					OlympiadGame game = manager.getOlympiadInstance(i);
					if (game != null && game.getState() > 0)
					{
						_arenaList.add(new ArenaInfo(i, game.getState(), game.getType().ordinal(), game.getTeamName1(), game.getTeamName2()));
					}
				}
			}
		}

		public MatchList(List<ArenaInfo> arenaList)
		{
			super(0);
			_arenaList = arenaList;
		}

		@Override
		protected void writeImpl()
		{
			super.writeImpl();
			writeD(_arenaList.size());
			writeD(0x00); // unknown
			for (ArenaInfo arena : _arenaList)
			{
				writeD(arena._id);
				writeD(arena._matchType);
				writeD(arena._status);
				writeS(arena._name1);
				writeS(arena._name2);
			}
		}

		public static class ArenaInfo
		{
			public int _status;
			private final int _id, _matchType;
			public String _name1, _name2;

			public ArenaInfo(int id, int status, int match_type, String name1, String name2)
			{
				_id = id;
				_status = status;
				_matchType = match_type;
				_name1 = name1;
				_name2 = name2;
			}
		}
	}

	public static class MatchResult extends ExReceiveOlympiad
	{
		private final boolean _tie;
		private final String _name;
		private final List<PlayerInfo> _teamOne = new ArrayList<PlayerInfo>(3);
		private final List<PlayerInfo> _teamTwo = new ArrayList<PlayerInfo>(3);

		public MatchResult(boolean tie, String winnerName)
		{
			super(1);
			_tie = tie;
			_name = winnerName;
		}

		public void addPlayer(TeamType team, TeamMember member, int gameResultPoints)
		{
			int points = Config.OLYMPIAD_OLDSTYLE_STAT ? 0 : member.getStat().getInteger(Olympiad.POINTS, 0);

			addPlayer(team, member.getName(), member.getClanName(), member.getClanId(), member.getClassId(), points, gameResultPoints, (int) member.getDamage());
		}

		public void addPlayer(TeamType team, String name, String clanName, int clanId, int classId, int points, int resultPoints, int damage)
		{
			switch (team)
			{
			case RED:
				_teamOne.add(new PlayerInfo(name, clanName, clanId, classId, points, resultPoints, damage));
				break;
			case BLUE:
				_teamTwo.add(new PlayerInfo(name, clanName, clanId, classId, points, resultPoints, damage));
				break;
			}
		}

		@Override
		protected void writeImpl()
		{
			super.writeImpl();
			writeD(_tie);
			writeS(_name);
			writeD(1);
			writeD(_teamOne.size());
			for (PlayerInfo playerInfo : _teamOne)
			{
				writeS(playerInfo._name);
				writeS(playerInfo._clanName);
				writeD(playerInfo._clanId);
				writeD(playerInfo._classId);
				writeD(playerInfo._damage);
				writeD(playerInfo._currentPoints);
				writeD(playerInfo._gamePoints);
			}
			writeD(2);
			writeD(_teamTwo.size());
			for (PlayerInfo playerInfo : _teamTwo)
			{
				writeS(playerInfo._name);
				writeS(playerInfo._clanName);
				writeD(playerInfo._clanId);
				writeD(playerInfo._classId);
				writeD(playerInfo._damage);
				writeD(playerInfo._currentPoints);
				writeD(playerInfo._gamePoints);
			}
		}

		private static class PlayerInfo
		{
			private final String _name, _clanName;
			public final int _clanId;
			private final int _classId, _currentPoints, _gamePoints, _damage;

			public PlayerInfo(String name, String clanName, int clanId, int classId, int currentPoints, int gamePoints, int damage)
			{
				_name = name;
				_clanName = clanName;
				_clanId = clanId;
				_classId = classId;
				_currentPoints = currentPoints;
				_gamePoints = gamePoints;
				_damage = damage;
			}
		}
	}

	private final int _type;

	public ExReceiveOlympiad(int type)
	{
		_type = type;
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0xD4);
		writeD(_type);
	}
}
