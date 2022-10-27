package l2mv.gameserver.model.entity.events.objects;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.dao.JdbcEntityState;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.entity.events.GlobalEvent;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.attachment.FlagItemAttachment;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.utils.ItemFunctions;

public class CTFCombatFlagObject implements SpawnableObject, FlagItemAttachment
{
	private static final Logger _log = LoggerFactory.getLogger(CTFCombatFlagObject.class);

	public static final int FLAG_ITEM_ID = 9819;

	private ItemInstance _item;
	private GlobalEvent _event;

	@Override
	public void spawnObject(GlobalEvent event)
	{
		if (_item != null)
		{
			_log.info("FortressCombatFlagObject: can't spawn twice: " + event);
			return;
		}
		_item = ItemFunctions.createItem(FLAG_ITEM_ID);
		_item.setAttachment(this);

		_event = event;
	}

	public ItemInstance getItem()
	{
		return _item;
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
			owner.getInventory().destroyItem(_item, "CTF Combat Flag");
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
	public void onDeath(Player owner, Creature killer)
	{
		despawnObject(_event);
	}

	@Override
	public void onOutTerritory(Player player)
	{
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
		Skill[] skills = player.getActiveWeaponItem().getAttachedSkills();
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
	public void setItem(ItemInstance item)
	{
		// ignored
	}

	public GlobalEvent getEvent()
	{
		return _event;
	}

	@Override
	public boolean canPickUp(Player player)
	{
		return false;
	}

	@Override
	public void pickUp(Player player)
	{

	}

	@Override
	public boolean canBeLost()
	{
		return false;
	}

	@Override
	public boolean canBeUnEquiped()
	{
		return false;
	}
}
