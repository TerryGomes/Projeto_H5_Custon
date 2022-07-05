package l2f.gameserver.handler.voicecommands.impl;

import java.text.SimpleDateFormat;

import org.apache.commons.lang3.math.NumberUtils;

import l2f.gameserver.data.xml.holder.ResidenceHolder;
import l2f.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2f.gameserver.model.entity.residence.Castle;
import l2f.gameserver.model.entity.residence.Dominion;
import l2f.gameserver.model.entity.residence.Fortress;
import l2f.gameserver.model.entity.residence.Residence;
import l2f.gameserver.network.serverpackets.CastleSiegeInfo;
import l2f.gameserver.network.serverpackets.ExShowDominionRegistry;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * Command .siege which allows players to Participate to Castle Sieges or check their starting dates.
 */
public class SiegeVoice implements IVoicedCommandHandler
{
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEEE HH:mm");

	private static final String MAIN_HTML_PATH = "command/siege.htm";
	private static final String FORT_HTML_PATH = "command/siegeFortress.htm";

	@Override
	public String[] getVoicedCommandList()
	{
		return new String[]
		{
			"siege"
		};
	}

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (!target.isEmpty())
		{
			final String[] targetSplit = target.split(" ");
			if (!NumberUtils.isNumber(targetSplit[0]))
			{
				showMainPage(activeChar);
				return true;
			}
			final int residenceId = Integer.parseInt(targetSplit[0]);
			final Residence residence = ResidenceHolder.getInstance().getResidence(residenceId);
			if (residence instanceof Castle)
			{
				showMainPage(activeChar);
				activeChar.sendPacket(new CastleSiegeInfo((Castle) residence, activeChar));
			}
			else if (residence instanceof Dominion)
			{
				showMainPage(activeChar);
				if (residence.getOwner() == null)
				{
					activeChar.sendMessage("You cannot register to this Territory War yet!");
					return true;
				}
				activeChar.sendPacket(new ExShowDominionRegistry(activeChar, (Dominion) residence));
			}
			else
			{
				if (residence instanceof Fortress)
				{
					if (targetSplit.length > 1)
					{
						final FortressSiegeEvent siegeEvent = residence.getSiegeEvent();
						final String s = targetSplit[1];
						switch (s)
						{
						case "register":
						{
							siegeEvent.tryToRegisterClan(activeChar, true, false, null, true);
							break;
						}
						case "unregister":
						{
							siegeEvent.tryToCancelRegisterClan(activeChar, true, false, null, true);
							break;
						}
						case "registerSingle":
						{
							siegeEvent.tryToRegisterSingle(activeChar, true, true);
							break;
						}
						case "unregisterSingle":
						{
							siegeEvent.tryToCancelRegisterSingle(activeChar, true, true);
							break;
						}
						}
					}
					showFortressPage(activeChar, (Fortress) residence);
					return true;
				}
				showMainPage(activeChar);
			}
		}
		else
		{
			showMainPage(activeChar);
		}
		return true;
	}

	private static void showMainPage(Player activeChar)
	{
		activeChar.sendPacket(new NpcHtmlMessage(0).setFile(MAIN_HTML_PATH));
	}

	private static void showFortressPage(Player activeChar, Fortress fortress)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(FORT_HTML_PATH);

		final FortressSiegeEvent siegeEvent = fortress.getSiegeEvent();
		String actionButton;
		if (siegeEvent.isInProgress())
		{
			actionButton = "<button value=\"Teleport near Fortress\" action=\"bypass -h scripts_Util:CommunityGatekeeper ";
			switch (fortress.getId())
			{
			case 106:
				actionButton += "159672 58856 -3392";
				break;
			case 109:
				actionButton += "156712 -73096 -3360";
				break;
			case 114:
				actionButton += "61048 134120 -3461";
				break;
			case 115:
				actionButton += "9208 90104 -3619";
				break;
			case 116:
				actionButton += "78248 85816 -3651";
				break;
			case 119:
				actionButton += "68824 184840 -3007";
				break;
			}

			actionButton += " 0\" width=200 height=30 back=L2UI_ct1.OlympiadWnd_DF_Apply_Down fore=L2UI_ct1.OlympiadWnd_DF_Apply>";
		}
		else if (FortressSiegeEvent.hasSignClanPrivilege(activeChar))
		{
			if (siegeEvent.tryToCancelRegisterClan(activeChar, false, false))
			{
				actionButton = "<button value=\"Cancel Registration\" action=\"bypass -h user_siege %fortressId% unregister\" width=200 height=30 back=L2UI_ct1.OlympiadWnd_DF_Back_Down fore=L2UI_ct1.OlympiadWnd_DF_Back>";
			}
			else
			{
				actionButton = "<button value=\"Register to the Siege\" action=\"bypass -h user_siege %fortressId% register\" width=200 height=30 back=L2UI_ct1.OlympiadWnd_DF_Apply_Down fore=L2UI_ct1.OlympiadWnd_DF_Apply>";
			}
		}
		else if (siegeEvent.tryToCancelRegisterSingle(activeChar, false, false))
		{
			actionButton = "<button value=\"Cancel Registration\" action=\"bypass -h user_siege %fortressId% unregisterSingle\" width=200 height=30 back=L2UI_ct1.OlympiadWnd_DF_Back_Down fore=L2UI_ct1.OlympiadWnd_DF_Back>";
		}
		else
		{
			actionButton = "<button value=\"Register to the Siege\" action=\"bypass -h user_siege %fortressId% registerSingle\" width=200 height=30 back=L2UI_ct1.OlympiadWnd_DF_Apply_Down fore=L2UI_ct1.OlympiadWnd_DF_Apply>";
		}

		html.replace("%actionButton%", actionButton);
		html.replace("%fortressId%", fortress.getId());
		html.replace("%fortressName%", fortress.getName());
		html.replace("%fortressOwner%", fortress.getOwner() != null ? fortress.getOwner().getName() : "No Owner");
		html.replace("%fortressLeader%", fortress.getOwner() != null ? fortress.getOwner().getLeaderName() : "No Owner");
		html.replace("%fortressSiegeDate%", DATE_FORMAT.format(fortress.getSiegeDate().getTime()));

		activeChar.sendPacket(html);
	}
}
