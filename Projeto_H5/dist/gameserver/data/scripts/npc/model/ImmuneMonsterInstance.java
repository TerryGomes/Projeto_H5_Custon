package npc.model;

import l2mv.gameserver.model.instances.MonsterInstance;
import l2mv.gameserver.templates.npc.NpcTemplate;

public class ImmuneMonsterInstance extends MonsterInstance
{
	public ImmuneMonsterInstance(int objectId, NpcTemplate template)
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
}