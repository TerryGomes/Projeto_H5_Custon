package l2f.gameserver.model.reference;

import l2f.commons.lang.reference.AbstractHardReference;

public class L2Reference<T> extends AbstractHardReference<T>
{
	public L2Reference(T reference)
	{
		super(reference);
	}
}
