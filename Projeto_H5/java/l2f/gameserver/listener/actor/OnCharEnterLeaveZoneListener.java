package l2f.gameserver.listener.actor;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Zone;

public interface OnCharEnterLeaveZoneListener
{
	void onEnter(Creature p0, Zone p1);

	void onLeave(Creature p0, Zone p1);
}
