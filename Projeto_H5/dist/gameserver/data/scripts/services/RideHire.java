package services;

import l2f.gameserver.Config;
import l2f.gameserver.cache.Msg;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.SetupGauge;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.tables.PetDataTable;

public class RideHire extends Functions
{
	public String DialogAppend_30827(Integer val)
	{

		if (!Config.SERVICES_RIDE_HIRE_ENABLED)
		{
			return "";
		}

		if (val == 0)
		{
			Player player = getSelf();
			return "<br>[scripts_services.RideHire:ride_prices|Ride hire mountable pet.]";
		}
		return "";
	}

	public void ride_prices()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null)
		{
			return;
		}

		show("scripts/services/ride-prices.htm", player, npc);
	}

	public void ride(String[] args)
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null)
		{
			return;
		}

		if (args.length != 3)
		{
			show("Incorrect input", player, npc);
			return;
		}

		if (!NpcInstance.canBypassCheck(player, npc))
		{
			return;
		}

		if (player.getActiveWeaponFlagAttachment() != null)
		{
			player.sendPacket(Msg.YOU_CANNOT_MOUNT_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
			return;
		}

		if (player.getTransformation() != 0)
		{
			show("Can't ride while in transformation mode.", player, npc);
			return;
		}

		if (player.getPet() != null || player.isMounted())
		{
			player.sendPacket(Msg.YOU_ALREADY_HAVE_A_PET);
			return;
		}

		int npc_id;

		switch (Integer.parseInt(args[0]))
		{
		case 1:
			npc_id = PetDataTable.WYVERN_ID;
			break;
		case 2:
			npc_id = PetDataTable.STRIDER_WIND_ID;
			break;
		case 3:
			npc_id = PetDataTable.WGREAT_WOLF_ID;
			break;
		case 4:
			npc_id = PetDataTable.WFENRIR_WOLF_ID;
			break;
		default:
			show("Unknown pet.", player, npc);
			return;
		}

		/*
		 * if ((npc_id == PetDataTable.WYVERN_ID || npc_id == PetDataTable.STRIDER_WIND_ID) && !SiegeUtils.getCanRide())
		 * {
		 * show(ru ? "Прокат виверн/страйдеров не работает во время осады." : "Can't ride wyvern/strider while Siege in progress.", player, npc);
		 * return;
		 * }
		 */

		Integer time = Integer.parseInt(args[1]);
		Long price = Long.parseLong(args[2]);

		if (time > 1800)
		{
			show("Too long time to ride.", player, npc);
			return;
		}

		if (player.getAdena() < price)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		player.reduceAdena(price, true, "Rename$ride");

		doLimitedRide(player, npc_id, time);
	}

	public void doLimitedRide(Player player, Integer npc_id, Integer time)
	{
		if (!ride(player, npc_id))
		{
			return;
		}
		player.sendPacket(new SetupGauge(player, 3, time * 1000));
		executeTask(player, "services.RideHire", "rideOver", new Object[0], time * 1000);
	}

	public void rideOver()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		unRide(player);
		show("Ride time is over.<br><br>Welcome back again!", player);
	}
}