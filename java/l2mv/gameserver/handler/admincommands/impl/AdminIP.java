package l2mv.gameserver.handler.admincommands.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import l2mv.gameserver.handler.admincommands.IAdminCommandHandler;
import l2mv.gameserver.hwid.HwidEngine;
import l2mv.gameserver.hwid.HwidGamer;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;

public class AdminIP implements IAdminCommandHandler
{
	private enum Commands
	{
		admin_charip, admin_ip, admin_show_hwids_over, admin_show_hwid_info, admin_show_ips_over, admin_show_ip_info, admin_real_online
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (!activeChar.getPlayerAccess().CanBan)
		{
			return false;
		}

		switch (command)
		{
		case admin_charip:
			if (wordList.length != 2)
			{
				activeChar.sendMessage("Command syntax: //charip <char_name>");
				activeChar.sendMessage(" Gets character's IP.");
				break;
			}

			Player pl = World.getPlayer(wordList[1]);

			if (pl == null)
			{
				activeChar.sendMessage("Character " + wordList[1] + " not found.");
				break;
			}

			String ip_adr = pl.getIP();
			if (ip_adr.equalsIgnoreCase("<not connected>"))
			{
				activeChar.sendMessage("Character " + wordList[1] + " not found.");
				break;
			}

			activeChar.sendMessage("Character's IP: " + ip_adr);
			break;
		case admin_ip:
			Player target;
			if (activeChar.getTarget() != null && activeChar.getTarget().isPlayer())
			{
				target = activeChar.getTarget().getPlayer();
			}
			else
			{
				target = activeChar;
			}

			if (target.getIP().equalsIgnoreCase("<not connected>"))
			{
				activeChar.sendMessage("Target not found.");
				return false;
			}

			activeChar.sendMessage("IP:" + target.getIP());

			for (Player player : GameObjectsStorage.getAllPlayersForIterate())
			{
				if (player.getIP().equals(target.getIP()))
				{
					activeChar.sendMessage("Player with same IP:" + player.getName());
				}
			}
			break;
		case admin_show_hwids_over:
		{
			try
			{
				final int minPlayersOnHwid = Integer.parseInt(wordList[1]);
				final int page = (wordList.length > 1 ? Integer.parseInt(wordList[2]) : 0);
				showHwidsOver(activeChar, minPlayersOnHwid, page);
			}
			catch (NumberFormatException | IndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Syntax: //show_hwids_over 6 [page]");
			}
			break;
		}
		case admin_show_hwid_info:
		{
			final String hwid = wordList[1];
			final int minPlayersOnHwid2 = Integer.parseInt(wordList[2]);
			final int page = (wordList.length > 2 ? Integer.parseInt(wordList[3]) : 0);
			showHwidInfo(activeChar, hwid, minPlayersOnHwid2, page);
			break;
		}
		case admin_show_ips_over:
		{
			try
			{
				final int minPlayersOnIP = Integer.parseInt(wordList[1]);
				final int page = (wordList.length > 1 ? Integer.parseInt(wordList[2]) : 0);
				showIPsOver(activeChar, minPlayersOnIP, page);
			}
			catch (NumberFormatException | IndexOutOfBoundsException e2)
			{
				activeChar.sendMessage("Syntax: //show_ips_over 6 [page]");
			}
			break;
		}
		case admin_show_ip_info:
		{
			final String ip = wordList[1];
			final int minPlayersOnIP2 = Integer.parseInt(wordList[2]);
			final int page = (wordList.length > 2 ? Integer.parseInt(wordList[3]) : 0);
			showIPInfo(activeChar, ip, minPlayersOnIP2, page);
			break;
		}
		case admin_real_online:
		{
			showOnlinePlayersCount(activeChar);
			break;
		}
		}
		return true;
	}

