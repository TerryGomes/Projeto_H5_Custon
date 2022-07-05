package l2f.gameserver.taskmanager;

import java.util.concurrent.Future;

import l2f.commons.threading.RunnableImpl;
import l2f.commons.threading.SteppingRunnableQueueManager;
import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.premium.PremiumEnd;

public class LazyPrecisionTaskManager extends SteppingRunnableQueueManager
{
	private static final LazyPrecisionTaskManager _instance = new LazyPrecisionTaskManager();

	public static final LazyPrecisionTaskManager getInstance()
	{
		return _instance;
	}

	private LazyPrecisionTaskManager()
	{
		super(1000L);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 1000L, 1000L);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				LazyPrecisionTaskManager.this.purge();
			}

		}, 60000L, 60000L);
	}

	public Future<?> addPCCafePointsTask(Player player)
	{
		long delay = Config.ALT_PCBANG_POINTS_DELAY * 60000L;

		return scheduleAtFixedRate(new RunnableImpl()
		{

			@Override
			public void runImpl() throws Exception
			{
				if (player.isInOfflineMode() || player.getLevel() < Config.ALT_PCBANG_POINTS_MIN_LVL)
				{
					return;
				}

				player.addPcBangPoints(Config.ALT_PCBANG_POINTS_BONUS, Config.ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE > 0 && Rnd.chance(Config.ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE));
			}

		}, delay, delay);
	}

	public Future<?> addVitalityRegenTask(Player player)
	{
		long delay = 60000L;

		return scheduleAtFixedRate(new RunnableImpl()
		{

			@Override
			public void runImpl() throws Exception
			{
				if (player.isInOfflineMode() || !player.isInPeaceZone())
				{
					return;
				}

				player.setVitality(player.getVitality() + 1);
			}

		}, delay, delay);
	}

	public Future<?> startBonusExpirationTask(Player player)
	{
		long delay = player.getBonus().getBonusExpire() * 1000L - System.currentTimeMillis();

		return schedule(new RunnableImpl()
		{

			@Override
			public void runImpl() throws Exception
			{
				PremiumEnd.getInstance().stopBonuses(player);
			}

		}, delay);
	}

	public Future<?> addNpcAnimationTask(NpcInstance npc)
	{
		return scheduleAtFixedRate(new RunnableImpl()
		{

			@Override
			public void runImpl() throws Exception
			{
				if (npc.isVisible() && !npc.isActionsDisabled() && !npc.isMoving && !npc.isInCombat())
				{
					npc.onRandomAnimation();
				}
			}

		}, 1000L, Rnd.get(Config.MIN_NPC_ANIMATION, Config.MAX_NPC_ANIMATION) * 1000L);
	}
}
