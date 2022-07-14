package npc.model.residences.fortress.peace;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2mv.gameserver.model.entity.events.impl.SiegeEvent;
import l2mv.gameserver.model.entity.residence.Fortress;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.templates.npc.NpcTemplate;

public class SuspiciousMerchantInstance extends NpcInstance
{
	public SuspiciousMerchantInstance(int objectID, NpcTemplate template)
	{
		super(objectID, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}
		final Fortress fortress = getFortress();
		final FortressSiegeEvent siegeEvent = (FortressSiegeEvent) fortress.getSiegeEvent();
		if (command.equalsIgnoreCase("register"))
		{
			siegeEvent.tryToRegisterClan(player, true, true, this, true);
		}
		else if (command.equalsIgnoreCase("cancel"))
		{
			siegeEvent.tryToCancelRegisterClan(player, true, true, this, true);
		}
		else if (command.equalsIgnoreCase("state"))
		{
			int attackersSize = siegeEvent.getObjects(SiegeEvent.ATTACKERS).size();
			if (attackersSize == 0)
			{
				showChatWindow(player, "residence2/fortress/fortress_ordery019.htm");
			}
			else
			{
				showChatWindow(player, "residence2/fortress/fortress_ordery020.htm");
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		Fortress fortress = getFortress();
		if (fortress.getOwner() != null)
		{
			html.setFile("residence2/fortress/fortress_ordery001a.htm");
			html.replace("%clan_name%", fortress.getOwner().getName());
		}
		else
		{
			html.setFile("residence2/fortress/fortress_ordery001.htm");
		}

		player.sendPacket(html);
	}
}