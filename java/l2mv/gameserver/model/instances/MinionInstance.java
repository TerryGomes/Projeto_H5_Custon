package l2mv.gameserver.model.instances;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.Location;

public class MinionInstance extends MonsterInstance
{
	private MonsterInstance _master;

	public MinionInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	public void setLeader(MonsterInstance leader)
	{
		_master = leader;
	}

	public MonsterInstance getLeader()
	{
		return _master;
	}

	public boolean isRaidFighter()
	{
		return getLeader() != null && getLeader().isRaid();
	}

	@Override
	protected void onDeath(Creature killer)
	{
		if (getLeader() != null)
		{
			getLeader().notifyMinionDied(this);
		}

		super.onDeath(killer);
	}

	@Override
	protected void onDecay()
	{
		decayMe();

		_spawnAnimation = 2;
	}

	@Override
	public boolean isFearImmune()
	{
		return isRaidFighter();
	}

	@Override
	public Location getSpawnedLoc()
	{
		return getLeader() != null ? getLeader().getLoc() : getLoc();
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}

	@Override
	public boolean isMinion()
	{
		return true;
	}
}