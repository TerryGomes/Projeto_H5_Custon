package l2mv.gameserver.skills.skillclasses;

import java.util.List;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.ai.CtrlEvent;
import l2mv.gameserver.ai.CtrlIntention;
import l2mv.gameserver.geodata.GeoEngine;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.FlyToLocation;
import l2mv.gameserver.network.serverpackets.SystemMessage;
import l2mv.gameserver.network.serverpackets.ValidateLocation;
import l2mv.gameserver.stats.Stats;
import l2mv.gameserver.templates.StatsSet;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.PositionUtils;

/**
 * @author claww
 * @date 13.03.2015
 **/

public class InstantJump extends Skill
{
	public InstantJump(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		if (targets.size() == 0)
		{
			return;
		}

		Creature target = targets.get(0);
		if (Rnd.chance(target.calcStat(Stats.PSKILL_EVASION, 0, activeChar, this)))
		{
			if (activeChar.isPlayer())
			{
				activeChar.sendPacket(new SystemMessage(SystemMessage.C1_DODGES_THE_ATTACK).addName(target));
			}
			if (target.isPlayer())
			{
				target.sendPacket(new SystemMessage(SystemMessage.C1_HAS_EVADED_C2S_ATTACK).addName(target).addName(activeChar));
			}
			return;
		}
		int x, y, z;

		int px = target.getX();
		int py = target.getY();
		double ph = PositionUtils.convertHeadingToDegree(target.getHeading());

		ph += 180;

		if (ph > 360)
		{
			ph -= 360;
		}

		ph = (Math.PI * ph) / 180;

		x = (int) (px + (25 * Math.cos(ph)));
		y = (int) (py + (25 * Math.sin(ph)));
		z = target.getZ();

		Location loc = new Location(x, y, z);

		if (Config.ALLOW_GEODATA)
		{
			loc = GeoEngine.moveCheck(activeChar.getX(), activeChar.getY(), activeChar.getZ(), x, y, activeChar.getReflection().getGeoIndex());
		}

		if (target.isNpc())
		{
			NpcInstance npc = (NpcInstance) target;
			npc.abortAttack(true, true);
			npc.abortCast(true, true);
			npc.getAI().notifyEvent(CtrlEvent.EVT_THINK);
		}
		else
		{
			target.setTarget(null);
		}

		activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		activeChar.broadcastPacket(new FlyToLocation(activeChar, loc, FlyToLocation.FlyType.DUMMY));
		activeChar.abortAttack(true, true);
		// activeChar.abortCast(true, false); Creates a splitsecond cast block which really fucks daggers...
		activeChar.setXYZ(loc.x, loc.y, loc.z);
		activeChar.setHeading(target.getHeading());
		activeChar.broadcastPacket(new ValidateLocation(activeChar));
	}
}
