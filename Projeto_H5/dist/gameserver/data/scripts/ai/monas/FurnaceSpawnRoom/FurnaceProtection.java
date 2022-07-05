package ai.monas.FurnaceSpawnRoom;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.data.xml.holder.EventHolder;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.entity.events.EventType;
import l2f.gameserver.model.entity.events.impl.MonasteryFurnaceEvent;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.components.NpcString;
import l2f.gameserver.scripts.Functions;

public class FurnaceProtection extends DefaultAI
{
	private boolean _firstTimeAttacked = true;

	public FurnaceProtection(NpcInstance actor)
	{
		super(actor);
		actor.setNameNpcString(NpcString.FURN3);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return;
		}

		int event_id = actor.getAISpawnParam();
		MonasteryFurnaceEvent furnace = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, event_id);

		if (_firstTimeAttacked && !furnace.isInProgress())
		{
			_firstTimeAttacked = false;
			attacker.setTarget(null);
			actor.setTargetable(false);
			actor.setNpcState((byte) 1);
			Functions.npcShout(actor, NpcString.FURN1);
			furnace.registerActions();
			ThreadPoolManager.getInstance().schedule(new ScheduleTimerTask(), 15000);
		}

		super.onEvtAttacked(attacker, damage);
	}

	private class ScheduleTimerTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			NpcInstance actor = getActor();
			int event_id = actor.getAISpawnParam();
			MonasteryFurnaceEvent furnace = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, event_id);
			furnace.spawnAction(MonasteryFurnaceEvent.PROTECTOR_ROOM, true);
		}
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		_firstTimeAttacked = true;
		super.onEvtDead(killer);
	}
}