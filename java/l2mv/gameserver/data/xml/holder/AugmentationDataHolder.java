package l2mv.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import l2mv.commons.data.xml.AbstractHolder;
import l2mv.gameserver.templates.augmentation.AugmentationInfo;

public class AugmentationDataHolder extends AbstractHolder
{
	private static AugmentationDataHolder _instance = new AugmentationDataHolder();
	private Set<AugmentationInfo> _augmentationInfos = new HashSet<AugmentationInfo>();
	private List<Integer> _lifestone = new ArrayList<Integer>();

	public static AugmentationDataHolder getInstance()
	{
		return _instance;
	}

	@Override
	public int size()
	{
		return _augmentationInfos.size();
	}

	@Override
	public void clear()
	{
		_augmentationInfos.clear();
	}

	public void addAugmentationInfo(AugmentationInfo augmentationInfo)
	{
		_augmentationInfos.add(augmentationInfo);
	}

	public void addStone(int item)
	{
		_lifestone.add(Integer.valueOf(item));
	}

	public boolean isStone(int item)
	{
		for (Iterator<Integer> i$ = _lifestone.iterator(); i$.hasNext();)
		{
			int id = i$.next().intValue();
			if (id == item)
			{
				return true;
			}
		}
		return false;
	}
}
