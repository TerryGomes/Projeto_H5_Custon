package ai;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;

public class NihilInvaderChest extends DefaultAI
{
	private static int[] _firstLevelItems =
	{
		4039,
		4040,
		4041,
		4042,
		4043,
		4044
	};
	private static int[] _secondLevelItems =
	{
		9628,
		9629,
		9630
	};

	public NihilInvaderChest(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (actor.getNpcId() == 18820)
		{
			if (Rnd.chance(40))
			{
				actor.broadcastPacket(new MagicSkillUse(actor, actor, 2025, 1, 0, 10));
				actor.dropItem(attacker.getPlayer(), _firstLevelItems[Rnd.get(0, _firstLevelItems.length - 1)], Rnd.get(10, 20));
				actor.doDie(null);
			}
		}
		else if (actor.getNpcId() == 18823)
		{
			if (Rnd.chance(40))
			{
				actor.broadcastPacket(new MagicSkillUse(actor, actor, 2025, 1, 0, 10));
				actor.dropItem(attacker.getPlayer(), _secondLevelItems[Rnd.get(0, _secondLevelItems.length - 1)], Rnd.get(10, 20));
				actor.doDie(null);
			}
		}
		for (NpcInstance npc : actor.getReflection().getNpcs())
		{
			if (npc.getNpcId() == actor.getNpcId())
			{
				npc.deleteMe();
			}
		}

		super.onEvtAttacked(attacker, damage);
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
	}

}