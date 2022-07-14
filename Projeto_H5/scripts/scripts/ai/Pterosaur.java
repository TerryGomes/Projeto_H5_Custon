package ai;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

public class Pterosaur extends DefaultAI
{
	private static final Location[] points =
	{
		new Location(3964, -7496, -3488),
		new Location(7093, -6207, -3447),
		new Location(7838, -7407, -3616),
		new Location(7155, -9208, -1467),
		new Location(7667, -10459, -3687),
		new Location(9431, -11590, -3979),
		new Location(8241, -13708, -3731),
		new Location(8417, -15135, -3698),
		new Location(7604, -15878, -3703),
		new Location(7835, -18087, -3564),
		new Location(7880, -20446, -3520),
		new Location(6889, -21556, -3430),
		new Location(5506, -21796, -3350),
		new Location(5350, -20690, -3511),
		new Location(3718, -19280, -3523),
		new Location(2819, -17029, -3583),
		new Location(2394, -14635, -3334),
		new Location(3169, -13397, -3609),
		new Location(2596, -11971, -3601),
		new Location(2040, -9636, -3546),
		new Location(2910, -7033, -3315),
		new Location(5099, -6510, -3396),
		new Location(5895, -8563, -3656),
		new Location(3970, -9894, -3684),
		new Location(5994, -10320, -3651),
		new Location(6468, -11106, -3660),
		new Location(7273, -18036, -3657),
		new Location(5827, -20411, -3527),
		new Location(4708, -18472, -3702),
		new Location(4104, -15834, -3609),
		new Location(5770, -15281, -3692),
		new Location(7596, -19798, -3631),
		new Location(10069, -22629, -3716),
		new Location(10015, -23379, -3714),
		new Location(8079, -22995, -3741),
		new Location(5846, -23514, -3756),
		new Location(5683, -24093, -3776),
		new Location(4663, -24953, -4166),
		new Location(7631, -25726, -4115),
		new Location(9875, -27738, -4417),
		new Location(11293, -27864, -4439),
		new Location(11058, -25030, -3688),
		new Location(11074, -23164, -3675),
		new Location(10370, -22899, -3704),
		new Location(9788, -24086, -3762),
		new Location(11039, -24780, -3669),
		new Location(11341, -23669, -3669),
		new Location(8189, -20399, -3500),
		new Location(6438, -20501, -3573),
		new Location(4972, -17586, -3728),
		new Location(6393, -13759, -3729),
		new Location(8841, -13530, -3891),
		new Location(9567, -12500, -3986),
		new Location(9023, -11165, -3996),
		new Location(7626, -11191, -3973),
		new Location(7341, -12035, -3937),
		new Location(11039, -24780, -3669),
		new Location(8234, -13204, -3986),
		new Location(9316, -12869, -3989),
		new Location(6935, -7852, -3685)
	};

	private int current_point = -1;
	private long wait_timeout = 0;
	private boolean wait = false;

	public Pterosaur(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		NpcInstance actor = getActor();
		actor.setFlying(true);
		actor.setHasChatWindow(false);
		super.onEvtSpawn();
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
			if (doTask())
			{
				clearTasks();
			}
			return true;
		}

		long now = System.currentTimeMillis();
		if (now > wait_timeout && (current_point > -1 || Rnd.chance(5)))
		{
			if (!wait)
			{
				switch (current_point)
				{
				case 0:
				case 8:
					wait_timeout = now + 10000;
					wait = false;
					return true;
				}
			}

			wait_timeout = 0;
			wait = true;
			current_point++;

			if (current_point >= points.length)
			{
				current_point = 0;
			}

			addTaskMove(points[current_point], false);
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