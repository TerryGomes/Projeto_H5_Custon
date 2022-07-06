package l2mv.gameserver.listener.actor;

import l2mv.gameserver.listener.CharListener;
import l2mv.gameserver.model.Creature;

public interface OnDeleteListener extends CharListener
{
	void onDelete(Creature p0);
}
