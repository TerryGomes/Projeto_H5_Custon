package ai.hellbound;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.NpcUtils;

/**
 * Darion Faithful Servant 6го этажа Tully Workshop
 * @автор pchayka, доработка VAVAN
 */
public class DarionFaithfulServant6Floor extends Fighter
{
	private static final int MysteriousAgent = 32372;

	public DarionFaithfulServant6Floor(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();

		if (Rnd.chance(15) && actor.isInZone("[tully6_room1]"))
		{
			NpcUtils.spawnSingle(MysteriousAgent, new Location(-13312, 279172, -13599, -20300), 600000L);
		}
		if (Rnd.chance(15) && actor.isInZone("[tully6_room2]"))
		{
			NpcUtils.spawnSingle(MysteriousAgent, new Location(-11696, 280208, -13599, 13244), 600000L);
		}
		if (Rnd.chance(15) && actor.isInZone("[tully6_room3]"))
		{
			NpcUtils.spawnSingle(MysteriousAgent, new Location(-13008, 280496, -13599, 27480), 600000L);
		}
		if (Rnd.chance(15) && actor.isInZone("[tully6_room4]"))
		{
			NpcUtils.spawnSingle(MysteriousAgent, new Location(-11984, 278880, -13599, -4472), 600000L);
		}
		super.onEvtDead(killer);
	}
}