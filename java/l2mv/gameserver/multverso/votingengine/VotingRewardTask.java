/*
 * Copyright (C) 2014-2015 Vote Rewarding System
 * This file is part of Vote Rewarding System.
 * Vote Rewarding System is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Vote Rewarding System is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2mv.gameserver.multverso.votingengine;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.multverso.votingengine.VotingSettings.MessageType;
import l2mv.gameserver.utils.ItemFunctions;

/**
 * @author UnAfraid
 */
public class VotingRewardTask
{
	private static final Logger _log = LoggerFactory.getLogger(VotingRewardTask.class);
	// Constants
	private static final String TOPZONE_API_URL = "https://api.l2topzone.com/v1/vote?token=%s&ip=%s";
	private static final String HOPZONE_API_URL = "https://api.hopzone.net/lineage2/vote?token=%s&ip_address=%s";

	public static void checkReward(Player player)
	{
		final long timeRemaining = VotingRewardCache.getInstance().getLastVotedTime(player);
		// Check if player voted
		if (timeRemaining <= 0)
		{
			for (String zone : VotingSettings.getInstance().getZones().keySet())
			{
				if (!isVotter(zone, player.getIP()))
				{
					String msg = VotingSettings.getInstance().getMessage(MessageType.ON_NOT_VOTED);
					if (msg != null)
					{
						msg = msg.replaceAll("%zoneName%", zone);
						player.sendMessage(msg);
					}
					return;
				}
			}
			// Give him reward
			giveReward(player);

			// Mark down this reward as given
			VotingRewardCache.getInstance().markAsVotted(player);

			if (Config.ENABLE_PLAYER_COUNTERS)
			{
				player.getCounters().timesVoted++;
			}

			// Send message to player
			final String msg = VotingSettings.getInstance().getMessage(MessageType.ON_SUCCESS);
			if (msg != null)
			{
				player.sendMessage(msg);
			}
		}
		else
		{
			VotingRewardAPI.sendReEnterMessage(timeRemaining, player);
		}
	}

	private static void giveReward(Player activeChar)
	{
		if (activeChar == null)
		{
			return;
		}
		for (RewardItem item : VotingSettings.getInstance().getDroplist().calculateDrops())
		{
			ItemFunctions.addItem(activeChar, item.getId(), item.getCount(), true, "VotingReward");
		}
		if (VotingSettings.getInstance().getColor() != null)
		{
			activeChar.setNameColor(Integer.decode("0x" + VotingSettings.getInstance().getColor()));
		}
	}

	private static final boolean isVotter(String zoneName, String ip)
	{
		try
		{
			String url = null;
			String urlAgent = null;
			final String apiKey = VotingSettings.getInstance().getAPIKey(zoneName);
			// final int serverId = VotingSettings.getInstance().getServerId(zoneName);
			// api key is not set return true to skip check.
			if (apiKey == null || apiKey.isEmpty())
			{
				return true;
			}

			if (zoneName.equals("Topzone"))
			{
				url = String.format(TOPZONE_API_URL, apiKey, ip);
				urlAgent = "L2TopZone";
			}
			else if (zoneName.equals("Hopzone"))
			{
				url = String.format(HOPZONE_API_URL, apiKey, ip);
				urlAgent = "L2HopZone";
			}

			final URL obj = new URL(url);
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// add request header
			con.setRequestProperty("User-Agent", urlAgent);
			con.setConnectTimeout(5 * 1000);
			final int responseCode = con.getResponseCode();
			if (responseCode == 200) // OK
			{
				final StringBuilder sb = new StringBuilder();
				try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())))
				{
					String inputLine;
					while ((inputLine = in.readLine()) != null)
					{
						sb.append(inputLine);
					}
				}
				if (zoneName.equals("Topzone"))
				{
					System.out.println(parseTopzone(sb));
					return parseTopzone(sb);
				}
				else if (zoneName.equals("Hopzone"))
				{
					return parseHopzone(sb);
				}
			}
		}
		catch (Exception e)
		{
			_log.error("Failed to establish connection with voting provider", e);
		}
		return false;
	}

	private static boolean parseHopzone(StringBuilder sb)
	{
		for (String s : sb.toString().split(","))
		{
			if (s == null)
			{
				continue;
			}
			if (s.contains("voted"))
			{
				return s.split(":")[1].equals("true");
			}
		}
		return false;
	}

	private static boolean parseTopzone(StringBuilder sb)
	{
		for (String s : sb.toString().split("(,)|\\{"))
		{
			if (s == null)
			{
				continue;
			}

			if (s.contains("isVoted"))
			{
				return s.split(":")[1].equals("true");
			}
		}
		return false;
	}
}
