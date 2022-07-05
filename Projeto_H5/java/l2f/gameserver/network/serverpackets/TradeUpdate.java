package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.items.ItemInfo;

public class TradeUpdate extends L2GameServerPacket
{
	private ItemInfo _item;
	private long _amount;

	public TradeUpdate(ItemInfo item, long amount)
	{
		_item = item;
		_amount = amount;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x81);
		writeH(1);
		writeH(_amount > 0 && _item.getItem().isStackable() ? 3 : 2);
		writeH(_item.getItem().getType1());
		writeD(_item.getObjectId());
		writeD(_item.getItemId());
		writeQ(_amount);
		writeH(_item.getItem().getType2ForPackets());
		writeH(_item.getCustomType1());
		writeD(_item.getItem().getBodyPart());
		writeH(_item.getEnchantLevel());
		writeH(0x00);
		writeH(_item.getCustomType2());
		writeH(_item.getAttackElement());
		writeH(_item.getAttackElementValue());
		writeH(_item.getDefenceFire());
		writeH(_item.getDefenceWater());
		writeH(_item.getDefenceWind());
		writeH(_item.getDefenceEarth());
		writeH(_item.getDefenceHoly());
		writeH(_item.getDefenceUnholy());
		writeH(0);
		writeH(0);
		writeH(0);
	}
}