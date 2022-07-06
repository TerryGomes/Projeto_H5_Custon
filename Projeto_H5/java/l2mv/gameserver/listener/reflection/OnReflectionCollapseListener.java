package l2mv.gameserver.listener.reflection;

import l2mv.commons.listener.Listener;
import l2mv.gameserver.model.entity.Reflection;

public interface OnReflectionCollapseListener extends Listener<Reflection>
{
	public void onReflectionCollapse(Reflection reflection);
}
