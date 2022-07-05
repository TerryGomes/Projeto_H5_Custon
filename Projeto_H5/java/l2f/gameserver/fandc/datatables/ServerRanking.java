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
//package l2f.gameserver.fandc.datatables;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.TreeMap;
//import java.util.concurrent.ConcurrentHashMap;
//
//import l2f.gameserver.Config;
//import l2f.gameserver.ThreadPoolManager;
//import l2f.gameserver.data.htm.HtmCache;
//import l2f.gameserver.database.DatabaseFactory;
//import l2f.gameserver.model.Player;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import l2f.gameserver.fandc.templates.Ranking;
//
///**
// * Este va a ser un holder para cargar el ranking top 10 de los 10 mejores de cada categoria, desde siempre
// * La idea es tener un ranking completo de decenas de categorias, por ejemplo, top 10 lvls, entonces buscamos los que tienen mayor lvl
// * y los cargamos a un array. Este array solo se llena al comenzar el server
// * Solo es un holder, no se guardan los datos en ningun lado ya que son stats desde siempre
// *
// * @author Synerge
// */
//public final class ServerRanking
//{
//	private static final Logger _log = LoggerFactory.getLogger(ServerRanking.class);
//
//	private static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("#,##0");
//
//	// Si muestra puntos
//	public static final boolean NO_POINTS = false;
//	public static final boolean SHOW_POINTS = true;
//
//	// Si la tabla debe ordenarse ascendente o descendente
//	public static final boolean ORDER_ASC = false;
//	public static final boolean ORDER_DESC = true;
//
//	// Si la estadistica es unica o acumulable
//	public static final boolean STAT_UNIQUE = false;
//	public static final boolean STAT_STACKABLE = true;
//
//	// Si tiene un tiempo asociado, se modifica el valor para lograr que el resultado sea en minutos
//	public enum RankTime
//	{
//		NONE,
//		MILISECONDS,
//		SECONDS,
//		MINUTES,
//		HOURS
//	}
//
//	// Listas para guardar los Top 10 de cada categoria de tiempo Real, se carga 1 sola vez cuando se crea esta tabla
//	protected final Map<Integer, RankingTop> _rankingsById = new ConcurrentHashMap<>();
//	private final Map<String, List<Integer>> _rankingsByCategory = new TreeMap<>();
//
//	protected ServerRanking()
//	{
//		// Inicializamos todas las variables de rankings
//		Ranking.generateRankings(_rankingsById);
//
//		// Llenamos los arrays con los datos de la db
//		loadTopTens();
//
//		// Creamos un thread que va a recargar los tops cada 1 hora
//		ThreadPoolManager.getInstance().scheduleAtFixedRate(new ReloadStatsFromDB(), 60 * 60 * 1000, 60 * 60 * 1000);
//	}
//
//	/**
//	 * Cargamos todos los Top 10 de la DB solo una vez, cuando inicia el server
//	 */
//	private void loadTopTens()
//	{
//		// Antes de cargar cada TOP, lo inicializamos con su id, el nombre del top y a que categoria pertenece
//		String statementText = "";
//
//		try (Connection con = DatabaseFactory.getInstance().getConnection())
//		{
//			// Cargamos dinamicamente cada top de la DB
//			for (RankingTop top : _rankingsById.values())
//			{
//				top.cleanPlayers();
//
//				// Si esta en la parte de estadisticas o es un valor que esta directamente en characters. Debe no estar baneado, ser mayor a lvl 40
//				// Los packs rusos no poseen level en characters, sino que estan en subclass, asi que directamente evito el chequeo de lvl para no hacer problemas
//				switch (top.getDbLocation())
//				{
//					case "characters":
//						statementText = "SELECT char_name," + top.getDbName() + " FROM characters WHERE accesslevel=0 ORDER BY " + top.getDbName()
//							+ (top.isDescendent() ? " DESC" : " ASC") + " LIMIT 10";
//						break;
//					case "clan_stats":
//						statementText = "SELECT t1.name,t2.value FROM clan_subpledges AS t1 INNER JOIN clan_stats AS t2 INNER JOIN characters AS t3 "
//							+ "WHERE t3.accesslevel=0 AND t1.clan_id=t2.clanId AND t2.variable='" + top.getDbName() + "' AND t1.leader_id=t3.obj_Id "
//							+ "ORDER BY t2.value " + (top.isDescendent() ? "DESC" : "ASC") + " LIMIT 10";
//						break;
//					case "character_subclasses":
//						statementText = "SELECT t1.char_name,t2." + top.getDbName() + " FROM characters AS t1 INNER JOIN character_subclasses AS t2 "
//							+ "WHERE t1.accesslevel=0 AND t1.obj_Id=t2.char_obj_id AND t2.isBase=1 "
//							+ "ORDER BY " + top.getDbName() + " " + (top.isDescendent() ? "DESC" : "ASC") + " LIMIT 10";
//						break;
//					default:
//						statementText = "SELECT t1.char_name,t2.value FROM characters AS t1 INNER JOIN " + top.getDbLocation() + " AS t2 "
//							+ " INNER JOIN character_subclasses as t3 WHERE t3.level >= 40 AND t1.accesslevel=0 AND t1.obj_Id=t2.charId AND t1.obj_Id=t3.char_obj_id "
//							+ "AND t3.isBase=1 AND t2.variable='" + top.getDbName()
//							+ "' ORDER BY t2.value " + (top.isDescendent() ? "DESC" : "ASC") + " LIMIT 10";
//						break;
//				}
//
//				try (PreparedStatement stmt = con.prepareStatement(statementText);
//					ResultSet rset = stmt.executeQuery())
//				{
//					while (rset.next())
//					{
//						switch (top.getDbLocation())
//						{
//							case "characters":
//							case "character_subclasses":
//								top.addPlayer(new RankingTopMember(rset.getString("char_name"), rset.getLong(top.getDbName())));
//								break;
//							case "clan_stats":
//								top.addPlayer(new RankingTopMember(rset.getString("name"), rset.getLong("value")));
//								break;
//							default:
//								top.addPlayer(new RankingTopMember(rset.getString("char_name"), rset.getLong("value")));
//								break;
//						}
//					}
//				}
//			}
//
//			_log.info(getClass().getSimpleName() + ": Loaded " + _rankingsById.size() + " statics categories for the ranking");
//		}
//		catch (Exception e)
//		{
//			_log.error(getClass().getSimpleName() + ": Error while loading rankings: " + e.getMessage() + ", " + statementText);
//			e.printStackTrace();
//		}
//
//		// Por ultimo recorremos todos los rankings y creamos un array alterno que va a tener como key su categoria
//		for (RankingTop rank : _rankingsById.values())
//		{
//			if (!_rankingsByCategory.containsKey(rank.getCategory(null)))
//				_rankingsByCategory.put(rank.getCategory(null), new ArrayList<Integer>());
//
//			_rankingsByCategory.get(rank.getCategory(null)).add(rank.getTopId());
//		}
//
//		// Ordenamos todos los tops segun su nombre, en vez de su ids
//		for (List<Integer> category : _rankingsByCategory.values())
//		{
//			Collections.sort(category, new Comparator<Integer>()
//			{
//				@Override
//				public int compare(Integer left, Integer right)
//				{
//					return _rankingsById.get(left).getTopName(null).compareTo(_rankingsById.get(right).getTopName(null));
//				}
//			});
//		}
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
//	 * @return Devuelve toda la informacion sobre cada ranking disponible
//	 */
//	public Map<Integer, RankingTop> getAllRankings()
//	{
//		return _rankingsById;
//	}
//
//	/**
//	 * @return Devuelve la lista de rankings ordenados segun categorias
//	 */
//	public Map<String, List<Integer>> getRankingsByCategory()
//	{
//		return _rankingsByCategory;
//	}
//
//	/**
//	 * @param activeChar
//	 * @param topId
//	 * @return Devuelve el html del ranking del server. Se va modificando segun que categoria se elije
//	 */
//	public String makeServerRankingHtm(Player activeChar, int topId)
//	{
//		String content = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "ranking_server.htm", activeChar);
//		if (content == null)
//			return "<html><body><br><br><center>404 :File Not foud: 'data/html/CommunityBoard/ranking_server.htm' </center></body></html>";
//
//		final RankingTop serverTop = getRankingById(topId);
//		final RankingTop monthTop = CharacterMonthlyRanking.getInstance().getRankingById(topId);
//		int i = 1;
//
//		// Llenamos el top 10 del ranking del mes de esta categoria
//		for (RankingTopMember members : monthTop.getPlayers())
//		{
//			// Si es descendente, debe ser mayor a 0 el valor, en caso contrario no lo ocupa nadie
//			if (monthTop.isDescendent() && members.getPoints() < 1)
//				break;
//
//			content = content.replace("%mNombre" + i + "%", members.getCharName());
//			if (monthTop.mustShowPoints())
//				content = content.replace("%mCantidad" + i + "%", getFormatedRankValue(activeChar, monthTop.getTime(), members.getPoints()));
//			else
//				content = content.replace("%mCantidad" + i + "%", "");
//			i++;
//
//			if (i > 10)
//				break;
//		}
//
//		while (i < 11)
//		{
//			content = content.replace("%mNombre" + i + "%", "-");
//			if (monthTop.mustShowPoints())
//				content = content.replace("%mCantidad" + i + "%", "0");
//			else
//				content = content.replace("%mCantidad" + i + "%", "");
//			i++;
//		}
//
//		// Ahora llenamos el top 10 del ranking total de esta categoria
//		i = 1;
//		for (RankingTopMember members : serverTop.getPlayers())
//		{
//			// Si es descendente, debe ser mayor a 0 el valor, en caso contrario no lo ocupa nadie
//			if (serverTop.isDescendent() && members.getPoints() < 1)
//				break;
//
//			content = content.replace("%tNombre" + i + "%", members.getCharName());
//			if (serverTop.mustShowPoints())
//				content = content.replace("%tCantidad" + i + "%", getFormatedRankValue(activeChar, serverTop.getTime(), members.getPoints()));
//			else
//				content = content.replace("%tCantidad" + i + "%", "");
//			i++;
//
//			if (i > 10)
//				break;
//		}
//
//		while (i < 11)
//		{
//			content = content.replace("%tNombre" + i + "%", "-");
//			if (serverTop.mustShowPoints())
//				content = content.replace("%tCantidad" + i + "%", "0");
//			else
//				content = content.replace("%tCantidad" + i + "%", "");
//			i++;
//		}
//
//		// Reemplazamos los nombres de todos los tabs y menues, segun la traduccion que corresponda
//		content = content.replace("%rank_tab_server_record%", TranslationMessagesTable.getInstance().getMessage(activeChar, "rank_tab_server_record"));
//		content = content.replace("%rank_tab_my_record%", TranslationMessagesTable.getInstance().getMessage(activeChar, "rank_tab_my_record"));
//		content = content.replace("%rank_tab_monthly_ranking%", TranslationMessagesTable.getInstance().getMessage(activeChar, "rank_tab_monthly_ranking"));
//		content = content.replace("%rank_tab_total_ranking%", TranslationMessagesTable.getInstance().getMessage(activeChar, "rank_tab_total_ranking"));
//
//		// Ahora recorremos cada categoria de top, y vamos escribiendo los botones de la categoria
//		final StringBuilder str = new StringBuilder();
//		i = 1;
//
//		for (Entry<String, List<Integer>> tops : getRankingsByCategory().entrySet())
//		{
//			// Si esta categoria es la categoria en la que esta el ranking que cliqueamos, entonces la abrimos y escribimos todos los rankings que contienen
//			if (tops.getKey().equalsIgnoreCase(serverTop.getCategory(null)))
//			{
//				str.append("<table width=260 cellspacing=-2>");
//				str.append("<tr><td width=6></td><td><button value=\"" + TranslationMessagesTable.getInstance().getMessage(activeChar, tops.getKey()) + "\" width=258 height=32 action=\"\" fore=L2UI_CT1.Button_DF_Calculator_Down back=L2UI_CT1.Button_DF_Calculator_Down></td></tr>");
//				str.append(" </table>");
//				str.append("<table width=260 height=360 cellspacing=-1>");
//
//				for (Integer rankId : tops.getValue())
//				{
//					if (rankId == topId)
//						str.append("<tr><td align=center height=25><button value=\"" + getRankingById(rankId).getTopName(activeChar) + "\" width=255 height=25 action=\"\" fore=L2UI_CH3.chatting_system back=L2UI_CH3.chatting_system></td></tr>");
//					else
//						str.append("<tr><td align=center height=25><button value=\"" + getRankingById(rankId).getTopName(activeChar) + "\" width=255 height=25 action=\"bypass _bbsrank;server;" + rankId + "\" fore=L2UI_CT1.ChatBalloon_DF_MiddleCenter back=L2UI_CT1.ChatBalloon_DF_MiddleCenter></td></tr>");
//					i++;
//				}
//
//				while (i < 16)
//				{
//					str.append("<tr><td align=center height=25><button value=\"\" width=255 height=25 action=\"\" fore=L2UI_CT1.ChatBalloon_DF_MiddleCenter back=L2UI_CT1.ChatBalloon_DF_MiddleCenter></td></tr>");
//					i++;
//				}
//
//				str.append("</table>");
//			}
//			// Si no es el ranking que buscamos, solo escribimos el boton de la categoria
//			else
//			{
//				str.append("<table width=260 cellspacing=-3>");
//				str.append("<tr><td width=7></td><td><button value=\"" + TranslationMessagesTable.getInstance().getMessage(activeChar, tops.getKey()) + "\" width=260 height=24 action=\"bypass _bbsrank;server;" + tops.getValue().get(0) + "\" fore=L2UI_CT1.Button_DF back=L2UI_CT1.Button_DF_Calculator_Down></td></tr>");
//				str.append("</table>");
//			}
//		}
//
//		content = content.replace("%categorias%", str.toString());
//		return content;
//	}
//
//	/**
//	 * @param activeChar
//	 * @param topId
//	 * @return Devuelve el html del ranking del server. Se va modificando segun que categoria se elije, y si es estadistica del server completo o solo del pj
//	 */
//	public String makeSelfRankingHtm(Player activeChar, int topId)
//	{
//		String content = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "ranking_my.htm", activeChar);
//		if (content == null)
//			return "<html><body><br><br><center>404 File Not Found: 'ranking_my.htm' - Please inform a GM! </center></body></html>";
//
//		final RankingTop top = getRankingById(topId);
//
//		// Reemplazamos los nombres de todos los tabs y menues, segun la traduccion que corresponda
//		content = content.replace("%rank_tab_server_record%", TranslationMessagesTable.getInstance().getMessage(activeChar, "rank_tab_server_record"));
//		content = content.replace("%rank_tab_my_record%", TranslationMessagesTable.getInstance().getMessage(activeChar, "rank_tab_my_record"));
//		content = content.replace("%rank_tab_category%", TranslationMessagesTable.getInstance().getMessage(activeChar, "rank_tab_category"));
//		content = content.replace("%rank_tab_monthly_total%", TranslationMessagesTable.getInstance().getMessage(activeChar, "rank_tab_monthly_total"));
//		content = content.replace("%rank_tab_total%", TranslationMessagesTable.getInstance().getMessage(activeChar, "rank_tab_total"));
//
//		// Ahora recorremos cada categoria de top, y vamos escribiendo los botones de la categoria
//		final StringBuilder categorias = new StringBuilder();
//		final StringBuilder valores = new StringBuilder();
//		RankingTop rank;
//		boolean changeColor = false;
//
//		for (Entry<String, List<Integer>> tops : getRankingsByCategory().entrySet())
//		{
//			// Si esta categoria es la categoria en la que esta el ranking que cliqueamos, entonces la abrimos y escribimos todos los rankings que contienen
//			if (tops.getKey().equalsIgnoreCase(top.getCategory(null)))
//			{
//				categorias.append("<tr><td width=15></td><td><button value=\"" + TranslationMessagesTable.getInstance().getMessage(activeChar, tops.getKey()) + "\" width=150 height=40 action=\"\" fore=L2UI_CT1.Button_DF_Calculator_Over back=L2UI_CT1.Button_DF_Calculator_Over></td></tr>");
//
//				for (Integer rankId : tops.getValue())
//				{
//					rank = getRankingById(rankId);
//					valores.append("<table width=598 height=26 cellpadding=-2 bgcolor=" + (changeColor ? "171612" : "23221e") + ">");
//					valores.append("<tr>");
//					valores.append("<td fixwidth=240 align=center>" + rank.getTopName(activeChar) + "</td>");
//					valores.append("<td fixwidth=180 align=center>" + getFormatedRankValue(activeChar, rank.getTime(), CharacterMonthlyRanking.getInstance().getPlayerStats((rank.isClanStat() && activeChar.getClan() != null ? activeChar.getClanId() : activeChar.getObjectId()), rankId)) + "</td>");
//					valores.append("<td fixwidth=180 align=center>" + getFormatedRankValue(activeChar, rank.getTime(), (rank.isClanStat() && activeChar.getClan() != null ? activeChar.getClan().getStats().getClanStats(rankId) : activeChar.getStats().getPlayerStats(rankId))) + "</td>");
//					valores.append("</tr>");
//					valores.append("</table>");
//					changeColor = !changeColor;
//				}
//			}
//			// Si no es el ranking que buscamos, solo escribimos el boton de la categoria
//			else
//			{
//				categorias.append("<tr><td width=15></td><td><button value=\"" + TranslationMessagesTable.getInstance().getMessage(activeChar, tops.getKey()) + "\" width=150 height=40 action=\"bypass _bbsrank;my;" + tops.getValue().get(0) + "\" fore=L2UI_CT1.Button_DF back=L2UI_CT1.Button_DF_Calculator_Down></td></tr>");
//			}
//		}
//
//		content = content.replace("%categorias%", categorias.toString());
//		content = content.replace("%tablas%", valores.toString());
//		return content;
//	}
//
//	/**
//	 * @param player
//	 * @param time
//	 * @param value
//	 * @return Devuelve el valor formateado segun tiempos, extras, divisores, etc para los rankings
//	 */
//	private String getFormatedRankValue(Player player, RankTime time, long value)
//	{
//		// Si posee un tiempo, debemos adaptarlo a minutos y agregarle la medida
//		switch (time)
//		{
//			case MILISECONDS:
//				value /= (1000 * 60);
//				break;
//			case SECONDS:
//				value /= 60;
//				break;
//			case MINUTES:
//				break;
//			case HOURS:
//				value *= 60;
//				break;
//			default:
//			case NONE:
//				return DECIMAL_FORMATTER.format(value).replace(".", ",");
//		}
//
//		// Si es mayor a 1000 minutos, usamos horas
//		if (value > 1000)
//			return DECIMAL_FORMATTER.format((int)(value / 60)).replace(".", ",") + " Hour(s)";
//
//		return DECIMAL_FORMATTER.format(value).replace(".", ",") + " Minute(s)";
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
//				// Antes de cargar cada TOP, lo inicializamos con su id, el nombre del top y a que categoria pertenece
//				try (Connection con = DatabaseFactory.getInstance().getConnection())
//				{
//					String statementText;
//
//					// Cargamos dinamicamente cada top de la DB
//					for (RankingTop top : _rankingsById.values())
//					{
//						top.cleanPlayers();
//
//						// Si esta en la parte de estadisticas o es un valor que esta directamente en characters. Debe no estar baneado, ser mayor a lvl 40
//						// Los packs rusos no poseen level en characters, sino que estan en subclass, asi que directamente evito el chequeo de lvl para no hacer problemas
//						switch (top.getDbLocation())
//						{
//							case "characters":
//								statementText = "SELECT char_name," + top.getDbName() + " FROM characters WHERE accesslevel=0 ORDER BY " + top.getDbName()
//									+ (top.isDescendent() ? " DESC" : " ASC") + " LIMIT 10";
//								break;
//							case "clan_stats":
//								statementText = "SELECT t1.name,t2.value FROM clan_subpledges AS t1 INNER JOIN clan_stats AS t2 INNER JOIN characters AS t3 "
//									+ "WHERE t3.accesslevel=0 AND t1.clan_id=t2.clanId AND t2.variable='" + top.getDbName() + "' AND t1.leader_id=t3.obj_Id "
//									+ "ORDER BY t2.value " + (top.isDescendent() ? "DESC" : "ASC") + " LIMIT 10";
//								break;
//							case "character_subclasses":
//								statementText = "SELECT t1.char_name,t2." + top.getDbName() + " FROM characters AS t1 INNER JOIN character_subclasses AS t2 "
//									+ "WHERE t1.accesslevel=0 AND t1.obj_Id=t2.char_obj_id AND t2.isBase=1 "
//									+ "ORDER BY " + top.getDbName() + " " + (top.isDescendent() ? "DESC" : "ASC") + " LIMIT 10";
//								break;
//							default:
//								statementText = "SELECT t1.char_name,t2.value FROM characters AS t1 INNER JOIN " + top.getDbLocation() + " AS t2 "
//									+ " INNER JOIN character_subclasses as t3 WHERE t3.level >= 40 AND t1.accesslevel=0 AND t1.obj_Id=t2.charId AND t1.obj_Id=t3.char_obj_id "
//									+ "AND t3.isBase=1 AND t2.variable='" + top.getDbName()
//									+ "' ORDER BY t2.value " + (top.isDescendent() ? "DESC" : "ASC") + " LIMIT 10";
//								break;
//						}
//
//						try (PreparedStatement stmt = con.prepareStatement(statementText);
//							ResultSet rset = stmt.executeQuery())
//						{
//							while (rset.next())
//							{
//								switch (top.getDbLocation())
//								{
//									case "character_subclasses":
//									case "characters":
//										top.addPlayer(new RankingTopMember(rset.getString("char_name"), rset.getLong(top.getDbName())));
//										break;
//									case "clan_stats":
//										top.addPlayer(new RankingTopMember(rset.getString("name"), rset.getLong("value")));
//										break;
//									default:
//										top.addPlayer(new RankingTopMember(rset.getString("char_name"), rset.getLong("value")));
//										break;
//								}
//							}
//						}
//					}
//				}
//				catch (SQLException e)
//				{
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//
//	public static ServerRanking getInstance()
//	{
//		return SingletonHolder._instance;
//	}
//
//	private static class SingletonHolder
//	{
//		protected static final ServerRanking _instance = new ServerRanking();
//	}
//
//	// Clase para almacenar los datos de un top en particular
//	public static class RankingTop
//	{
//		private final int _topId;
//		private final String _name;
//		private final String _category;
//		private final String _dbName;
//		private final boolean _mustShowPoints;
//		private final String _dbLocation;
//		private final boolean _isDescendent;
//		private final boolean _isStackable;
//		private final RankTime _time;
//		private final ArrayList<RankingTopMember> _players = new ArrayList<>();
//
//		public RankingTop(int topId, String name, String category, String dbName, boolean mustShowPoints, String dbLocation, boolean isDescendent, boolean isStackable, RankTime time)
//		{
//			_topId = topId;
//			_name = name;
//			_category = category;
//			_dbName = dbName;
//			_mustShowPoints = mustShowPoints;
//			_dbLocation = dbLocation;
//			_isDescendent = isDescendent;
//			_isStackable = isStackable;
//			_time = time;
//		}
//
//		public void addPlayer(RankingTopMember player)
//		{
//			_players.add(player);
//		}
//
//		public void cleanPlayers()
//		{
//			_players.clear();
//		}
//
//		public int getTopId()
//		{
//			return _topId;
//		}
//
//		public String getTopName(Player player)
//		{
//			return TranslationMessagesTable.getInstance().getMessage(player, _name);
//		}
//
//		public String getCategory(Player player)
//		{
//			return TranslationMessagesTable.getInstance().getMessage(player, _category);
//		}
//
//		public String getDbName()
//		{
//			return _dbName;
//		}
//
//		public boolean mustShowPoints()
//		{
//			return _mustShowPoints;
//		}
//
//		public String getDbLocation()
//		{
//			return _dbLocation;
//		}
//
//		public boolean isDescendent()
//		{
//			return _isDescendent;
//		}
//
//		public boolean isStackable()
//		{
//			return _isStackable;
//		}
//
//		public boolean isClanStat()
//		{
//			return getDbLocation().equalsIgnoreCase("clan_stats");
//		}
//
//		public RankTime getTime()
//		{
//			return _time;
//		}
//
//		public ArrayList<RankingTopMember> getPlayers()
//		{
//			return _players;
//		}
//	}
//
//	// Clase simple para almacenar el nombre y los puntos del pj que pertenece al top
//	public static class RankingTopMember
//	{
//		public long _points = 0;
//		private String _charName = "";
//
//		public RankingTopMember(String charName, long points)
//		{
//			_charName = charName;
//			_points = points;
//		}
//
//		public String getCharName()
//		{
//			return _charName;
//		}
//
//		public long getPoints()
//		{
//			return _points;
//		}
//	}
//}
