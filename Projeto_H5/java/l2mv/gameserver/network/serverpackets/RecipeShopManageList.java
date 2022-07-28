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
		this.sellerId = seller.getObjectId();
		this.adena = seller.getAdena();
		this.isDwarven = isDwarvenCraft;
		if (this.isDwarven)
		{
			this.recipes = seller.getDwarvenRecipeBook();
		}
		else
		{
			this.recipes = seller.getCommonRecipeBook();
		}
		this.createList = seller.getCreateList();
		for (ManufactureItem mi : this.createList)
		{
			if (!seller.findRecipe(mi.getRecipeId()))
			{
				this.createList.remove(mi);
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xde);
		this.writeD(this.sellerId);
		this.writeD((int) Math.min(this.adena, Integer.MAX_VALUE)); // FIXME не менять на writeQ, в текущем клиенте там все еще D (видимо баг NCSoft)
		this.writeD(this.isDwarven ? 0x00 : 0x01);
		this.writeD(this.recipes.size());
		int i = 1;
		for (Recipe recipe : this.recipes)
		{
			this.writeD(recipe.getId());
			this.writeD(i++);
		}
		this.writeD(this.createList.size());
		for (ManufactureItem mi : this.createList)
		{
			this.writeD(mi.getRecipeId());
			this.writeD(0x00); // ??
			this.writeQ(mi.getCost());
		}
	}
}