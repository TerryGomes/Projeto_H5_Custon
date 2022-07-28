package l2mv.gameserver.network.serverpackets;

import java.util.List;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.TradeItem;

public class PrivateStoreListSell extends L2GameServerPacket
{
	private final int _sellerId;
	private final long _adena;
	private final boolean _package;
	private final List<TradeItem> _sellList;

	/**
	 * Список вещей в личном магазине продажи, показываемый покупателю
	 * @param buyer
	 * @param seller
	 */
	public PrivateStoreListSell(Player buyer, Player seller)
	{
		this._sellerId = seller.getObjectId();
		this._adena = buyer.getAdena();
		this._package = seller.getPrivateStoreType() == Player.STORE_PRIVATE_SELL_PACKAGE;
		this._sellList = seller.getSellList();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xA1);
		this.writeD(this._sellerId);
		this.writeD(this._package ? 1 : 0);
		this.writeQ(this._adena);
		this.writeD(this._sellList.size());
		for (TradeItem si : this._sellList)
		{
			this.writeItemInfo(si);
			this.writeQ(si.getOwnersPrice());
			this.writeQ(si.getStorePrice());
		}
	}
}