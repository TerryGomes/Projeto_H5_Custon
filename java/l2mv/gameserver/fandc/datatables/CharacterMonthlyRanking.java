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
//package l2mv.gameserver.fandc.datatables;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//import l2mv.gameserver.ThreadPoolManager;
//import l2mv.gameserver.database.DatabaseFactory;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import l2mv.gameserver.fandc.datatables.ServerRanking.RankingTop;
//import l2mv.gameserver.fandc.datatables.ServerRanking.RankingTopMember;
//import l2mv.gameserver.fandc.templates.Ranking;
//
///**
// * Esta tabla es encargada de manejar y guardar todas las stats que aparecen en el ranking del server, pero cada pj y por mes
// * O sea se comienza desde 0 el mes, se guarda lo que se hace cada dia por cada pj, y se muestra en el ranking
// * Cuando finaliza el mes, se elimina la tabla completa, se limpia todo y vuelven a comenzar los contadores
// * El top 10 de cada categoria de stat se calcula al inicio del server unicamente. Se salvan los nuevos valores, pero no modifican nada, para no matar a la cpu recalculando todo cada vez
// *
// * @author Synerge
// */
//public final class CharacterMonthlyRanking
//{
//	private static final Logger _log = LoggerFactory.getLogger(CharacterMonthlyRanking.class);
//
//	protected final Map<Integer, RankingTop> _rankingsById = new ConcurrentHashMap<>();
//	private final Map<Integer, MonthlyStats> _monthlyStats = new ConcurrentHashMap<>();
//
//	private long _nextMonthStart;
//
//	protected CharacterMonthlyRanking()
//	{
//		// Generamos nuevamente el listado de rankings para usarlo en las stats del mes. No puedo copiar las del server, porque no va a funcionar por las instancias
//		Ranking.generateRankings(_rankingsById);
//
//		// Obtenemos todas las stats desde la DB
//		try (Connection con = DatabaseFactory.getInstance().getConnection())
//		{
//			int charId;
//			MonthlyStats stats;
//
//			// Stats de pjs
//			// Los packs rusos tienen obj_Id en characters en vez de charId
//			try (PreparedStatement statement = con.prepareStatement("SELECT t1.*, t2.char_name, t2.accesslevel FROM monthly_character_stats AS t1 INNER JOIN characters AS t2 WHERE t2.accesslevel>=0 AND t1.charId=t2.obj_Id");
//					ResultSet rset = statement.executeQuery())
//			{
//				while (rset.next())
//				{
//					charId = rset.getInt("charId");
//					stats = new MonthlyStats(charId);
//
//					// Cargamos dinamicamente cada estadistica de la DB, usando toda la informacion de los rankings de server. Asi no tengo que actualizar esto si agrego mas stats
//					for (RankingTop top : _rankingsById.values())
//					{
//						if (top.getDbName().equalsIgnoreCase(rset.getString("variable")))
//						{
//							stats.setPlayerStats(top.getTopId(), rset.getLong("value"));
//
//							// Agregamos este pj al ranking con sus puntos, que luego va a ser ordenado y va a salir el top 10 de cada categoria de este mes. Solo si no es gm
//							if (rset.getInt("accesslevel") == 0)
//								top.addPlayer(new RankingTopMember(rset.getString("char_name"), rset.getLong("value")));
//						}
//					}
//
//					_monthlyStats.put(charId, stats);
//				}
//			}
//
//			// Stats de clan
//			// Los packs rusos tienen obj_Id en characters en vez de charId
//			try (PreparedStatement statement = con.prepareStatement("SELECT t1.*, t2.accesslevel, t3.name FROM monthly_character_stats AS t1 INNER JOIN characters AS t2 " +
//				"INNER JOIN clan_subpledges as t3 WHERE t2.accesslevel>=0 AND t3.leader_id=t2.obj_Id AND t3.clan_id=t1.charId");
//					ResultSet rset = statement.executeQuery())
//			{
//				while (rset.next())
//				{
//					charId = rset.getInt("charId");
//					stats = new MonthlyStats(charId);
//
//					// Cargamos dinamicamente cada estadistica de la DB, usando toda la informacion de los rankings de server. Asi no tengo que actualizar esto si agrego mas stats
//					for (RankingTop top : _rankingsById.values())
//					{
//						if (top.getDbName().equalsIgnoreCase(rset.getString("variable")))
//						{
//							stats.setPlayerStats(top.getTopId(), rset.getLong("value"));
//
//							// Agregamos este pj al ranking con sus puntos, que luego va a ser ordenado y va a salir el top 10 de cada categoria de este mes. Solo si no es gm
//							if (rset.getInt("accesslevel") == 0)
//							{
//								top.addPlayer(new RankingTopMember(rset.getString("name"), rset.getLong("value")));
//							}
//						}
//					}
//
//					_monthlyStats.put(charId, stats);
//				}
//			}
//
//			_log.info(getClass().getSimpleName() + ": Loaded statics of this month for " + _monthlyStats.size() + " characters");
//		}
//		catch (Exception e)
//		{
//			_log.error(getClass().getSimpleName() + ": Error while loading monthly rankings:" + e.getMessage());
//			e.printStackTrace();
//		}
//
//		// Ordenamos cada ranking disponible, a todos sus pjs, logrando un top completo, de los cuales luego solo voy a usar el top 10
//		for (RankingTop rank : _rankingsById.values())
//		{
//			final List<RankingTopMember> sortedTop = new ArrayList<RankingTopMember>(rank.getPlayers());
//			Collections.sort(sortedTop, RANKING_POINTS_COMPARATOR);
//
//			// Ponemos la lista de pjs ordenados en la categoria esta
//			rank.cleanPlayers();
//			rank.getPlayers().addAll(sortedTop);
//		}
//
//		// Guardamos en una variable para no tener que chequear todo el tiempo, cuando comienza el proximo mes. Seria el 1 del proximo mes a las 6am
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.MONTH, 1);
//		calendar.set(Calendar.DAY_OF_MONTH, 1);
//		calendar.set(Calendar.HOUR_OF_DAY, 6);
//		calendar.set(Calendar.MINUTE, 0);
//		_nextMonthStart = calendar.getTimeInMillis();
//
//		// Creamos un thread que va a recargar los tops de este mes cada 1 hora
//		ThreadPoolManager.getInstance().scheduleAtFixedRate(new ReloadStatsFromDB(), 60 * 60 * 1000, 60 * 60 * 1000);
//	}
//
//	/**
//	 * @param topId
//	 * @return Devuelve un ranking segun su id
//	 */
//	public RankingTop getRankingById(int topId)
//	{
//		return _rankingsById.get(topId);
//	}
//
//	/**
//	 * Agrega X valor a determinada stat de un pj. Originalmente es inicializado desde la db, y luego cada vez que cambia
//	 * se aumenta aca para llevar la cuenta del mes de cada pj
//	 * Si el mes termino, se resetea todo, hago el chequeo aca, para no crear threads al pedo
//	 *
//	 * @param charId
//	 * @param statId
//	 * @param points
//	 * @param mustSet
//	 */
//	public void addPlayerStats(int charId, int statId, long points, boolean mustSet)
//	{
//		// Si el mes termino, limpiamos los arrays y eliminamos todos los datos de la db, ya que empieza el nuevo mes y todo desde 0
//		if (System.currentTimeMillis() >= _nextMonthStart)
//			cleanAllMonthlyStats();
//
//		if (!_monthlyStats.containsKey(charId))
//			_monthlyStats.put(charId, new MonthlyStats(charId));
//
//		_monthlyStats.get(charId).addPlayerStats(statId, points, mustSet);
//	}
//
//	/**
//	 * @param charId
//	 * @param statId
//	 * @return Devuelve las stats del mes para un pj en determinada estadistica
//	 */
//	public long getPlayerStats(int charId, int statId)
//	{
//		if (!_monthlyStats.containsKey(charId))
//			return 0;
//
//		return _monthlyStats.get(charId).getPlayerStats(statId);
//	}
//
//	/**
//	 * Limpia el array de estadisticas de este mes y elimina todas las stats de la db. Esto se llama cuando un nuevo mes comienza, y se resetea todo a 0
//	 */
//	public void cleanAllMonthlyStats()
//	{
//		_monthlyStats.clear();
//
//		try (Connection con = DatabaseFactory.getInstance().getConnection();
//			Statement statement = con.createStatement())
//		{
//			statement.execute("DELETE FROM monthly_character_stats WHERE charId > 0");
//		}
//		catch (Exception e)
//		{
//			_log.error(getClass().getSimpleName() + ": Error while removing all last month stats ! " + e.getMessage(), e);
//		}
//	}
//
//	/**
//	 * Salva todas las stats a la DB. Llamado unicamente al hacer shutdown
//	 */
//	public void saveMonthlyStats()
//	{
//		if (_monthlyStats.isEmpty())
//			return;
//
//		// Creo un query dinamico de cada valor que se va a actualizar en la DB, a partir del listado de rankings actuales
//		try (Connection con = DatabaseFactory.getInstance().getConnection();
//			PreparedStatement statement = con.prepareStatement("REPLACE INTO monthly_character_stats VALUES (?, ?, ?)"))
//		{
//			for (MonthlyStats stats : _monthlyStats.values())
//			{
//				// Solo salvamos a la DB aquellos pjs los cuales se les haya modificado alguna stat
//				if (!stats.isModified())
//					continue;
//
//				// Hago dinamico el seteo de parametros para ejecutar el update a traves de la lista de rankings que tengo
//				for (RankingTop top : _rankingsById.values())
//				{
//					statement.setInt(1, stats.getCharId());
//					statement.setString(2, top.getDbName());
//					statement.setLong(3, stats.getPlayerStats(top.getTopId()));
//					statement.execute();
//				}
//
//				// Ya que salvamos no hay modificaciones que salvar para la proxima
//				stats.setIsModified(false);
//			}
//		}
//		catch (Exception e)
//		{
//			_log.warn(getClass().getSimpleName() + ": Error while saving all character stats for this month ! " + e.getMessage(), e);
//		}
//	}
//
//	/**
//	 * Un thread que se va a ejecutar cada 1 hora y va a volver a cargar los tops de este mes. Sincronizamos para evitar que otras funciones accedan a la tabla
//	 */
//	protected class ReloadStatsFromDB implements Runnable
//	{
//		@Override
//		public void run()
//		{
//			synchronized (_rankingsById)
//			{
//				// Primero salvamos todos los cambios de stats actuales a la DB, y luego las volvemos a cargar. Se hace esto porque hay muchos pjs no existentes porque no estan online
//				saveMonthlyStats();
//
//				// Pausamos por las dudas, asi le damos tiempo
//				try
//				{
//					Thread.sleep(100);
//				}
//				catch (InterruptedException e1) {}
//
//				// Limpiamos los rankings actuales
//				for (RankingTop rank : _rankingsById.values())
//				{
//					rank.cleanPlayers();
//				}
//
//				// Obtenemos todas las stats desde la DB
//				try (Connection con = DatabaseFactory.getInstance().getConnection())
//				{
//					// Stats de pjs
//					// Los packs rusos tienen obj_Id en characters en vez de charId
//					try (PreparedStatement statement = con.prepareStatement("SELECT t1.*, t2.char_name, t2.accesslevel FROM monthly_character_stats AS t1 INNER JOIN characters AS t2 WHERE t2.accesslevel=0 AND t1.charId=t2.obj_Id");
//							ResultSet rset = statement.executeQuery())
//					{
//						while (rset.next())
//						{
//							// Cargamos dinamicamente cada estadistica de la DB, usando toda la informacion de los rankings de server. Asi no tengo que actualizar esto si agrego mas stats
//							for (RankingTop top : _rankingsById.values())
//							{
//								if (top.getDbName().equalsIgnoreCase(rset.getString("variable")))
//								{
//									// Agregamos este pj al ranking con sus puntos, que luego va a ser ordenado y va a salir el top 10 de cada categoria de este mes
//									top.addPlayer(new RankingTopMember(rset.getString("char_name"), rset.getLong("value")));
//								}
//							}
//						}
//					}
//
//					// Stats de clan
//					// Los packs rusos tienen obj_Id en characters en vez de charId
//					try (PreparedStatement statement = con.prepareStatement("SELECT t1.*, t2.accesslevel, t3.name FROM monthly_character_stats AS t1 INNER JOIN characters AS t2 " +
//						"INNER JOIN clan_subpledges as t3 WHERE t2.accesslevel=0 AND t3.leader_id=t2.obj_Id AND t3.clan_id=t1.charId");
//							ResultSet rset = statement.executeQuery())
//					{
//						while (rset.next())
//						{
//							// Cargamos dinamicamente cada estadistica de la DB, usando toda la informacion de los rankings de server. Asi no tengo que actualizar esto si agrego mas stats
//							for (RankingTop top : _rankingsById.values())
//							{
//								if (top.getDbName().equalsIgnoreCase(rset.getString("variable")))
//								{
//									// Agregamos este pj al ranking con sus puntos, que luego va a ser ordenado y va a salir el top 10 de cada categoria de este mes. Solo si no es gm
//									top.addPlayer(new RankingTopMember(rset.getString("name"), rset.getLong("value")));
//								}
//							}
//						}
//					}
//				}
//				catch (SQLException e)
//				{
//					e.printStackTrace();
//				}
//
//				// Ordenamos cada ranking disponible, a todos sus pjs, logrando un top completo, de los cuales luego solo voy a usar el top 10
//				for (RankingTop rank : _rankingsById.values())
//				{
//					final List<RankingTopMember> sortedTop = new ArrayList<RankingTopMember>(rank.getPlayers());
//					Collections.sort(sortedTop, RANKING_POINTS_COMPARATOR);
//
//					// Ponemos la lista de pjs ordenados en la categoria esta
//					rank.cleanPlayers();
//					rank.getPlayers().addAll(sortedTop);
//				}
//			}
//		}
//	}
//
//	// Clase especial para guardar las stats de cada pj en este mes
//	public class MonthlyStats
//	{
//		private final int _charId;
//		private boolean _isModified = false;
//		private final long[] _playerStats = new long[Ranking.STATS_SIZE];
//
//		public MonthlyStats(int charId)
//		{
//			_charId = charId;
//		}
//
//		public int getCharId()
//		{
//			return _charId;
//		}
//
//		public void setPlayerStats(int statId, long points)
//		{
//			_playerStats[statId] = points;
//		}
//
//		public void addPlayerStats(int statId, long points, boolean mustSet)
//		{
//			if (mustSet)
//			{
//				if (_playerStats[statId] < points)
//					_playerStats[statId] = points;
//			}
//			else
//				_playerStats[statId] += points;
//			_isModified = true;
//		}
//
//		public long getPlayerStats(int statId)
//		{
//			return _playerStats[statId];
//		}
//
//		public void setIsModified(boolean val)
//		{
//			_isModified = val;
//		}
//
//		public boolean isModified()
//		{
//			return _isModified;
//		}
//	}
//
//	protected static final Comparator<RankingTopMember> RANKING_POINTS_COMPARATOR = new Comparator<RankingTopMember>()
//	{
//		@Override
//		public int compare(RankingTopMember left, RankingTopMember right)
//		{
//	        if (left.getPoints() < right.getPoints())
//	            return 1;
//	        else if (left.getPoints() == right.getPoints())
//	            return 0;
//	        else
//	            return -1;
//		}
//	};
//
//	public static final CharacterMonthlyRanking getInstance()
//	{
//		return SingletonHolder._instance;
//	}
//
//	private static class SingletonHolder
//	{
//		protected static final CharacterMonthlyRanking _instance = new CharacterMonthlyRanking();
//	}
//}
