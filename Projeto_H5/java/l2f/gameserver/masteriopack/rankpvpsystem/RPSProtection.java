/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2f.gameserver.masteriopack.rankpvpsystem;

import java.util.Map;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.Zone.ZoneType;

/**
 * Class contains some check methods.
 * Each L2j pack use difference variables or method names, so this class is dedicated for each pack.
 * @author Masterio
 */
public class RPSProtection
{
	/**
	 * No farming detected if return TRUE.<BR>
	 * Checking: Party, Clan/Ally, IP, self-kill.
	 * @param player1
	 * @param player2
	 * @return
	 */
	public static final boolean antiFarmCheck(Player player1, Player player2)
	{

		if (player1 == null || player2 == null)
		{
			return true;
		}

		if (player1.equals(player2))
		{
			return false;
		}

		// Anti FARM Clan - Ally
		if (RPSConfig.ANTI_FARM_CLAN_ALLY_ENABLED && checkClan(player1, player2) && checkAlly(player1, player2))
		{
			player1.sendMessage("PvP Farm is not allowed!");
			return false;
		}

		// Anti FARM Party
		// Anti FARM same IP
		if ((RPSConfig.ANTI_FARM_PARTY_ENABLED && checkParty(player1, player2)) || (RPSConfig.ANTI_FARM_IP_ENABLED && checkIP(player1, player2)))
		{
			player1.sendMessage("PvP Farm is not allowed!");
			return false;
		}

		return true;
	}

	/**
	 * If player on the event or on olympiad, return TRUE.
	 * This method can be difference for any l2jServer revisions.
	 * @param player
	 * @return
	 */
	public static final boolean checkEvent(Player player)
	{
		if (player.isInOlympiadMode() || player.isOlympiadGameStart())
		{
			return true;
		}

		return false;
	}

	/**
	 * If player1 and player2 are in the same clan, return TRUE.
	 * @param player1
	 * @param player2
	 * @return
	 */
	public final static boolean checkClan(Player player1, Player player2)
	{
		if (player1.getClanId() > 0 && player2.getClanId() > 0 && player1.getClanId() == player2.getClanId())
		{
			return true;
		}

		return false;
	}

	/**
	 * If player1 and player2 in the same ally, return TRUE.
	 * @param player1
	 * @param player2
	 * @return
	 */
	public final static boolean checkAlly(Player player1, Player player2)
	{
		if (player1.getAllyId() > 0 && player2.getAllyId() > 0 && player1.getAllyId() == player2.getAllyId())
		{
			return true;
		}

		return false;
	}

	/**
	 * If player1 and player2 clans have a war, return TRUE.
	 * @param player1
	 * @param player2
	 * @return
	 */
	public final static boolean checkWar(Player player1, Player player2)
	{
		if (player1.getClanId() > 0 && player2.getClanId() > 0 && player1.getClan() != null && player2.getClan() != null && player1.getClan().isAtWarWith(player2.getClan().getClanId()))
		{
			return true;
		}

		return false;
	}

	/**
	 * If player1 and player2 are in party return TRUE.
	 * @param player1
	 * @param player2
	 * @return
	 */
	public final static boolean checkParty(Player player1, Player player2)
	{
		if (player1.getParty() != null && player2.getParty() != null && player1.getParty().equals(player2.getParty()))
		{
			return true;
		}

		return false;
	}

	/**
	 *  If killer and victim have the same IP address return TRUE.
	 * @param killer
	 * @param victim
	 * @return
	 */
	public final static boolean checkIP(Player killer, Player victim)
	{
		if (killer.getClient() != null && victim.getClient() != null)
		{
			String ip1 = killer.getClient().getConnection().getClient().getIpAddr();
			String ip2 = victim.getClient().getConnection().getClient().getIpAddr();

			if (ip1.equals(ip2))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns true if character is in allowed zone.
	 * @param player
	 * @return
	 */
	public static final boolean isInPvpAllowedZone(Player player)
	{
		if (RPSConfig.ALLOWED_ZONES_IDS.size() == 0)
		{
			return true;
		}

		for (Integer value : RPSConfig.ALLOWED_ZONES_IDS)
		{
			ZoneType zone = getZoneId(value);

			if (zone != null && player.isInZone(zone))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns true if character is in restricted zone.
	 * @param player
	 * @return
	 */
	public static final boolean isInPvpRestrictedZone(Player player)
	{
		for (Integer value : RPSConfig.RESTRICTED_ZONES_IDS)
		{
			ZoneType zone = getZoneId(value);

			if (zone != null && player.isInZone(zone))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns true if character is in restricted zone for death manager.
	 * @param player
	 * @return
	 */
	public static final boolean isInDMRestrictedZone(Player player)
	{
		for (Integer value : RPSConfig.DEATH_MANAGER_RESTRICTED_ZONES_IDS)
		{
			ZoneType zone = getZoneId(value);

			if (zone != null && player.isInZone(zone))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns 1.0 if character is not in Bonus Ratio zone, otherwise returns ratio from configuration file.
	 * @param player
	 * @return
	 */
	public static final double getZoneBonusRatio(Player player)
	{
		for (Map.Entry<Integer, Double> e : RPSConfig.RANK_POINTS_BONUS_ZONES_IDS.entrySet())
		{
			ZoneType zone = getZoneId(e.getKey());

			if (zone != null && player.isInZone(zone))
			{
				return e.getValue();
			}
		}

		return 1.0;
	}

	/**
	 * Returns the ZoneId.<br>
	 * ZoneId not exists in lower revisions of l2jServer (H5), then this method can be removed.<br>
	 * <b>IMPORTANT:</b> L2jServer have difference zone id's for each L2 chronicle.
	 * @param zoneId
	 * @return
	 */
	private static final ZoneType getZoneId(int zoneId)
	{
		ZoneType zone = null;

		switch (zoneId)
		{
		case 0:
			return ZoneType.battle_zone;
		case 1:
			return ZoneType.peace_zone;
		case 2:
			return ZoneType.SIEGE;
		case 3:
			return ZoneType.mother_tree;
		case 6:
			return ZoneType.no_landing;
		case 7:
			return ZoneType.water;
		case 11:
			return ZoneType.swamp;
		}

		return zone;
	}
}
