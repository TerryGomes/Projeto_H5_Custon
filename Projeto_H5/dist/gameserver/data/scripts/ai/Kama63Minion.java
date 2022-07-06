package ai;

import java.util.concurrent.ScheduledFuture;

import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.CtrlEvent;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.scripts.Functions;

/**
 * АИ для камалоки 63 уровня.
 * Каждые 30 секунд босс призывает миньона, который через 25 секунд совершает суицид и восстанавливает здоровье
 * боса.
 * @author SYS
 */
public class Kama63Minion extends Fighter
{
	private static final int BOSS_ID = 18571;
	private static final int MINION_DIE_TIME = 25000;
	private long _wait_timeout = 0;
	private NpcInstance _boss;
	private boolean _spawned = false;
	ScheduledFuture<?> _dieTask = null;

	public Kama63Minion(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		_boss = findBoss(BOSS_ID);
		super.onEvtSpawn();
	}

	@Override
	protected boolean thinkActive()
	{
		if (_boss == null)
		{
			_boss = findBoss(BOSS_ID);
		}
		else if (!_spawned)
		{
			_spawned = true;
			Functions.npcSayCustomMessage(_boss, "Kama63Boss");
			NpcInstance minion = getActor();
			minion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, _boss.getAggroList().getRandomHated(), Rnd.get(1, 100));
			_dieTask = ThreadPoolManager.getInstance().schedule(new DieScheduleTimerTask(minion, _boss), MINION_DIE_TIME);
		}
		return super.thinkActive();
	}

	private NpcInstance findBoss(int npcId)
	{
		// Ищем боса не чаще, чем раз в 15 секунд, если по каким-то причинам его нету
		if (System.currentTimeMillis() < _wait_timeout)
		{
			return null;
		}

		_wait_timeout = System.currentTimeMillis() + 15000;

		NpcInstance minion = getActor();
		if (minion == null)
		{
			return null;
		}

		for (NpcInstance npc : World.getAroundNpc(minion))
		{
			if (npc.getNpcId() == npcId)
			{
				return npc;
			}
		}
		return null;
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		_spawned = false;
		if (_dieTask != null)
		{
			_dieTask.cancel(false);
			_dieTask = null;
		}
		super.onEvtDead(killer);
	}

	public class DieScheduleTimerTask extends RunnableImpl
	{
		NpcInstance _minion = null;
		NpcInstance _master = null;

		public DieScheduleTimerTask(NpcInstance minion, NpcInstance master)
		{
			_minion = minion;
			_master = master;
		}

		@Override
		public void runImpl()
		{
			if (_master != null && _minion != null && !_master.isDead() && !_minion.isDead())
			{
				_master.setCurrentHp(_master.getCurrentHp() + _minion.getCurrentHp() * 5, false);
			}
			Functions.npcSayCustomMessage(_minion, "Kama63Minion");
			_minion.doDie(_minion);
		}
	}
}