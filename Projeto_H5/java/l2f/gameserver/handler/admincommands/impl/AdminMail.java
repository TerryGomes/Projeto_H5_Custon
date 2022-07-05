package l2f.gameserver.handler.admincommands.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2f.gameserver.handler.admincommands.IAdminCommandHandler;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;

public class AdminMail implements IAdminCommandHandler
{
	public static final String MAIL_ALL_TEXT = "MAIL_ALL";
	public static final String MAIL_LIST = "MAIL_LIST";
	private static final Map<Integer, List<String>> mailNicks = new HashMap<>();

	private enum Commands
	{
		admin_add_mail, admin_remove_mail
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (!activeChar.getPlayerAccess().CanAnnounce)
		{
			return false;
		}

		switch (command)
		{
		case admin_add_mail:
			String targetToAdd;
			if (wordList.length > 1)
			{
				targetToAdd = wordList[1];
			}
			else if (activeChar.getTarget() != null && activeChar.getTarget().isPlayable())
			{
				targetToAdd = activeChar.getTarget().getPlayer().getName();
			}
			else
			{
				activeChar.sendMessage("Target a player and use //add_mail or use //add_mail nick");
				return false;
			}

			List<String> nicks = mailNicks.containsKey(activeChar.getObjectId()) ? mailNicks.get(activeChar.getObjectId()) : new ArrayList<String>();
			nicks.add(targetToAdd);
			mailNicks.put(activeChar.getObjectId(), nicks);
			activeChar.sendMessage("Player " + targetToAdd + " was added to the list!");
			showList(activeChar);
			break;
		case admin_remove_mail:
			String targetToRemove;
			if (wordList.length > 1)
			{
				targetToRemove = wordList[1];
			}
			else if (activeChar.getTarget() != null && activeChar.getTarget().isPlayable())
			{
				targetToRemove = activeChar.getTarget().getPlayer().getName();
			}
			else
			{
				activeChar.sendMessage("Target a player and use //remove_mail or use //remove_mail nick");
				return false;
			}
			List<String> currentNicks = mailNicks.containsKey(activeChar.getObjectId()) ? mailNicks.get(activeChar.getObjectId()) : new ArrayList<String>();
			currentNicks.remove(targetToRemove);
			mailNicks.put(activeChar.getObjectId(), currentNicks);
			activeChar.sendMessage("Player " + targetToRemove + " was removed from the list!");
			showList(activeChar);
			break;
		}

		return true;
	}

	private static void showList(Player activeChar)
	{
		NpcHtmlMessage msg = new NpcHtmlMessage(0);

		StringBuilder htmlBuilder = new StringBuilder("<html><title>Mail</title><body>");
		htmlBuilder.append("<table width=270>");
		int index = 0;
		for (String name : mailNicks.get(Integer.valueOf(activeChar.getObjectId())))
		{
			if (index % 3 == 0)
			{
				if (index > 0)
				{
					htmlBuilder.append("</tr>");
				}
				htmlBuilder.append("<tr>");
			}
			htmlBuilder.append("<td width=90><center>").append(name).append("</center></td>");
			index++;
		}
		htmlBuilder.append("</table></html>");// TODO end <tr>

		msg.setHtml(htmlBuilder.toString());
		activeChar.sendPacket(msg);
	}

	public static List<String> getMailNicks(Integer gmObjectId)
	{
		return mailNicks.get(gmObjectId);
	}

	public static void clearNicks(Integer gmObjectId)
	{
		mailNicks.get(gmObjectId).clear();
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}