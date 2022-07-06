package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

public class SystemMessage2 extends SysMsgContainer<SystemMessage2>
{
	public SystemMessage2(SystemMsg message)
	{
		super(message);
	}

	public SystemMessage2()
	{
		super("");
	}

	public static SystemMessage2 obtainItems(int itemId, long count, int enchantLevel)
	{
		if (itemId == 57)
		{
			return new SystemMessage2(SystemMsg.YOU_HAVE_EARNED_S1_ADENA).addLong(count);
		}
		if (count > 1)
		{
			return new SystemMessage2(SystemMsg.YOU_HAVE_EARNED_S2_S1S).addItemName(itemId).addLong(count);
		}
		if (enchantLevel > 0)
		{
			return new SystemMessage2(SystemMsg.YOU_HAVE_OBTAINED_A_S1_S2).addInteger(enchantLevel).addItemName(itemId);
		}
		return new SystemMessage2(SystemMsg.YOU_HAVE_EARNED_S1).addItemName(itemId);
	}

	public static SystemMessage2 obtainItems(ItemInstance item)
	{
		return obtainItems(item.getItemId(), item.getCount(), item.isEquipable() ? item.getEnchantLevel() : 0);
	}

	public static SystemMessage2 obtainItemsBy(int itemId, long count, int enchantLevel, Creature target)
	{
		if (count > 1)
		{
			return new SystemMessage2(SystemMsg.C1_HAS_OBTAINED_S3_S2).addName(target).addItemName(itemId).addLong(count);
		}
		if (enchantLevel > 0)
		{
			return new SystemMessage2(SystemMsg.C1_HAS_OBTAINED_S2S3).addName(target).addInteger(enchantLevel).addItemName(itemId);
		}
		return new SystemMessage2(SystemMsg.C1_HAS_OBTAINED_S2).addName(target).addItemName(itemId);
	}

	public static SystemMessage2 obtainItemsBy(ItemInstance item, Creature target)
	{
		return obtainItemsBy(item.getItemId(), item.getCount(), item.isEquipable() ? item.getEnchantLevel() : 0, target);
	}

	public static SystemMessage2 removeItems(int itemId, long count)
	{
		if (itemId == 57)
		{
			return new SystemMessage2(SystemMsg.S1_ADENA_DISAPPEARED).addLong(count);
		}
		if (count > 1)
		{
			return new SystemMessage2(SystemMsg.S2_S1_HAS_DISAPPEARED).addItemName(itemId).addLong(count);
		}
		return new SystemMessage2(SystemMsg.S1_HAS_DISAPPEARED).addItemName(itemId);
	}

	public static SystemMessage2 removeItems(ItemInstance item)
	{
		return removeItems(item.getItemId(), item.getCount());
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x62);
		writeElements();
	}
}
