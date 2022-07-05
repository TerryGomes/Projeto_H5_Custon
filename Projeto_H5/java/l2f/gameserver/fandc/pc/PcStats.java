///*
// * This program is free software: you can redistribute it and/or modify it under
// * the terms of the GNU General Public License as published by the Free Software
// * Foundation, either version 3 of the License, or (at your option) any later
// * version.
// *
// * This program is distributed in the hope that it will be useful, but WITHOUT
// * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
// * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
// * details.
// *
// * You should have received a copy of the GNU General Public License along with
// * this program. If not, see <http://www.gnu.org/licenses/>.
// */
//package l2f.gameserver.fandc.pc;
//
//import l2f.gameserver.fandc.datatables.CharacterMonthlyRanking;
//import l2f.gameserver.fandc.templates.Ranking;
//
///**
// * Es un modulo que separe del L2PcInstance. Contiene todas las funciones, y variables para las estadisticas que he ido
// * creando como Donador, AutoLoot, seguidilla de pvps, PvPs de Arena, etc
// *
// * @author Synerge
// */
//public class PcStats
//{
//	private final int _ownerId;
//
//	private long[] _playerStats = new long[Ranking.STATS_SIZE];
//
//	public PcStats(int ownerId)
//	{
//		_ownerId = ownerId;
//	}
//
//	public int getOwnerId()
//	{
//		return _ownerId;
//	}
//
//	public void setPlayerStats(Ranking rank, long points)
//	{
//		_playerStats[rank.getTopId()] = points;
//	}
//
//	public void addPlayerStats(Ranking rank)
//	{
//		addPlayerStats(rank, 1);
//	}
//
//	public void addPlayerStats(Ranking rank, long points)
//	{
//		// Limite maximo es long
//		if (_playerStats[rank.getTopId()] > Long.MAX_VALUE - points)
//			return;
//
//		// Si la stat es unica, se debe setear, si es stackable entonces se suma a la anterior
//		if (rank.isStackable())
//		{
//			_playerStats[rank.getTopId()] += points;
//
//			// Agrego esta stat tambien en las estadisticas de este mes para el pj
//			CharacterMonthlyRanking.getInstance().addPlayerStats(_ownerId, rank.getTopId(), points, false);
//		}
//		else
//		{
//			_playerStats[rank.getTopId()] = points;
//
//			// Agrego esta stat tambien en las estadisticas de este mes para el pj
//			CharacterMonthlyRanking.getInstance().addPlayerStats(_ownerId, rank.getTopId(), points, true);
//		}
//	}
//
//	public long getPlayerStats(Ranking rank)
//	{
//		return _playerStats[rank.getTopId()];
//	}
//
//	public long getPlayerStats(int statId)
//	{
//		return _playerStats[statId];
//	}
//}
