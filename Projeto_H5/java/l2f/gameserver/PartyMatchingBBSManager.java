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
//package com.l2f.gameserver.communitybbs.Manager;
package l2f.gameserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javolution.text.TextBuilder;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.handler.bbs.ICommunityBoardHandler;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.ShowBoard;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.utils.Util;

public class PartyMatchingBBSManager extends Functions implements ICommunityBoardHandler
{
	public List<Player> partyMatchingPlayersList = new ArrayList<>();
	public Map<Integer, String> partyMatchingDescriptionList = new HashMap<>();
	@SuppressWarnings("unused")
	private static final Logger _log = LoggerFactory.getLogger(PartyMatchingBBSManager.class);

	public void parsecmd(String command, Player activeChar)
	{
		if (command.equals("_maillist_0_1_0_") || command.equals("_bbsPartyMatching"))
		{
			ShowBoard.separateAndSend(partyMatchingList(activeChar, 1), activeChar);
		}
		else if (command.startsWith("_bbsPartyMatching;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			int page;
			if (st.hasMoreTokens())
			{
				page = Integer.parseInt(st.nextToken());
			}
			else
			{
				page = 1;
			}

			ShowBoard.separateAndSend(partyMatchingList(activeChar, page), activeChar);
		}
		else if (command.startsWith("_bbsPartyMatchingEnter"))
		{
			if (partyMatchingPlayersList.contains(activeChar))
			{
				activeChar.sendMessage("You're alredy on the Party Matching list!");
			}
			else if (activeChar.isInParty())
			{
				activeChar.sendMessage("You cant join the Party Matching list while you're in party!");
			}
			else if (command.substring(22).isEmpty())
			{
				activeChar.sendMessage("Fill the description box in order to join the Party Matching list.");
			}
			else if (command.substring(22).length() <= 10)
			{
				activeChar.sendMessage("Please, input more than 10 characters.");
			}
			/*
			 * else if (command.substring(22).length() >= 55) { activeChar.sendMessage("Please, input less than 55 characters."); }
			 */
			else
			{
				partyMatchingPlayersList.add(activeChar);
				partyMatchingDescriptionList.put(activeChar.getObjectId(), command.substring(23));
				activeChar.sendMessage("You've joined the Party Matching list.");
			}

			ShowBoard.separateAndSend(partyMatchingList(activeChar, 1), activeChar);
		}
		else if (command.equals("_bbsPartyMatchingLeave"))
		{
			if (!partyMatchingPlayersList.contains(activeChar))
			{
				activeChar.sendMessage("You're not in the Party Matching list!");
			}
			else if (activeChar.isInParty())
			{
				activeChar.sendMessage("You cant join the Party Matching list while you're in party!");
			}
			else
			{
				partyMatchingPlayersList.remove(activeChar);
				partyMatchingDescriptionList.remove(activeChar.getObjectId());
				activeChar.sendMessage("You've left the Party Matching list.");
			}

			ShowBoard.separateAndSend(partyMatchingList(activeChar, 1), activeChar);
		}
		else
		{
			ShowBoard.separateAndSend("<html><body><br><br><center>The command: " + command + " is not implemented yet.</center><br><br></body></html>", activeChar);
		}
	}

	public String partyMatchingList(Player player, int currentPage)
	{
		String content = HtmCache.getInstance().getNotNull("CommunityBoard/PartyMatching/Home.htm", player);
		if (content == null)
		{
			content = "<html><body><br><br><center>Error 404: File not found: 'CommunityBoard/PartyMatching/Home.htm'.<br>Report to an administrator.</center></body></html>";
		}

		int maxPlayersPerPage = 10;
		int page = 1;
		int count = 0;

		final int maxPage = (int) Math.ceil(partyMatchingPlayersList.size() / (double) maxPlayersPerPage);
		{
			currentPage = Math.min(currentPage, maxPage);
		}

		int changeColor = 0;
		TextBuilder partyMatchingList = new TextBuilder();
		if (partyMatchingPlayersList.isEmpty())
		{
			partyMatchingList.append("<table width=780 height=31 bgcolor=171612>");
			partyMatchingList.append("<tr>");
			partyMatchingList.append("<td fixwidth=780 align=center>There's nobody on the Party Matching list yet.</td>");
			partyMatchingList.append("</tr>");
			partyMatchingList.append("</table>");
			partyMatchingList.append("<img src=\"L2UI.SquareGray\" width=780 height=1>");
		}
		else
		{
			for (Player activeChar : partyMatchingPlayersList)
			{
				count++;
				if (count >= maxPlayersPerPage)
				{
					count = 0;
					page++;
					continue;
				}

				if (page != currentPage)
				{
					continue;
				}

				changeColor++;
				partyMatchingList.append("<table width=775 height=35 " + ((changeColor % 2) == 1 ? "" : "bgcolor=171612") + "><tr>");
				partyMatchingList.append("<td fixwidth=200 align=center>" + activeChar.getName() + "</td>");
				partyMatchingList.append("<td fixwidth=14 height=18><img src=\"L2UI_CH3." + getClassIcon(activeChar.getClassId().getId()) + "\" width=12 height=12></td>");
				partyMatchingList.append("<td fixwidth=125>" + Util.getFullClassName(activeChar.getClassId()) + "</td>");
				partyMatchingList.append("<td fixwidth=350>" + partyMatchingDescriptionList.get(activeChar.getObjectId()) + "</td>");
				partyMatchingList.append("<td fixwidth=80 align=right><button action=\"bypass -h partyMatchingInvite " + activeChar.getName() + " \" value=\"Invite\" width=80 height=27 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
				partyMatchingList.append("</tr></table>");
				partyMatchingList.append("<img src=\"L2UI.SquareGray\" width=780 height=1>");
			}
		}

		StringBuilder pages = new StringBuilder();
		for (int i = Math.max(currentPage - (maxPlayersPerPage / 2), 1); i <= maxPage; i++)
		{
			if (i == currentPage)
			{
				pages.append("<td fixwidth=20 align=center><font color=LEVEL>" + i + "</font></td>");
			}
			else
			{
				pages.append("<td fixwidth=20 align=center><a action=\"bypass _bbsPartyMatching;" + i + "\">" + i + "</a></td>");
			}

			if (i != maxPage)
			{
				pages.append("<td align=center valign=middle><img src=L2UI.SquareGray width=1 height=12></td>");
			}
		}

		content = content.replace("%pages%", pages.toString());
		content = content.replace("%partyMatchingMembersList%", partyMatchingList.toString());

		return content;
	}

	public String getClassIcon(int classId)
	{
		switch (classId)
		{
		case 0:
			return "party_styleicon1_1";
		case 1:
			return "party_styleicon1_1";
		case 2:
			return "party_styleicon1";
		case 3:
			return "party_styleicon1";
		case 4:
			return "party_styleicon1_1";
		case 5:
			return "party_styleicon3";
		case 6:
			return "party_styleicon3";
		case 7:
			return "party_styleicon1_1";
		case 8:
			return "party_styleicon1";
		case 9:
			return "party_styleicon2";
		case 10:
			return "party_styleicon1_2";
		case 11:
			return "party_styleicon1_2";
		case 12:
			return "party_styleicon5";
		case 13:
			return "party_styleicon5";
		case 14:
			return "party_styleicon7";
		case 15:
			return "party_styleicon1_2";
		case 16:
			return "party_styleicon6";
		case 17:
			return "party_styleicon6";
		case 18:
			return "party_styleicon1_1";
		case 19:
			return "party_styleicon1_1";
		case 20:
			return "party_styleicon3";
		case 21:
			return "party_styleicon4";
		case 22:
			return "party_styleicon1_1";
		case 23:
			return "party_styleicon1";
		case 24:
			return "party_styleicon2";
		case 25:
			return "party_styleicon1_2";
		case 26:
			return "party_styleicon1_2";
		case 27:
			return "party_styleicon5";
		case 28:
			return "party_styleicon7";
		case 29:
			return "party_styleicon6";
		case 30:
			return "party_styleicon6";
		case 31:
			return "party_styleicon1_1";
		case 32:
			return "party_styleicon1_1";
		case 33:
			return "party_styleicon3";
		case 34:
			return "party_styleicon4";
		case 35:
			return "party_styleicon1_1";
		case 36:
			return "party_styleicon1";
		case 37:
			return "party_styleicon2";
		case 38:
			return "party_styleicon1_2";
		case 39:
			return "party_styleicon1_2";
		case 40:
			return "party_styleicon5";
		case 41:
			return "party_styleicon7";
		case 42:
			return "party_styleicon6";
		case 43:
			return "party_styleicon6";
		case 44:
			return "party_styleicon1_1";
		case 45:
			return "party_styleicon1_1";
		case 46:
			return "party_styleicon1";
		case 47:
			return "party_styleicon1_1";
		case 48:
			return "party_styleicon1";
		case 49:
			return "party_styleicon1_2";
		case 50:
			return "party_styleicon1_2";
		case 51:
			return "party_styleicon6";
		case 52:
			return "party_styleicon6";
		case 53:
			return "party_styleicon1_1";
		case 54:
			return "party_styleicon1_1";
		case 55:
			return "party_styleicon1";
		case 56:
			return "party_styleicon1_1";
		case 57:
			return "party_styleicon1";
		case 88:
			return "party_styleicon1_3";
		case 89:
			return "party_styleicon1_3";
		case 90:
			return "party_styleicon3_3";
		case 91:
			return "party_styleicon3_3";
		case 92:
			return "party_styleicon2_3";
		case 93:
			return "party_styleicon1_3";
		case 94:
			return "party_styleicon5_3";
		case 95:
			return "party_styleicon5_3";
		case 96:
			return "party_styleicon7_3";
		case 97:
			return "party_styleicon6_3";
		case 98:
			return "party_styleicon6_3";
		case 99:
			return "party_styleicon3_3";
		case 100:
			return "party_styleicon4_3";
		case 101:
			return "party_styleicon1_3";
		case 102:
			return "party_styleicon2_3";
		case 103:
			return "party_styleicon5_3";
		case 104:
			return "party_styleicon7_3";
		case 105:
			return "party_styleicon6_3";
		case 106:
			return "party_styleicon3_3";
		case 107:
			return "party_styleicon4_3";
		case 108:
			return "party_styleicon1_3";
		case 109:
			return "party_styleicon2_3";
		case 110:
			return "party_styleicon5_3";
		case 111:
			return "party_styleicon7_3";
		case 112:
			return "party_styleicon6_3";
		case 113:
			return "party_styleicon1_3";
		case 114:
			return "party_styleicon1_3";
		case 115:
			return "party_styleicon6_3";
		case 116:
			return "party_styleicon6_3";
		case 117:
			return "party_styleicon1_3";
		case 118:
			return "party_styleicon1_3";
		default:
			return "party_styleicon1_1";
		}
	}

	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, Player activeChar)
	{
	}

	public static PartyMatchingBBSManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final PartyMatchingBBSManager _instance = new PartyMatchingBBSManager();
	}

	@Override
	public String[] getBypassCommands()
	{
		return new String[]
		{
			"_bbsPartyMatching",
			"_bbsPartyMatching;",
			"_bbsPartyMatchingEnter",
			"_bbsPartyMatchingLeave"
		};
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		parsecmd(bypass, player);
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		// TODO Auto-generated method stub

	}
}