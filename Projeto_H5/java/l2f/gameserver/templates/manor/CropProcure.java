package l2f.gameserver.templates.manor;

public class CropProcure
{
	int _rewardType;
	int _cropId;
	long _buyResidual;
	long _buy;
	long _price;

	public CropProcure(int id)
	{
		_cropId = id;
		_buyResidual = 0;
		_rewardType = 0;
		_buy = 0;
		_price = 0;
	}

	public CropProcure(int id, long amount, int type, long buy, long price)
	{
		_cropId = id;
		_buyResidual = amount;
		_rewardType = type;
		_buy = buy;
		_price = price;
		if (_price < 0L)
		{
			_price = 0L;
		}
	}

	public int getReward()
	{
		return _rewardType;
	}

	public int getId()
	{
		return _cropId;
	}

	public long getAmount()
	{
		return _buyResidual;
	}

	public long getStartAmount()
	{
		return _buy;
	}

	public long getPrice()
	{
		return _price;
	}

	public void setAmount(long amount)
	{
		_buyResidual = amount;
	}
}
