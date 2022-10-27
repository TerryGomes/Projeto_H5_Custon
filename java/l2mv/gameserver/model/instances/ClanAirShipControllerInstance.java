package l2mv.gameserver.model.instances;

import l2mv.commons.lang.reference.HardReference;
import l2mv.commons.lang.reference.HardReferences;
import l2mv.gameserver.data.xml.holder.AirshipDockHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.boat.ClanAirShip;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.templates.AirshipDock;
import l2mv.gameserver.templates.npc.NpcTemplate;

public class ClanAirShipControllerInstance extends AirShipControllerInstance
{
	protected static final int ENERGY_STAR_STONE = 13277;
	protected static final int AIRSHIP_SUMMON_LICENSE = 13559;

	private HardReference<ClanAirShip> _dockedShipRef = HardReferences.emptyRef();

	private final AirshipDock _dock;
	private final AirshipDock.AirshipPlatform _platform;

	public ClanAirShipControllerInstance(int objectID, NpcTemplate template)
	{
		super(objectID, template);
		int dockId = template.getAIParams().getInteger("dockId", 0);
		int platformId = template.getAIParams().getInteger("platformId", 0);
		_dock = AirshipDockHolder.getInstance().getDock(dockId);
		_platform = _dock.getPlatform(platformId);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		if (command.equalsIgnoreCase("summon"))
		{
			if (player.getClan() == null || player.getClan().getLevel() < 5)
			{
				player.sendPacket(SystemMsg.IN_ORDER_TO_ACQUIRE_AN_AIRSHIP_THE_CLANS_LEVEL_MUST_BE_LEVEL_5_OR_HIGHER);
				return;
			}

			if ((player.getClanPrivileges() & Clan.CP_CL_SUMMON_AIRSHIP) != Clan.CP_CL_SUMMON_AIRSHIP)
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}

			if (!player.getClan().isHaveAirshipLicense())
			{
				player.sendPacket(SystemMsg.AN_AIRSHIP_CANNOT_BE_SUMMONED_BECAUSE_EITHER_YOU_HAVE_NOT_REGISTERED_YOUR_AIRSHIP_LICENSE_OR_THE_AIRSHIP_HAS_NOT_YET_BEEN_SUMMONED);
				return;
			}

			ClanAirShip dockedAirShip = getDockedAirShip();
			ClanAirShip clanAirship = player.getClan().getAirship();

			if (clanAirship != null)
			{
				if (clanAirship == dockedAirShip)
				{
					player.sendPacket(SystemMsg.THE_CLAN_OWNED_AIRSHIP_ALREADY_EXISTS);
				}
				else
				{
					player.sendPacket(SystemMsg.YOUR_CLANS_AIRSHIP_IS_ALREADY_BEING_USED_BY_ANOTHER_CLAN_MEMBER);
				}
				return;
			}

			if (dockedAirShip != null)
			{
				Functions.npcSay(this, NpcString.IN_AIR_HARBOR_ALREADY_AIRSHIP_DOCKED_PLEASE_WAIT_AND_TRY_AGAIN, ChatType.SHOUT, 5000);
				return;
			}

			if (Functions.removeItem(player, ENERGY_STAR_STONE, 5, "Clan Airship") != 5)
			{
				player.sendPacket(new SystemMessage2(SystemMsg.AN_AIRSHIP_CANNOT_BE_SUMMONED_BECAUSE_YOU_DONT_HAVE_ENOUGH_S1).addItemName(ENERGY_STAR_STONE));
				return;
			}

			ClanAirShip dockedShip = new ClanAirShip(player.getClan());
			dockedShip.setDock(_dock);
			dockedShip.setPlatform(_platform);

			dockedShip.setHeading(0);
			dockedShip.spawnMe(_platform.getSpawnLoc());
			dockedShip.startDepartTask();

			Functions.npcSay(this, NpcString.AIRSHIP_IS_SUMMONED_IS_DEPART_IN_5_MINUTES, ChatType.SHOUT, 5000);
		}
		else if (command.equalsIgnoreCase("register"))
		{
			if (player.getClan() == null || !player.isClanLeader() || player.getClan().getLevel() < 5)
			{
				player.sendPacket(SystemMsg.IN_ORDER_TO_ACQUIRE_AN_AIRSHIP_THE_CLANS_LEVEL_MUST_BE_LEVEL_5_OR_HIGHER);
				return;
			}

			if (player.getClan().isHaveAirshipLicense())
			{
				player.sendPacket(SystemMsg.THE_AIRSHIP_SUMMON_LICENSE_HAS_ALREADY_BEEN_ACQUIRED);
				return;
			}

			if (Functions.getItemCount(player, AIRSHIP_SUMMON_LICENSE) == 0)
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
				return;
			}

			Functions.removeItem(player, AIRSHIP_SUMMON_LICENSE, 1, "Clan Airship");
			player.getClan().setAirshipLicense(true);
			player.getClan().setAirshipFuel(ClanAirShip.MAX_FUEL);
			player.getClan().updateClanInDB();
			player.sendPacket(SystemMsg.THE_AIRSHIP_SUMMON_LICENSE_HAS_BEEN_ENTERED);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	@Override
	protected ClanAirShip getDockedAirShip()
	{
		ClanAirShip ship = _dockedShipRef.get();
		if (ship != null && ship.isDocked())
		{
			return ship;
		}
		else
		{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public void setDockedShip(ClanAirShip dockedShip)
	{
		ClanAirShip old = _dockedShipRef.get();
		if (old != null)
		{
			old.setDock(null);
			old.setPlatform(null);
		}

		if (dockedShip != null)
		{
			boolean alreadyEnter = dockedShip.getDock() != null;
			dockedShip.setDock(_dock);
			dockedShip.setPlatform(_platform);
			if (!alreadyEnter)
			{
				dockedShip.startArrivalTask();
			}
		}

		if (dockedShip == null)
		{
			_dockedShipRef = HardReferences.emptyRef();
		}
		else
		{
			_dockedShipRef = (HardReference<ClanAirShip>) dockedShip.getRef();
		}
	}
}
