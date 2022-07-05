package l2f.gameserver.listener.zone;

import l2f.commons.listener.Listener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Zone;

public interface OnZoneEnterLeaveListener extends Listener<Zone>
{
	public void onZoneEnter(Zone zone, Creature actor);

	public void onZoneLeave(Zone zone, Creature actor);

	public void onEquipChanged(Zone zone, Creature actor);
}
