package ai.hellbound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.utils.Location;

public class NaiaLock extends Fighter
{
	private static final Logger LOG = LoggerFactory.getLogger(NaiaLock.class);
	private static boolean _attacked = false;
	private static boolean _entranceactive = false;

	public NaiaLock(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		_entranceactive = true;
		Functions.npcShout(actor, "The lock has been removed from the Controller device");
		super.onEvtDead(killer);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		NpcInstance actor = getActor();
		_entranceactive = false;
		Functions.npcShout(actor, "The lock has been put on the Controller device");
	}

	@Override
	protected boolean checkAggression(Creature target, boolean avoidAttack)
	{
		return false;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();

		if (!_attacked)
		{
			for (int i = 0; i < 4; i++)
			{
				try
				{
					SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(18493));
					sp.setLoc(Location.findPointToStay(actor, 150, 250));
					sp.setReflection(actor.getReflection());
					sp.doSpawn(true);
					sp.stopRespawn();
				}
				catch (RuntimeException e)
				{
					LOG.error("Error on Naia Lock attacked", e);
				}
			}
			_attacked = true;
		}
	}

	public static boolean isEntranceActive()
	{
		return _entranceactive;
	}
}