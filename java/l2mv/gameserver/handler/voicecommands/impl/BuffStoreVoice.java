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
package l2mv.gameserver.handler.voicecommands.impl;

import l2mv.gameserver.Config;
import l2mv.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.scripts.Functions;

/**
 * Un voiced para poder setear buff stores
 *
 * @author Prims
 */
public class BuffStoreVoice extends Functions implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"buffstore"
	};

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String params)
	{
		try
		{
			// Check if the player can set a store
			if (!Config.BUFF_STORE_ALLOWED_CLASS_LIST.contains(activeChar.getClassId().getId()))
			{
				activeChar.sendMessage("Your profession is not allowed to set an Buff Store");
				return false;
			}

			// Shows the initial buff store window
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("command/buffstore/buff_store.htm");
			if (activeChar.getPrivateStoreType() == Player.STORE_PRIVATE_BUFF)
			{
				html.replace("%link%", "Stop Store");
				html.replace("%bypass%", "bypass -h BuffStore stopstore");
			}
			else
			{
				html.replace("%link%", "Create Store");
				html.replace("%bypass%", "bypass -h player_help command/buffstore/buff_store_create.htm");
			}
			activeChar.sendPacket(html);

			return true;
		}
		catch (Exception e)
		{
			activeChar.sendMessage("Use: .buffstore");
		}

		return false;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
