package l2mv.gameserver.network.serverpackets;

import java.util.Collection;
import java.util.List;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Recipe;
import l2mv.gameserver.model.items.ManufactureItem;

public class RecipeShopManageList extends L2GameServerPacket
{
	private List<ManufactureItem> createList;
	private Collection<Recipe> recipes;
	private int sellerId;
	private long adena;
	private boolean isDwarven;

	public RecipeShopManageList(Player seller, boolean isDwarvenCraft)
	{
		sellerId = seller.getObjectId();
		adena = seller.getAdena();
		isDwarven = isDwarvenCraft;
		if (isDwarven)
		{
			recipes = seller.getDwarvenRecipeBook();
		}
		else
		{
			recipes = seller.getCommonRecipeBook();
		}
		createList = seller.getCreateList();
		for (ManufactureItem mi : createList)
		{
			if (!seller.findRecipe(mi.getRecipeId()))
			{
				createList.remove(mi);
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xde);
		writeD(sellerId);
		writeD((int) Math.min(adena, Integer.MAX_VALUE)); // FIXME не менять на writeQ, в текущем клиенте там все еще D (видимо баг NCSoft)
		writeD(isDwarven ? 0x00 : 0x01);
		writeD(recipes.size());
		int i = 1;
		for (Recipe recipe : recipes)
		{
			writeD(recipe.getId());
			writeD(i++);
		}
		writeD(createList.size());
		for (ManufactureItem mi : createList)
		{
			writeD(mi.getRecipeId());
			writeD(0x00); // ??
			writeQ(mi.getCost());
		}
	}
}