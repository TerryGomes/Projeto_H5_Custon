package npc.model.residences.fortress;

import l2f.gameserver.data.xml.holder.EventHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.events.EventType;
import l2f.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2f.gameserver.model.entity.residence.Fortress;
import l2f.gameserver.model.entity.residence.Residence;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.network.serverpackets.L2GameServerPacket;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.network.serverpackets.components.NpcString;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.HtmlUtils;
import npc.model.residences.ResidenceManager;

public class ManagerInstance extends ResidenceManager
{
	private static final long REWARD_CYCLE = 6 * 60 * 60; // каждых 6 часов

	public ManagerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	protected void setDialogs()
	{
		_mainDialog = "residence2/fortress/fortress_steward001.htm";
		_failDialog = "residence2/fortress/fortress_steward002.htm";
		_siegeDialog = "residence2/fortress/fortress_steward018.htm";
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		if (command.equalsIgnoreCase("receive_report"))
		{
			int ownedTime = (int) ((System.currentTimeMillis() - getFortress().getOwnDate().getTimeInMillis()) / 60000L);

			NpcHtmlMessage html = new NpcHtmlMessage(player, this);

			Fortress fortress = getFortress();
			if (fortress.getContractState() == Fortress.CONTRACT_WITH_CASTLE)
			{
				html.setFile("residence2/fortress/fortress_steward022.htm");
				html.replace("%castle_name%", HtmlUtils.htmlResidenceName(getFortress().getCastleId()));
				html.replaceNpcString("%contract%", NpcString.CONTRACT_STATE);

				long leftTime = (REWARD_CYCLE - (3600 - fortress.getCycleDelay()) - fortress.getPaidCycle() * 3600) / 60;

				html.replace("%rent_cost%", String.valueOf(Fortress.CASTLE_FEE));
				html.replace("%next_hour%", String.valueOf(leftTime / 60));
				html.replace("%next_min%", String.valueOf(leftTime % 60));
			}
			else
			{
				html.setFile("residence2/fortress/fortress_steward023.htm");
			}

			html.replaceNpcString("%time_remained%", NpcString.S1HOUR_S2MINUTE, ownedTime / 60, ownedTime % 60);

			player.sendPacket(html);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	@Override
	protected int getCond(Player player)
	{
		DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);

		Residence residence = getResidence();
		Clan residenceOwner = residence.getOwner();
		if (residenceOwner != null && player.getClan() == residenceOwner)
		{
			if (residence.getSiegeEvent().isInProgress() || runnerEvent.isInProgress())
			{
				return COND_SIEGE;
			}
			else
			{
				return COND_OWNER;
			}
		}
		else
		{
			return COND_FAIL;
		}
	}

	@Override
	protected Residence getResidence()
	{
		return getFortress();
	}

	@Override
	public L2GameServerPacket decoPacket()
	{
		return null;
	}

	@Override
	protected int getPrivUseFunctions()
	{
		return Clan.CP_CS_USE_FUNCTIONS;
	}

	@Override
	protected int getPrivSetFunctions()
	{
		return Clan.CP_CS_SET_FUNCTIONS;
	}

	@Override
	protected int getPrivDismiss()
	{
		return Clan.CP_CS_DISMISS;
	}

	@Override
	protected int getPrivDoors()
	{
		return Clan.CP_CS_ENTRY_EXIT;
	}
}