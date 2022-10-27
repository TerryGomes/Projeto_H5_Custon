package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.items.ItemInfo;

public class TradeUpdate extends L2GameServerPacket
{
	private ItemInfo _item;
	private long _amount;

	public TradeUpdate(ItemInfo item, long amount)
	{
		this._item = item;
		this._amount = amount;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x81);
		this.writeH(1);
		this.writeH(this._amount > 0 && this._item.getItem().isStackable() ? 3 : 2);
		this.writeH(this._item.getItem().getType1());
		this.writeD(this._item.getObjectId());
		this.writeD(this._item.getItemId());
		this.writeQ(this._amount);
		this.writeH(this._item.getItem().getType2ForPackets());
		this.writeH(this._item.getCustomType1());
		this.writeD(this._item.getItem().getBodyPart());
		this.writeH(this._item.getEnchantLevel());
		this.writeH(0x00);
		this.writeH(this._item.getCustomType2());
		this.writeH(this._item.getAttackElement());
		this.writeH(this._item.getAttackElementValue());
		this.writeH(this._item.getDefenceFire());
		this.writeH(this._item.getDefenceWater());
		this.writeH(this._item.getDefenceWind());
		this.writeH(this._item.getDefenceEarth());
		this.writeH(this._item.getDefenceHoly());
		this.writeH(this._item.getDefenceUnholy());
		this.writeH(0);
		this.writeH(0);
		this.writeH(0);
	}
}