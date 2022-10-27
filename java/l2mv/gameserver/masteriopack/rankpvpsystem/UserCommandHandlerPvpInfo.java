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

import l2mv.gameserver.handler.usercommands.IUserCommandHandler;
import l2mv.gameserver.model.Player;

/**
 * @author Masterio
 */
public class UserCommandHandlerPvpInfo implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		RPSConfig.PVP_INFO_USER_COMMAND_ID
	};

	@Override
	public boolean useUserCommand(int id, Player activeChar)
	{
		if ((id != COMMAND_IDS[0]) || (activeChar == null) || (activeChar.getRPSCookie() == null))
		{
			return false;
		}

		RPSCookie pc = activeChar.getRPSCookie();

		// reset death status:
		if (!activeChar.isDead())
		{
			pc.setDeathStatus(null);
		}

		// save target of active player when command executed:
		if (activeChar.getTarget() != null && activeChar.getTarget() instanceof Player)
		{
			pc.setTarget((Player) activeChar.getTarget());
		}
		else
		{
			pc.setTarget(activeChar);
			activeChar.sendMessage("PvP Status executed on self!");
		}

		RPSHtmlPvpStatus.sendPage(activeChar, pc.getTarget());

		return true;
	}

	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}

}