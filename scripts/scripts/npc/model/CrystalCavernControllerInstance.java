package npc.model;

import bosses.BaylorManager;
import instances.CrystalCaverns;
import l2mv.gameserver.instancemanager.ReflectionManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.Location;

/**
 * @author pchayka
 */
public class CrystalCavernControllerInstance extends NpcInstance
{
	private static final long serialVersionUID = -1L;

	public CrystalCavernControllerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String htmlpath = null;
		if ((val == 0) && (player.isInParty() && player.getParty().getLeader() == player))
		{
			if (getNpcId() == 32280)
			{
				htmlpath = "default/32280-2.htm";
			}
			else if (getNpcId() == 32278)
			{
				htmlpath = "default/32278.htm";
			}
			else if (getNpcId() == 32276)
			{
				htmlpath = "default/32276.htm";
			}
			else if (getNpcId() == 32279)
			{
				htmlpath = "default/32279.htm";
			}
			else if (getNpcId() == 32277)
			{
				htmlpath = "default/32277.htm";
			}
		}
		else
		{
			htmlpath = "default/32280-1.htm";
		}
		return htmlpath;
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		if (command.equalsIgnoreCase("request_emerald"))
		{
			((CrystalCaverns) getReflection()).notifyEmeraldRequest();
		}
		else if (command.equalsIgnoreCase("request_coral"))
		{
			((CrystalCaverns) getReflection()).notifyCoralRequest();
		}
		else if (command.equalsIgnoreCase("request_baylor"))
		{
			int state = BaylorManager.canIntoBaylorLair(player);
			switch (state)
			{
			case 1:
			case 2:
				showChatWindow(player, "default/32276-1.htm");
				return;
			case 4:
				showChatWindow(player, "default/32276-2.htm");
				return;
			case 3:
				showChatWindow(player, "default/32276-3.htm");
				return;
			default:
				break;
			}
			if (player.isInParty())
			{
				for (Player p : player.getParty().getMembers())
				{
					if (ItemFunctions.getItemCount(p, 9695) < 1)
					{
						Functions.npcSay(this, NpcString.S1___________________, p.getName());
						return;
					}
					if (ItemFunctions.getItemCount(p, 9696) < 1)
					{
						Functions.npcSay(this, NpcString.S1__________________, p.getName());
						return;
					}
					if (ItemFunctions.getItemCount(p, 9697) < 1)
					{
						Functions.npcSay(this, NpcString.S1____________________, p.getName());
						return;
					}
					if (!isInRange(p, 400))
					{
						Functions.npcSay(this, NpcString.S1_____________________, p.getName());
						return;
					}
				}
				ItemFunctions.addItem(player, 10015, 1, true, "CrystalCavernControllerInstance");
				for (Player p : player.getParty().getMembers())
				{
					ItemFunctions.removeItem(p, 9695, 1, true, "CrystalCavernControllerInstance");
					ItemFunctions.removeItem(p, 9696, 1, true, "CrystalCavernControllerInstance");
					ItemFunctions.removeItem(p, 9697, 1, true, "CrystalCavernControllerInstance");
					p.teleToLocation(new Location(153526, 142172, -12736));
				}
				BaylorManager.entryToBaylorLair(player);
				deleteMe();
			}
		}
		else if (command.equalsIgnoreCase("request_parme"))
		{
			player.teleToLocation(new Location(153736, 142008, -9744));
		}
		else if (command.equalsIgnoreCase("request_exit"))
		{
			if (getReflection().getInstancedZoneId() == 10)
			{
				player.teleToLocation(getReflection().getInstancedZone().getReturnCoords(), ReflectionManager.DEFAULT);
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}
