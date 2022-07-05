package npc.model.fightClub;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.templates.npc.NpcTemplate;

public class LifeControlTowerInstance extends NpcInstance
{
	public LifeControlTowerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);

		setHasChatWindow(false);
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return !attacker.isMonster();
	}

	@Override
	public boolean isAttackable(Creature attacker)
	{
		return isAutoAttackable(attacker);
	}

	@Override
	public boolean isInvul()
	{
		return false;
	}

	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return true;
	}

	@Override
	public boolean isLethalImmune()
	{
		return true;
	}

	@Override
	protected void onDecay()
	{
		decayMe();

		_spawnAnimation = 2;
	}

	@Override
	public Clan getClan()
	{
		return null;
	}
}
