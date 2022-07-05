package l2f.gameserver.listener.actor;

import l2f.gameserver.listener.CharListener;
import l2f.gameserver.model.Creature;

public interface OnStatusUpdateBroadcastListener extends CharListener
{
	void onStatusUpdate(Creature p0);
}
