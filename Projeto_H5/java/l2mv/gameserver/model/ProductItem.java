package l2mv.gameserver.model;

import java.util.ArrayList;
import java.util.Calendar;

public class ProductItem
{
	// Базовые параметры, если продукт не имеет лимита времени продаж
	public static final long NOT_LIMITED_START_TIME = 315547200000L;
	public static final long NOT_LIMITED_END_TIME = 2127445200000L;
	public static final int NOT_LIMITED_START_HOUR = 0;
	public static final int NOT_LIMITED_END_HOUR = 23;
	public static final int NOT_LIMITED_START_MIN = 0;
	public static final int NOT_LIMITED_END_MIN = 59;

	private final int _productId;
	private final int _category;
	private final int _points;
	private final int _tabId;

	private final long _startTimeSale;
	private final long _endTimeSale;
	private final int _startHour;
	private final int _endHour;
	private final int _startMin;
	private final int _endMin;

	private ArrayList<ProductItemComponent> _components;

	public ProductItem(int productId, int category, int points, int tabId, long startTimeSale, long endTimeSale)
	{
		_productId = productId;
		_category = category;
		_points = points;
		_tabId = tabId;

		Calendar calendar;
		if (startTimeSale > 0)
		{
			calendar = Calendar.getInstance();
			calendar.setTimeInMillis(startTimeSale);

			_startTimeSale = startTimeSale;
			_startHour = calendar.get(Calendar.HOUR_OF_DAY);
			_startMin = calendar.get(Calendar.MINUTE);
		}
		else
		{
			_startTimeSale = NOT_LIMITED_START_TIME;
			_startHour = NOT_LIMITED_START_HOUR;
			_startMin = NOT_LIMITED_START_MIN;
		}

		if (endTimeSale > 0)
		{
			calendar = Calendar.getInstance();
			calendar.setTimeInMillis(endTimeSale);

			_endTimeSale = endTimeSale;
			_endHour = calendar.get(Calendar.HOUR_OF_DAY);
			_endMin = calendar.get(Calendar.MINUTE);
		}
		else
		{
			_endTimeSale = NOT_LIMITED_END_TIME;
			_endHour = NOT_LIMITED_END_HOUR;
			_endMin = NOT_LIMITED_END_MIN;
		}
	}

	public void setComponents(ArrayList<ProductItemComponent> a)
	{
		_components = a;
	}

	public ArrayList<ProductItemComponent> getComponents()
	{
		if (_components == null)
		{
			_components = new ArrayList<ProductItemComponent>();
		}

		return _components;
	}

	public int getProductId()
	{
		return _productId;
	}

	public int getCategory()
	{
		return _category;
	}

	public int getPoints()
	{
		return _points;
	}

	public int getTabId()
	{
		return _tabId;
	}

	public long getStartTimeSale()
	{
		return _startTimeSale;
	}

	public int getStartHour()
	{
		return _startHour;
	}

	public int getStartMin()
	{
		return _startMin;
	}

	public long getEndTimeSale()
	{
		return _endTimeSale;
	}

	public int getEndHour()
	{
		return _endHour;
	}

	public int getEndMin()
	{
		return _endMin;
	}
}
