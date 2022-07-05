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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.CreatureSay;

/**
 * This class contains informations about Combo Level and Last Combo Kill Time.
 * @author Masterio
 */
public class RPSHtmlComboKill
{
	private List<Integer> _victims = new ArrayList<>(); // list of victims id's
	private int _comboLevel = 0; // contains real combo size, do not use _victims.size() !!!
	private long _lastKillTime = 0; // the time of the last kill counted to combo, this is not standard PvP kill time !

	// combo shout definitions:
	public static final int SAY_TYPE = 2; // local or global area are defined by sending methods.

	/**
	 * Add victim id into victims list and increase combo level with defined rules.
	 * @param victimId
	 * @param killTime
	 * @return
	 */
	public boolean addVictim(int victimId, long killTime)
	{

		if (!RPSConfig.COMBO_KILL_PROTECTION_NO_REPEAT_ENABLED)
		{
			_comboLevel++;
			_lastKillTime = killTime;

			return true;
		}

		// else if RPSConfig.COMBO_KILL_PROTECTION_NO_REPEAT_ENABLED:
		if (!_victims.contains(victimId))
		{
			_victims.add(victimId);
			_comboLevel++;
			_lastKillTime = killTime;

			return true;
		}

		return false;
	}

	/**
	 * Shout in LOCAL or GLOBAL area information about killer's combo.
	 * @param killer
	 * @param victim
	 */
	public void shoutComboKill(Player killer, Player victim)
	{

		String msg = null;

		CreatureSay cs;

		if (!RPSConfig.COMBO_KILL_ALT_MESSAGES_ENABLED)
		{
			if (RPSConfig.COMBO_KILL_LOCAL_AREA_MESSAGES.containsKey(getComboLevel()))
			{
				msg = RPSConfig.COMBO_KILL_LOCAL_AREA_MESSAGES.get(getComboLevel());
				msg = msg.replace("%killer%", killer.getName());
				msg = msg.replace("%victim%", victim.getName());
				msg = msg.replace("%combo_level%", Integer.toString(getComboLevel()));

				cs = new CreatureSay(0, SAY_TYPE, "", msg);

				GameObjectsStorage.getAllPlayers().forEach(s -> s.sendPacket(cs));
			}
			else if (RPSConfig.COMBO_KILL_GLOBAL_AREA_MESSAGES.containsKey(getComboLevel()))
			{
				msg = RPSConfig.COMBO_KILL_GLOBAL_AREA_MESSAGES.get(getComboLevel());
				msg = msg.replace("%killer%", killer.getName());
				msg = msg.replace("%victim%", victim.getName());
				msg = msg.replace("%combo_level%", Integer.toString(getComboLevel()));

				cs = new CreatureSay(0, SAY_TYPE, "", msg);

				GameObjectsStorage.getAllPlayers().forEach(s -> s.sendPacket(cs));
			}
			else
			{
				// global have higher priority than local.
				Entry<Integer, String> last = null;

				for (Entry<Integer, String> value : RPSConfig.COMBO_KILL_GLOBAL_AREA_MESSAGES.entrySet())
				{
					last = value;
				}

				if (last != null && last.getKey() != null && getComboLevel() > last.getKey())
				{
					// if combo size greater than global max key.
					msg = last.getValue();
					msg = msg.replace("%killer%", killer.getName());
					msg = msg.replace("%victim%", victim.getName());
					msg = msg.replace("%combo_level%", Integer.toString(getComboLevel()));

					cs = new CreatureSay(0, SAY_TYPE, "", msg);

					GameObjectsStorage.getAllPlayers().forEach(s -> s.sendPacket(cs));
				}
				else if (last != null && last.getKey() != null && getComboLevel() > last.getKey())
				{
					// if combo size greater than local max key.
					msg = last.getValue();
					msg = msg.replace("%killer%", killer.getName());
					msg = msg.replace("%victim%", victim.getName());
					msg = msg.replace("%combo_level%", Integer.toString(getComboLevel()));

					cs = new CreatureSay(0, SAY_TYPE, "", msg);

					GameObjectsStorage.getAllPlayers().forEach(s -> s.sendPacket(cs));
				}
			}
		}
		else if (getComboLevel() > 1)
		{
			if (RPSConfig.COMBO_KILL_ALT_GLOBAL_MESSAGE_LVL > 0 && getComboLevel() >= RPSConfig.COMBO_KILL_ALT_GLOBAL_MESSAGE_LVL)
			{
				msg = RPSConfig.COMBO_KILL_ALT_MESSAGE;
				msg = msg.replace("%killer%", killer.getName());
				msg = msg.replace("%victim%", victim.getName());
				msg = msg.replace("%combo_level%", Integer.toString(getComboLevel()));

				cs = new CreatureSay(0, SAY_TYPE, "", msg);

				GameObjectsStorage.getAllPlayers().forEach(s -> s.sendPacket(cs));
			}
			else
			{
				msg = RPSConfig.COMBO_KILL_ALT_MESSAGE;
				msg = msg.replace("%killer%", killer.getName());
				msg = msg.replace("%victim%", victim.getName());
				msg = msg.replace("%combo_level%", Integer.toString(getComboLevel()));

				cs = new CreatureSay(0, SAY_TYPE, "", msg);

				GameObjectsStorage.getAllPlayers().forEach(s -> s.sendPacket(cs));
			}
		}
	}

