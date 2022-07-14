package instances;

import java.util.concurrent.ScheduledFuture;

import org.apache.commons.lang3.ArrayUtils;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.listener.actor.OnDeathListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.ExSendUIEvent;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.utils.Location;

/**
 * @author pchayka
 */

public class SufferingHallDefence extends Reflection
{
	private static final int AliveTumor = 18704;
	private static final int DeadTumor = 18705;
	private static final int Yehan = 25665;
	private static final int RegenerationCoffin = 18706;
	public int timeSpent;
	private int tumorIndex = 300;
	private boolean doCountCoffinNotifications = false;
	private static final int[] monsters =
	{
		22509,
		22510,
		22511,
		22512,
		22513,
		22514,
		22515,
		AliveTumor
	};
	private static final Location roomCenter = new Location(-173704, 218092, -9562, 27768);
	private long _savedTime = 0;
	private DeathListener _deathListener = new DeathListener();
	private ScheduledFuture<?> coffinSpawnTask;
	private ScheduledFuture<?> monstersSpawnTask;
	private int stage = 1;

	@Override
	protected void onCreate()
	{
		super.onCreate();
		_savedTime = System.currentTimeMillis();
		timeSpent = 0;
		startDefence();
	}

	private void startDefence()
	{
		spawnByGroup("soi_hos_defence_tumor");
		doCountCoffinNotifications = true;
		coffinSpawnTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				addSpawnWithoutRespawn(RegenerationCoffin, new Location(-173704, 218092, -9562, Location.getRandomHeading()), 250);
			}
		}, 1000L, 10000L);
		monstersSpawnTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				spawnMonsters();
			}
		}, 60000L);
	}

	private class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if (!self.isNpc())
			{
				return;
			}
			if (ArrayUtils.contains(monsters, self.getNpcId()) && !checkAliveMonsters())
			{
				if (monstersSpawnTask != null)
				{
					monstersSpawnTask.cancel(false);
				}
				monstersSpawnTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl()
				{
					@Override
					public void runImpl() throws Exception
					{
						spawnMonsters();
					}
				}, 40000L);
			}
			if (self.getNpcId() == AliveTumor)
			{
				self.deleteMe();
				addSpawnWithoutRespawn(DeadTumor, roomCenter, 0);
				tumorIndex = 300;
				doCountCoffinNotifications = true;
			}
			else if (self.getNpcId() == Yehan)
			{
				ThreadPoolManager.getInstance().schedule(new RunnableImpl()
				{
					@Override
					public void runImpl() throws Exception
					{
						if (monstersSpawnTask != null)
						{
							monstersSpawnTask.cancel(false);
						}
						if (coffinSpawnTask != null)
						{
							coffinSpawnTask.cancel(false);
						}
						clearReflection(5, true);
						spawnByGroup("soi_hos_defence_tepios");

						setReenterTime(System.currentTimeMillis());
						for (Player p : getPlayers())
						{
							p.sendPacket(new ExSendUIEvent(p, true, true, 0, 0));
						}

						timeSpent = (int) (System.currentTimeMillis() - _savedTime) / 1000;
					}
				}, 10000L);
			}
		}
	}

	private void invokeDeathListener()
	{
		for (NpcInstance npc : getNpcs())
		{
			npc.addListener(_deathListener);
		}
	}

	public void notifyCoffinActivity()
	{
		if (!doCountCoffinNotifications)
		{
			return;
		}
		tumorIndex -= 5;
		if (tumorIndex == 100)
		{
			for (Player p : getPlayers())
			{
				p.sendPacket(new ExShowScreenMessage(NpcString.THE_AREA_NEAR_THE_TUMOR_IS_FULL_OF_OMINOUS_ENERGY, 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false, 1, -1, false));
			}
		}
		else if (tumorIndex == 30)
		{
			for (Player p : getPlayers())
			{
				p.sendPacket(new ExShowScreenMessage(NpcString.YOU_CAN_FEEL_THE_SURGING_ENERGY_OF_DEATH_FROM_THE_TUMOR, 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false, 1, -1, false));
			}
		}
		if (tumorIndex <= 0)
		{
			if (getTumor(DeadTumor) != null)
			{
				getTumor(DeadTumor).deleteMe();
			}
			NpcInstance alivetumor = addSpawnWithoutRespawn(AliveTumor, roomCenter, 0);
			alivetumor.setCurrentHp(alivetumor.getMaxHp() * .4, false);
			doCountCoffinNotifications = false;
			invokeDeathListener();
		}
	}

	private void spawnMonsters()
	{
		if (stage > 6)
		{
			return;
		}
		String group = null;
		switch (stage)
		{
		case 1:
			group = "soi_hos_defence_mobs_1";
			getZone("[soi_hos_defence_attackup_1]").setActive(true);
			getZone("[soi_hos_defence_defenceup_1]").setActive(true);
			break;
		case 2:
			group = "soi_hos_defence_mobs_2";
			getZone("[soi_hos_defence_attackup_1]").setActive(false);
			getZone("[soi_hos_defence_defenceup_1]").setActive(false);
			getZone("[soi_hos_defence_attackup_2]").setActive(true);
			getZone("[soi_hos_defence_defenceup_2]").setActive(true);
			break;
		case 3:
			group = "soi_hos_defence_mobs_3";
			getZone("[soi_hos_defence_attackup_2]").setActive(false);
			getZone("[soi_hos_defence_defenceup_2]").setActive(false);
			getZone("[soi_hos_defence_attackup_3]").setActive(true);
			getZone("[soi_hos_defence_defenceup_3]").setActive(true);
			break;
		case 4:
			group = "soi_hos_defence_mobs_4";
			getZone("[soi_hos_defence_attackup_3]").setActive(false);
			getZone("[soi_hos_defence_defenceup_3]").setActive(false);
			getZone("[soi_hos_defence_attackup_4]").setActive(true);
			getZone("[soi_hos_defence_defenceup_4]").setActive(true);
			break;
		case 5:
			group = "soi_hos_defence_mobs_5";
			getZone("[soi_hos_defence_attackup_4]").setActive(false);
			getZone("[soi_hos_defence_defenceup_4]").setActive(false);
			getZone("[soi_hos_defence_attackup_5]").setActive(true);
			getZone("[soi_hos_defence_defenceup_5]").setActive(true);
			break;
		case 6:
			doCountCoffinNotifications = false;
			group = "soi_hos_defence_brothers";
			getZone("[soi_hos_defence_attackup_5]").setActive(false);
			getZone("[soi_hos_defence_defenceup_5]").setActive(false);
			break;
		default:
			break;
		}
		stage++;
		if (group != null)
		{
			spawnByGroup(group);
		}
		for (NpcInstance n : getNpcs())
		{
			if (n.isMonster() && ArrayUtils.contains(monsters, n.getNpcId()))
			{
				n.setRunning();
				n.moveToLocation(roomCenter, 200, false);
			}
		}
		invokeDeathListener();
	}

	private boolean checkAliveMonsters()
	{
		for (NpcInstance n : getNpcs())
		{
			if (ArrayUtils.contains(monsters, n.getNpcId()) && !n.isDead())
			{
				return true;
			}
		}
		return false;
	}

	private NpcInstance getTumor(int id)
	{
		for (NpcInstance npc : getNpcs())
		{
			if (npc.getNpcId() == id && !npc.isDead())
			{
				return npc;
			}
		}
		return null;
	}

	@Override
	public void onPlayerEnter(Player player)
	{
		super.onPlayerEnter(player);
		player.sendPacket(new ExSendUIEvent(player, false, true, (int) (System.currentTimeMillis() - _savedTime) / 1000, 0, NpcString.NONE));
	}

	@Override
	public void onPlayerExit(Player player)
	{
		super.onPlayerExit(player);
		player.sendPacket(new ExSendUIEvent(player, true, true, 0, 0));
	}

	@Override
	protected void onCollapse()
	{
		if (coffinSpawnTask != null)
		{
			coffinSpawnTask.cancel(false);
		}
		if (monstersSpawnTask != null)
		{
			monstersSpawnTask.cancel(false);
		}
		super.onCollapse();
	}

}