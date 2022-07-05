package l2f.gameserver.model.instances;

import l2f.gameserver.Announcements;
import l2f.gameserver.Config;
import l2f.gameserver.cache.Msg;
import l2f.gameserver.instancemanager.CoupleManager;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.Couple;
import l2f.gameserver.model.items.Inventory;
import l2f.gameserver.network.serverpackets.MagicSkillUse;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.templates.item.ItemTemplate;
import l2f.gameserver.templates.npc.NpcTemplate;

public class WeddingManagerInstance extends NpcInstance
{
	private int WEDDING_RING_MALE = 21159;
	private int WEDDING_RING_FEMALE = 21160;
	private int SALVATION_BOW = 9140;

	public WeddingManagerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		String filename = "wedding/start.htm";
		String replace = "";
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile(filename);
		html.replace("%replace%", replace);
		html.replace("%npcname%", getName());
		player.sendPacket(html);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		// standard msg
		String filename = "wedding/start.htm";
		String replace = "";

		// if player has no partner
		if (player.getPartnerId() == 0)
		{
			filename = "wedding/nopartner.htm";
			sendHtmlMessage(player, filename, replace);
			return;
		}

		Player ptarget = GameObjectsStorage.getPlayer(player.getPartnerId());

		// partner online ?
		if (ptarget == null || !ptarget.isOnline())
		{
			filename = "wedding/notfound.htm";
			sendHtmlMessage(player, filename, replace);
			return;
		}
		else if (player.isMaried()) // already married ?
		{
			filename = "wedding/already.htm";
			sendHtmlMessage(player, filename, replace);
			return;
		}
		else if (command.startsWith("AcceptWedding"))
		{
			// accept the wedding request
			player.setMaryAccepted(true);
			Couple couple = CoupleManager.getInstance().getCouple(player.getCoupleId());
			couple.marry();

			// messages to the couple
			player.sendMessage(new CustomMessage("l2f.gameserver.model.instances.L2WeddingManagerMessage", player));
			player.setMaried(true);
			player.setMaryRequest(false);
			ptarget.sendMessage(new CustomMessage("l2f.gameserver.model.instances.L2WeddingManagerMessage", ptarget));
			ptarget.setMaried(true);
			ptarget.setMaryRequest(false);

			// wedding march
			player.broadcastPacket(new MagicSkillUse(player, player, 2230, 1, 1, 0));
			ptarget.broadcastPacket(new MagicSkillUse(ptarget, ptarget, 2230, 1, 1, 0));

			// fireworks
			player.broadcastPacket(new MagicSkillUse(player, player, 2025, 1, 1, 0));
			ptarget.broadcastPacket(new MagicSkillUse(ptarget, ptarget, 2025, 1, 1, 0));

			Announcements.getInstance().announceByCustomMessage("l2f.gameserver.model.instances.L2WeddingManagerMessage.announce", new String[]
			{
				player.getName(),
				ptarget.getName()
			});

			// wedding rings
			if (player.getSex() == 0)
			{
				Functions.addItem(player, WEDDING_RING_MALE, 1, "Added Ring");
			}
			else
			{
				Functions.addItem(player, WEDDING_RING_FEMALE, 1, "Added Ring");
			}
			if (ptarget.getSex() == 0)
			{
				Functions.addItem(ptarget, WEDDING_RING_MALE, 1, "Added Ring");
			}
			else
			{
				Functions.addItem(ptarget, WEDDING_RING_FEMALE, 1, "Added Ring");
			}
			// wedding bow
			Functions.addItem(player, SALVATION_BOW, 1, "Added Ring");
			Functions.addItem(ptarget, SALVATION_BOW, 1, "Added Ring");

			player.getCounters().timesMarried++;
			ptarget.getCounters().timesMarried++;

			filename = "wedding/accepted.htm";
			replace = ptarget.getName();
			sendHtmlMessage(ptarget, filename, replace);
			return;
		}
		else if (player.isMaryRequest())
		{
			// check for formalwear
			if (Config.WEDDING_FORMALWEAR && !isWearingFormalWear(player))
			{
				filename = "wedding/noformal.htm";
				sendHtmlMessage(player, filename, replace);
				return;
			}
			filename = "wedding/ask.htm";
			player.setMaryRequest(false);
			ptarget.setMaryRequest(false);
			replace = ptarget.getName();
			sendHtmlMessage(player, filename, replace);
			return;
		}
		else if (command.startsWith("AskWedding"))
		{
			// check for formalwear
			if (Config.WEDDING_FORMALWEAR && !isWearingFormalWear(player))
			{
				filename = "wedding/noformal.htm";
				sendHtmlMessage(player, filename, replace);
				return;
			}
			else if (player.getAdena() < Config.WEDDING_PRICE)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
			else
			{
				player.setMaryAccepted(true);
				ptarget.setMaryRequest(true);
				replace = ptarget.getName();
				filename = "wedding/requested.htm";
				player.reduceAdena(Config.WEDDING_PRICE, true, "AskWedding");
				sendHtmlMessage(player, filename, replace);
				return;
			}
		}
		else if (command.startsWith("DeclineWedding"))
		{
			player.setMaryRequest(false);
			ptarget.setMaryRequest(false);
			player.setMaryAccepted(false);
			ptarget.setMaryAccepted(false);
			player.sendMessage("You declined");
			ptarget.sendMessage("Your partner declined");
			replace = ptarget.getName();
			filename = "wedding/declined.htm";
			sendHtmlMessage(ptarget, filename, replace);
			return;
		}
		else if (player.isMaryAccepted())
		{
			filename = "wedding/waitforpartner.htm";
			sendHtmlMessage(player, filename, replace);
			return;
		}
		sendHtmlMessage(player, filename, replace);
	}

	private static boolean isWearingFormalWear(Player player)
	{
		if (player != null && player.getInventory() != null && player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_CHEST) == ItemTemplate.ITEM_ID_FORMAL_WEAR)
		{
			return true;
		}
		return false;
	}

	private void sendHtmlMessage(Player player, String filename, String replace)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile(filename);
		html.replace("%replace%", replace);
		html.replace("%npcname%", getName());
		player.sendPacket(html);
	}
}