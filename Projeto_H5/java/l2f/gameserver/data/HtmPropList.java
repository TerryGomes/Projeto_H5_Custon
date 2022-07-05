package l2f.gameserver.data;

import java.util.List;

public class HtmPropList
{
	private final List<HtmProp> list;

	public HtmPropList(List<HtmProp> list)
	{
		this.list = list;
	}

	public String getText(String keyWord)
	{
		for (HtmProp prop : list)
		{
			if (prop.getKeyWord().equals(keyWord))
			{
				return prop.getText();
			}
		}
		return "";
	}
}