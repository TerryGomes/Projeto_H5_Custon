package npc.model.residences.castle;

import l2f.gameserver.ai.CtrlIntention;
import l2f.gameserver.instancemanager.CastleManorManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.network.serverpackets.MyTargetSelected;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.network.serverpackets.ValidateLocation;
import l2f.gameserver.templates.npc.NpcTemplate;

public class BlacksmithInstance extends NpcInstance
{
	protected static final int COND_ALL_FALSE = 0;
	protected static final int COND_BUSY_BECAUSE_OF_SIEGE = 1;
	protected static final int COND_OWNER = 2;

	public BlacksmithInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onAction(Player player, boolean shift)
	{
		if (this != player.getTarget())
		{
			player.setTarget(this);
			player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()));
			player.sendPacket(new ValidateLocation(this));
		}
		else
		{
			player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()));
			if (!isInRange(player, INTERACTION_DISTANCE))
			{
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
				player.sendActionFailed();
			}
			else
			{
				if (CastleManorManager.getInstance().isDisabled())
				{
					NpcHtmlMessage html = new NpcHtmlMessage(player, this);
					html.setFile("npcdefault.htm");
					player.sendPacket(html);
				}
				else
				{
					showMessageWindow(player, 0);
				}
				player.sendActionFailed();
			}
		}
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		if (CastleManorManager.getInstance().isDisabled())
		{
			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile("npcdefault.htm");
			player.sendPacket(html);
			return;
		}

		int condition = validateCondition(player);
		if ((condition <= COND_ALL_FALSE) || (condition == COND_BUSY_BECAUSE_OF_SIEGE))
		{
			return;
		}

		if (condition == COND_OWNER)
		{
			if (command.startsWith("Chat"))
			{
				int val = 0;
				try
				{
					val = Integer.parseInt(command.substring(5));
				}
				catch (IndexOutOfBoundsException ioobe)
				{
				}
				catch (NumberFormatException nfe)
				{
				}
				showMessageWindow(player, val);
			}
			else
			{
				super.onBypassFeedback(player, command);
			}
		}
	}

	private void showMessageWindow(Player player, int val)
	{
		player.sendActionFailed();
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);

		if (CastleManorManager.getInstance().isDisabled())
		{
			html.setFile("npcdefault.htm");
			player.sendPacket(html);
			return;
		}
		String filename = "castle/blacksmith/castleblacksmith-no.htm";

		int condition = validateCondition(player);
		if (condition > COND_ALL_FALSE)
		{
			if (condition == COND_BUSY_BECAUSE_OF_SIEGE)
			{
				filename = "castle/blacksmith/castleblacksmith-busy.htm"; // Busy because of siege
			}
			else if (condition == COND_OWNER)
			{
				if (val == 0)
				{
					filename = "castle/blacksmith/castleblacksmith.htm";
				}
				else
				{
					filename = "castle/blacksmith/castleblacksmith-" + val + ".htm";
				}
			}
		}

		html.setFile(filename);
		html.replace("%castleid%", Integer.toString(getCastle().getId()));
		player.sendPacket(html);
	}

	protected int validateCondition(Player player)
	{
		if (player.isGM())
		{
			return COND_OWNER;
		}
		if (getCastle() != null && getCastle().getId() > 0)
		{
			if (player.getClan() != null)
			{
				if (getCastle().getSiegeEvent().isInProgress())
				{
					return COND_BUSY_BECAUSE_OF_SIEGE; // Busy because of siege
				}
				else if (getCastle().getOwnerId() == player.getClanId() // Clan owns castle
							&& (player.getClanPrivileges() & Clan.CP_CS_MANOR_ADMIN) == Clan.CP_CS_MANOR_ADMIN)
				{
					return COND_OWNER; // Owner
				}
			}
		}
		return COND_ALL_FALSE;
	}
}