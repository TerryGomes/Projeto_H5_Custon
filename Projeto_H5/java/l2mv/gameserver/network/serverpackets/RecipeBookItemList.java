package l2mv.gameserver.network.serverpackets;

import java.util.Collection;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Recipe;

public class RecipeBookItemList extends L2GameServerPacket
{
	private Collection<Recipe> _recipes;
	private final boolean _isDwarvenCraft;
	private final int _currentMp;

	public RecipeBookItemList(Player player, boolean isDwarvenCraft)
	{
		this._isDwarvenCraft = isDwarvenCraft;
		this._currentMp = (int) player.getCurrentMp();
		if (isDwarvenCraft)
		{
			this._recipes = player.getDwarvenRecipeBook();
		}
		else
		{
			this._recipes = player.getCommonRecipeBook();
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xdc);
		this.writeD(this._isDwarvenCraft ? 0x00 : 0x01);
		this.writeD(this._currentMp);

		this.writeD(this._recipes.size());

		for (Recipe recipe : this._recipes)
		{
			this.writeD(recipe.getId());
			this.writeD(1); // ??
		}
	}
}