package services.community;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.handler.bbs.CommunityBoardManager;
import l2f.gameserver.handler.bbs.ICommunityBoardHandler;
import l2f.gameserver.idfactory.IdFactory;
import l2f.gameserver.instancemanager.ServerVariables;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.model.base.ClassId;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.items.ItemInstance.ItemLocation;
import l2f.gameserver.model.mail.Mail;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.network.serverpackets.ExNoticePostArrived;
import l2f.gameserver.network.serverpackets.ShowBoard;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.tables.ClanTable;
import l2f.gameserver.utils.Util;

/**
 * Custom ranking community system
 *
 * @author Synerge
 */
public class CommunityRanking implements ScriptFile, ICommunityBoardHandler
{
	private static final Logger _log = LoggerFactory.getLogger(CommunityRanking.class);

	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

	private static final boolean USE_TALES_DESIGN = true;

	private final int TIME_UPDATE = 60 * 60 * 1000;

	private final Map<RankingEnum, List<RankingStats>> _stats = new ConcurrentHashMap<>();
	private long _lastUpdate = System.currentTimeMillis();
	private volatile boolean _isLoading = false;
	private Future<?> _reloadThread;

	private static enum RankingType
	{
		NORMAL, REWARDS, SPECIAL
	}

	private static enum RankingEnum
	{
		PVP("PvP", "pk", "pvpkills", RankingType.NORMAL), PK("PK", "pvp", "pkkills", RankingType.NORMAL), RAID("Raid Kills", "rk", "raidkills", RankingType.NORMAL),
		EVENT("Event Kills", "event", "eventKills", RankingType.NORMAL), SIEGE("Siege Kills", "siege", "siege_kills", RankingType.NORMAL), OLY("Olympiad Wins", "oly", "oly_wins", RankingType.NORMAL),
		ADENA("Adena", "adena", "adena", RankingType.SPECIAL);

		private final String _name;
		private final String _bypass;
		private final String _dbName;
		private final RankingType _rankingType;

		private RankingEnum(String name, String bypass, String dbName, RankingType rankingType)
		{
			_name = name;
			_bypass = bypass;
			_dbName = dbName;
			_rankingType = rankingType;
		}

		public String getName()
		{
			return _name;
		}

		public String getBypass()
		{
			return _bypass;
		}

		public String getDbName()
		{
			return _dbName;
		}

		public RankingType getRankingType()
		{
			return _rankingType;
		}

