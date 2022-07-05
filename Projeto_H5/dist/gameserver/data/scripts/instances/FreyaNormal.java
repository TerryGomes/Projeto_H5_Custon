package instances;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

import l2f.commons.geometry.Polygon;
import l2f.commons.threading.RunnableImpl;
import l2f.commons.util.Rnd;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2f.gameserver.listener.actor.OnDeathListener;
import l2f.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.Territory;
import l2f.gameserver.model.Zone;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.network.serverpackets.EventTrigger;
import l2f.gameserver.network.serverpackets.ExChangeClientEffectInfo;
import l2f.gameserver.network.serverpackets.ExSendUIEvent;
import l2f.gameserver.network.serverpackets.ExShowScreenMessage;
import l2f.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2f.gameserver.network.serverpackets.ExStartScenePlayer;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.network.serverpackets.components.NpcString;
import l2f.gameserver.utils.Location;
import quests._10286_ReunionWithSirra;

public class FreyaNormal extends Reflection
{
	private static final int FreyaThrone = 29177;
	private static final int FreyaStandNormal = 29179;
	private static final int IceKnightNormal = 18855; // state 1 - in ice, state 2 - ice shattering, then normal state
	private static final int IceKnightLeaderNormal = 25699;
	private static final int IceCastleBreath = 18854;
	private static final int Glacier = 18853; // state 1 - falling, state 2 - waiting
	private static final int IceCastleController = 18932; // state 1-7
	private static final int Sirra = 32762;
	private static final int Jinia = 18850;
	private static final int Kegor = 18851;

	private static final int[] _eventTriggers =
	{
		23140202,
		23140204,
		23140206,
		23140208,
		23140212,
		23140214,
		23140216
	};

	private Zone damagezone, attackUp, pcbuff, pcbuff2;

	private ScheduledFuture<?> firstStageGuardSpawn;
	private ScheduledFuture<?> secondStageGuardSpawn;
	private ScheduledFuture<?> thirdStageGuardSpawn;

	private final ZoneListener _epicZoneListener = new ZoneListener();
	private final ZoneListenerL _landingZoneListener = new ZoneListenerL();
	private final DeathListener _deathListener = new DeathListener();
	private final CurrentHpListener _currentHpListener = new CurrentHpListener();

	private boolean _entryLocked = false;
	private boolean _startLaunched = false;
	private boolean _freyaSlayed = false;

	private final AtomicInteger raidplayers = new AtomicInteger();

	private static Territory centralRoom = new Territory().add(new Polygon().add(114264, -113672).add(113640, -114344).add(113640, -115240).add(114264, -115912).add(115176, -115912).add(115800, -115272).add(115800, -114328).add(115192, -113672).setZmax(-11225).setZmin(-11225));

	@Override
	protected void onCreate()
	{
		super.onCreate();

		attackUp = getZone("[freya_attack_up]");
		pcbuff = getZone("[freya_pc_buff1]");
		pcbuff2 = getZone("[freya_pc_buff2]");
		getZone("[freya_normal_epic]").addListener(_epicZoneListener);
		getZone("[freya_landing_room_epic]").addListener(_landingZoneListener);
	}

	private void manageDamageZone(int level, boolean disable)
	{
		if (disable)
		{
			damagezone.setActive(false);
			return;
		}
		if (damagezone != null)
		{
			damagezone.setActive(false);
		}

		switch (level)
		{
		case 1:
			damagezone = getZone("[freya_normal_freezing_01]");
			break;
		case 2:
			damagezone = getZone("[freya_normal_freezing_02]");
			break;
		case 3:
			damagezone = getZone("[freya_normal_freezing_03]");
			break;
		case 4:
			damagezone = getZone("[freya_normal_freezing_04]");
			break;
		case 5:
			damagezone = getZone("[freya_normal_freezing_05]");
			break;
		case 6:
			damagezone = getZone("[freya_normal_freezing_06]");
			break;
		case 7:
			damagezone = getZone("[freya_normal_freezing_07]");
			break;
		default:
			break;
		}
		if (damagezone != null)
		{
			damagezone.setActive(true);
		}
	}

	private void manageAttackUpZone(boolean disable)
	{
		if (attackUp != null && disable)
		{
			attackUp.setActive(false);
			return;
		}
		if (attackUp != null)
		{
			attackUp.setActive(true);
		}
	}

