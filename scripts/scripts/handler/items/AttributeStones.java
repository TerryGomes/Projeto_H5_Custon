package handler.items;

import l2mv.gameserver.cache.Msg;
import l2mv.gameserver.handler.items.ItemHandler;
import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.ExChooseInventoryAttributeItem;
import l2mv.gameserver.scripts.ScriptFile;

public class AttributeStones extends ScriptItemHandler implements ScriptFile
{
	private static final int[] _itemIds =
	{
		9546,
		9547,
		9548,
		9549,
		9550,
		9551,
		9552,
		9553,
		9554,
		9555,
		9556,
		9557,
		9558,
		9563,
		9561,
		9560,
		9562,
		9559,
		9567,
		9566,
		9568,
		9565,
		9564,
		9569,
		10521,
		10522,
		10523,
		10524,
		10525,
		10526
	};

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if (playable == null || !playable.isPlayer())
		{
			return false;
		}
		Player player = (Player) playable;

		if (player.getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
		{
			player.sendPacket(Msg.YOU_CANNOT_ADD_ELEMENTAL_POWER_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			return false;
		}

		if (player.getEnchantScroll() != null)
		{
			return false;
		}

		player.setEnchantScroll(item);
		player.setIsEnchantAllAttribute(ctrl);
		player.sendPacket(Msg.PLEASE_SELECT_ITEM_TO_ADD_ELEMENTAL_POWER);
		player.sendPacket(new ExChooseInventoryAttributeItem(item));
		return true;
	}

	@Override
	public final int[] getItemIds()
	{
		return _itemIds;
	}

	@Override
	public boolean pickupItem(Playable playable, ItemInstance item)
	{
		return true;
	}

	@Override
	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}

	@Override
	public void onReload()
	{

	}

	@Override
	public void onShutdown()
	{

	}
}