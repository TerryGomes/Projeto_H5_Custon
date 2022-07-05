package ai;

import l2f.gameserver.ai.Fighter;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;

public class HekatonPrime extends Fighter
{
	private long _lastTimeAttacked;

	public HekatonPrime(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		_lastTimeAttacked = System.currentTimeMillis();
	}

	@Override
	protected boolean thinkActive()
	{
		if (_lastTimeAttacked + 600000 < System.currentTimeMillis())
		{
			if (getActor().getMinionList().hasMinions())
			{
				getActor().getMinionList().deleteMinions();
			}
			getActor().deleteMe();
			return true;
		}
		return false;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		_lastTimeAttacked = System.currentTimeMillis();
		super.onEvtAttacked(attacker, damage);
	}
}