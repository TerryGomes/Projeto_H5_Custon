package ai.seedofdestruction;

import org.apache.commons.lang3.ArrayUtils;

import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

public class ThroneofDestruction extends DefaultAI
{
	private static final int DOOR = 12240031;
	private static final int TIAT_NPC_ID = 29163;
	private static final Location TIAT_LOC = new Location(-250403, 207273, -11952, 16384);
	private static final int[] checkNpcs =
	{
		18778,
		18777
	};

	public ThroneofDestruction(NpcInstance actor)
	{
		super(actor);
		actor.block();
		actor.startDamageBlocked();
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		Reflection ref = actor.getReflection();
		if (checkAllDestroyed(actor.getNpcId()))
		{
			ref.openDoor(DOOR);
			ref.addSpawnWithoutRespawn(TIAT_NPC_ID, TIAT_LOC, 0);
		}
		super.onEvtDead(killer);
	}

	/**
	 * Проверяет, уничтожены ли все Throne of Destruction Powerful Device в текущем измерении
	 * @return true если все уничтожены
	 */
	private boolean checkAllDestroyed(int mobId)
	{
		for (NpcInstance npc : getActor().getReflection().getNpcs())
		{
			if (ArrayUtils.contains(checkNpcs, npc.getNpcId()) && !npc.isDead())
			{
				return false;
			}
		}
		return true;
	}
}