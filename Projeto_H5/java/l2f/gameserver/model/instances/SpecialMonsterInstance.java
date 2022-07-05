package l2f.gameserver.model.instances;

import l2f.gameserver.templates.npc.NpcTemplate;

public class SpecialMonsterInstance extends MonsterInstance
{
	public SpecialMonsterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}
}