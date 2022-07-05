package l2f.gameserver.model.instances;

import l2f.gameserver.model.Creature;
import l2f.gameserver.templates.npc.NpcTemplate;

public class ReflectionBossInstance extends RaidBossInstance
{
	private final static int COLLAPSE_AFTER_DEATH_TIME = 5; // 5 мин

	public ReflectionBossInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	protected void onDeath(Creature killer)
	{
		getMinionList().unspawnMinions();
		super.onDeath(killer);
		clearReflection();
	}

	@Override
	public boolean isBoss()
	{
		return true;
	}

	/**
	 * Удаляет все спауны из рефлекшена и запускает 5ти минутный коллапс-таймер.
	 */
	protected void clearReflection()
	{
		getReflection().clearReflection(COLLAPSE_AFTER_DEATH_TIME, true);
	}
}