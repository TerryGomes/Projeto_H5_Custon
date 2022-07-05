package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.items.ItemInfo;

public class TradeOtherAdd extends L2GameServerPacket
{
	private ItemInfo _temp;
	private long _amount;

	public TradeOtherAdd(ItemInfo item, long amount)
	{
		_temp = item;
		_amount = amount;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x1b);
		writeH(1); // item count
		writeH(_temp.getItem().getType1());
		writeD(_temp.getObjectId());
		writeD(_temp.getItemId());
		writeQ(_amount);
		writeH(_temp.getItem().getType2ForPackets());
		writeH(_temp.getCustomType1());
		writeD(_temp.getItem().getBodyPart());
		writeH(_temp.getEnchantLevel());
		writeH(0x00);
		writeH(_temp.getCustomType2());
		writeH(_temp.getAttackElement());
		writeH(_temp.getAttackElementValue());
		writeH(_temp.getDefenceFire());
		writeH(_temp.getDefenceWater());
		writeH(_temp.getDefenceWind());
		writeH(_temp.getDefenceEarth());
		writeH(_temp.getDefenceHoly());
		writeH(_temp.getDefenceUnholy());
		writeH(0);
		writeH(0);
		writeH(0);
	}
}