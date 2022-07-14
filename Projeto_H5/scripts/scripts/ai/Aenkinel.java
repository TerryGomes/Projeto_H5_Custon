package ai;

import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;

public class Aenkinel extends Fighter
{

	public Aenkinel(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();

		// Устанавливаем реюз для Tower и Great Seal
		if (actor.getNpcId() == 25694 || actor.getNpcId() == 25695)
		{
			Reflection ref = actor.getReflection();
			ref.setReenterTime(System.currentTimeMillis());
		}

		if (actor.getNpcId() == 25694)
		{
			for (int i = 0; i < 4; i++)
			{
				actor.getReflection().addSpawnWithoutRespawn(18820, actor.getLoc(), 250);
			}
		}
		else if (actor.getNpcId() == 25695)
		{
			for (int i = 0; i < 4; i++)
			{
				actor.getReflection().addSpawnWithoutRespawn(18823, actor.getLoc(), 250);
			}
		}

		super.onEvtDead(killer);
	}
}