package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Recipe;

/**
 * format ddddd
 */
public class RecipeItemMakeInfo extends L2GameServerPacket
{
	private int _id;
	private boolean _isDwarvenRecipe;
	private int _status;
	private int _curMP;
	private int _maxMP;

	public RecipeItemMakeInfo(Player player, Recipe recipeList, int status)
	{
		this._id = recipeList.getId();
		this._isDwarvenRecipe = recipeList.isDwarvenRecipe();
		this._status = status;
		this._curMP = (int) player.getCurrentMp();
		this._maxMP = player.getMaxMp();
		//
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xdd);
		this.writeD(this._id); // ID рецепта
		this.writeD(this._isDwarvenRecipe ? 0x00 : 0x01);
		this.writeD(this._curMP);
		this.writeD(this._maxMP);
		this.writeD(this._status); // итог крафта; 0xFFFFFFFF нет статуса, 0 удача, 1 провал
	}
}