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
package l2mv.gameserver.masteriopack.rankpvpsystem;

import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;

/**
 * Class used in Player. Contains some system variables used in game.
 * From Killer side.
 * @author Masterio
 */
public class RPSCookie
{
	private RPSHtmlDeathStatus _deathStatus = null;
	private RPSHtmlComboKill _comboKill = null;

	private Player _target = null;

	public void runPvpTask(Player player, Creature target)
	{
		if (RPSConfig.RANK_PVP_SYSTEM_ENABLED)
		{
			if (player != null && target != null && target instanceof Player)
			{
				((Player) target).getRPSCookie().setTarget(player);

				ThreadPoolManager.getInstance().execute(new RankPvpSystemPvpTask(player, (Player) target));
			}
		}
	}

	public class RankPvpSystemPvpTask implements Runnable
	{
		private Player _killer = null;
		private Player _victim = null;

		public RankPvpSystemPvpTask(Player killer, Player victim)
		{
			_killer = killer;
			_victim = victim;
		}

		@Override
		public void run()
		{
			RankPvpSystem rps = new RankPvpSystem(_killer, _victim);

			rps.doPvp();
		}
	}

	public RPSHtmlDeathStatus getDeathStatus()
	{
		return _deathStatus;
	}

	public boolean isDeathStatusActive()
	{
		if (_deathStatus == null)
		{
			return false;
		}

		return true;
	}

	public void setDeathStatus(RPSHtmlDeathStatus deathStatus)
	{
		_deathStatus = deathStatus;
	}

	public RPSHtmlComboKill getComboKill()
	{
		return _comboKill;
	}

	public boolean isComboKillActive()
	{
		if (_comboKill == null)
		{
			return false;
		}

		return true;
	}

	public void setComboKill(RPSHtmlComboKill comboKill)
	{
		_comboKill = comboKill;
	}

	/**
	 * The player's Target.
	 * @return
	 */
	public Player getTarget()
	{
		return _target;
	}

	/**
	 * The player's Target.
	 * @param target
	 */
	public void setTarget(Player target)
	{
		_target = target;
	}
}
