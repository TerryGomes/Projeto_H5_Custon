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
//package l2mv.gameserver.fandc.templates;
//
//import java.util.Map;
//
//import l2mv.gameserver.fandc.datatables.ServerRanking;
//import l2mv.gameserver.fandc.datatables.ServerRanking.RankTime;
//import l2mv.gameserver.fandc.datatables.ServerRanking.RankingTop;
//
//
///**
// * Statics list for the ranking
// *
// * @author Synerge
// */
//public enum Ranking
//{
//	// Structure: Name, Category, DbCellName, If must show points, DbTableName, If must be ordered descendent, If is unique or stackable (set or add from the last value)
//	STAT_TOP_PVP_KILLS("rank_top_pvpkills", "rank_category_combat", "pvpkills", ServerRanking.SHOW_POINTS, "characters", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top PvP
//	STAT_TOP_PK_KILLS("rank_top_pkkills", "rank_category_combat", "real_pk_kills", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Pk
//	STAT_TOP_EXP("rank_top_experience", "rank_category_general", "exp", ServerRanking.SHOW_POINTS, "character_subclasses", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Exp
//	STAT_TOP_PVP_DEATHS("rank_top_pvpdeaths", "rank_category_combat", "pvp_deaths", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top PvP Deaths
//	STAT_TOP_PK_DEATHS("rank_top_pkdeaths", "rank_category_combat", "pk_deaths", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Pk Deaths
//	STAT_TOP_CRAFTS_SUCCEED("rank_top_crafts_succeed", "rank_category_general", "crafts_succeed", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Successful Crafts
//	STAT_TOP_CRAFTS_FAILED("rank_top_crafts_failed", "rank_category_general", "crafts_failed", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Failed Crafts
//	STAT_TOP_ONLINE("rank_top_onlinetime", "rank_category_general", "onlinetime", ServerRanking.SHOW_POINTS, "characters", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE, RankTime.SECONDS), // Top Online
//	STAT_TOP_BSOES_USED("rank_top_bsoes_used", "rank_category_general", "bsoes_used", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top BSOEs Usados
//	STAT_TOP_EVENTS_PARTICIPATED("rank_top_events_participated", "rank_category_general", "events_participated", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Eventos Participados
//	STAT_TOP_SIEGE_KILLS("rank_top_siege_kills", "rank_category_combat", "siege_kills", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Kills en Asedios
//	STAT_TOP_SIEGE_DEATHS("rank_top_siege_deaths", "rank_category_combat", "siege_deaths", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Muertes en Asedios
//	STAT_TOP_RAIDS_KILLED("rank_top_raids_killed", "rank_category_hunting", "raids_killed", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Raids Matados
//	STAT_TOP_ENCHANTS_SUCCEED("rank_top_enchants_succeed", "rank_category_general", "enchants_succeed", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Enchants Exitosos
//	STAT_TOP_ENCHANTS_FAILED("rank_top_enchants_failed", "rank_category_general", "enchants_failed", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Enchants Fallidos
//	STAT_TOP_FISHES_CAPTURED("rank_top_fishes_captured", "rank_category_hunting", "fishes_captured", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Peces Capturados
//	STAT_TOP_QUESTS_FINISHED("rank_top_quests_finished", "rank_category_general", "quests_finished", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Quests Terminadas
//	STAT_TOP_ARENA_KILLS("rank_top_arena_kills", "rank_category_combat", "arena_kills", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Kills en Arena
//	STAT_TOP_ARENA_DEATHS("rank_top_arena_deaths", "rank_category_combat", "arena_deaths", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Deaths en Arena
//	STAT_TOP_OLY_KILLS("rank_top_oly_kills", "rank_category_combat", "oly_kills", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Combates de Olys Ganados
//	STAT_TOP_OLY_DEATHS("rank_top_oly_deaths", "rank_category_combat", "oly_deaths", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Combates de Olys Perdidos
//	STAT_TOP_MOBS_KILLS("rank_top_mobs_kills", "rank_category_hunting", "mobs_kills", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Mobs Matados
//	STAT_TOP_MOBS_DEATHS("rank_top_mobs_death", "rank_category_hunting", "mobs_death", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Muertes por Mobs
//	STAT_TOP_DUELS_WIN("rank_top_duels_win", "rank_category_combat", "duels_win", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Duelos Ganados
//	STAT_TOP_DUELS_LOST("rank_top_duels_lost", "rank_category_combat", "duels_lost", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Duelos Perdidos
//	STAT_TOP_LONGEST_ONLINE_TIME("rank_top_longest_online", "rank_category_general", "longestOnline", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_UNIQUE, RankTime.MINUTES), // Top Tiempo Online mas Largo
//	STAT_TOP_EXP_ACQUIRED("rank_top_exp_acquired", "rank_category_hunting", "exp_acquired", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Experiencia Obtenida
//	STAT_TOP_EXP_LOST("rank_top_exp_lost", "rank_category_hunting", "exp_lost", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Experiencia Perdida
//	STAT_TOP_ADENA_ACQUIRED("rank_top_adena_acquired", "rank_category_general", "adena_acquired", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_UNIQUE), // Top Adena Obtenida
//	STAT_TOP_FAME_ACQUIRED("rank_top_fame_acquired", "rank_category_general", "fame_acquired", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Fama Obtenida
//	STAT_TOP_DAMAGE("rank_top_damage_done", "rank_category_general", "damage_done", ServerRanking.SHOW_POINTS, "character_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Daï¿½o Hecho
//	STAT_TOP_CLAN_MEMBERS_COUNT("rank_top_clan_members_count", "rank_category_clan", "clan_members_count", ServerRanking.SHOW_POINTS, "clan_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_UNIQUE), // Top Clan Members Count
//	STAT_TOP_CLAN_MEMBERS_RECRUITED("rank_top_clan_members_recruited", "rank_category_clan", "clan_members_recruited", ServerRanking.SHOW_POINTS, "clan_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Clan Members Recruited
//	STAT_TOP_CLAN_MEMBERS_WITHDREW("rank_top_clan_members_withdrew", "rank_category_clan", "clan_members_withdrew", ServerRanking.SHOW_POINTS, "clan_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Clan Members Withdrew
//	STAT_TOP_CLAN_FAME("rank_top_clan_fame", "rank_category_clan", "clan_fame", ServerRanking.SHOW_POINTS, "clan_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Clan Fame
//	STAT_TOP_CLAN_PVP_KILLS("rank_top_clan_pvp_kills", "rank_category_clan", "clan_pvp_kills", ServerRanking.SHOW_POINTS, "clan_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Clan PvP Kills
//	STAT_TOP_CLAN_WARS_WON("rank_top_clan_wars_won", "rank_category_clan", "clan_wars_won", ServerRanking.SHOW_POINTS, "clan_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE), // Top Clan Wars Won
//	STAT_TOP_CLAN_WARS_LOST("rank_top_clan_wars_lost", "rank_category_clan", "clan_wars_lost", ServerRanking.SHOW_POINTS, "clan_stats", ServerRanking.ORDER_DESC, ServerRanking.STAT_STACKABLE); // Top Clan Wars Lost
//
//	public static final int STATS_SIZE = Ranking.values().length + 1;
//
//	private final String _name;
//	private final String _category;
//	private final String _dbName;
//	private final boolean _mustShowPoints;
//	private final String _dbLocation;
//	private final boolean _isDescendent;
//	private final boolean _isStackable;
//	private final RankTime _time;
//
//	private Ranking(String name, String category, String dbName, boolean mustShowPoints, String dbLocation, boolean isDescendent, boolean isStackable)
//	{
//		_name = name;
//		_category = category;
//		_dbName = dbName;
//		_mustShowPoints = mustShowPoints;
//		_dbLocation = dbLocation;
//		_isDescendent = isDescendent;
//		_isStackable = isStackable;
//		_time = RankTime.NONE;
//	}
//
//	private Ranking(String name, String category, String dbName, boolean mustShowPoints, String dbLocation, boolean isDescendent, boolean isStackable, RankTime time)
//	{
//		_name = name;
//		_category = category;
//		_dbName = dbName;
//		_mustShowPoints = mustShowPoints;
//		_dbLocation = dbLocation;
//		_isDescendent = isDescendent;
//		_isStackable = isStackable;
//		_time = time;
//	}
//
//	public int getTopId()
//	{
//		return ordinal();
//	}
//
//	public String getTopName()
//	{
//		return _name;
//	}
//
//	public String getCategory()
//	{
//		return _category;
//	}
//
//	public String getDbName()
//	{
//		return _dbName;
//	}
//
//	public boolean mustShowPoints()
//	{
//		return _mustShowPoints;
//	}
//
//	public String getDbLocation()
//	{
//		return _dbLocation;
//	}
//
//	public boolean isDescendent()
//	{
//		return _isDescendent;
//	}
//
//	public boolean isStackable()
//	{
//		return _isStackable;
//	}
//
//	public RankTime getTime()
//	{
//		return _time;
//	}
//
//	/**
//	 * Genera la lista de Rankings en un mapa con la clase RankingTop
//	 *
//	 * @param rankings
//	 */
//	public static void generateRankings(Map<Integer, RankingTop> rankings)
//	{
//		for (Ranking rank : Ranking.values())
//		{
//			rankings.put(rank.getTopId(), new RankingTop(rank.getTopId(), rank.getTopName(), rank.getCategory(),
//								rank.getDbName(), rank.mustShowPoints(), rank.getDbLocation(), rank.isDescendent(), rank.isStackable(), rank.getTime()));
//		}
//	}
//}