	/**
	 * Shout in chat window information about defeated killer, <br>
	 * who have combo level >= COMBO_KILL_DEFEAT_MESSAGE_MIN_LVL.
	 * @param killer - defeated player (victim).
	 */
	public void shoutDefeatMessage(Player killer)
	{
		if (RPSConfig.COMBO_KILL_DEFEAT_MESSAGE_ENABLED)
		{
			if (getComboLevel() >= RPSConfig.COMBO_KILL_DEFEAT_MESSAGE_MIN_LVL)
			{
				String msg = RPSConfig.COMBO_KILL_DEFEAT_MESSAGE;
				msg = msg.replace("%killer%", killer.getName());
				msg = msg.replace("%combo_level%", Integer.toString(getComboLevel()));

				CreatureSay cs = new CreatureSay(0, SAY_TYPE, "", msg);

				GameObjectsStorage.getAllPlayers().stream().filter(s -> s.getDistance(s) <= 2700).forEach(s -> s.sendPacket(cs));
			}
		}
	}

	/**
	 * Get Rank Points ratio for combo size.
	 * @return
	 */
	public double getComboKillRankPointsRatio()
	{
		if (getComboLevel() > 0)
		{
			Map<Integer, Double> list = RPSConfig.COMBO_KILL_RANK_POINTS_RATIO;

			// checking if combo size is in combo rank points ratio table:
			if (list.containsKey(_comboLevel))
			{
				return list.get(_comboLevel);
			}

			// if not, then check the last element of table.
			// Reason: combo size can be greater than max table value, then killer should get max ratio:
			Entry<Integer, Double> last = null;

			for (Entry<Integer, Double> value : list.entrySet())
			{
				last = value;
			}

			if (last != null && last.getKey() < getComboLevel())
			{
				return last.getValue();
			}
		}

		return 1.0;
	}

	/**
	 * @return the _victims
	 */
	public List<Integer> getVictims()
	{
		return _victims;
	}

	/**
	 * @param victims the _victims to set
	 */
	public void setVictims(List<Integer> victims)
	{
		_victims = victims;
	}

	/**
	 * @return the _victimsSize
	 */
	public int getComboLevel()
	{
		return _comboLevel;
	}

	/**
	 * @param comboLevel the _comboLevel to set
	 */
	public void setComboLevel(int comboLevel)
	{
		_comboLevel = comboLevel;
	}

	/**
	 * @return the _lastKillTime
	 */
	public long getLastKillTime()
	{
		return _lastKillTime;
	}

	/**
	 * @param lastKillTime the _lastKillTime to set
	 */
	public void setLastKillTime(long lastKillTime)
	{
		_lastKillTime = lastKillTime;
	}
}
