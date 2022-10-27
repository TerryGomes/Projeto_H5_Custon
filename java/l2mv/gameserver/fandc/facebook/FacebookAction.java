package l2mv.gameserver.fandc.facebook;

import l2mv.commons.annotations.Nullable;

public interface FacebookAction
{
	String getId();

	FacebookActionType getActionType();

	FacebookProfile getExecutor();

	long getCreatedDate();

	long getExtractionDate();

	String getMessage();

	void changeMessage(String p0);

	FacebookAction getFather();

	default boolean hasSameFather(@Nullable FacebookAction father)
	{
		if (getFather() == null)
		{
			return father == null;
		}
		return getFather().equals(father);
	}

	boolean canBeRemoved();

	void remove();
}
