package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

/**
 * dddddQ
 */
public class RecipeShopItemInfo extends L2GameServerPacket
{
	private int _recipeId, _shopId, _curMp, _maxMp;
	private int _success = 0xFFFFFFFF;
	private long _price;

	public RecipeShopItemInfo(Player activeChar, Player manufacturer, int recipeId, long price, int success)
	{
		this._recipeId = recipeId;
		this._shopId = manufacturer.getObjectId();
		this._price = price;
		this._success = success;
		this._curMp = (int) manufacturer.getCurrentMp();
		this._maxMp = manufacturer.getMaxMp();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xe0);
		this.writeD(this._shopId);
		this.writeD(this._recipeId);
		this.writeD(this._curMp);
		this.writeD(this._maxMp);
		this.writeD(this._success);
		this.writeQ(this._price);
	}
}