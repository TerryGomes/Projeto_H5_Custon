package ai.dragonvalley;

import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.ai.CtrlEvent;
import l2f.gameserver.ai.Mystic;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.NpcUtils;

/**
 * @author FandC
 *
 * Emerald Drake(22829)
 * Spawns leeches (22860) when attacked.
 */
public class EmeraldDrake extends Mystic
{

	public EmeraldDrake(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		if (Rnd.chance(Config.EDRAKE_MS_CHANCE))
		{
			NpcInstance actor = getActor();
			NpcInstance n = NpcUtils.spawnSingle(22860, (actor.getX() + Rnd.get(-100, 100)), (actor.getY() + Rnd.get(-100, 100)), actor.getZ());
			n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 2);
		}
		super.onEvtAttacked(attacker, damage);
	}
}
