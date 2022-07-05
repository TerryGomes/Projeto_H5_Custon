package l2f.gameserver.listener.actor;

import l2f.gameserver.listener.CharListener;
import l2f.gameserver.model.Creature;

public interface OnDeleteListener extends CharListener
{
	void onDelete(Creature p0);
}
