package npc.model;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.Experience;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.HtmlUtils;

public final class DelevelManagerInstance extends NpcInstance
{
	public DelevelManagerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		NpcHtmlMessage msg = new NpcHtmlMessage(player, this);
		msg.setFile("custom/201.htm");
		msg.replace("%itemName%", HtmlUtils.htmlItemName(Config.SERVICES_DELEVEL_ITEM));
		msg.replace("%itemCount%", "" + Config.SERVICES_DELEVEL_COUNT);
		player.sendPacket(msg);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		NpcHtmlMessage msg = new NpcHtmlMessage(player, this);
		msg.setFile("custom/201.htm");
		msg.replace("%itemName%", HtmlUtils.htmlItemName(Config.SERVICES_DELEVEL_ITEM));
		msg.replace("%itemCount%", "" + Config.SERVICES_DELEVEL_COUNT);

		if (!canBypassCheck(player, this))
		{
			return;
		}

		if (command.startsWith("delevel"))
		{
			if (!Config.SERVICES_DELEVEL_ENABLED)
			{
				player.sendMessage("This service is not available.");
				return;
			}
			if (player.getLevel() <= Config.SERVICES_DELEVEL_MIN_LEVEL)
			{
				player.sendMessage(new CustomMessage("scripts.npc.model.delevelmanagerinstance.condition_level", player, Config.SERVICES_DELEVEL_MIN_LEVEL));
				return;
			}
			else if (player.getInventory().getCountOf(Config.SERVICES_DELEVEL_ITEM) < Config.SERVICES_DELEVEL_COUNT)
			{
				player.sendMessage("You don't have enugh items.");
				return;
			}
			else
			{
				long pXp = player.getExp();
				long tXp = Experience.LEVEL[(player.getLevel() - 1)];
				if (pXp <= tXp)
				{
					return;
				}
				player.getInventory().destroyItemByItemId(Config.SERVICES_DELEVEL_ITEM, Config.SERVICES_DELEVEL_COUNT, "DelevelM");
				player.sendPacket(SystemMessage2.removeItems(Config.SERVICES_DELEVEL_ITEM, Config.SERVICES_DELEVEL_COUNT));
				player.addExpAndSp(-(pXp - tXp), 0, 0, 0, false, false);
			}

			player.sendPacket(msg);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}