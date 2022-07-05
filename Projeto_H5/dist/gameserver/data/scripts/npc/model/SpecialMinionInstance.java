package npc.model;

import l2f.gameserver.model.instances.MonsterInstance;
import l2f.gameserver.templates.npc.NpcTemplate;

/**
 * @author pchayka
 */
public final class SpecialMinionInstance extends MonsterInstance
{
	public SpecialMinionInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
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
	public boolean canChampion()
	{
		return false;
	}

	@Override
	public void onRandomAnimation()
	{
		return;
	}

}