package ai.dragonvalley;

import org.apache.commons.lang3.ArrayUtils;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.Fighter;
import l2f.gameserver.model.instances.MonsterInstance;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.tables.SkillTable;
import l2f.gameserver.utils.Location;

/**
 * @author FandC
 */

public class Patrollers extends Fighter
{
	protected Location[] _points;
	private final int[] _teleporters =
	{
		22857,
		22833,
		22834
	};
	private int _lastPoint = 0;
	private boolean _firstThought = true;
	private volatile boolean _moving = false;

	public Patrollers(NpcInstance actor)
	{
		super(actor);
		MAX_PURSUE_RANGE = Integer.MAX_VALUE - 10;
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected void thinkAttack()
	{
		final NpcInstance actor = getActor();
		if (actor.isDead())
		{
			return;
		}
		if (doTask() && !actor.isAttackingNow() && !actor.isCastingNow())
		{
			if (!createNewTask())
			{
				if (System.currentTimeMillis() > getAttackTimeout())
				{
					returnHome();
				}
			}
		}
	}

	@Override
	protected boolean thinkActive()
	{
		if (super.thinkActive())
		{
			return true;
		}

		if (!getActor().isMoving)
		{
			startMoveTask();
		}

		return true;
	}

	private void startMoveTask()
	{
		if (_moving)
		{
			return;
		}
		try
		{
			_moving = true;
			final NpcInstance npc = getActor();
			if (_firstThought)
			{
				_lastPoint = getIndex(Location.findNearest(npc, _points));
				_firstThought = false;
			}
			else
			{
				_lastPoint++;
			}
			if (_lastPoint >= _points.length)
			{
				_lastPoint = 0;
				if (ArrayUtils.contains(_teleporters, npc.getNpcId()))
				{
					npc.teleToLocation(_points[_lastPoint]);
				}
			}
			npc.setRunning();
			if (Rnd.chance(30))
			{
				npc.altOnMagicUseTimer(npc, SkillTable.getInstance().getInfo(6757, 1));
			}
			addTaskMove(Location.findPointToStay(_points[_lastPoint], 450, npc.getGeoIndex()), true);
			if (npc.isMonster())
			{
				final MonsterInstance _monster = (MonsterInstance) npc;
				if (_monster.getMinionList() != null && _monster.getMinionList().hasMinions())
				{
					for (final NpcInstance _npc : _monster.getMinionList().getAliveMinions())
					{
						_npc.setRunning();
						((Fighter) _npc.getAI()).addTaskMove(Location.findPointToStay(_points[_lastPoint], 250, _npc.getGeoIndex()), true);
					}
				}
			}
			doTask();
		}
		finally
		{
			_moving = false;
		}
	}

	private int getIndex(Location loc)
	{
		for (int i = 0; i < _points.length; i++)
		{
			if (_points[i] == loc)
			{
				return i;
			}
		}
		return 0;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected boolean maybeMoveToHome()
	{
		return false;
	}

	@Override
	protected void teleportHome()
	{
	}

	@Override
	protected void returnHome(boolean clearAggro, boolean teleport)
	{
		super.returnHome(clearAggro, teleport);
		clearTasks();
		_firstThought = true;
		startMoveTask();
	}
}
