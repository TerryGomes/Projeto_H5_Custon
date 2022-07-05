package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Player;

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
		_recipeId = recipeId;
		_shopId = manufacturer.getObjectId();
		_price = price;
		_success = success;
		_curMp = (int) manufacturer.getCurrentMp();
		_maxMp = manufacturer.getMaxMp();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xe0);
		writeD(_shopId);
		writeD(_recipeId);
		writeD(_curMp);
		writeD(_maxMp);
		writeD(_success);
		writeQ(_price);
	}
}