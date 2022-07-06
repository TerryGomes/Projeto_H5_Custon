package handler.items;

import l2mv.gameserver.handler.items.ItemHandler;
import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.tables.PetDataTable;
import l2mv.gameserver.tables.SkillTable;

public class PetSummon extends ScriptItemHandler implements ScriptFile
{
	// all the items ids that this handler knowns
	private static final int[] _itemIds = PetDataTable.getPetControlItems();
	private static final int _skillId = 2046;

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if (playable == null || !playable.isPlayer())
		{
			return false;
		}
		Player player = (Player) playable;

		player.setPetControlItem(item);
		player.getAI().Cast(SkillTable.getInstance().getInfo(_skillId, 1), player, false, true);
		return true;
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

	@Override
	public final int[] getItemIds()
	{
		return _itemIds;
	}
}