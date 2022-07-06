package l2mv.gameserver.handler.admincommands.impl;

import java.util.ArrayList;
import java.util.StringTokenizer;

import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.handler.admincommands.IAdminCommandHandler;
import l2mv.gameserver.instancemanager.CastleManorManager;
import l2mv.gameserver.instancemanager.ServerVariables;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.residence.Castle;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.templates.manor.CropProcure;
import l2mv.gameserver.templates.manor.SeedProduction;

/**
 * Admin comand handler for Manor System
 * This class handles following admin commands:
 * - manor_info = shows info about current manor state
 * - manor_approve = approves settings for the next manor period
 * - manor_setnext = changes manor settings to the next day's
 * - manor_reset castle = resets all manor data for specified castle (or all)
 * - manor_setmaintenance = sets manor system under maintenance mode
 * - manor_save = saves all manor data into database
 * - manor_disable = disables manor system
 */
@SuppressWarnings("unused")
public class AdminManor implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_manor, admin_manor_reset, admin_manor_save, admin_manor_disable
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (!activeChar.getPlayerAccess().Menu)
		{
			return false;
		}

		StringTokenizer st = new StringTokenizer(fullString);
		fullString = st.nextToken();

		if (fullString.equals("admin_manor"))
		{
			showMainPage(activeChar);
		}
		else if (fullString.equals("admin_manor_reset"))
		{
			int castleId = 0;
			try
			{
				castleId = Integer.parseInt(st.nextToken());
			}
			catch (Exception e)
			{
			}

			if (castleId > 0)
			{
				Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, castleId);
				castle.setCropProcure(new ArrayList<CropProcure>(), CastleManorManager.PERIOD_CURRENT);
				castle.setCropProcure(new ArrayList<CropProcure>(), CastleManorManager.PERIOD_NEXT);
				castle.setSeedProduction(new ArrayList<SeedProduction>(), CastleManorManager.PERIOD_CURRENT);
				castle.setSeedProduction(new ArrayList<SeedProduction>(), CastleManorManager.PERIOD_NEXT);
				castle.saveCropData();
				castle.saveSeedData();
				activeChar.sendMessage("Manor data for " + castle.getName() + " was nulled");
			}
			else
			{
				for (Castle castle : ResidenceHolder.getInstance().getResidenceList(Castle.class))
				{
					castle.setCropProcure(new ArrayList<CropProcure>(), CastleManorManager.PERIOD_CURRENT);
					castle.setCropProcure(new ArrayList<CropProcure>(), CastleManorManager.PERIOD_NEXT);
					castle.setSeedProduction(new ArrayList<SeedProduction>(), CastleManorManager.PERIOD_CURRENT);
					castle.setSeedProduction(new ArrayList<SeedProduction>(), CastleManorManager.PERIOD_NEXT);
					castle.saveCropData();
					castle.saveSeedData();
				}
				activeChar.sendMessage("Manor data was nulled");
			}
			showMainPage(activeChar);
		}
		else if (fullString.equals("admin_manor_save"))
		{
			CastleManorManager.getInstance().save();
			activeChar.sendMessage("Manor System: all data saved");
			showMainPage(activeChar);
		}
		else if (fullString.equals("admin_manor_disable"))
		{
			boolean mode = CastleManorManager.getInstance().isDisabled();
			CastleManorManager.getInstance().setDisabled(!mode);
			if (mode)
			{
				activeChar.sendMessage("Manor System: enabled");
			}
			else
			{
				activeChar.sendMessage("Manor System: disabled");
			}
			showMainPage(activeChar);
		}

		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void showMainPage(Player activeChar)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		StringBuilder replyMSG = new StringBuilder("<html><body>");

		replyMSG.append("<center><font color=\"LEVEL\"> [Manor System] </font></center><br>");
		replyMSG.append("<table width=\"100%\">");
		replyMSG.append("<tr><td>Disabled: " + (CastleManorManager.getInstance().isDisabled() ? "yes" : "no") + "</td>");
		replyMSG.append("<td>Under Maintenance: " + (CastleManorManager.getInstance().isUnderMaintenance() ? "yes" : "no") + "</td></tr>");
		replyMSG.append("<tr><td>Approved: " + (ServerVariables.getBool("ManorApproved") ? "yes" : "no") + "</td></tr>");
		replyMSG.append("</table>");

		replyMSG.append("<center><table>");
		replyMSG.append("<tr><td><button value=\"" + (CastleManorManager.getInstance().isDisabled() ? "Enable" : "Disable") + "\" action=\"bypass -h admin_manor_disable\" width=110 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Reset\" action=\"bypass -h admin_manor_reset\" width=110 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
		replyMSG.append("<tr><td><button value=\"Refresh\" action=\"bypass -h admin_manor\" width=110 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Back\" action=\"bypass -h admin_admin\" width=110 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
		replyMSG.append("</table></center>");

		replyMSG.append("<br><center>Castle Information:<table width=\"100%\">");
		replyMSG.append("<tr><td></td><td>Current Period</td><td>Next Period</td></tr>");

		for (Castle c : ResidenceHolder.getInstance().getResidenceList(Castle.class))
		{
			replyMSG.append("<tr><td>" + c.getName() + "</td>" + "<td>" + c.getManorCost(CastleManorManager.PERIOD_CURRENT) + "a</td>" + "<td>" + c.getManorCost(CastleManorManager.PERIOD_NEXT) + "a</td>" + "</tr>");
		}
		replyMSG.append("</table><br>");

		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}
}