	private void showHwidsOver(Player activeChar, int minPlayersOnHwid, int page)
	{
		final Map<String, Integer> hwidsAboveMin = getHwidsAboveCount(minPlayersOnHwid);

		int MaxCharactersPerPage = 10;
		int MaxPages = hwidsAboveMin.size() / MaxCharactersPerPage;
		if (hwidsAboveMin.size() > MaxCharactersPerPage * MaxPages)
		{
			MaxPages++;
		}

		// Check if number of users changed
		page = Math.max(Math.min(MaxPages, page), 0);

		int CharactersStart = MaxCharactersPerPage * page;
		int CharactersEnd = hwidsAboveMin.size();
		if (CharactersEnd - CharactersStart > MaxCharactersPerPage)
		{
			CharactersEnd = CharactersStart + MaxCharactersPerPage;
		}

		final StringBuilder pages = new StringBuilder();
		for (int x = 0; x < MaxPages; x++)
		{
			int pagenr = x + 1;
			pages.append("<center><a action=\"bypass -h admin_show_hwids_over " + minPlayersOnHwid + " " + x + "\">Page " + pagenr + "</a></center>");
		}

		final StringBuilder builder = new StringBuilder();
		int count = 0;
		for (Map.Entry<String, Integer> hwidWithCount : hwidsAboveMin.entrySet())
		{
			builder.append("<tr>");
			builder.append("<td align=center width=200>");
			builder.append("<a action=\"bypass -h admin_show_hwid_info ").append(hwidWithCount.getKey()).append(' ').append(minPlayersOnHwid).append(' ').append(page).append("\">");
			builder.append(hwidWithCount.getKey());
			builder.append("</a>");
			builder.append("</td>");
			builder.append("<td align=center width=94>");
			builder.append(hwidWithCount.getValue());
			builder.append("</td>");
			builder.append("</tr>");

			count++;
			if (count >= 10)
			{
				break;
			}
		}
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setFile("admin/hwidsOver.htm");
		adminReply.replace("%hwids%", builder.toString());
		adminReply.replace("%pages%", pages.toString());
		activeChar.sendPacket(adminReply);
	}

	private void showHwidInfo(Player activeChar, String hwid, int minPlayersOnHwid, int page)
	{
		final HwidGamer gamer = HwidEngine.getInstance().getGamerByHwid(hwid);
		final StringBuilder builder = new StringBuilder();
		int count = 0;
		for (Player player : gamer.getOnlineChars())
		{
			builder.append("<tr>");
			builder.append("<td align=center width=147>");
			builder.append(player.getAccountName());
			builder.append("</td>");
			builder.append("<td align=center width=147>");
			builder.append(player.getName());
			builder.append("</td>");
			builder.append("</tr>");

			count++;
			if (count >= 10)
			{
				break;
			}
		}
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setFile("admin/hwidInfo.htm");
		adminReply.replace("%aboveCount%", String.valueOf(minPlayersOnHwid));
		adminReply.replace("%players%", builder.toString());
		adminReply.replace("%page%", String.valueOf(page));
		activeChar.sendPacket(adminReply);
	}

