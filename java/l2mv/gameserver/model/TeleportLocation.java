package l2mv.gameserver.model;

import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.utils.Location;

public class TeleportLocation extends Location
{
	private static final long serialVersionUID = 1L;
	private final long _price;
	private final ItemTemplate _item;
	private final int _name;
	private final int _castleId;
	private final String _stringName;
	private final String _stringNameLang;

	public TeleportLocation(int item, long price, int name, String StringName, String StringNameLang, int castleId)
	{
		_price = price;
		_name = name;
		_stringName = StringName;
		_stringNameLang = StringNameLang;
		_item = ItemHolder.getInstance().getTemplate(item);
		_castleId = castleId;
	}

	public long getPrice()
	{
		return _price;
	}

	public ItemTemplate getItem()
	{
		return _item;
	}

	public int getName()
	{
		return _name;
	}

	public int getCastleId()
	{
		return _castleId;
	}

	public String getStringName()
	{
		return _stringName;
	}

	public String getStringNameLang()
	{
		return _stringNameLang;
	}
}
