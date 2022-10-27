package npc.model.residences.castle;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.skills.skillclasses.Call;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.Location;

public class CourtInstance extends NpcInstance
{
	protected static final int COND_ALL_FALSE = 0;
	protected static final int COND_BUSY_BECAUSE_OF_SIEGE = 1;
	protected static final int COND_OWNER = 2;

	/**
	 * @param template
	 */
	public CourtInstance(int objectId, NpcTemplate template)
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
		if ((condition <= COND_ALL_FALSE) || (condition == COND_BUSY_BECAUSE_OF_SIEGE))
		{
			return;
		}
		else if ((player.getClanPrivileges() & Clan.CP_CS_USE_FUNCTIONS) != Clan.CP_CS_USE_FUNCTIONS)
		{
			player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		else if (condition == COND_OWNER)
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
				showChatWindow(player, val);
				return;
			}
			if (command.startsWith("gotoleader"))
			{
				if (player.getClan() != null)
				{
					Player clanLeader = player.getClan().getLeader().getPlayer();
					if (clanLeader == null)
					{
						return;
					}

					if (clanLeader.getEffectList().getEffectsBySkillId(3632) != null)
					{
						if ((Call.canSummonHere(clanLeader) != null) || (Call.canBeSummoned(clanLeader, player) != null))
						{
							return;
						}

						player.teleToLocation(Location.findAroundPosition(clanLeader, 100));
						return;
					}
					showChatWindow(player, "castle/CourtMagician/CourtMagician-nogate.htm");
				}
			}
			else
			{
				super.onBypassFeedback(player, command);
			}
		}
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		player.sendActionFailed();
		String filename = "castle/CourtMagician/CourtMagician-no.htm";

		int condition = validateCondition(player);
		if (condition > COND_ALL_FALSE)
		{
			if (condition == COND_BUSY_BECAUSE_OF_SIEGE)
			{
				filename = "castle/CourtMagician/CourtMagician-busy.htm"; // Busy because of siege
			}
			else if (condition == COND_OWNER)
			{
				if (val == 0)
				{
					filename = "castle/CourtMagician/CourtMagician.htm";
				}
				else
				{
					filename = "castle/CourtMagician/CourtMagician-" + val + ".htm";
				}
			}
		}

		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile(filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%npcname%", getName());
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
				else if (getCastle().getOwnerId() == player.getClanId()) // Clan owns castle
				{
					return COND_OWNER;
				}
			}
		}
		return COND_ALL_FALSE;
	}
}