package l2mv.gameserver.model.entity.events.objects;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.dao.JdbcEntityState;
import l2mv.gameserver.Config;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.entity.events.GlobalEvent;
import l2mv.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2mv.gameserver.model.entity.events.impl.SiegeEvent;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.attachment.FlagItemAttachment;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.Location;

public class FortressCombatFlagObject implements SpawnableObject, FlagItemAttachment
{
	private static final Logger _log = LoggerFactory.getLogger(FortressCombatFlagObject.class);
	private ItemInstance _item;
	private final Location _location;

	private GlobalEvent _event;

	public FortressCombatFlagObject(Location location)
	{
		_location = location;
	}

	@Override
	public void spawnObject(GlobalEvent event)
	{
		if (_item != null)
		{
			_log.info("FortressCombatFlagObject: can't spawn twice: " + event);
			return;
		}
		_item = ItemFunctions.createItem(9819);
		_item.setAttachment(this);
		_item.dropMe(null, _location);
		_item.setTimeToDeleteAfterDrop(0);

		_event = event;
	}

	@Override
	public void despawnObject(GlobalEvent event)
	{
		if (_item == null)
		{
			return;
		}

		Player owner = GameObjectsStorage.getPlayer(_item.getOwnerId());
		if (owner != null)
		{
			owner.getInventory().destroyItem(_item, "Fortress Combat Flag");
			owner.sendDisarmMessage(_item);
		}

		_item.setAttachment(null);
		_item.setJdbcState(JdbcEntityState.UPDATED);
		_item.delete();

		_item.deleteMe();
		_item = null;

		_event = null;
	}

	@Override
	public void refreshObject(GlobalEvent event)
	{
	}

	@Override
	public void onLogout(Player player)
	{
		onDeath(player, null);
	}

	@Override
	public void onOutTerritory(Player player)
	{
		if (!Config.FORTRESS_REMOVE_FLAG_ON_LEAVE_ZONE)
		{
			return;
		}
		onDeath(player, null);
	}

	@Override
	public void onDeath(Player owner, Creature killer)
	{
		owner.getInventory().removeItem(_item, "Fortress Combat Flag");

		_item.setOwnerId(0);
		_item.setJdbcState(JdbcEntityState.UPDATED);
		_item.update();

		owner.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_DROPPED_S1).addItemName(_item.getItemId()));

		_item.dropMe(null, _location);
		_item.setTimeToDeleteAfterDrop(0);
	}

	@Override
	public boolean canPickUp(Player player)
	{
		if (player.getActiveWeaponFlagAttachment() != null || player.isMounted())
		{
			return false;
		}
		final FortressSiegeEvent event = player.getEvent(FortressSiegeEvent.class);
		if (event == null)
		{
			return false;
		}
		final SiegeClanObject object = event.getSiegeClan(SiegeEvent.ATTACKERS, player.getClan());
		if (object == null)
		{
			return false;
		}
		return true;
	}

	@Override
	public void pickUp(Player player)
	{
		player.getInventory().equipItem(_item);

		FortressSiegeEvent event = player.getEvent(FortressSiegeEvent.class);
		event.broadcastTo(new SystemMessage2(SystemMsg.C1_HAS_ACQUIRED_THE_FLAG).addName(player), FortressSiegeEvent.ATTACKERS, FortressSiegeEvent.DEFENDERS);
	}

	@Override
	public boolean canAttack(Player player)
	{
		player.sendPacket(SystemMsg.THAT_WEAPON_CANNOT_PERFORM_ANY_ATTACKS);
		return false;
	}

	@Override
	public boolean canCast(Player player, Skill skill)
	{
		if (player.getActiveWeaponItem() == null)
		{
			player.sendPacket(SystemMsg.THAT_WEAPON_CANNOT_USE_ANY_OTHER_SKILL_EXCEPT_THE_WEAPONS_SKILL);
			return false;
		}
		final Skill[] skills = player.getActiveWeaponItem().getAttachedSkills();
		if (!ArrayUtils.contains(skills, skill))
		{
			player.sendPacket(SystemMsg.THAT_WEAPON_CANNOT_USE_ANY_OTHER_SKILL_EXCEPT_THE_WEAPONS_SKILL);
			return false;
		}
		else
		{
			return true;
		}
	}

	@Override
	public boolean canBeLost()
	{
		return true;
	}

	@Override
	public boolean canBeUnEquiped()
	{
		return true;
	}

	@Override
	public void setItem(ItemInstance item)
	{
		// ignored
	}

	public GlobalEvent getEvent()
	{
		return _event;
	}
}
