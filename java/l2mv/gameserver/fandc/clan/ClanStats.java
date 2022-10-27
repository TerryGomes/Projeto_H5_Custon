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
//package l2mv.gameserver.fandc.clan;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import l2mv.gameserver.database.DatabaseFactory;
//import l2mv.gameserver.model.pledge.Clan;
//import l2mv.gameserver.fandc.datatables.CharacterMonthlyRanking;
//import l2mv.gameserver.fandc.templates.Ranking;
//
///**
// * Modulo que sirve para guardar todas las estadisticas usadas por los clanes
// * Seria el equivalente al PcStats de los pjs solo que para clanes
// *
// * @author Synerge
// */
//public class ClanStats
//{
//	private static final Logger _log = Logger.getLogger(ClanStats.class.getName());
//
//	private final Clan _owner;
//
//	private final long[] _clanStats = new long[Ranking.STATS_SIZE];
//
//	public ClanStats(Clan owner)
//	{
//		_owner = owner;
//	}
//
//	public Clan getOwner()
//	{
//		return _owner;
//	}
//
//	public void setClanStats(Ranking rank, long points)
//	{
//		_clanStats[rank.getTopId()] = points;
//	}
//
//	public void addClanStats(Ranking rank)
//	{
//		addClanStats(rank, 1);
//	}
//
//	public void addClanStats(Ranking rank, long points)
//	{
//		// Limite maximo es long
//		if (_clanStats[rank.getTopId()] > Long.MAX_VALUE - points)
//			return;
//
//		// Si la stat es unica, se debe setear, si es stackable entonces se suma a la anterior
//		if (rank.isStackable())
//		{
//			_clanStats[rank.getTopId()] += points;
//
//			// Agrego esta stat tambien en las estadisticas de este mes para el pj
//			CharacterMonthlyRanking.getInstance().addPlayerStats(_owner.getClanId(), rank.getTopId(), points, false);
//		}
//		else
//		{
//			if (points > _clanStats[rank.getTopId()])
//			{
//				_clanStats[rank.getTopId()] = points;
//
//				// Agrego esta stat tambien en las estadisticas de este mes para el pj
//				CharacterMonthlyRanking.getInstance().addPlayerStats(_owner.getClanId(), rank.getTopId(), points, true);
//			}
//		}
//	}
//
//	public long getClanStats(Ranking rank)
//	{
//		return _clanStats[rank.getTopId()];
//	}
//
//	public long getClanStats(int statId)
//	{
//		return _clanStats[statId];
//	}
//
//	/**
//	 * Restauramos las stats del clan desde la DB
//	 */
//	public void restoreClanStats()
//	{
//		try (Connection con = DatabaseFactory.getInstance().getConnection();
//			PreparedStatement statement = con.prepareStatement("SELECT variable,value FROM clan_stats WHERE clanId=?"))
//		{
//			statement.setInt(1, _owner.getClanId());
//			try (ResultSet rset = statement.executeQuery())
//			{
//				while (rset.next())
//				{
//					// Obtengo dinamicamente cada ranking perteneciente a esta tabla con su valor correspondiente
//					for (Ranking top : Ranking.values())
//					{
//						if (top.getDbName().equalsIgnoreCase(rset.getString("variable")) && top.getDbLocation().equalsIgnoreCase("clan_stats"))
//						{
//							setClanStats(top, rset.getLong("value"));
//						}
//					}
//				}
//			}
//		}
//		catch (Exception e)
//		{
//			_log.log(Level.SEVERE, "Failed loading clan stats", e);
//		}
//	}
//
//	/**
//	 * Ahora actualizamos las stats del clan en la db a traves de un batch
//	 */
//	public void updateClanStatsToDB()
//	{
//		try (Connection con = DatabaseFactory.getInstance().getConnection();
//			PreparedStatement statement = con.prepareStatement("REPLACE INTO clan_stats VALUES (?, ?, ?)"))
//		{
//			// Agrego dinamicamente cada ranking perteneciente a esta tabla con su valor correspondiente, para lograr un update totalmente dinamico
//			for (Ranking top : Ranking.values())
//			{
//				if (top.getDbLocation().equalsIgnoreCase("clan_stats"))
//				{
//					// Solo lo salvamos si el cantidad es mayor a 0, sino creamos celdas en la db al pedo, el default es 0
//					if (getClanStats(top) > 0)
//					{
//						statement.setInt(1, _owner.getClanId());
//						statement.setString(2, top.getDbName());
//						statement.setLong(3, getClanStats(top));
//						statement.execute();
//					}
//				}
//			}
//		}
//		catch (Exception e)
//		{
//			_log.log(Level.WARNING, "Could not update clan stats to db: " + this + " - " + e.getMessage(), e);
//		}
//	}
//}
