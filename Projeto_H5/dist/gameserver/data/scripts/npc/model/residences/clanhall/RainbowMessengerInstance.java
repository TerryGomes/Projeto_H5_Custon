package npc.model.residences.clanhall;

import l2mv.gameserver.dao.SiegeClanDAO;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.events.impl.ClanHallMiniGameEvent;
import l2mv.gameserver.model.entity.events.objects.CMGSiegeClanObject;
import l2mv.gameserver.model.entity.events.objects.SiegeClanObject;
import l2mv.gameserver.model.entity.residence.ClanHall;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.TimeUtils;

/**
 * @author VISTALL
 * @date 8:01/07.05.2011
 */
public class RainbowMessengerInstance extends NpcInstance
{
	public static final int ITEM_ID = 8034;

	public RainbowMessengerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(final Player player, final String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		ClanHall clanHall = getClanHall();
		ClanHallMiniGameEvent miniGameEvent = clanHall.getSiegeEvent();
		if (command.equalsIgnoreCase("register"))
		{
			if (miniGameEvent.isRegistrationOver())
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti014.htm");
				return;
			}

			Clan clan = player.getClan();
			if (clan == null || clan.getLevel() < 3 || clan.getAllSize() <= 5)
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti011.htm");
				return;
			}
			if (clan.isPlacedForDisband())
			{
				player.sendPacket(SystemMsg.YOU_HAVE_ALREADY_REQUESTED_THE_DISSOLUTION_OF_YOUR_CLAN);
				return;
			}
			if (clan.getLeaderId() != player.getObjectId())
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti010.htm");
				return;
			}
			if (clan.getHasHideout() > 0)
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti012.htm");
				return;
			}

			if (miniGameEvent.getSiegeClan(ClanHallMiniGameEvent.ATTACKERS, clan) != null)
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti013.htm");
				return;
			}

			long count = player.getInventory().getCountOf(ITEM_ID);
			if (count == 0)
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti008.htm");
			}
			else
			{
				if (!player.consumeItem(ITEM_ID, count))
				{
					return;
				}

				CMGSiegeClanObject siegeClanObject = new CMGSiegeClanObject(ClanHallMiniGameEvent.ATTACKERS, clan, count);
				miniGameEvent.addObject(ClanHallMiniGameEvent.ATTACKERS, siegeClanObject);
				SiegeClanDAO.getInstance().insert(clanHall, siegeClanObject);

				showChatWindow(player, "residence2/clanhall/messenger_yetti009.htm");
			}
		}
		else if (command.equalsIgnoreCase("cancel"))
		{
			if (miniGameEvent.isRegistrationOver())
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti017.htm");
				return;
			}

			Clan clan = player.getClan();
			if (clan == null || clan.getLevel() < 3)
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti011.htm");
				return;
			}
			if (clan.getLeaderId() != player.getObjectId())
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti010.htm");
				return;
			}

			SiegeClanObject siegeClanObject = miniGameEvent.getSiegeClan(ClanHallMiniGameEvent.ATTACKERS, clan);
			if (siegeClanObject == null)
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti016.htm");
			}
			else
			{
				miniGameEvent.removeObject(ClanHallMiniGameEvent.ATTACKERS, siegeClanObject);
				SiegeClanDAO.getInstance().delete(clanHall, siegeClanObject);

				ItemFunctions.addItem(player, ITEM_ID, siegeClanObject.getParam() / 2L, true, "RainbowMessenger");

				showChatWindow(player, "residence2/clanhall/messenger_yetti005.htm");
			}
		}
		else if (command.equalsIgnoreCase("refund"))
		{
			if (miniGameEvent.isRegistrationOver())
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti010.htm");
				return;
			}

			Clan clan = player.getClan();
			if (clan == null || clan.getLevel() < 3)
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti011.htm");
				return;
			}
			if (clan.getLeaderId() != player.getObjectId())
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti010.htm");
				return;
			}

			SiegeClanObject siegeClanObject = miniGameEvent.getSiegeClan(ClanHallMiniGameEvent.REFUND, clan);
			if (siegeClanObject == null)
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti020.htm");
			}
			else
			{
				miniGameEvent.removeObject(ClanHallMiniGameEvent.REFUND, siegeClanObject);
				SiegeClanDAO.getInstance().delete(clanHall, siegeClanObject);

				ItemFunctions.addItem(player, ITEM_ID, siegeClanObject.getParam(), true, "RainbowMessenger");

				showChatWindow(player, "residence2/clanhall/messenger_yetti019.htm");
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
		ClanHall clanHall = getClanHall();
		Clan clan = clanHall.getOwner();
		NpcHtmlMessage msg = new NpcHtmlMessage(player, this);
		if (clan != null)
		{
			msg.setFile("residence2/clanhall/messenger_yetti001.htm");
			msg.replace("%owner_name%", clan.getName());
		}
		else
		{
			msg.setFile("residence2/clanhall/messenger_yetti001a.htm");
		}
		msg.replace("%siege_date%", TimeUtils.toSimpleFormat(clanHall.getSiegeDate()));

		player.sendPacket(msg);
	}
}
