package l2mv.gameserver.model.premium;

import java.util.List;

public class PremiumAccount
{
	private final int _id;
	private final int _time;
	private final String _name;
	private final String _icon;
	private final List<PremiumGift> _list;
	private final int _price_id;
	private final long _price_count;
	private double _exp = 1.0D;
	private double _sp = 1.0D;
	private double _epaulette = 1.0D;
	private double _adena = 1.0D;
	private double _spoil = 1.0D;
	private double _items = 1.0D;
	private double _weight = 1.0D;
	private int _craft = 0;
	private int _masterwork = 0;
	private int _attribute = 0;

	public PremiumAccount(int id, int time, String name, String icon, List<PremiumGift> list, int price_id, long price_count)
	{
		_id = id;
		_time = time;
		_name = name;
		_icon = icon;
		_list = list;
		_price_id = price_id;
		_price_count = price_count;
	}

	public int getId()
	{
		return _id;
	}

	public int getTime()
	{
		return _time;
	}

	public String getName()
	{
		return _name;
	}

	public String getIcon()
	{
		return _icon;
	}

	public int getPriceId()
	{
		return _price_id;
	}

	public long getPriceCount()
	{
		return _price_count;
	}

	public double getExp()
	{
		return _exp;
	}

	public double getSp()
	{
		return _sp;
	}

	public double getEpaulette()
	{
		return _epaulette;
	}

	public double getAdena()
	{
		return _adena;
	}

	public double getSpoil()
	{
		return _spoil;
	}

	public double getItems()
	{
		return _items;
	}

	public double getWeight()
	{
		return _weight;
	}

	public int getCraftChance()
	{
		return _craft;
	}

	public int getMasterWorkChance()
	{
		return _masterwork;
	}

	public int getAttributeChance()
	{
		return _attribute;
	}

	public void setRate(PremiumKeys key, String value)
	{
		switch (key)
		{
		case ADENA:
			_adena = Double.parseDouble(value);
			break;
		case CRAFT:
			_craft = Integer.parseInt(value);
			break;
		case DROP:
			_items = Double.parseDouble(value);
			break;
		case EXP:
			_exp = Double.parseDouble(value);
			break;
		case MASTERWORK_CRAFT:
			_masterwork = Integer.parseInt(value);
			break;
		case SIEGE:
			_epaulette = Double.parseDouble(value);
			break;
		case SP:
			_sp = Double.parseDouble(value);
			break;
		case SPOIL:
			_spoil = Double.parseDouble(value);
			break;
		case WEIGHT_LIMIT:
			_weight = Double.parseDouble(value);
			break;
		case ATTRIBUTE:
			_attribute = Integer.parseInt(value);
			break;
		}
	}

	public List<PremiumGift> getGifts()
	{
		return _list;
	}
}
