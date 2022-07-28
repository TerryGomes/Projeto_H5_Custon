package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.base.TeamType;
import l2mv.gameserver.model.entity.olympiad.Olympiad;
import l2mv.gameserver.model.entity.olympiad.OlympiadGame;
import l2mv.gameserver.model.entity.olympiad.OlympiadManager;
import l2mv.gameserver.model.entity.olympiad.TeamMember;

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
				this._arenaList = new ArrayList<ArenaInfo>();
				for (int i = 0; i < Olympiad.STADIUMS.length; i++)
				{
					OlympiadGame game = manager.getOlympiadInstance(i);
					if (game != null && game.getState() > 0)
					{
						this._arenaList.add(new ArenaInfo(i, game.getState(), game.getType().ordinal(), game.getTeamName1(), game.getTeamName2()));
					}
				}
			}
		}

		public MatchList(List<ArenaInfo> arenaList)
		{
			super(0);
			this._arenaList = arenaList;
		}

		@Override
		protected void writeImpl()
		{
			super.writeImpl();
			this.writeD(this._arenaList.size());
			this.writeD(0x00); // unknown
			for (ArenaInfo arena : this._arenaList)
			{
				this.writeD(arena._id);
				this.writeD(arena._matchType);
				this.writeD(arena._status);
				this.writeS(arena._name1);
				this.writeS(arena._name2);
			}
		}

		public static class ArenaInfo
		{
			public int _status;
			private final int _id, _matchType;
			public String _name1, _name2;

			public ArenaInfo(int id, int status, int match_type, String name1, String name2)
			{
				this._id = id;
				this._status = status;
				this._matchType = match_type;
				this._name1 = name1;
				this._name2 = name2;
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
			this._tie = tie;
			this._name = winnerName;
		}

		public void addPlayer(TeamType team, TeamMember member, int gameResultPoints)
		{
			int points = Config.OLYMPIAD_OLDSTYLE_STAT ? 0 : member.getStat().getInteger(Olympiad.POINTS, 0);

			this.addPlayer(team, member.getName(), member.getClanName(), member.getClanId(), member.getClassId(), points, gameResultPoints, (int) member.getDamage());
		}

		public void addPlayer(TeamType team, String name, String clanName, int clanId, int classId, int points, int resultPoints, int damage)
		{
			switch (team)
			{
			case RED:
				this._teamOne.add(new PlayerInfo(name, clanName, clanId, classId, points, resultPoints, damage));
				break;
			case BLUE:
				this._teamTwo.add(new PlayerInfo(name, clanName, clanId, classId, points, resultPoints, damage));
				break;
			}
		}

		@Override
		protected void writeImpl()
		{
			super.writeImpl();
			this.writeD(this._tie);
			this.writeS(this._name);
			this.writeD(1);
			this.writeD(this._teamOne.size());
			for (PlayerInfo playerInfo : this._teamOne)
			{
				this.writeS(playerInfo._name);
				this.writeS(playerInfo._clanName);
				this.writeD(playerInfo._clanId);
				this.writeD(playerInfo._classId);
				this.writeD(playerInfo._damage);
				this.writeD(playerInfo._currentPoints);
				this.writeD(playerInfo._gamePoints);
			}
			this.writeD(2);
			this.writeD(this._teamTwo.size());
			for (PlayerInfo playerInfo : this._teamTwo)
			{
				this.writeS(playerInfo._name);
				this.writeS(playerInfo._clanName);
				this.writeD(playerInfo._clanId);
				this.writeD(playerInfo._classId);
				this.writeD(playerInfo._damage);
				this.writeD(playerInfo._currentPoints);
				this.writeD(playerInfo._gamePoints);
			}
		}

		private static class PlayerInfo
		{
			private final String _name, _clanName;
			public final int _clanId;
			private final int _classId, _currentPoints, _gamePoints, _damage;

			public PlayerInfo(String name, String clanName, int clanId, int classId, int currentPoints, int gamePoints, int damage)
			{
				this._name = name;
				this._clanName = clanName;
				this._clanId = clanId;
				this._classId = classId;
				this._currentPoints = currentPoints;
				this._gamePoints = gamePoints;
				this._damage = damage;
			}
		}
	}

	private final int _type;

	public ExReceiveOlympiad(int type)
	{
		this._type = type;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xD4);
		this.writeD(this._type);
	}
}