	private Map<String, Integer> getHwidsAboveCount(int minPlayersOnHwid)
	{
		final Map<String, Integer> countByHwid = new HashMap<String, Integer>();
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player.isOnline() && player.getHWID() != null && !player.getHWID().equals("NO-SMART-GUARD-ENABLED"))
			{
				if (countByHwid.containsKey(player.getHWID()))
				{
					countByHwid.put(player.getHWID(), countByHwid.get(player.getHWID()) + 1);
				}
				else
				{
					countByHwid.put(player.getHWID(), 1);
				}
			}
		}
		return getRecordsAboveCount(countByHwid, minPlayersOnHwid);
	}

	private void showIPsOver(Player activeChar, int minPlayersOnIP, int page)
	{
		final Map<String, Integer> ipsAboveMin = getIPsAboveCount(minPlayersOnIP);

		int MaxCharactersPerPage = 10;
		int MaxPages = ipsAboveMin.size() / MaxCharactersPerPage;
		if (ipsAboveMin.size() > MaxCharactersPerPage * MaxPages)
		{
			MaxPages++;
		}

		// Check if number of users changed
		page = Math.max(Math.min(MaxPages, page), 0);

		int CharactersStart = MaxCharactersPerPage * page;
		int CharactersEnd = ipsAboveMin.size();
		if (CharactersEnd - CharactersStart > MaxCharactersPerPage)
		{
			CharactersEnd = CharactersStart + MaxCharactersPerPage;
		}

		final StringBuilder pages = new StringBuilder();
		for (int x = 0; x < MaxPages; x++)
		{
			int pagenr = x + 1;
			pages.append("<center><a action=\"bypass -h admin_show_ips_over " + minPlayersOnIP + " " + x + "\">Page " + pagenr + "</a></center>");
		}

		final StringBuilder ips = new StringBuilder();
		int count = 0;
		for (Map.Entry<String, Integer> ipWithCount : ipsAboveMin.entrySet())
		{
			ips.append("<tr>");
			ips.append("<td align=center width=200>");
			ips.append("<a action=\"bypass -h admin_show_ip_info ").append(ipWithCount.getKey()).append(' ').append(minPlayersOnIP).append(' ').append(page).append("\">");
			ips.append(ipWithCount.getKey());
			ips.append("</a>");
			ips.append("</td>");
			ips.append("<td align=center width=94>");
			ips.append(ipWithCount.getValue());
			ips.append("</td>");
			ips.append("</tr>");

			count++;
			if (count >= 10)
			{
				break;
			}
		}
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setFile("admin/ipsOver.htm");
		adminReply.replace("%ips%", ips.toString());
		adminReply.replace("%pages%", pages.toString());
		activeChar.sendPacket(adminReply);
	}

	private void showIPInfo(Player activeChar, String ip, int minPlayersOnIP, int page)
	{
		final List<Player> playersWithIP = getPlayersByIP(ip);
		final StringBuilder builder = new StringBuilder();
		for (Player player : playersWithIP)
		{
			builder.append("<tr>");
			builder.append("<td align=center width=147>");
			builder.append(player.getAccountName());
			builder.append("</td>");
			builder.append("<td align=center width=147>");
			builder.append(player.getName());
			builder.append("</td>");
			builder.append("</tr>");
		}
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setFile("admin/ipInfo.htm");
		adminReply.replace("%aboveCount%", String.valueOf(minPlayersOnIP));
		adminReply.replace("%page%", String.valueOf(page));
		adminReply.replace("%players%", builder.toString());
		activeChar.sendPacket(adminReply);
	}

	private List<Player> getPlayersByIP(String ip)
	{
		final List<Player> playersWithIP = new ArrayList<Player>();
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player.isOnline() && player.getIP() != null && player.getIP().equals(ip))
			{
				playersWithIP.add(player);
			}
		}
		return playersWithIP;
	}

	private Map<String, Integer> getIPsAboveCount(int minPlayersOnIp)
	{
		final Map<String, Integer> countByIP = new HashMap<String, Integer>();
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player.isOnline() && player.getIP() != null && !player.getIP().equals("?.?.?.?"))
			{
				if (countByIP.containsKey(player.getIP()))
				{
					countByIP.put(player.getIP(), countByIP.get(player.getIP()) + 1);
				}
				else
				{
					countByIP.put(player.getIP(), 1);
				}
			}
		}
		return getRecordsAboveCount(countByIP, minPlayersOnIp);
	}

	private static Map<String, Integer> getRecordsAboveCount(Map<String, Integer> currentMap, int valueAbove)
	{
		final Map<String, Integer> aboveCount = new HashMap<String, Integer>();
		for (Map.Entry<String, Integer> hwidWithCount : currentMap.entrySet())
		{
			if (hwidWithCount.getValue() >= valueAbove)
			{
				aboveCount.put(hwidWithCount.getKey(), hwidWithCount.getValue());
			}
		}
		return aboveCount;
	}

	private static void showOnlinePlayersCount(Player activeChar)
	{
		final Set<String> uniquePlayers = new HashSet<>();
		int totalPlayers = 0;
		for (Player player : GameObjectsStorage.getAllPlayersCopy())
		{
			if (!player.isInStoreMode())
			{
				++totalPlayers;
				uniquePlayers.add(player.getHWID());
			}
		}
		activeChar.sendMessage("Online No Traders: " + totalPlayers);
		activeChar.sendMessage("Real Unique Online: " + uniquePlayers.size());
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}