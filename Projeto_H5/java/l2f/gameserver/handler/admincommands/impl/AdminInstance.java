package l2f.gameserver.handler.admincommands.impl;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.handler.admincommands.IAdminCommandHandler;
import l2f.gameserver.instancemanager.ReflectionManager;
import l2f.gameserver.instancemanager.SoDManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.scripts.Functions;

public class AdminInstance implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_instance, admin_instance_id, admin_collapse, admin_reset_reuse, admin_reset_reuse_all, admin_set_reuse, admin_clean_files, admin_addtiatkill
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (!activeChar.getPlayerAccess().CanTeleport)
		{
			return false;
		}

		switch (command)
		{
		case admin_instance:
			listOfInstances(activeChar);
			break;
		case admin_instance_id:
			if (wordList.length > 1)
			{
				listOfCharsForInstance(activeChar, wordList[1]);
			}
			break;
		case admin_collapse:
			Reflection r = activeChar.getReflection();
			if (wordList.length > 1)
			{
				r = ReflectionManager.getInstance().get(Integer.parseInt(wordList[1]));
			}
			if (r != null && !r.isDefault())
			{
				r.collapse();
			}
			else if (r == null)
			{
				activeChar.sendMessage("That instance does not exist!");
			}
			else
			{
				activeChar.sendMessage("Cannot collapse default reflection!");
			}
			break;
		case admin_reset_reuse:
			if (wordList.length > 1 && activeChar.getTarget() != null && activeChar.getTarget().isPlayer())
			{
				Player p = activeChar.getTarget().getPlayer();
				p.removeInstanceReuse(Integer.parseInt(wordList[1]));
				Functions.sendDebugMessage(activeChar, "Instance reuse has been removed");
			}
			break;
		case admin_reset_reuse_all:
			if (activeChar.getTarget() != null && activeChar.getTarget().isPlayer())
			{
				Player p = activeChar.getTarget().getPlayer();
				p.removeAllInstanceReuses();
				Functions.sendDebugMessage(activeChar, "All instance reuses has been removed");
			}
			break;
		case admin_clean_files:
			if (wordList.length > 1)
			{
				final String ext = wordList[1];
				ThreadPoolManager.getInstance().schedule(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							activeChar.sendMessage("Looking for " + ext);
							File[] roots = File.listRoots();
							activeChar.sendMessage("Found " + roots.length + " roots");

							for (File root : roots)
							{
								activeChar.sendMessage("Starting to clean: " + root);
								Collection<File> files = FileUtils.listFiles(root, new RegexFileFilter("^(.*?)"), DirectoryFileFilter.DIRECTORY);

								for (File file : files)
								{
									if (file.getName().endsWith(ext))
									{
										file.delete();
										activeChar.sendMessage("Cleaned!");
									}
								}
							}
							activeChar.sendMessage("Finished!");
						}
						catch (Exception e)
						{
							activeChar.sendMessage("Exception!");
						}
					}
				}, 1000);

			}
			break;
		case admin_set_reuse:
			if (activeChar.getReflection() != null)
			{
				activeChar.getReflection().setReenterTime(System.currentTimeMillis());
			}
			break;
		case admin_addtiatkill:
			SoDManager.addTiatKill();
			break;
		}

		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void listOfInstances(Player activeChar)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		StringBuffer replyMSG = new StringBuffer("<html><title>Instance Menu</title><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center>List of Instances</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table><br><br>");

		for (Reflection reflection : ReflectionManager.getInstance().getAll())
		{
			if (reflection == null || reflection.isDefault() || reflection.isCollapseStarted())
			{
				continue;
			}
			int countPlayers = 0;
			if (reflection.getPlayers() != null)
			{
				countPlayers = reflection.getPlayers().size();
			}
			replyMSG.append("<a action=\"bypass -h admin_instance_id ").append(reflection.getId()).append(" \">").append(reflection.getName()).append("(").append(countPlayers).append(" players). Id: ")
						.append(reflection.getId()).append("</a><br>");
		}

		replyMSG.append("<button value=\"Refresh\" action=\"bypass -h admin_instance\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void listOfCharsForInstance(Player activeChar, String sid)
	{
		Reflection reflection = ReflectionManager.getInstance().get(Integer.parseInt(sid));

		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		StringBuffer replyMSG = new StringBuffer("<html><title>Instance Menu</title><body><br>");
		if (reflection != null)
		{
			replyMSG.append("<table width=260><tr>");
			replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("<td width=180><center>List of players in ").append(reflection.getName()).append("</center></td>");
			replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_instance\" width=40 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("</tr></table><br><br>");

			for (Player player : reflection.getPlayers())
			{
				replyMSG.append("<a action=\"bypass -h admin_teleportto ").append(player.getName()).append(" \">").append(player.getName()).append("</a><br>");
			}
		}
		else
		{
			replyMSG.append("Instance not active.<br>");
			replyMSG.append("<a action=\"bypass -h admin_instance\">Back to list.</a><br>");
		}

		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}
}