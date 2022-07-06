package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.base.Element;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.utils.ItemFunctions;

public class ExChooseInventoryAttributeItem extends L2GameServerPacket
{
	private int _itemId;
	private boolean _disableFire;
	private boolean _disableWater;
	private boolean _disableEarth;
	private boolean _disableWind;
	private boolean _disableDark;
	private boolean _disableHoly;
	private int _stoneLvl;

	public ExChooseInventoryAttributeItem(ItemInstance item)
	{
		_itemId = item.getItemId();
		_disableFire = ItemFunctions.getEnchantAttributeStoneElement(item.getItemId(), false) == Element.FIRE;
		_disableWater = ItemFunctions.getEnchantAttributeStoneElement(item.getItemId(), false) == Element.WATER;
		_disableWind = ItemFunctions.getEnchantAttributeStoneElement(item.getItemId(), false) == Element.WIND;
		_disableEarth = ItemFunctions.getEnchantAttributeStoneElement(item.getItemId(), false) == Element.EARTH;
		_disableHoly = ItemFunctions.getEnchantAttributeStoneElement(item.getItemId(), false) == Element.HOLY;
		_disableDark = ItemFunctions.getEnchantAttributeStoneElement(item.getItemId(), false) == Element.UNHOLY;
		_stoneLvl = item.getTemplate().isAttributeCrystal() ? 6 : 3;
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0x62);
		writeD(_itemId);
		writeD(_disableFire ? 1 : 0); // fire
		writeD(_disableWater ? 1 : 0); // water
		writeD(_disableWind ? 1 : 0); // wind
		writeD(_disableEarth ? 1 : 0); // earth
		writeD(_disableHoly ? 1 : 0); // holy
		writeD(_disableDark ? 1 : 0); // dark
		writeD(_stoneLvl); // max enchant lvl
	}
}