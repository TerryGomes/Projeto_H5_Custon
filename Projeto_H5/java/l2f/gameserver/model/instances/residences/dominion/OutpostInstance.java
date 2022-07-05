package l2f.gameserver.model.instances.residences.dominion;

import org.apache.commons.lang3.StringUtils;

import l2f.commons.geometry.Circle;
import l2f.gameserver.instancemanager.ReflectionManager;
import l2f.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Territory;
import l2f.gameserver.model.World;
import l2f.gameserver.model.Zone;
import l2f.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2f.gameserver.model.instances.residences.SiegeFlagInstance;
import l2f.gameserver.stats.Stats;
import l2f.gameserver.stats.funcs.FuncMul;
import l2f.gameserver.templates.StatsSet;
import l2f.gameserver.templates.ZoneTemplate;
import l2f.gameserver.templates.npc.NpcTemplate;

public class OutpostInstance extends SiegeFlagInstance
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private class OnZoneEnterLeaveListenerImpl implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature actor)
		{
			DominionSiegeEvent siegeEvent = OutpostInstance.this.getEvent(DominionSiegeEvent.class);
			if ((siegeEvent == null) || (actor.getEvent(DominionSiegeEvent.class) != siegeEvent))
			{
				return;
			}

			actor.addStatFunc(new FuncMul(Stats.REGENERATE_HP_RATE, 0x40, OutpostInstance.this, 2.));
			actor.addStatFunc(new FuncMul(Stats.REGENERATE_MP_RATE, 0x40, OutpostInstance.this, 2.));
			actor.addStatFunc(new FuncMul(Stats.REGENERATE_CP_RATE, 0x40, OutpostInstance.this, 2.));
		}

		@Override
		public void onZoneLeave(Zone zone, Creature actor)
		{
			actor.removeStatsOwner(OutpostInstance.this);
		}

		@Override
		public void onEquipChanged(Zone zone, Creature actor)
		{
		}
	}

	private Zone _zone = null;

	public OutpostInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();

		Circle c = new Circle(getLoc(), 250);
		c.setZmax(World.MAP_MAX_Z);
		c.setZmin(World.MAP_MIN_Z);

		StatsSet set = new StatsSet();
		set.set("name", StringUtils.EMPTY);
		set.set("type", Zone.ZoneType.dummy);
		set.set("territory", new Territory().add(c));

		_zone = new Zone(new ZoneTemplate(set));
		_zone.setReflection(ReflectionManager.DEFAULT);
		_zone.addListener(new OnZoneEnterLeaveListenerImpl());
		_zone.setActive(true);
	}

	@Override
	public void onDelete()
	{
		super.onDelete();

		_zone.setActive(false);
		_zone = null;
	}

	@Override
	public boolean isInvul()
	{
		return true;
	}
}