		public static boolean hasRankWithRewards()
		{
			for (RankingEnum rank : values())
			{
				if (rank.getRankingType() == RankingType.REWARDS)
				{
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public void onLoad()
	{
		if (Config.COMMUNITYBOARD_ENABLED)
		{
			_log.info("Ranking in the community board has been updated.");
			CommunityBoardManager.getInstance().registerHandler(this);

			loadRankings();

			// Create a thread to reload the rankings every xxx time
			_reloadThread = ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
			{
				loadRankings();
				_log.info("Ranking in the community board has been updated.");
			}, TIME_UPDATE, TIME_UPDATE);
		}
	}

	@Override
	public void onReload()
	{
		if (Config.COMMUNITYBOARD_ENABLED)
		{
			CommunityBoardManager.getInstance().removeHandler(this);

			if (_reloadThread != null)
			{
				_reloadThread.cancel(true);
				_reloadThread = null;
				_isLoading = false;
			}
		}
	}

	@Override
	public String[] getBypassCommands()
	{
		return new String[]
		{
			"_bbsloc",
			"_bbsranking"
		};
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		if (_isLoading)
		{
			return;
		}

		final StringTokenizer st = new StringTokenizer(bypass, ":");
		st.nextToken();
		final String type = (st.hasMoreTokens() ? st.nextToken() : "pvp");
		final int page = (st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 0);

		switch (type)
		{
		case "pk":
			showRanking(player, RankingEnum.PK, page);
			break;
		case "pvp":
			showRanking(player, RankingEnum.PVP, page);
			break;
		case "rk":
			showRanking(player, RankingEnum.RAID, page);
			break;
		case "event":
			showRanking(player, RankingEnum.EVENT, page);
			break;
		case "siege":
			showRanking(player, RankingEnum.SIEGE, page);
			break;
		case "oly":
			showRanking(player, RankingEnum.OLY, page);
			break;
		case "adena":
			if (player.getAccessLevel() > 0)
			{
				showRanking(player, RankingEnum.ADENA, page);
			}
			break;
		}
	}

	private void showRanking(Player player, RankingEnum type, int page)
	{
		// Tales alternative design
		if (USE_TALES_DESIGN)
		{
			showTalesRanking(player, type, page);
			return;
		}

		final List<RankingStats> stats = _stats.get(type);

		String html = HtmCache.getInstance().getNotNull(
					Config.BBS_HOME_DIR + "ranking/" + (type.getRankingType() == RankingType.REWARDS ? "rank_rewards" : (type.getRankingType() == RankingType.NORMAL ? "rank_normal" : "rank_special") + ".htm"),
					player);

		// Top 3
		int index = 0;
		while (index < 3)
		{
			if (stats.size() > index)
			{
				final RankingStats playerStats = stats.get(index);
				html = html.replace("<?top3_name_" + index + "?>", playerStats.getPlayerName());
				html = html.replace("<?top3_clan_" + index + "?>", playerStats.getClanName() == null ? "<font color=B59A75>No Clan</font>" : playerStats.getClanName());
				html = html.replace("<?top3_class_" + index + "?>", Util.getFullClassName(playerStats.getClassId()));
				html = html.replace("<?top3_count_" + index + "?>", String.valueOf(playerStats.getValue()));
			}
			else
			{
				html = html.replace("<?top3_name_" + index + "?>", "...");
				html = html.replace("<?top3_clan_" + index + "?>", "...");
				html = html.replace("<?top3_class_" + index + "?>", "...");
				html = html.replace("<?top3_count_" + index + "?>", "...");
			}

			index++;
		}

		// Top 20. La mayoria usa top 10, pero algunos son especiales y solo tienen tops, asi que llega hasta el 20
		index = 0;
		while (index < (type.getRankingType() == RankingType.SPECIAL ? 20 : 10))
		{
			if (stats.size() > index)
			{
				final RankingStats playerStats = stats.get(index);
				html = html.replace("<?top10_name_" + index + "?>", playerStats.getPlayerName());
				html = html.replace("<?top10_clan_" + index + "?>", playerStats.getClanName() == null ? "<font color=B59A75>No Clan</font>" : playerStats.getClanName());
				html = html.replace("<?top10_class_" + index + "?>", Util.getFullClassName(playerStats.getClassId()));
				html = html.replace("<?top10_count_" + index + "?>", String.valueOf(playerStats.getValue()));
			}
			else
			{
				html = html.replace("<?top10_name_" + index + "?>", "...");
				html = html.replace("<?top10_clan_" + index + "?>", "...");
				html = html.replace("<?top10_class_" + index + "?>", "...");
				html = html.replace("<?top10_count_" + index + "?>", "...");
			}

			index++;
		}

		// Top 10 Current Position. We try to put the player in the middle of the top 10 for his current position
		index = 0;
		for (RankingStats playerStats : stats)
		{
			if (player.getObjectId() == playerStats.getPlayerId())
			{
				break;
			}
			index++;
		}

		final int currentIndex = index;
		final int maxIndex = stats.size() - 1;
		final int minIndex = Math.max(index - 5 - (maxIndex >= currentIndex + 5 ? 0 : 5 - (maxIndex - currentIndex)), 0);

		index = 0;
		for (int i = minIndex; i < minIndex + 10; i++)
		{
			html = html.replace("<?current_index_" + index + "?>", String.valueOf(i + 1));

			if (stats.size() > i)
			{
				final RankingStats playerStats = stats.get(i);

				// The current index for the player should be in another color
				if (i == currentIndex)
				{
					html = html.replace("<?current_name_" + index + "?>", "<font color=LEVEL>" + playerStats.getPlayerName() + "</font>");
					html = html.replace("<?current_clan_" + index + "?>", playerStats.getClanName() == null ? "<font color=B59A75>No Clan</font>" : "<font color=LEVEL>" + playerStats.getClanName() + "</font>");
					html = html.replace("<?current_class_" + index + "?>", "<font color=LEVEL>" + Util.getFullClassName(playerStats.getClassId()) + "</font>");
					html = html.replace("<?current_count_" + index + "?>",
								"<font color=LEVEL>" + String.valueOf((type == RankingEnum.ADENA ? Util.convertToLineagePriceFormat(playerStats.getValue()) : playerStats.getValue())) + "</font>");
				}
				else
				{
					html = html.replace("<?current_name_" + index + "?>", playerStats.getPlayerName());
					html = html.replace("<?current_clan_" + index + "?>", playerStats.getClanName() == null ? "<font color=B59A75>No Clan</font>" : playerStats.getClanName());
					html = html.replace("<?current_class_" + index + "?>", Util.getFullClassName(playerStats.getClassId()));
					html = html.replace("<?current_count_" + index + "?>", String.valueOf(playerStats.getValue()));
				}
			}
			else
			{
				html = html.replace("<?current_name_" + index + "?>", "...");
				html = html.replace("<?current_clan_" + index + "?>", "...");
				html = html.replace("<?current_class_" + index + "?>", "...");
				html = html.replace("<?current_count_" + index + "?>", "...");
			}

			index++;
		}

		html = html.replace("<?ranking_menu?>", HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "ranking/menu.htm", player));
		html = html.replace("%rankName%", type.getName());
		html = html.replace("%reward1%", String.valueOf(Config.SERVER_RANKING_REWARD_ITEM_COUNT[0]));
		html = html.replace("%reward2%", String.valueOf(Config.SERVER_RANKING_REWARD_ITEM_COUNT[1]));
		html = html.replace("%reward3%", String.valueOf(Config.SERVER_RANKING_REWARD_ITEM_COUNT[2]));
		html = html.replace("<?update?>", String.valueOf(TIME_UPDATE / (60 * 1000)));
		html = html.replace("<?last_update?>", TIME_FORMAT.format(_lastUpdate));
		html = html.replace("%adenaBypass%",
					(player.getAccessLevel() > 0 ? "<td align=center><button action=\"bypass _bbsranking:adena\" value=\"Adena\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\"></td>"
								: ""));
		ShowBoard.separateAndSend(html, player);
	}

	private void showTalesRanking(Player player, RankingEnum type, int page)
	{
		player.sendMessage("Page: " + page);
		final List<RankingStats> stats = _stats.get(type);

		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "ranking/rank_tales.htm", player);

		final StringBuilder sb = new StringBuilder();
		final int scoresPerPage = 10;
		for (int index = page * scoresPerPage; index < ((page * scoresPerPage) + scoresPerPage) - 1; index++)
		{
			if (stats.size() > index)
			{
				final RankingStats playerStats = stats.get(index);
				sb.append("<table width=540 height=25 bgcolor=" + (playerStats.getPlayerId() == player.getObjectId() ? "3a0000" : (index % 2 == 0 ? "00080b" : "011118")) + ">");
				sb.append("<tr>");
				sb.append("<td align=center>");
				sb.append("<table>");
				sb.append("<tr>");
				sb.append("<td width=25 valign=top>");
				sb.append("<font name=__SystemeditBoxFont color=ff8e3b>" + (index + 1) + "</font>");
				sb.append("</td>");
				sb.append("<td width=115 valign=top>");
				switch (index)
				{
				case 0:
					sb.append("<font color=ffd700>");
					break;
				case 1:
					sb.append("<font color=c0c0c0>");
					break;
				case 2:
					sb.append("<font color=b56c47>");
					break;
				default:
					sb.append("<font color=989da0>");
					break;
				}
				sb.append(playerStats.getPlayerName());
				sb.append("</font>");
				sb.append("</td>");
				sb.append("<td width=100 align=center valign=top>");
				if (playerStats.getClanName() != null)
				{
					sb.append("<font color=bdccd4>" + playerStats.getClanName() + "</font>");
				}
				else
				{
					sb.append("<br>");
				}
				sb.append("</td>");
				sb.append("<td width=140 align=center valign=top>");
				sb.append("<font color=bdccd4>" + ClassId.getById(playerStats.getClassId()).toPrettyString() + "</font>");
				sb.append("</td>");
				sb.append("<td width=60 align=center valign=top>");
				sb.append("<font color=bdccd4>" + (type == RankingEnum.ADENA ? Util.convertToLineagePriceFormat(playerStats.getValue()) : playerStats.getValue()) + "</font>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
			}
			else
			{
				sb.append("<table width=540 height=25 bgcolor=" + (index % 2 == 0 ? "001017" : "021823") + ">");
				sb.append("<tr>");
				sb.append("<td align=center>");
				sb.append("<table>");
				sb.append("<tr>");
				sb.append("<td width=25 valign=top>");
				sb.append("<br>");
				sb.append("</td>");
				sb.append("<td width=115 valign=top>");
				sb.append("<br>");
				sb.append("</td>");
				sb.append("<td width=100 align=center valign=top>");
				sb.append("<br>");
				sb.append("</td>");
				sb.append("<td width=140 align=center valign=top>");
				sb.append("<br>");
				sb.append("</td>");
				sb.append("<td width=50 align=center valign=top>");
				sb.append("<br>");
				sb.append("</td>");
				sb.append("<td width=60 align=center valign=top>");
				sb.append("<br>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
			}
		}

		// Previous page
		final String previousPage;
		if (page > 0)
		{
			previousPage = "<br1><button action=\"bypass _bbsranking:" + type.getBypass() + ":" + (page - 1) + "\" width=15 height=15 back=\"L2UI_CH3.prev1_down\" fore=\"L2UI_CH3.prev1\">";
		}
		else
		{
			previousPage = "<br>";
		}

		// Next page
		final String nextPage;
		if (stats.size() > (scoresPerPage * page) + scoresPerPage)
		{
			nextPage = "<br1><button action=\"bypass _bbsranking:" + type.getBypass() + ":" + (page + 1) + "\" width=15 height=15 back=\"L2UI_CH3.next1_down\" fore=\"L2UI_CH3.next1\">";
		}
		else
		{
			nextPage = "<br>";
		}

		html = html.replace("%rankName%", type.getName());
		html = html.replace("%playerList%", sb.toString());
		html = html.replace("%previousPage%", previousPage);
		html = html.replace("%nextPage%", nextPage);
		html = html.replace("%page%", String.valueOf(page + 1));
		html = html.replace("<?update?>", String.valueOf(TIME_UPDATE / (60 * 1000)));
		html = html.replace("<?last_update?>", TIME_FORMAT.format(_lastUpdate));
		html = html.replace("%adenaBypass%",
					(player.getAccessLevel() > 0
								? "<tr><td height=40 align=center><button action=\"bypass _bbsranking:adena\" value=\"   Adena\" width=120 height=25 back=\"cb.mx_button_down\" fore=\"cb.mx_button\"></td></tr>"
								: ""));
		ShowBoard.separateAndSend(html, player);
	}

	private void loadRankings()
	{
		_isLoading = true;

		// Put all the ranking types with a clean list
		for (RankingEnum type : RankingEnum.values())
		{
			_stats.put(type, new ArrayList<RankingStats>());
		}

		// Last update
		_lastUpdate = System.currentTimeMillis();

		// TODO: Limite de 10 quitado para poder tener la posicion de todos los jugadores. Si esto se convierte en un problema de rendimiento volver a agregar y quitar la posicion
		// Load rankings from db
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			for (RankingEnum type : RankingEnum.values())
			{
				if (type == RankingEnum.ADENA)
				{
					try (PreparedStatement statement = con.prepareStatement(
								"SELECT obj_Id,char_name,class_id,clanid,it.count FROM characters AS c LEFT JOIN character_subclasses AS cs ON (c.obj_Id=cs.char_obj_id) JOIN items AS it ON (c.obj_Id=it.owner_id) WHERE cs.isBase=1 AND it.item_id=57 AND accesslevel = 0 ORDER BY it.count DESC");
								ResultSet rset = statement.executeQuery())
					{
						while (rset.next())
						{
							if (rset.getString("char_name").isEmpty())
							{
								continue;
							}

							_stats.get(type).add(new RankingStats(rset.getInt("obj_Id"), rset.getString("char_name"), rset.getInt("clanid"), rset.getInt("class_id"), rset.getLong("count")));
						}
					}
				}
				else
				{
					try (PreparedStatement statement = con.prepareStatement("SELECT obj_Id,char_name,class_id,clanid," + type.getDbName()
								+ " FROM characters AS c LEFT JOIN character_subclasses AS cs ON (c.obj_Id=cs.char_obj_id) WHERE cs.isBase=1 AND accesslevel = 0 AND " + type.getDbName() + " > 0 ORDER BY "
								+ type.getDbName() + " DESC"); ResultSet rset = statement.executeQuery())
					{
						while (rset.next())
						{
							if (rset.getString("char_name").isEmpty())
							{
								continue;
							}

							_stats.get(type).add(new RankingStats(rset.getInt("obj_Id"), rset.getString("char_name"), rset.getInt("clanid"), rset.getInt("class_id"), rset.getLong(type.getDbName())));
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// Now we check for the rankings that must have rewards, so we can reward the top 3 chars. If a week has passed from the last rewards given, then we give the new reward for this
		// week
		if (RankingEnum.hasRankWithRewards())
		{
			final long lastRewardTime = ServerVariables.getLong("LastRankingRewardsTime", 0);
			if (lastRewardTime == 0 || System.currentTimeMillis() > lastRewardTime + 7 * 24 * 60 * 60 * 1000)
			{
				// The first time or week we only set the variable, we dont reward the first week
				if (lastRewardTime > 0)
				{
					for (RankingEnum type : RankingEnum.values())
					{
						if (type.getRankingType() != RankingType.REWARDS)
						{
							continue;
						}

						// The reward goes to the top 3 chars
						for (int i = 0; i < Math.min(3, _stats.get(type).size()); i++)
						{
							final RankingStats stat = _stats.get(type).get(i);

							// We send the mail with the corresponding prize to the characters in the top 3 of the rewarding categories
							final Mail mail = new Mail();
							mail.setSenderId(0);
							mail.setSenderName("Server Ranking Top 3 Reward");
							mail.setReceiverId(stat.getPlayerId());
							mail.setReceiverName(stat.getPlayerName());
							mail.setTopic("Server Ranking Top 3 Reward");
							mail.setBody("This week prize for the Ranking Top 3 goes to you\nIn the category " + type.getName() + " you ended up in the " + (i + 1) + " position\nCongratulations");
							mail.setPrice(0);
							mail.setUnread(true);
							mail.setType(Mail.SenderType.NONE);
							mail.setExpireTime(0);
							final ItemInstance newItem = new ItemInstance(IdFactory.getInstance().getNextId(), Config.SERVER_RANKING_REWARD_ITEM_ID);
							newItem.setCount(Config.SERVER_RANKING_REWARD_ITEM_COUNT[i]);
							newItem.setOwnerId(stat.getPlayerId());
							newItem.setLocation(ItemLocation.MAIL);
							mail.addAttachment(newItem);
							mail.save();

							final Player target = World.getPlayer(stat.getPlayerId());
							if (target != null)
							{
								target.sendPacket(ExNoticePostArrived.STATIC_TRUE);
								target.sendPacket(SystemMsg.THE_MAIL_HAS_ARRIVED);
							}
						}
					}
				}

				// The reward time is always set to each Monday 00:00am
				final Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				ServerVariables.set("LastRankingRewardsTime", calendar.getTimeInMillis());
			}
		}

		_isLoading = false;
	}

	@Override
	public void onShutdown()
	{
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
	}

	private static class RankingStats
	{
		private final int _playerId;
		private final String _playerName;
		private final String _clanName;
		private final int _classId;
		private final long _value;

		public RankingStats(int playerId, String playerName, int clanId, int classId, long value)
		{
			_playerId = playerId;
			_playerName = playerName;
			_classId = classId;
			_value = value;

			final Clan clan = (clanId == 0 ? null : ClanTable.getInstance().getClan(clanId));
			_clanName = (clan == null ? null : clan.getName());
		}

		public int getPlayerId()
		{
			return _playerId;
		}

		public String getPlayerName()
		{
			return _playerName;
		}

		public String getClanName()
		{
			return _clanName;
		}

		public int getClassId()
		{
			return _classId;
		}

		public long getValue()
		{
			return _value;
		}
	}
}
