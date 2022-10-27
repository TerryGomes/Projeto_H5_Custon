package l2mv.gameserver.templates.augmentation;

import l2mv.commons.math.random.RndSelector;
import l2mv.gameserver.templates.item.ItemTemplate;

public class AugmentationInfo
{
	public static final int MAX_AUGMENTATION_COUNT = 2;
	private final int _mineralId;
	private final int _feeItemId;
	private final long _feeItemCount;
	private final long _cancelFee;
	private RndSelector<OptionGroup>[][] _optionGroups;

	public AugmentationInfo(int mineralId, int feeItemId, long feeItemCount, long cancelFee, RndSelector<OptionGroup>[][] rndSelectors)
	{
		_mineralId = mineralId;
		_feeItemId = feeItemId;
		_feeItemCount = feeItemCount;
		_cancelFee = cancelFee;
		_optionGroups = rndSelectors;
	}

	public int getMineralId()
	{
		return _mineralId;
	}

	public int getFeeItemId()
	{
		return _feeItemId;
	}

	public long getFeeItemCount()
	{
		return _feeItemCount;
	}

	public long getCancelFee()
	{
		return _cancelFee;
	}

	public int[] randomOption(ItemTemplate itemTemplate)
	{
		RndSelector<OptionGroup>[] rnd = _optionGroups[0];
		if (rnd == null)
		{
			return null;
		}
		int[] data = new int[rnd.length];
		for (int i = 0; i < data.length; i++)
		{
			RndSelector<OptionGroup> groupSelector = rnd[i];
			if (groupSelector != null)
			{
				OptionGroup randomGroup = groupSelector.chance(1000000);
				if (randomGroup != null)
				{
					Integer random = randomGroup.random();
					data[i] = (random == null ? 0 : random.intValue());
				}
			}
		}
		return data;
	}
}
