package l2mv.gameserver.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import l2mv.gameserver.data.xml.holder.InstantZoneHolder;
import l2mv.gameserver.instancemanager.ReflectionManager;
import l2mv.gameserver.model.CommandChannel;
import l2mv.gameserver.model.Party;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Zone;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.DoorInstance;
import l2mv.gameserver.templates.InstantZone;
import l2mv.gameserver.templates.InstantZoneEntryType;

public class ReflectionUtils
{
	public static DoorInstance getDoor(int id)
	{
		return ReflectionManager.DEFAULT.getDoor(id);
	}

	public static Zone getZone(String name)
	{
		return ReflectionManager.DEFAULT.getZone(name);
	}

	public static List<Zone> getZonesByType(Zone.ZoneType zoneType)
	{
		final Collection<Zone> zones = ReflectionManager.DEFAULT.getZones();
		if (zones.isEmpty())
		{
			return Collections.emptyList();
		}

		List<Zone> zones2 = new ArrayList<Zone>(5);
		for (Zone z : zones)
		{
			if (z.getType() == zoneType)
			{
				zones2.add(z);
			}
		}

		return zones2;
	}

	public static Reflection enterReflection(Player invoker, int instancedZoneId)
	{
		final InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(instancedZoneId);
		return enterReflection(invoker, new Reflection(), iz);
	}

	public static Reflection enterReflection(Player invoker, Reflection r, int instancedZoneId)
	{
		final InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(instancedZoneId);
		return enterReflection(invoker, r, iz);
	}

	public static Reflection enterReflection(Player invoker, Reflection r, InstantZone iz)
	{
		r.init(iz);

		if (r.getReturnLoc() == null)
		{
			r.setReturnLoc(invoker.getLoc());
		}

		InstantZoneEntryType type = iz.getEntryType();
		// If type is command channel and can be also one Party(Impossible to make Command Channel)
		if (type == InstantZoneEntryType.COMMAND_CHANNEL && (iz.getMinParty() <= 9 || (invoker.getParty() != null && invoker.getParty().getLeader() != null && invoker.getParty().getLeader().isGM())))
		{
			// If has only party, without command channel
			if (invoker.getParty() != null && !invoker.getParty().isInCommandChannel())
			{
				type = InstantZoneEntryType.PARTY;
			}
		}

		switch (type)
		{
		case SOLO:
		{
			if (iz.getRemovedItemId() > 0)
			{
				ItemFunctions.removeItem(invoker, iz.getRemovedItemId(), iz.getRemovedItemCount(), true, "ReflectionUtils");
			}
			if (iz.getGiveItemId() > 0)
			{
				ItemFunctions.addItem(invoker, iz.getGiveItemId(), iz.getGiveItemCount(), true, "ReflectionUtils");
			}
			if (iz.isDispelBuffs())
			{
				invoker.dispelBuffs();
			}
			if (iz.getSetReuseUponEntry() && iz.getResetReuse().next(System.currentTimeMillis()) > System.currentTimeMillis())
			{
				invoker.setInstanceReuse(iz.getId(), System.currentTimeMillis());
			}
			invoker.setVar("backCoords", invoker.getLoc().toXYZString(), -1);
			if (iz.getTeleportCoord() != null)
			{
				invoker.teleToLocation(iz.getTeleportCoord(), r);
			}
			break;
		}
		case PARTY:
		{
			Party party = invoker.getParty();

			party.setReflection(r);
			r.setParty(party);

			for (Player member : party.getMembers())
			{
				if (iz.getRemovedItemId() > 0)
				{
					ItemFunctions.removeItem(member, iz.getRemovedItemId(), iz.getRemovedItemCount(), true, "ReflectionUtils");
				}
				if (iz.getGiveItemId() > 0)
				{
					ItemFunctions.addItem(member, iz.getGiveItemId(), iz.getGiveItemCount(), true, "ReflectionUtils");
				}
				if (iz.isDispelBuffs())
				{
					member.dispelBuffs();
				}
				if (iz.getSetReuseUponEntry())
				{
					member.setInstanceReuse(iz.getId(), System.currentTimeMillis());
				}
				member.setVar("backCoords", invoker.getLoc().toXYZString(), -1);
				if (iz.getTeleportCoord() != null)
				{
					member.teleToLocation(iz.getTeleportCoord(), r);
				}
			}
			break;
		}
		case COMMAND_CHANNEL:
		{
			Party commparty = invoker.getParty();
			CommandChannel cc = commparty.getCommandChannel();

			cc.setReflection(r);
			r.setCommandChannel(cc);

			for (Player member : cc)
			{
				if (iz.getRemovedItemId() > 0)
				{
					ItemFunctions.removeItem(member, iz.getRemovedItemId(), iz.getRemovedItemCount(), true, "ReflectionUtils");
				}
				if (iz.getGiveItemId() > 0)
				{
					ItemFunctions.addItem(member, iz.getGiveItemId(), iz.getGiveItemCount(), true, "ReflectionUtils");
				}
				if (iz.isDispelBuffs())
				{
					member.dispelBuffs();
				}
				if (iz.getSetReuseUponEntry())
				{
					member.setInstanceReuse(iz.getId(), System.currentTimeMillis());
				}
				member.setVar("backCoords", invoker.getLoc().toXYZString(), -1);
				member.teleToLocation(iz.getTeleportCoord(), r);
			}

			break;
		}
		}

		return r;
	}
}
