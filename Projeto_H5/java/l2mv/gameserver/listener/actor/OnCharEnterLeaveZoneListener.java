package l2mv.gameserver.listener.actor;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Zone;

public interface OnCharEnterLeaveZoneListener
{
	void onEnter(Creature p0, Zone p1);

	void onLeave(Creature p0, Zone p1);
}
