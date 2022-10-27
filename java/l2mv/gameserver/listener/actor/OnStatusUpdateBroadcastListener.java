package l2mv.gameserver.listener.actor;

import l2mv.gameserver.listener.CharListener;
import l2mv.gameserver.model.Creature;

public interface OnStatusUpdateBroadcastListener extends CharListener
{
	void onStatusUpdate(Creature p0);
}
