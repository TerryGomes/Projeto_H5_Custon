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
package l2mv.gameserver.multverso.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;

public class AntiFeedManager
{
	private final Map<Integer, Long> _lastDeathTimes = new ConcurrentHashMap<>();

	/**
	 * Set time of the last player's death to current
	 * @param objectId Player's objectId
	 */
	public final void setLastDeathTime(int objectId)
	{
		_lastDeathTimes.put(objectId, System.currentTimeMillis());
	}

	/**
	 * Check if current kill should be counted as non-feeded.
	 * @param attacker Attacker character
	 * @param target Target character
	 * @return True if kill is non-feeded.
	 */
	public final boolean check(Creature attacker, Creature target)
	{
		if (!Config.ANTIFEED_ENABLE)
		{
			return true;
		}

		if (target == null || attacker == null)
		{
			return false;
		}

		final Player targetPlayer = target.getPlayer();
		if (targetPlayer == null)
		{
			return false;
		}

		final Player attackerPlayer = attacker.getPlayer();
		if (attackerPlayer == null)
		{
			return false;
		}

		// Tiempo de muerte
		if (Config.ANTIFEED_INTERVAL > 0 && _lastDeathTimes.containsKey(targetPlayer.getObjectId()))
		{
			if (System.currentTimeMillis() - _lastDeathTimes.get(targetPlayer.getObjectId()) < Config.ANTIFEED_INTERVAL)
			{
				return false;
			}
		}

		// Synerge - Chequeamos diferencias de level maximo
		// Synerge - Chequeamos si esta en el mismo clan o ally
		if ((Config.ANTIFEED_MAX_LVL_DIFFERENCE > 0 && attackerPlayer.getLevel() - target.getLevel() > Config.ANTIFEED_MAX_LVL_DIFFERENCE) || (targetPlayer.getClanId() > 0 && targetPlayer.getClanId() == attackerPlayer.getClanId()))
		{
			return false;
		}

		if (targetPlayer.getAllyId() > 0 && targetPlayer.getAllyId() == attackerPlayer.getAllyId())
		{
			return false;
		}

		// Dualbox
		if (Config.ANTIFEED_DUALBOX)
		{
			// Unable to check ip address
			if (Config.ANTIFEED_DISCONNECTED_AS_DUALBOX)
			{
				if (!attackerPlayer.isConnected() || !attackerPlayer.isOnline() || !targetPlayer.isConnected() || !targetPlayer.isOnline())
				{
					return false;
				}
			}

			// Check for same hwid
			return !attackerPlayer.getHWID().equalsIgnoreCase(targetPlayer.getHWID());
		}

		return true;
	}

	/**
	 * Clears all timestamps
	 */
	public final void clear()
	{
		_lastDeathTimes.clear();
	}

	public static final AntiFeedManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final AntiFeedManager _instance = new AntiFeedManager();
	}
}
