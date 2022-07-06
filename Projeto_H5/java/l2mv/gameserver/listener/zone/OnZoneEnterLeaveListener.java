package l2mv.gameserver.listener.zone;

import l2mv.commons.listener.Listener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Zone;

public interface OnZoneEnterLeaveListener extends Listener<Zone>
{
	public void onZoneEnter(Zone zone, Creature actor);

	public void onZoneLeave(Zone zone, Creature actor);

	public void onEquipChanged(Zone zone, Creature actor);
}