	private void managePcBuffZone(boolean disable)
	{
		if (pcbuff != null && pcbuff2 != null && disable)
		{
			pcbuff.setActive(false);
			pcbuff2.setActive(false);
			return;
		}
		if (pcbuff != null)
		{
			pcbuff.setActive(true);
		}
		if (pcbuff2 != null)
		{
			pcbuff2.setActive(true);
		}
	}

	private void manageCastleController(int state)
	{
		// 1-7 enabled, 8 - disabled
		for (NpcInstance n : getNpcs())
		{
			if (n.getNpcId() == IceCastleController)
			{
				n.setNpcState(state);
			}
		}
	}

	private void manageStorm(boolean active)
	{
		for (Player p : getPlayers())
		{
			for (int _eventTrigger : _eventTriggers)
			{
				p.sendPacket(new EventTrigger(_eventTrigger, active));
			}
		}
	}

	private class StartNormalFreya extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			_entryLocked = true;
			closeDoor(23140101);
			for (Player player : getPlayers())
			{
				QuestState qs = player.getQuestState(_10286_ReunionWithSirra.class);
				if (qs != null && qs.getCond() == 5)
				{
					qs.setCond(6);
				}
				player.showQuestMovie(ExStartScenePlayer.SCENE_BOSS_FREYA_OPENING);
			}
			ThreadPoolManager.getInstance().schedule(new PreStage(), 55000L); // 53.5sec for movie
		}
	}

	private class PreStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			manageDamageZone(1, false);
			// screen message
			for (Player player : getPlayers())
			{
				player.sendPacket(new ExShowScreenMessage(NpcString.BEGIN_STAGE_1_FREYA, 6000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
			}
			// spawning few guards
			for (int i = 0; i < 10; i++)
			{
				addSpawnWithoutRespawn(IceKnightNormal, Territory.getRandomLoc(centralRoom, getGeoIndex()), 0);
			}
			ThreadPoolManager.getInstance().schedule(new FirstStage(), 40000L);
		}
	}

	private class FirstStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			manageCastleController(1);
			manageDamageZone(2, false);
			for (Player player : getPlayers())
			{
				player.sendPacket(new ExShowScreenMessage(NpcString.FREYA_HAS_STARTED_TO_MOVE, 4000, ScreenMessageAlign.MIDDLE_CENTER, true));
			}
			// Spawning Freya Throne
			NpcInstance freyaTrhone = addSpawnWithoutRespawn(FreyaThrone, new Location(114720, -117085, -11088, 15956), 0);
			freyaTrhone.addListener(_deathListener);
			firstStageGuardSpawn = ThreadPoolManager.getInstance().scheduleAtFixedRate(new GuardSpawnTask(1), 2000L, 30000L);
		}
	}

	private class GuardSpawnTask extends RunnableImpl
	{
		int _mode, _knightsMin, _knightsMax, _breathMin, _breathMax;

		public GuardSpawnTask(int mode) // 1 - light, 2 - normal, 3 - hard, 4 - extreme
		{
			_mode = mode;
			if (_mode < 1 || _mode > 4)
			{
				_mode = 1;
			}
		}

		@Override
		public void runImpl() throws Exception
		{
			switch (_mode)
			{
			case 1:
				_knightsMin = 2;
				_knightsMax = 3;
				_breathMin = 1;
				_breathMax = 2;
				break;
			case 2:
				_knightsMin = 2;
				_knightsMax = 4;
				_breathMin = 1;
				_breathMax = 3;
				break;
			case 3:
				_knightsMin = 3;
				_knightsMax = 8;
				_breathMin = 2;
				_breathMax = 4;
				break;
			case 4:
				_knightsMin = 6;
				_knightsMax = 10;
				_breathMin = 3;
				_breathMax = 7;
				break;
			default:
				break;
			}
			for (int i = 0; i < Rnd.get(_knightsMin, _knightsMax); i++)
			{
				addSpawnWithoutRespawn(IceKnightNormal, Territory.getRandomLoc(centralRoom, getGeoIndex()), 0);
			}
			for (int i = 0; i < Rnd.get(_breathMin, _breathMax); i++)
			{
				addSpawnWithoutRespawn(IceCastleBreath, Territory.getRandomLoc(centralRoom, getGeoIndex()), 0);
			}
			if (Rnd.chance(60))
			{
				for (int i = 0; i < Rnd.get(1, 3); i++)
				{
					addSpawnWithoutRespawn(Glacier, Territory.getRandomLoc(centralRoom, getGeoIndex()), 0);
				}
			}
		}
	}

	private class PreSecondStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			firstStageGuardSpawn.cancel(true);
			for (NpcInstance n : getNpcs())
			{
				if (n.getNpcId() != Sirra && n.getNpcId() != IceCastleController)
				{
					n.deleteMe();
				}
			}

			for (Player p : getPlayers())
			{
				p.showQuestMovie(ExStartScenePlayer.SCENE_BOSS_FREYA_PHASE_A);
			}
			ThreadPoolManager.getInstance().schedule(new TimerToSecondStage(), 22000L); // 22.1 secs for movie
		}
	}

	private class TimerToSecondStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for (Player p : getPlayers())
			{
				p.sendPacket(new ExSendUIEvent(p, false, false, 60, 0, NpcString.TIME_REMAINING_UNTIL_NEXT_BATTLE));
			}
			ThreadPoolManager.getInstance().schedule(new SecondStage(), 60000L);
		}
	}

	private class SecondStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			manageCastleController(3);
			manageDamageZone(3, false);
			for (Player p : getPlayers())
			{
				p.sendPacket(new ExShowScreenMessage(NpcString.BEGIN_STAGE_2_FREYA, 6000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
			}
			secondStageGuardSpawn = ThreadPoolManager.getInstance().scheduleAtFixedRate(new GuardSpawnTask(2), 2000L, 30000L);
			ThreadPoolManager.getInstance().schedule(new KnightCaptainSpawnMovie(), 60000L);
		}
	}

	private class KnightCaptainSpawnMovie extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for (NpcInstance n : getNpcs())
			{
				n.block();
			}
			for (Player p : getPlayers())
			{
				p.showQuestMovie(ExStartScenePlayer.SCENE_ICE_HEAVYKNIGHT_SPAWN);
			}
			ThreadPoolManager.getInstance().schedule(new KnightCaptainSpawn(), 7500L);
		}
	}

	private class KnightCaptainSpawn extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			manageDamageZone(4, false);
			for (NpcInstance n : getNpcs())
			{
				n.unblock();
			}
			NpcInstance knightLeader = addSpawnWithoutRespawn(IceKnightLeaderNormal, new Location(114707, -114799, -11199, 15956), 0);
			knightLeader.addListener(_deathListener);
		}
	}

	private class PreThirdStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for (Player p : getPlayers())
			{
				p.sendPacket(new ExSendUIEvent(p, false, false, 60, 0, NpcString.TIME_REMAINING_UNTIL_NEXT_BATTLE));
			}
			secondStageGuardSpawn.cancel(true);
			for (NpcInstance n : getNpcs())
			{
				if (n.getNpcId() != Sirra && n.getNpcId() != IceCastleController)
				{
					n.deleteMe();
				}
			}
			ThreadPoolManager.getInstance().schedule(new PreThirdStageM(), 60000L);
		}
	}

	private class PreThirdStageM extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for (Player p : getPlayers())
			{
				p.showQuestMovie(ExStartScenePlayer.SCENE_BOSS_FREYA_PHASE_B);
			}
			ThreadPoolManager.getInstance().schedule(new ThirdStage(), 22000L); // 21.5 secs for movie
		}
	}

	private class ThirdStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			manageCastleController(4);
			manageAttackUpZone(false);
			manageDamageZone(5, false);
			manageStorm(true);
			for (Player p : getPlayers())
			{
				p.sendPacket(new ExShowScreenMessage(NpcString.BEGIN_STAGE_3_FREYA, 6000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
				p.sendPacket(new ExChangeClientEffectInfo(2));
			}
			thirdStageGuardSpawn = ThreadPoolManager.getInstance().scheduleAtFixedRate(new GuardSpawnTask(3), 2000L, 30000L);
			NpcInstance freyaStand = addSpawnWithoutRespawn(FreyaStandNormal, new Location(114720, -117085, -11088, 15956), 0);
			freyaStand.addListener(_currentHpListener);
			freyaStand.addListener(_deathListener);
		}
	}

	private class PreForthStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for (NpcInstance n : getNpcs())
			{
				n.block();
			}
			for (Player p : getPlayers())
			{
				p.block();
				p.showQuestMovie(ExStartScenePlayer.SCENE_BOSS_KEGOR_INTRUSION);
			}
			ThreadPoolManager.getInstance().schedule(new ForthStage(), 28000L); // 27 secs for movie
		}
	}

	private class ForthStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for (NpcInstance n : getNpcs())
			{
				n.unblock();
			}
			for (Player p : getPlayers())
			{
				p.unblock();
				p.sendPacket(new ExShowScreenMessage(NpcString.BEGIN_STAGE_4_FREYA, 6000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
			}
			addSpawnWithoutRespawn(Jinia, new Location(114727, -114700, -11200, -16260), 0);
			addSpawnWithoutRespawn(Kegor, new Location(114690, -114700, -11200, -16260), 0);
			managePcBuffZone(false);
		}
	}

	private class FreyaDeathStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			setReenterTime(System.currentTimeMillis());
			// Guard spawn task cancellation
			thirdStageGuardSpawn.cancel(true);
			// switching off zones
			manageDamageZone(1, true);
			manageAttackUpZone(true);
			managePcBuffZone(true);
			// Deleting all NPCs + Freya corpse
			for (NpcInstance n : getNpcs())
			{
				n.deleteMe();
			}
			// Movie + quest update
			for (Player p : getPlayers())
			{
				QuestState qs = p.getQuestState(_10286_ReunionWithSirra.class);
				if (qs != null && qs.getCond() == 6)
				{
					qs.setCond(7);
				}
				p.showQuestMovie(ExStartScenePlayer.SCENE_BOSS_FREYA_ENDING_A);
			}
			ThreadPoolManager.getInstance().schedule(new ConclusionMovie(), 16200L); // 16 secs for movie
		}
	}

	private class ConclusionMovie extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for (Player p : getPlayers())
			{
				p.showQuestMovie(ExStartScenePlayer.SCENE_BOSS_FREYA_ENDING_B);
			}
			ThreadPoolManager.getInstance().schedule(new InstanceConclusion(), 57000L); // 56 secs for movie
		}
	}

	private class InstanceConclusion extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			startCollapseTimer(5 * 60 * 1000L);
			doCleanup();
			for (Player p : getPlayers())
			{
				p.sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(5));
			}
		}
	}

	private class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if (self.isNpc() && self.getNpcId() == FreyaThrone)
			{
				ThreadPoolManager.getInstance().schedule(new PreSecondStage(), 10);
				self.deleteMe();
			}
			else if (self.isNpc() && self.getNpcId() == IceKnightLeaderNormal)
			{
				ThreadPoolManager.getInstance().schedule(new PreThirdStage(), 10);
			}
			else if (self.isNpc() && self.getNpcId() == FreyaStandNormal)
			{
				ThreadPoolManager.getInstance().schedule(new FreyaDeathStage(), 10);
			}
		}
	}

	public class CurrentHpListener implements OnCurrentHpDamageListener
	{
		@Override
		public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill)
		{
			if (actor == null || actor.isDead() || actor.getNpcId() != FreyaStandNormal)
			{
				return;
			}
			double newHp = actor.getCurrentHp() - damage;
			double maxHp = actor.getMaxHp();
			if (!_freyaSlayed && newHp <= 0.2 * maxHp)
			{
				_freyaSlayed = true;
				ThreadPoolManager.getInstance().schedule(new PreForthStage(), 10);
				actor.removeListener(_currentHpListener);
			}
		}
	}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			if (_entryLocked)
			{
				return;
			}

			Player player = cha.getPlayer();
			if (player == null || !cha.isPlayer())
			{
				return;
			}

			if (checkstartCond(raidplayers.incrementAndGet()))
			{
				ThreadPoolManager.getInstance().schedule(new StartNormalFreya(), 30000L);
				_startLaunched = true;
			}
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
		}

		@Override
		public void onEquipChanged(Zone zone, Creature actor)
		{
		}
	}

	public class ZoneListenerL implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			if (cha.isPlayer())
			{
				cha.sendPacket(new ExChangeClientEffectInfo(1));
			}
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
		}

		@Override
		public void onEquipChanged(Zone zone, Creature actor)
		{
		}
	}

	private boolean checkstartCond(int raidplayers)
	{
		return !(raidplayers < getInstancedZone().getMinParty() || _startLaunched);
	}

	private void doCleanup()
	{
		if (firstStageGuardSpawn != null)
		{
			firstStageGuardSpawn.cancel(true);
		}
		if (secondStageGuardSpawn != null)
		{
			secondStageGuardSpawn.cancel(true);
		}
		if (thirdStageGuardSpawn != null)
		{
			thirdStageGuardSpawn.cancel(true);
		}
	}

	@Override
	protected void onCollapse()
	{
		super.onCollapse();
		doCleanup();
	}
}