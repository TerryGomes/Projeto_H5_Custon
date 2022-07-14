package npc.model.residences.fortress.peace;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.templates.npc.NpcTemplate;

public class SupportUnitCaptionInstance extends NpcInstance
{
	protected static final int COND_ALL_FALSE = 0;
	protected static final int COND_BUSY_BECAUSE_OF_SIEGE = 1;
	protected static final int COND_OWNER = 2;

	public SupportUnitCaptionInstance(int objectId, NpcTemplate template)
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

		int condition = validateCondition(player);
		if (condition <= COND_ALL_FALSE || condition == COND_BUSY_BECAUSE_OF_SIEGE)
		{
			return;
		}

		if ((player.getClanPrivileges() & Clan.CP_CS_USE_FUNCTIONS) != Clan.CP_CS_USE_FUNCTIONS)
		{
			player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}

		if (condition == COND_OWNER)
		{
			super.onBypassFeedback(player, command);
		}
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		player.sendActionFailed();
		String filename = "fortress/SupportUnitCaptain-no.htm";

		int condition = validateCondition(player);
		if (condition > COND_ALL_FALSE)
		{
			if (condition == COND_BUSY_BECAUSE_OF_SIEGE)
			{
				filename = "fortress/SupportUnitCaptain-busy.htm"; // Busy because of siege
			}
			else if (condition == COND_OWNER)
			{
				if (val == 0)
				{
					filename = "fortress/SupportUnitCaptain.htm";
				}
				else
				{
					filename = "fortress/SupportUnitCaptain-" + val + ".htm";
				}
			}
		}

		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile(filename);
		player.sendPacket(html);
	}

	protected int validateCondition(Player player)
	{
		if (player.isGM())
		{
			return COND_OWNER;
		}
		if (getFortress() != null && getFortress().getId() > 0)
		{
			if (player.getClan() != null)
			{
				if (getFortress().getSiegeEvent().isInProgress())
				{
					return COND_BUSY_BECAUSE_OF_SIEGE; // Busy because of siege
				}
				else if (getFortress().getOwnerId() == player.getClanId()) // Clan owns fortress
				{
					return COND_OWNER;
				}
			}
		}
		return COND_ALL_FALSE;
	}
}