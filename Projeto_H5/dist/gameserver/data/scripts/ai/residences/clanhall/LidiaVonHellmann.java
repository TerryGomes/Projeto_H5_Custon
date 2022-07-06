package ai.residences.clanhall;

import ai.residences.SiegeGuardFighter;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.utils.PositionUtils;

/**
 * @author VISTALL
 * @date 18:22/10.05.2011
 */
public class LidiaVonHellmann extends SiegeGuardFighter
{
	private static final Skill DRAIN_SKILL = SkillTable.getInstance().getInfo(4999, 1);
	private static final Skill DAMAGE_SKILL = SkillTable.getInstance().getInfo(4998, 1);

	public LidiaVonHellmann(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public void onEvtSpawn()
	{
		super.onEvtSpawn();

		Functions.npcShout(getActor(), NpcString.HMM_THOSE_WHO_ARE_NOT_OF_THE_BLOODLINE_ARE_COMING_THIS_WAY_TO_TAKE_OVER_THE_CASTLE__HUMPH__THE_BITTER_GRUDGES_OF_THE_DEAD);
	}

	@Override
	public void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);

		Functions.npcShout(getActor(), NpcString.GRARR_FOR_THE_NEXT_2_MINUTES_OR_SO_THE_GAME_ARENA_ARE_WILL_BE_CLEANED);
	}

	@Override
	public void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();

		super.onEvtAttacked(attacker, damage);

		if (Rnd.chance(0.22))
		{
			addTaskCast(attacker, DRAIN_SKILL);
		}
		else if (actor.getCurrentHpPercents() < 20 && Rnd.chance(0.22))
		{
			addTaskCast(attacker, DRAIN_SKILL);
		}

		if (PositionUtils.calculateDistance(actor, attacker, false) > 300 && Rnd.chance(0.13))
		{
			addTaskCast(attacker, DAMAGE_SKILL);
		}
	}
}
