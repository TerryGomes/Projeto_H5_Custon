package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

public class RecipeShopMsg extends L2GameServerPacket
{
	private int _objectId;
	private String _storeName;

	public RecipeShopMsg(Player player)
	{
		this._objectId = player.getObjectId();
		this._storeName = player.getManufactureName();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xe1);
		this.writeD(this._objectId);
		this.writeS(this._storeName);
	}
}