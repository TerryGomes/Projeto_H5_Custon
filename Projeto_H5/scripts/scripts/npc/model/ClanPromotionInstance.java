package npc.model;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.network.serverpackets.InventoryUpdate;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import l2mv.gameserver.network.serverpackets.PledgeStatusChanged;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.HtmlUtils;
import l2mv.gameserver.utils.ItemFunctions;

public final class ClanPromotionInstance extends NpcInstance
{
	public ClanPromotionInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		NpcHtmlMessage msg = new NpcHtmlMessage(player, this);
		msg.setFile("custom/400.htm");
		msg.setFile("custom/400.htm");
		msg.replace("%itemName%", HtmlUtils.htmlItemName(Config.SERVICES_CLAN_PROMOTION_ITEM));
		msg.replace("%itemCount%", "" + Config.SERVICES_CLAN_PROMOTION_ITEM_COUNT);
		msg.replace("%minClanLevel%", "" + Config.SERVICES_CLAN_PROMOTION_MAX_LEVEL);
		msg.replace("%minOnlineMembers%", "" + Config.SERVICES_CLAN_PROMOTION_MIN_ONLINE);
		msg.replace("%clanLevel%", "" + Config.SERVICES_CLAN_PROMOTION_SET_LEVEL);
		msg.replace("%clanReputation%", "" + Config.SERVICES_CLAN_PROMOTION_ADD_REP);
		msg.replace("%clanEggs%", Config.SERVICE_CLAN_PRMOTION_ADD_EGGS ? "Earn Random Amount of Clan Eggs." : "&nbsp;");
		player.sendPacket(msg);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		NpcHtmlMessage msg = new NpcHtmlMessage(player, this);
		msg.setFile("custom/400.htm");
		msg.replace("%itemName%", HtmlUtils.htmlItemName(Config.SERVICES_CLAN_PROMOTION_ITEM));
		msg.replace("%itemCount%", "" + Config.SERVICES_CLAN_PROMOTION_ITEM_COUNT);
		msg.replace("%minClanLevel%", "" + Config.SERVICES_CLAN_PROMOTION_MAX_LEVEL);
		msg.replace("%minOnlineMembers%", "" + Config.SERVICES_CLAN_PROMOTION_MIN_ONLINE);
		msg.replace("%clanLevel%", "" + Config.SERVICES_CLAN_PROMOTION_SET_LEVEL);
		msg.replace("%clanReputation%", "" + Config.SERVICES_CLAN_PROMOTION_ADD_REP);
		msg.replace("%clanEggs%", Config.SERVICE_CLAN_PRMOTION_ADD_EGGS ? "Earn Random Amount of Clan Eggs." : "&nbsp;");

		NpcHtmlMessage msg1 = new NpcHtmlMessage(player, this);
		msg.setFile("custom/400-done.htm");

		if (!canBypassCheck(player, this))
		{
			return;
		}

		if (command.startsWith("increaseLevel"))
		{
			if (!Config.SERVICES_CLAN_PROMOTION_ENABLE)
			{
				player.sendMessage(new CustomMessage("scripts.npc.model.clanpromotioninstance.service_disabled", player));
				return;
			}
			if (player.getClan() == null || player.getObjectId() != player.getClan().getLeaderId())
			{
				player.sendMessage(new CustomMessage("scripts.npc.model.clanpromotioninstance.clan_leader_only", player));
				return;
			}
			if (player.getClan().getLevel() >= Config.SERVICES_CLAN_PROMOTION_MAX_LEVEL)
			{
				player.sendMessage(new CustomMessage("scripts.npc.model.clanpromotioninstance.min_clan_level", player, Config.SERVICES_CLAN_PROMOTION_MAX_LEVEL));
				return;
			}
			if (player.getClan().getOnlineMembers(0).size() < Config.SERVICES_CLAN_PROMOTION_MIN_ONLINE)
			{
				player.sendMessage(new CustomMessage("scripts.npc.model.clanpromotioninstance.min_clan_members", player, Config.SERVICES_CLAN_PROMOTION_MIN_ONLINE));
				return;
			}
			else if (player.getInventory().getCountOf(Config.SERVICES_CLAN_PROMOTION_ITEM) < Config.SERVICES_CLAN_PROMOTION_ITEM_COUNT)
			{
				player.sendMessage(new CustomMessage("scripts.npc.model.clanpromotioninstance.not_enough_items", player));
				return;
			}
			else
			{
				int clanSetLevel = Config.SERVICES_CLAN_PROMOTION_SET_LEVEL;
				int clanAddRept = Config.SERVICES_CLAN_PROMOTION_ADD_REP;

				try
				{
					Clan clan = player.getClan();

					clan.setLevel(clanSetLevel);
					clan.updateClanInDB();

					if (player.getClan().getLevel() == 5)
					{
						player.sendPacket(SystemMsg.NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS);
					}

					if (player.getClan().getLevel() >= 5 && clanAddRept > 0)
					{
						player.getClan().incReputation(clanAddRept, false, "clan_promotion");
					}

					PledgeShowInfoUpdate pu = new PledgeShowInfoUpdate(clan);
					PledgeStatusChanged ps = new PledgeStatusChanged(clan);

					for (Player member : clan.getOnlineMembers(0))
					{
						member.updatePledgeClass();
						member.sendPacket(SystemMsg.YOUR_CLANS_LEVEL_HAS_INCREASED, pu, ps);
						member.broadcastUserInfo(true);
					}
					player.sendPacket(msg1);

					for (String rewards : Config.CLAN_PROMOTION_CLAN_EGGS)
					{
						if (!Config.SERVICE_CLAN_PRMOTION_ADD_EGGS || rewards.isEmpty())
						{
							return;
						}

						String[] reward = rewards.split(",");

						int id = Integer.parseInt(reward[0]);
						int count = Integer.parseInt(reward[1]);

						ItemInstance item = ItemFunctions.createItem(id);
						item.setCount(count);
						item.setCustomFlags(ItemInstance.FLAG_NO_DROP | ItemInstance.FLAG_NO_TRADE | ItemInstance.FLAG_NO_TRANSFER);
						player.getInventory().addItem(item, "rewarded clan item");
						player.sendPacket(new InventoryUpdate().addModifiedItem(item));
						player.broadcastUserInfo(true);
						player.sendPacket(SystemMessage2.obtainItems(item));

					}
				}
				catch (Exception e)
				{
				}
			}

			player.sendPacket(msg);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}