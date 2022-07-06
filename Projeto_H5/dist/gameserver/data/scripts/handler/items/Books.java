package handler.items;

import l2mv.gameserver.handler.items.ItemHandler;
import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.SSQStatus;
import l2mv.gameserver.network.serverpackets.ShowXMasSeal;
import l2mv.gameserver.scripts.ScriptFile;

public class Books extends SimpleItemHandler implements ScriptFile
{
	private static final int[] ITEM_IDS = new int[]
	{
		5555,
		5707
	};

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

	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}

	@Override
	protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl)
	{
		int itemId = item.getItemId();

		switch (itemId)
		{
		case 5555:
			player.sendPacket(new ShowXMasSeal(5555));
			break;
		case 5707:
			player.sendPacket(new SSQStatus(player, 1));
			break;
		default:
			return false;
		}

		return true;
	}
}
