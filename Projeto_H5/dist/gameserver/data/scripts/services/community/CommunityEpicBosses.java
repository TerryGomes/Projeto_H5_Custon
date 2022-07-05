//package services.community;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.StringTokenizer;
//
//import l2f.gameserver.Config;
//import l2f.gameserver.cache.ImagesCache;
//import l2f.gameserver.data.htm.HtmCache;
//import l2f.gameserver.data.xml.holder.ItemHolder;
//import l2f.gameserver.handler.bbs.CommunityBoardManager;
//import l2f.gameserver.handler.bbs.ICommunityBoardHandler;
//import l2f.gameserver.model.Playable;
//import l2f.gameserver.model.Player;
//import l2f.gameserver.model.entity.olympiad.Olympiad;
//import l2f.gameserver.network.serverpackets.HideBoard;
//import l2f.gameserver.network.serverpackets.Say2;
//import l2f.gameserver.network.serverpackets.ShowBoard;
//import l2f.gameserver.network.serverpackets.SystemMessage2;
//import l2f.gameserver.network.serverpackets.components.ChatType;
//import l2f.gameserver.scripts.ScriptFile;
//import l2f.gameserver.templates.item.ItemTemplate;
//import l2f.gameserver.utils.BbsUtil;
//import l2f.gameserver.utils.Location;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import bosses.EpicBossState;
//
///**
// * Grand Bosses Community Manager
// *
// * @author Synerge
// */
//public class CommunityEpicBosses implements ScriptFile, ICommunityBoardHandler
//{
//	private static final Logger _log = LoggerFactory.getLogger(CommunityEpicBosses.class);
//
//	private static final SimpleDateFormat WINDOWS_MIN_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
//	private static final SimpleDateFormat WINDOWS_MAX_FORMAT = new SimpleDateFormat("HH:mm");
//
//	private static final int[] BOSSES = new int[] { 29068, 29028, 29020, 29118, 29001, 29014 };
//
//	private static final Map<Integer, Location[]> BOSSES_LOCATIONS = new HashMap<>();
//	static
//	{
//		BOSSES_LOCATIONS.put(29068, new Location[] { new Location(153368, 119528, -3834), new Location(152472, 120184, -3834), new Location(152184, 119160, -3828),
//			new Location(150856, 118040, -3721), new Location(149944, 116616, -3731), new Location(149240, 115560, -3734) });
//		BOSSES_LOCATIONS.put(29028, new Location[] { new Location(182744, -115048, -3362), new Location(184584, -115768, -3357), new Location(183544, -117592, -3362),
//			new Location(181880, -116168, -3362), new Location(181928, -117944, -3353), new Location(185688, -117304, -3313) });
//		BOSSES_LOCATIONS.put(29020, new Location[] { new Location(114168, 13352, 9538), new Location(111896, 15640, 9538), new Location(113368, 14840, 9539),
//			new Location(114744, 17224, 8981), new Location(115736, 16344, 8981), new Location(113496, 14968, 8981) });
//		BOSSES_LOCATIONS.put(29118, new Location[] { new Location(17896, 282760, -9704), new Location(17928, 283832, -9704), new Location(19320, 283896, -9704),
//			new Location(18920, 284248, -9704), new Location(8952, 251944, -2032), new Location(9144, 250728, -1984) });
//		BOSSES_LOCATIONS.put(29001, new Location[] { new Location(-21880, 184488, -5720), new Location(-22392, 183000, -5747), new Location(-21096, 183208, -5747),
//			new Location(-19496, 183848, -5630), new Location(-21768, 185768, -5630), new Location(-23896, 183976, -5630) });
//		BOSSES_LOCATIONS.put(29014, new Location[] { new Location(54152, 18024, -5478), new Location(56344, 18312, -5496), new Location(56488, 17016, -5501),
//			new Location(54552, 16088, -5517), new Location(51016, 18568, -5185), new Location(50440, 15928, -5089) });
//	}
//
//	@Override
//	public void onLoad()
//	{
//		if (Config.COMMUNITYBOARD_ENABLED && Config.ALLOW_EPIC_BOSSES_PAGE)
//		{
//			_log.info("CommunityBoard: Bosses loaded.");
//			CommunityBoardManager.getInstance().registerHandler(this);
//		}
//	}
//
//	@Override
//	public void onReload()
//	{
//		if (Config.COMMUNITYBOARD_ENABLED && Config.ALLOW_EPIC_BOSSES_PAGE)
//			CommunityBoardManager.getInstance().removeHandler(this);
//	}
//
//	@Override
//	public void onShutdown()
//	{
//	}
//
//	@Override
//	public String[] getBypassCommands()
//	{
//		return new String[]
//		{
//			"_bbsepicboss"
//		};
//	}
//
//	@Override
//	public void onBypassCommand(Player player, String bypass)
//	{
//		StringTokenizer st = new StringTokenizer(bypass, "_");
//		String cmd = st.nextToken();
//
//		if ("bbsmemo".equals(cmd) || ("bbsepicboss".equals(cmd) && !st.hasMoreTokens()))
//		{
//			sendBossListPage(player);
//		}
//		else if ("bbsepicboss".equals(cmd) && st.hasMoreTokens())
//		{
//			switch (st.nextToken())
//			{
//				case "details":
//				{
//					int bossId = Integer.parseInt(st.nextToken());
//					getBossDetailsPage(player, bossId);
//					break;
//				}
//				case "teleport":
//				{
//					if (canTeleport(player, true))
//					{
//						int x = Integer.parseInt(st.nextToken());
//						int y = Integer.parseInt(st.nextToken());
//						int z = Integer.parseInt(st.nextToken());
//
//						player.teleToLocation(x, y, z);
//
//						int requiredItemId = Config.EPIC_BOSSES_TELEPORT_PRICE_ID;
//						if (requiredItemId > 0)
//						{
//							ItemTemplate requiredItem = ItemHolder.getInstance().getTemplate(requiredItemId);
//							long count = Config.EPIC_BOSSES_TELEPORT_PRICE_COUNT;
//							if ((requiredItem != null) && (count > 0L) && (player.getInventory().getCountOf(requiredItemId) < count))
//							{
//								player.getInventory().destroyItemByItemId(requiredItemId, count, "TeleportToEpicBoss");
//								player.sendPacket(SystemMessage2.removeItems(requiredItemId, count));
//							}
//						}
//						player.sendPacket(HideBoard.PACKET);
//					}
//					break;
//				}
//			}
//		}
//	}
//
//	/**
//	 * Shows the list of all epic bosses
//	 *
//	 * @param player
//	 */
//	private static void sendBossListPage(Player player)
//	{
//		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "epicBosses/main.htm", player);
//
//		StringBuilder builder = new StringBuilder();
//		int i = 1;
//		for (int bossId : BOSSES)
//		{
//			EpicBossState state = EpicBossState.getState(bossId);
//			if (state == null)
//				continue;
//
//			if (i == 1)
//				builder.append("<tr>");
//
//			builder.append("<td align=center height=206>");
//
//			switch (state.getState())
//			{
//				case ALIVE:
//				case NOTSPAWN:
//					builder.append("<img src=Crest.crest_1_" + bossId + " width=200 height=128>");
//					builder.append("<br1>");
//					builder.append("<font color=12C421 name=hs12>Alive</font>");
//					builder.append("<br1>");
//					builder.append("<button value=\"Teleport\" action=\"bypass _bbsepicboss_details_" + bossId + "\" width=200 height=32 back=L2UI_CT1.OlympiadWnd_DF_Apply_Down fore=L2UI_CT1.OlympiadWnd_DF_Apply>");
//					break;
//				default:
//					builder.append("<img src=Crest.crest_1_" + bossId + " width=200 height=128>");
//					builder.append("<br1>");
//					builder.append("<font color=bc2b0e name=hs12>" + WINDOWS_MIN_FORMAT.format(new Date(state.getRespawnDateWindowsMin())) + " - " + WINDOWS_MAX_FORMAT.format(new Date(state.getRespawnDateWindowsMax())) + "</font>");
//					builder.append("<br1>");
//					builder.append("<button value=\"Teleport\" action=\"bypass _bbsepicboss_details_" + bossId + "\" width=200 height=32 back=L2UI_CT1.OlympiadWnd_DF_Apply_Down fore=L2UI_CT1.OlympiadWnd_DF_Apply>");
//					break;
//			}
//
//			builder.append("</td>");
//
//			if (i == 3)
//			{
//				builder.append("</tr>");
//				i = 1;
//			}
//			else
//				i++;
//		}
//
//		if (i != 3)
//			builder.append("</tr>");
//
//		html = html.replace("%status%", builder.toString());
//		html = BbsUtil.htmlAll(html, player);
//
//		ImagesCache.getInstance().sendUsedImages(html, player);
//		ShowBoard.separateAndSend(html, player);
//	}
//
//	/**
//	 * Shows the boss details page
//	 *
//	 * @param player
//	 * @param bossId
//	 */
//	private static void getBossDetailsPage(Player player, int bossId)
//	{
//		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "epicBosses/bossDetails.htm", player);
//
//		EpicBossState state = EpicBossState.getState(bossId);
//
//		// Status
//		switch (state.getState())
//		{
//			case ALIVE:
//			case NOTSPAWN:
//				html = html.replace("%status%", "<font color=12C421 name=hs12>Alive</font>");
//				break;
//			default:
//				html = html.replace("%status%", "<font color=bc2b0e name=hs12>" + WINDOWS_MIN_FORMAT.format(new Date(state.getRespawnDateWindowsMin())) + " - " + WINDOWS_MAX_FORMAT.format(new Date(state.getRespawnDateWindowsMax())) + "</font>");
//				break;
//		}
//
//		// Tp locations
//		Location[] locs = BOSSES_LOCATIONS.get(bossId);
//		html = html.replace("%tpSpot1%", "bypass _bbsepicboss_teleport_" + locs[0].getX() + "_" + locs[0].getY() + "_" + locs[0].getZ());
//		html = html.replace("%tpSpot2%", "bypass _bbsepicboss_teleport_" + locs[1].getX() + "_" + locs[1].getY() + "_" + locs[1].getZ());
//		html = html.replace("%tpSpot3%", "bypass _bbsepicboss_teleport_" + locs[2].getX() + "_" + locs[2].getY() + "_" + locs[2].getZ());
//		html = html.replace("%tpSpot4%", "bypass _bbsepicboss_teleport_" + locs[3].getX() + "_" + locs[3].getY() + "_" + locs[3].getZ());
//		html = html.replace("%tpSpot5%", "bypass _bbsepicboss_teleport_" + locs[4].getX() + "_" + locs[4].getY() + "_" + locs[4].getZ());
//		html = html.replace("%tpSpot6%", "bypass _bbsepicboss_teleport_" + locs[5].getX() + "_" + locs[5].getY() + "_" + locs[5].getZ());
//
//		html = html.replace("%bossId%", String.valueOf(bossId));
//		html = BbsUtil.htmlAll(html, player);
//
//		ImagesCache.getInstance().sendUsedImages(html, player);
//		ShowBoard.separateAndSend(html, player);
//	}
//
//	public boolean canTeleport(Player player, boolean sendMessage)
//	{
//		if (!Config.ALLOW_EPIC_BOSSES_TELEPORT)
//		{
//			if (sendMessage)
//			{
//				sendErrorMessage(player, "This feature is temporarily disabled!", true);
//			}
//			return false;
//		}
//		if ((!player.hasBonus()) && Config.EPIC_BOSSES_TELEPORT_ONLY_FOR_PREMIUM)
//		{
//			if (sendMessage)
//			{
//				sendErrorMessage(player, "This feature is allowed only for Premium users!", true);
//			}
//			return false;
//		}
//		if ((!player.isInZonePeace()) && Config.EPIC_BOSSES_TELEPORT_ONLY_FROM_PEACE)
//		{
//			if (sendMessage)
//			{
//				sendErrorMessage(player, "You can do it only in safe zone!", true);
//			}
//			return false;
//		}
//		if ((Olympiad.isRegistered(player)) || (player.isInOlympiadMode()))
//		{
//			if (sendMessage)
//			{
//				sendErrorMessage(player, "You cannot do it while being registered in Olympiad Battle!", true);
//			}
//			return false;
//		}
//		if (player.isInStoreMode())
//		{
//			if (sendMessage)
//			{
//				sendErrorMessage(player, "You cannot teleport while trading!", true);
//			}
//			return false;
//		}
//		if (!player.getReflection().isDefault())
//		{
//			if (sendMessage)
//			{
//				sendErrorMessage(player, "You need to get out of Instanced Zone first", true);
//			}
//			return false;
//		}
//		int requiredItemId = Config.EPIC_BOSSES_TELEPORT_PRICE_ID;
//		if (requiredItemId > 0)
//		{
//			ItemTemplate requiredItem = ItemHolder.getInstance().getTemplate(requiredItemId);
//			if ((requiredItem != null) && (Config.EPIC_BOSSES_TELEPORT_PRICE_COUNT > 0L) && (player.getInventory().getCountOf(requiredItemId) < Config.EPIC_BOSSES_TELEPORT_PRICE_COUNT))
//			{
//				if (sendMessage)
//				{
//					sendErrorMessage(player, "You don't have enough " + requiredItem.getName() + "!", true);
//				}
//				return false;
//			}
//		}
//		return true;
//	}
//
//	private void sendErrorMessage(Playable playable, String msg, boolean refresh)
//	{
//		Player player = playable.getPlayer();
//		player.sendPacket(new Say2(0, ChatType.COMMANDCHANNEL_ALL, "Error", msg));
//		if (refresh)
//		{
//			onBypassCommand(player, "_bbsepicboss");
//		}
//	}
//
//	@Override
//	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
//	{
//	}
//}
