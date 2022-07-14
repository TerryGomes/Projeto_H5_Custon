package ai;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.utils.Location;

public class Taurin extends DefaultAI
{
	static final Location[] points =
	{
		new Location(80752, 146400, -3533),
		new Location(80250, 146988, -3559),
		new Location(80070, 146942, -3559),
		new Location(80048, 146705, -3547),
		new Location(79784, 146561, -3546),
		new Location(79476, 146800, -3547),
		new Location(79490, 147480, -3559),
		new Location(79812, 148310, -3559),
		new Location(79692, 148564, -3559),
		new Location(77569, 148495, -3623),
		new Location(77495, 148191, -3622),
		new Location(77569, 148495, -3623),
		new Location(79819, 148740, -3559),
		new Location(79773, 149110, -3559),
		new Location(79291, 149523, -3559),
		new Location(79569, 150214, -3548),
		new Location(79679, 150717, -3543),
		new Location(80106, 150630, -3547),
		new Location(81207, 150276, -3559),
		new Location(81820, 150666, -3559),
		new Location(82038, 150589, -3559),
		new Location(82394, 149943, -3559),
		new Location(82038, 150589, -3559),
		new Location(81820, 150666, -3559),
		new Location(81582, 150590, -3559),
		new Location(81535, 149653, -3495),
		new Location(83814, 148630, -3420),
		new Location(87001, 148637, -3428),
		new Location(83814, 148630, -3420),
		new Location(82921, 148467, -3495),
		new Location(82060, 148070, -3495),
		new Location(82060, 148070, -3495),
		new Location(82060, 148070, -3495),
		new Location(82060, 148070, -3495),
		new Location(81544, 147514, -3491),
		new Location(81691, 146578, -3559),
		new Location(83190, 146687, -3491),
		new Location(81691, 146578, -3559),
		new Location(81331, 146915, -3559),
		new Location(81067, 146925, -3559),
		new Location(80752, 146400, -3533)
	};

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public Taurin(NpcInstance actor)
	{
		super(actor);
		AI_TASK_ATTACK_DELAY = 250;
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (actor.isDead())
		{
			return true;
		}

		if (_def_think)
		{
			doTask();
			return true;
		}

		if (System.currentTimeMillis() > wait_timeout && (current_point > -1 || Rnd.chance(5)))
		{
			if (!wait)
			{
				switch (current_point)
				{
				// Это паузы на определеных точках в милисекундах
				// (case 3:) Это номер точки на которой делать паузу
				// npcSayInRange - сказать в Зону
				// npcSayToAll - Сказать всем
				// npcSayToPlayer - Сказать определеному игроку
				// npcShout - просто сказать всем видимым
				// пользовать вот так например
				// Functions.npcShout(actor, "Всем лежать, у меня бомба!");
				// Использывание скиллов //
				// actor.broadcastPacket(new MagicSkillUse(actor, actor, _skillId, skilllevel, castTime, 0));
				// На примере 2025 Large Firework //
				// actor.broadcastPacket(new MagicSkillUse(actor, actor, 2025, 1, 500, 0));
				case 4:
					wait_timeout = System.currentTimeMillis() + 10000;
					wait = true;
					return true;
				case 10:
					wait_timeout = System.currentTimeMillis() + 10000;
					wait = true;
					return true;
				case 14:
					wait_timeout = System.currentTimeMillis() + 10000;
					wait = true;
					return true;
				case 16:
					wait_timeout = System.currentTimeMillis() + 10000;
					wait = true;
					return true;
				case 21:
					wait_timeout = System.currentTimeMillis() + 10000;
					wait = true;
					return true;
				case 27:
					wait_timeout = System.currentTimeMillis() + 10000;
					wait = true;
					return true;
				case 30:
					wait_timeout = System.currentTimeMillis() + 10000;
					Functions.npcSay(actor, "Everybody down, I have a bomb!");
					wait = true;
					return true;
				case 31:
					wait_timeout = System.currentTimeMillis() + 15000;
					Functions.npcSay(actor, "I'm sick, I can not vouch for yourself!!");
					wait = true;
					return true;
				case 32:
					wait_timeout = System.currentTimeMillis() + 15000;
					Functions.npcSay(actor, "Are you still here? I warned you!!");
					wait = true;
					return true;
				case 33:
					actor.broadcastPacket(new MagicSkillUse(actor, actor, 2025, 1, 500, 0));
					wait_timeout = System.currentTimeMillis() + 1000;
					wait = true;
					return true;
				case 35:
					wait_timeout = System.currentTimeMillis() + 10000;
					wait = true;
					return true;
				case 37:
					wait_timeout = System.currentTimeMillis() + 30000000;
					wait = true;
					return true;
				}
			}

			wait_timeout = 0;
			wait = false;
			current_point++;

			if (current_point >= points.length)
			{
				current_point = 0;
			}

			addTaskMove(points[current_point], true);
			doTask();
			return true;
		}

		if (randomAnimation())
		{
			return true;
		}

		return false;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
	}
}