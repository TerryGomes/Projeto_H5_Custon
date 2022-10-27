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
		this._itemId = item.getItemId();
		this._disableFire = ItemFunctions.getEnchantAttributeStoneElement(item.getItemId(), false) == Element.FIRE;
		this._disableWater = ItemFunctions.getEnchantAttributeStoneElement(item.getItemId(), false) == Element.WATER;
		this._disableWind = ItemFunctions.getEnchantAttributeStoneElement(item.getItemId(), false) == Element.WIND;
		this._disableEarth = ItemFunctions.getEnchantAttributeStoneElement(item.getItemId(), false) == Element.EARTH;
		this._disableHoly = ItemFunctions.getEnchantAttributeStoneElement(item.getItemId(), false) == Element.HOLY;
		this._disableDark = ItemFunctions.getEnchantAttributeStoneElement(item.getItemId(), false) == Element.UNHOLY;
		this._stoneLvl = item.getTemplate().isAttributeCrystal() ? 6 : 3;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x62);
		this.writeD(this._itemId);
		this.writeD(this._disableFire ? 1 : 0); // fire
		this.writeD(this._disableWater ? 1 : 0); // water
		this.writeD(this._disableWind ? 1 : 0); // wind
		this.writeD(this._disableEarth ? 1 : 0); // earth
		this.writeD(this._disableHoly ? 1 : 0); // holy
		this.writeD(this._disableDark ? 1 : 0); // dark
		this.writeD(this._stoneLvl); // max enchant lvl
	}
}