package npc.model;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.MonsterInstance;
import l2f.gameserver.templates.npc.NpcTemplate;

public final class GvGBossInstance extends MonsterInstance
{
	public GvGBossInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
	}

	@Override
	public void showChatWindow(Player player, String filename, Object... replace)
	{
	}

	@Override
	public boolean canChampion()
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
}