package l2f.gameserver.listener.reflection;

import l2f.commons.listener.Listener;
import l2f.gameserver.model.entity.Reflection;

public interface OnReflectionCollapseListener extends Listener<Reflection>
{
	public void onReflectionCollapse(Reflection reflection);
}
