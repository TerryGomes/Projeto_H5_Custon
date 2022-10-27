package npc.model.residences.fortress.peace;

import java.util.List;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2mv.gameserver.model.entity.events.impl.SiegeEvent;
import l2mv.gameserver.model.entity.events.objects.DoorObject;
import l2mv.gameserver.model.entity.residence.Fortress;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.templates.npc.NpcTemplate;
import npc.model.residences.fortress.FacilityManagerInstance;

/**
 * @author VISTALL
 * @date 16:29/17.04.2011
 */
public class GuardCaptionInstance extends FacilityManagerInstance
{
	public GuardCaptionInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		Fortress fortress = getFortress();
		if (command.equalsIgnoreCase("defenceInfo"))
		{
			if ((player.getClanPrivileges() & Clan.CP_CS_MANAGE_SIEGE) != Clan.CP_CS_MANAGE_SIEGE)
			{
				showChatWindow(player, "residence2/fortress/fortress_not_authorized.htm");
				return;
			}

			if (fortress.getContractState() != Fortress.CONTRACT_WITH_CASTLE)
			{
				showChatWindow(player, "residence2/fortress/fortress_supply_officer005.htm");
				return;
			}

			showChatWindow(player, "residence2/fortress/fortress_garrison002.htm", "%facility_0%", fortress.getFacilityLevel(Fortress.REINFORCE), "%facility_2%", fortress.getFacilityLevel(Fortress.DOOR_UPGRADE), "%facility_3%", fortress.getFacilityLevel(Fortress.DWARVENS), "%facility_4%", fortress.getFacilityLevel(Fortress.SCOUT));
		}
		else if (command.equalsIgnoreCase("defenceUp1") || command.equalsIgnoreCase("defenceUp2"))
		{
			buyFacility(player, Fortress.REINFORCE, Integer.parseInt(command.substring(9, 10)), 100000);
		}
		else if (command.equalsIgnoreCase("deployScouts"))
		{
			buyFacility(player, Fortress.SCOUT, 1, 150000);
		}
		else if (command.equalsIgnoreCase("doorUpgrade"))
		{
			boolean buy = buyFacility(player, Fortress.DOOR_UPGRADE, 1, 200000);
			if (buy)
			{
				List<DoorObject> doorObjects = fortress.getSiegeEvent().getObjects(FortressSiegeEvent.UPGRADEABLE_DOORS);
				for (DoorObject d : doorObjects)
				{
					d.setUpgradeValue(fortress.<SiegeEvent>getSiegeEvent(), d.getDoor().getMaxHp() * fortress.getFacilityLevel(Fortress.DOOR_UPGRADE));
				}
			}
		}
		else if (command.equalsIgnoreCase("hireDwarves"))
		{
			buyFacility(player, Fortress.DWARVENS, 1, 100000);
		}
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		showChatWindow(player, "residence2/fortress/fortress_garrison001.htm");
	}
}
