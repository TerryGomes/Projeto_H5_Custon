package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.items.ItemInfo;

public class TradeOtherAdd extends L2GameServerPacket
{
	private ItemInfo _temp;
	private long _amount;

	public TradeOtherAdd(ItemInfo item, long amount)
	{
		this._temp = item;
		this._amount = amount;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x1b);
		this.writeH(1); // item count
		this.writeH(this._temp.getItem().getType1());
		this.writeD(this._temp.getObjectId());
		this.writeD(this._temp.getItemId());
		this.writeQ(this._amount);
		this.writeH(this._temp.getItem().getType2ForPackets());
		this.writeH(this._temp.getCustomType1());
		this.writeD(this._temp.getItem().getBodyPart());
		this.writeH(this._temp.getEnchantLevel());
		this.writeH(0x00);
		this.writeH(this._temp.getCustomType2());
		this.writeH(this._temp.getAttackElement());
		this.writeH(this._temp.getAttackElementValue());
		this.writeH(this._temp.getDefenceFire());
		this.writeH(this._temp.getDefenceWater());
		this.writeH(this._temp.getDefenceWind());
		this.writeH(this._temp.getDefenceEarth());
		this.writeH(this._temp.getDefenceHoly());
		this.writeH(this._temp.getDefenceUnholy());
		this.writeH(0);
		this.writeH(0);
		this.writeH(0);
	}
}