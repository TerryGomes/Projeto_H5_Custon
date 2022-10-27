package ai.Zone.DragonValley.DV_RB;

import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.tables.SkillTable;

/**
 * @author FandC
 */

public class MuscleBomber extends Fighter
{
//
//	private boolean spawn_50 = true;
//	private boolean spawn_33 = true;
//	private boolean spawn_5 = true;

	// private int drakos_assassin = 22823;

	private long last_attack_time = 0;

	public MuscleBomber(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		super.thinkActive();
		if (last_attack_time != 0 && last_attack_time + 30 * 60 * 1000L < System.currentTimeMillis())
		{
			getActor().deleteMe();
		}
		return true;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		getActor().altOnMagicUseTimer(getActor(), SkillTable.getInstance().getInfo(6842, 1));
		last_attack_time = System.currentTimeMillis();
	}

//	@Override
//	protected void onEvtAttacked(Creature attacker, int damage)
//	{
//		NpcInstance actor = getActor();
//		if (actor.getCurrentHpPercents() <= 50 && spawn_50)
//		{
//			spawn_drakos(actor, attacker);
//			spawn_50 = false;
//		} else if (actor.getCurrentHpPercents() <= 33 && spawn_33)
//		{
//			spawn_drakos(actor, attacker);
//			spawn_33 = false;
//		} else if (actor.getCurrentHpPercents() <= 5 && spawn_5)
//		{
//			spawn_drakos(actor, attacker);
//			spawn_5 = false;
//		}
//		super.onEvtAttacked(attacker, damage);
//	}

//	private void spawn_drakos(NpcInstance actor, Creature attacker)
//	{
//		for (int i = 0; i < 3; i++)
//		{
//			NpcInstance n = NpcUtils.spawnSingle(drakos_assassin, (actor.getX() + Rnd.get(-100, 100)), (actor.getY() + Rnd.get(-100, 100)), actor.getZ());
//			n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 2);
//		}
//	}
}
