package l2f.gameserver.model;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import l2f.commons.threading.RunnableImpl;
import l2f.commons.util.Rnd;
import l2f.gameserver.GameTimeController;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.ai.CtrlEvent;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.idfactory.IdFactory;
import l2f.gameserver.instancemanager.games.FishingChampionShipManager;
import l2f.gameserver.model.Skill.SkillType;
import l2f.gameserver.model.instances.MonsterInstance;
import l2f.gameserver.network.serverpackets.ExFishingEnd;
import l2f.gameserver.network.serverpackets.ExFishingHpRegen;
import l2f.gameserver.network.serverpackets.ExFishingStart;
import l2f.gameserver.network.serverpackets.ExFishingStartCombat;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.templates.FishTemplate;
import l2f.gameserver.utils.ItemFunctions;
import l2f.gameserver.utils.Location;

public class Fishing
{
	private final Player _fisher;

	public final static int FISHING_NONE = 0;
	public final static int FISHING_STARTED = 1;
	public final static int FISHING_WAITING = 2;
	public final static int FISHING_COMBAT = 3;

	private final AtomicInteger _state;

	private int _time;
	private int _stop;
	private int _gooduse;
	private int _anim;
	private int _combatMode = -1;
	private int _deceptiveMode;
	private int _fishCurHP;

	private FishTemplate _fish;
	private int _lureId;

	private Future<?> _fishingTask;

	private final Location _fishLoc = new Location();

	public Fishing(Player fisher)
	{
		_fisher = fisher;
		_state = new AtomicInteger(FISHING_NONE);
	}

	public void setFish(FishTemplate fish)
	{
		_fish = fish;
	}

	public void setLureId(int lureId)
	{
		_lureId = lureId;
	}

	public int getLureId()
	{
		return _lureId;
	}

	public void setFishLoc(Location loc)
	{
		_fishLoc.x = loc.x;
		_fishLoc.y = loc.y;
		_fishLoc.z = loc.z;
	}

	public Location getFishLoc()
	{
		return _fishLoc;
	}

	/**
	 * Начинаем рыбалку, запускаем задачу ожидания рыбешки
	 */
	public void startFishing()
	{
		if (!_state.compareAndSet(FISHING_NONE, FISHING_STARTED))
		{
			return;
		}

		_fisher.setFishing(true);
		_fisher.broadcastCharInfo();
		_fisher.broadcastPacket(new ExFishingStart(_fisher, _fish.getType(), _fisher.getFishLoc(), isNightLure(_lureId)));
		_fisher.sendPacket(SystemMsg.STARTS_FISHING);

		startLookingForFishTask();
	}

	/**
	 * Отменяем рыбалку, завершаем текущую задачу
	 */
	public void stopFishing()
	{
		if (_state.getAndSet(FISHING_NONE) == FISHING_NONE)
		{
			return;
		}

		stopFishingTask();

		_fisher.setFishing(false);
		_fisher.broadcastPacket(new ExFishingEnd(_fisher, false));
		_fisher.broadcastCharInfo();
		_fisher.sendPacket(SystemMsg.CANCELS_FISHING);
	}

	/**
	 * Заканчиваем рыбалку, в случае удачи или неудачи, завершаем текущую задачу
	 * @param win
	 */
	public void endFishing(boolean win)
	{
		if (!_state.compareAndSet(FISHING_COMBAT, FISHING_NONE))
		{
			return;
		}

		stopFishingTask();

		_fisher.setFishing(false);
		_fisher.broadcastPacket(new ExFishingEnd(_fisher, win));
		_fisher.broadcastCharInfo();
		_fisher.sendPacket(SystemMsg.ENDS_FISHING);

		if (win)
		{
			_fisher.getCounters().fishCaught++;
		}

		// Synerge - Add one fish captured to the stats if succesful
//		if (win)
//			_fisher.addPlayerStats(Ranking.STAT_TOP_FISHES_CAPTURED);
	}

	private void stopFishingTask()
	{
		if (_fishingTask != null)
		{
			_fishingTask.cancel(false);
			_fishingTask = null;
		}
	}

	/** LookingForFishTask */
	protected class LookingForFishTask extends RunnableImpl
	{
		private final long _endTaskTime;

		protected LookingForFishTask()
		{
			_endTaskTime = System.currentTimeMillis() + _fish.getWaitTime() + 10000L;
		}

		@Override
		public void runImpl()
		{
			if ((System.currentTimeMillis() >= _endTaskTime) || (!GameTimeController.getInstance().isNowNight() && isNightLure(_lureId)))
			{
				_fisher.sendPacket(SystemMsg.BAITS_HAVE_BEEN_LOST_BECAUSE_THE_FISH_GOT_AWAY);
				stopFishingTask();
				endFishing(false);
				return;
			}

			int check = Rnd.get(1000);

			if (_fish.getFishGuts() > check)
			{
				stopFishingTask();
				startFishCombat();
			}
		}
	}

	private void startLookingForFishTask()
	{
		if (!_state.compareAndSet(FISHING_STARTED, FISHING_WAITING))
		{
			return;
		}

		long checkDelay = 10000L;

		switch (_fish.getGroup())
		{
		case 0:
			checkDelay = Math.round(_fish.getGutsCheckTime() * 1.33);
			break;
		case 1:
			checkDelay = _fish.getGutsCheckTime();
			break;
		case 2:
			checkDelay = Math.round(_fish.getGutsCheckTime() * 0.66);
			break;
		}

		_fishingTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new LookingForFishTask(), 10000L, checkDelay);
	}

	private class FishCombatTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if (_fishCurHP >= _fish.getHP() * 2)
			{
				// The fish got away
				_fisher.sendPacket(SystemMsg.THE_FISH_GOT_AWAY);
				doDie(false);
			}
			else if (_time <= 0)
			{
				// Time is up, so that fish got away
				_fisher.sendPacket(SystemMsg.TIME_IS_UP_SO_THAT_FISH_GOT_AWAY);
				doDie(false);
			}
			else
			{
				_time--;

				if (_combatMode == 1 && _deceptiveMode == 0 || _combatMode == 0 && _deceptiveMode == 1)
				{
					_fishCurHP += _fish.getHpRegen();
				}

				if (_stop == 0)
				{
					_stop = 1;
					if (Rnd.chance(30))
					{
						_combatMode = _combatMode == 0 ? 1 : 0;
					}

					if (_fish.getGroup() == 2)
					{
						if (Rnd.chance(10))
						{
							_deceptiveMode = _deceptiveMode == 0 ? 1 : 0;
						}
					}
				}
				else
				{
					_stop--;
				}

				ExFishingHpRegen efhr = new ExFishingHpRegen(_fisher, _time, _fishCurHP, _combatMode, 0, _anim, 0, _deceptiveMode);
				if (_anim != 0)
				{
					_fisher.broadcastPacket(efhr);
				}
				else
				{
					_fisher.sendPacket(efhr);
				}
			}
		}
	}

	public boolean isInCombat()
	{
		return _state.get() == FISHING_COMBAT;
	}

	private void startFishCombat()
	{
		if (!_state.compareAndSet(FISHING_WAITING, FISHING_COMBAT))
		{
			return;
		}

		_stop = 0;
		_gooduse = 0;
		_anim = 0;
		_time = _fish.getCombatTime() / 1000;
		_fishCurHP = _fish.getHP();
		_combatMode = Rnd.chance(20) ? 1 : 0;

		switch (getLureGrade(_lureId))
		{
		case 0:
		case 1:
			_deceptiveMode = 0;
			break;
		case 2:
			_deceptiveMode = Rnd.chance(10) ? 1 : 0;
			break;
		}

		ExFishingStartCombat efsc = new ExFishingStartCombat(_fisher, _time, _fish.getHP(), _combatMode, _fish.getGroup(), _deceptiveMode);
		_fisher.broadcastPacket(efsc);
		_fisher.sendPacket(SystemMsg.SUCCEEDED_IN_GETTING_A_BITE);

		_fishingTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new FishCombatTask(), 1000L, 1000L);
	}

	private void changeHp(int hp, int pen)
	{
		_fishCurHP -= hp;
		if (_fishCurHP < 0)
		{
			_fishCurHP = 0;
		}

		_fisher.broadcastPacket(new ExFishingHpRegen(_fisher, _time, _fishCurHP, _combatMode, _gooduse, _anim, pen, _deceptiveMode));

		_gooduse = 0;
		_anim = 0;
		if (_fishCurHP > _fish.getHP() * 2)
		{
			_fishCurHP = _fish.getHP() * 2;
			doDie(false);
		}
		else if (_fishCurHP == 0)
		{
			doDie(true);
		}
	}

	private void doDie(boolean win)
	{
		stopFishingTask();

		boolean isMonster = false;
		if (win)
		{
			if (!_fisher.isInPeaceZone() && Rnd.chance(5))
			{
				win = false;
				_fisher.sendPacket(SystemMsg.YOU_HAVE_CAUGHT_A_MONSTER);
				spawnPenaltyMonster(_fisher);
				isMonster = true;
			}
			else
			{
				_fisher.sendPacket(SystemMsg.SUCCEEDED_IN_FISHING);
				// TODO [G1ta0] добавить проверку на перевес
				ItemFunctions.addItem(_fisher, _fish.getId(), 1, true, "Fishing");
				FishingChampionShipManager.getInstance().newFish(_fisher, _lureId);
			}
		}

		// Synerge - Fish died listener
		_fisher.getListeners().onFishDied(win ? _fish.getId() : 0, isMonster);

		endFishing(win);
	}

	public void useFishingSkill(int dmg, int pen, SkillType skillType)
	{
		if (!isInCombat())
		{
			return;
		}

		int mode;
		if (skillType == SkillType.REELING && !GameTimeController.getInstance().isNowNight())
		{
			mode = 1;
		}
		else if (skillType == SkillType.PUMPING && GameTimeController.getInstance().isNowNight())
		{
			mode = 1;
		}
		else
		{
			mode = 0;
		}

		_anim = mode + 1;
		if (Rnd.chance(10))
		{
			_fisher.sendPacket(SystemMsg.FISH_HAS_RESISTED);
			_gooduse = 0;
			changeHp(0, pen);
			return;
		}

		if (_combatMode == mode)
		{
			if (_deceptiveMode == 0)
			{
				showMessage(_fisher, dmg, pen, skillType, 1);
				_gooduse = 1;
				changeHp(dmg, pen);
			}
			else
			{
				showMessage(_fisher, dmg, pen, skillType, 2);
				_gooduse = 2;
				changeHp(-dmg, pen);
			}
		}
		else if (_deceptiveMode == 0)
		{
			showMessage(_fisher, dmg, pen, skillType, 2);
			_gooduse = 2;
			changeHp(-dmg, pen);
		}
		else
		{
			showMessage(_fisher, dmg, pen, skillType, 3);
			_gooduse = 1;
			changeHp(dmg, pen);
		}
	}

	private static void showMessage(Player fisher, int dmg, int pen, SkillType skillType, int messageId)
	{
		switch (messageId)
		{
		case 1:
			if (skillType == SkillType.PUMPING)
			{
				fisher.sendPacket(new SystemMessage2(SystemMsg.PUMPING_IS_SUCCESSFUL_DAMAGE_S1).addInteger(dmg));
				if (pen == 50)
				{
					fisher.sendPacket(new SystemMessage2(SystemMsg.YOUR_PUMPING_WAS_SUCCESSFUL_MASTERY_PENALTYS1_).addInteger(pen));
				}
			}
			else
			{
				fisher.sendPacket(new SystemMessage2(SystemMsg.REELING_IS_SUCCESSFUL_DAMAGE_S1).addInteger(dmg));
				if (pen == 50)
				{
					fisher.sendPacket(new SystemMessage2(SystemMsg.YOUR_REELING_WAS_SUCCESSFUL_MASTERY_PENALTYS1_).addInteger(pen));
				}
			}
			break;
		case 2:
			if (skillType == SkillType.PUMPING)
			{
				fisher.sendPacket(new SystemMessage2(SystemMsg.PUMPING_FAILED_DAMAGE_S1).addInteger(dmg));
			}
			else
			{
				fisher.sendPacket(new SystemMessage2(SystemMsg.REELING_FAILED_DAMAGE_S1).addInteger(dmg));
			}
			break;
		case 3:
			if (skillType == SkillType.PUMPING)
			{
				fisher.sendPacket(new SystemMessage2(SystemMsg.PUMPING_IS_SUCCESSFUL_DAMAGE_S1).addInteger(dmg));
				if (pen == 50)
				{
					fisher.sendPacket(new SystemMessage2(SystemMsg.YOUR_PUMPING_WAS_SUCCESSFUL_MASTERY_PENALTYS1_).addInteger(pen));
				}
			}
			else
			{
				fisher.sendPacket(new SystemMessage2(SystemMsg.REELING_IS_SUCCESSFUL_DAMAGE_S1).addInteger(dmg));
				if (pen == 50)
				{
					fisher.sendPacket(new SystemMessage2(SystemMsg.YOUR_REELING_WAS_SUCCESSFUL_MASTERY_PENALTYS1_).addInteger(pen));
				}
			}
			break;
		default:
			break;
		}
	}

	public static void spawnPenaltyMonster(Player fisher)
	{
		int npcId = 18319 + Math.min(fisher.getLevel() / 11, 7); // 18319-18326

		MonsterInstance npc = new MonsterInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(npcId));
		npc.setSpawnedLoc(Location.findPointToStay(fisher, 100, 120));
		npc.setReflection(fisher.getReflection());
		npc.setHeading(fisher.getHeading() - 32768);
		npc.spawnMe(npc.getSpawnedLoc());
		npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, fisher, Rnd.get(1, 100));
	}

	public static int getRandomFishType(int lureId)
	{
		int check = Rnd.get(100);
		int type;

		switch (lureId)
		{
		case 7807: // Green Colored Lure - For Beginners, preferred by fast-moving (nimble) fish (type 5)
			if (check <= 54)
			{
				type = 5;
			}
			else if (check <= 77)
			{
				type = 4;
			}
			else
			{
				type = 6;
			}
			break;
		case 7808: // Purple Colored Lure - For Beginners, preferred by fat fish (type 4)
			if (check <= 54)
			{
				type = 4;
			}
			else if (check <= 77)
			{
				type = 6;
			}
			else
			{
				type = 5;
			}
			break;
		case 7809: // Yellow Colored Lure - For Beginners, preferred by ugly fish (type 6)
			if (check <= 54)
			{
				type = 6;
			}
			else if (check <= 77)
			{
				type = 5;
			}
			else
			{
				type = 4;
			}
			break;
		case 8486: // Prize-Winning Novice Fishing Lure
			if (check <= 33)
			{
				type = 4;
			}
			else if (check <= 66)
			{
				type = 5;
			}
			else
			{
				type = 6;
			}
			break;
		case 7610: // Wind Fishing Lure
		case 7611: // Icy Air Fishing Lure
		case 7612: // Earth Fishing Lure
		case 7613: // Flaming Fishing Lure

		case 8496: // Gludio's Luminous Lure
		case 8497: // Dion's Luminous Lure
		case 8498: // Giran's Luminous Lure
		case 8499: // Oren's Luminous Lure
		case 8500: // Aden's Luminous Lure
		case 8501: // Innadril's Luminous Lure
		case 8502: // Goddard's Luminous Lure
		case 8503: // Rune's Luminous Lure
		case 8504: // Schuttgart's Luminous Lure
		case 8548: // Hot Springs Lure
			type = 3;
			break;
		// all theese lures (green) are prefered by fast-moving (nimble) fish (type 1)
		case 6519: // Green Colored Lure - Low Grade
		case 8505: // Green Luminous Lure - Low Grade
		case 6520: // Green Colored Lure
		case 6521: // Green Colored Lure - High Grade
		case 8507: // Green Colored Lure - High Grade
			if (check <= 54)
			{
				type = 1;
			}
			else if (check <= 74)
			{
				type = 0;
			}
			else if (check <= 94)
			{
				type = 2;
			}
			else
			{
				type = 3;
			}
			break;
		// all theese lures (purple) are prefered by fat fish (type 0)
		case 6522: // Purple Colored Lure - Low Grade
		case 6523: // Purple Colored Lure
		case 6524: // Purple Colored Lure - High Grade
		case 8508: // Purple Luminous Lure - Low Grade
		case 8510: // Purple Luminous Lure - High Grade
			if (check <= 54)
			{
				type = 0;
			}
			else if (check <= 74)
			{
				type = 1;
			}
			else if (check <= 94)
			{
				type = 2;
			}
			else
			{
				type = 3;
			}
			break;
		// all theese lures (yellow) are prefered by ugly fish (type 2)
		case 6525: // Yellow Colored Lure - Low Grade
		case 6526: // Yellow Colored Lure
		case 6527: // Yellow Colored Lure - High Grade
		case 8511: // Yellow Luminous Lure - Low Grade
		case 8513: // Yellow Luminous Lure
			if (check <= 55)
			{
				type = 2;
			}
			else if (check <= 74)
			{
				type = 1;
			}
			else if (check <= 94)
			{
				type = 0;
			}
			else
			{
				type = 3;
			}
			break;
		case 8484: // Prize-Winning Fishing Lure
			if (check <= 33)
			{
				type = 0;
			}
			else if (check <= 66)
			{
				type = 1;
			}
			else
			{
				type = 2;
			}
			break;
		case 8506: // Green Luminous Lure, preferred by fast-moving (nimble) fish (type 8)
			if (check <= 54)
			{
				type = 8;
			}
			else if (check <= 77)
			{
				type = 7;
			}
			else
			{
				type = 9;
			}
			break;
		case 8509: // Purple Luminous Lure, preferred by fat fish (type 7)
			if (check <= 54)
			{
				type = 7;
			}
			else if (check <= 77)
			{
				type = 9;
			}
			else
			{
				type = 8;
			}
			break;
		case 8512: // Yellow Luminous Lure, preferred by ugly fish (type 9)
			if (check <= 54)
			{
				type = 9;
			}
			else if (check <= 77)
			{
				type = 8;
			}
			else
			{
				type = 7;
			}
			break;
		case 8485: // Prize-Winning Night Fishing Lure, prize-winning fishing lure
			if (check <= 33)
			{
				type = 7;
			}
			else if (check <= 66)
			{
				type = 8;
			}
			else
			{
				type = 9;
			}
			break;
		default:
			type = 1;
			break;
		}

		return type;
	}

	public static int getRandomFishLvl(Player player)
	{
		int skilllvl = 0;

		// Проверка на Fisherman's Potion
		Effect effect = player.getEffectList().getEffectByStackType("fishPot");
		if (effect != null)
		{
			skilllvl = (int) effect.getSkill().getPower();
		}
		else
		{
			skilllvl = player.getSkillLevel(1315);
		}

		if (skilllvl <= 0)
		{
			return 1;
		}

		int randomlvl;
		int check = Rnd.get(100);

		if (check < 50)
		{
			randomlvl = skilllvl;
		}
		else if (check <= 85)
		{
			randomlvl = skilllvl - 1;
			if (randomlvl <= 0)
			{
				randomlvl = 1;
			}
		}
		else
		{
			randomlvl = skilllvl + 1;
		}

		randomlvl = Math.min(27, Math.max(1, randomlvl));

		return randomlvl;
	}

	public static int getFishGroup(int lureId)
	{
		switch (lureId)
		{
		case 7807: // Green Colored Lure - For Beginners
		case 7808: // Purple Colored Lure - For Beginners
		case 7809: // Yellow Colored Lure - For Beginners
		case 8486: // Prize-Winning Novice Fishing Lure
			return 0;
		case 8506: // Green Luminous Lure
		case 8509: // Purple Luminous Lure
		case 8512: // Yellow Luminous Lure
		case 8485: // Prize-Winning Night Fishing Lure
			return 2;
		default:
			return 1;
		}
	}

	public static int getLureGrade(int lureId)
	{
		switch (lureId)
		{
		case 6519: // Green Colored Lure - Low Grade
		case 6522: // Purple Colored Lure - Low Grade
		case 6525: // Yellow Colored Lure - Low Grade
		case 8505: // Green Luminous Lure - Low Grade
		case 8508: // Purple Luminous Lure - Low Grade
		case 8511: // Yellow Luminous Lure - Low Grade
			return 0;
		case 6520: // Green Colored Lure
		case 6523: // Purple Colored Lure
		case 6526: // Yellow Colored Lure

		case 7610: // Wind Fishing Lure
		case 7611: // Icy Air Fishing Lure
		case 7612: // Earth Fishing Lure
		case 7613: // Flaming Fishing Lure

		case 7807: // Green Colored Lure - For Beginners
		case 7808: // Purple Colored Lure - For Beginners
		case 7809: // Yellow Colored Lure - For Beginners
		case 8484: // Prize-Winning Fishing Lure
		case 8485: // Prize-Winning Night Fishing Lure
		case 8486: // Prize-Winning Novice Fishing Lure

		case 8496: // Gludio's Luminous Lure
		case 8497: // Dion's Luminous Lure
		case 8498: // Giran's Luminous Lure
		case 8499: // Oren's Luminous Lure
		case 8500: // Aden's Luminous Lure
		case 8501: // Innadril's Luminous Lure
		case 8502: // Goddard's Luminous Lure
		case 8503: // Rune's Luminous Lure
		case 8504: // Schuttgart's Luminous Lure
		case 8548: // Hot Springs Lure

		case 8506: // Green Luminous Lure
		case 8509: // Purple Luminous Lure
		case 8512: // Yellow Luminous Lure
			return 1;
		case 6521: // Green Colored Lure - High Grade
		case 6524: // Purple Colored Lure - High Grade
		case 6527: // Yellow Colored Lure - High Grade
		case 8507: // Green Colored Lure - High Grade
		case 8510: // Purple Luminous Lure - High Grade
		case 8513: // Yellow Luminous Lure - High Grade
			return 2;
		default:
			return -1;
		}
	}

	public static boolean isNightLure(int lureId)
	{
		switch (lureId)
		{
		case 8505: // Green Luminous Lure - Low Grade
		case 8508: // Purple Luminous Lure - Low Grade
		case 8511: // Yellow Luminous Lure - Low Grade
			return true;
		case 8496: // Gludio's Luminous Lure
		case 8497: // Dion's Luminous Lure
		case 8498: // Giran's Luminous Lure
		case 8499: // Oren's Luminous Lure
		case 8500: // Aden's Luminous Lure
		case 8501: // Innadril's Luminous Lure
		case 8502: // Goddard's Luminous Lure
		case 8503: // Rune's Luminous Lure
		case 8504: // Schuttgart's Luminous Lure
			return true;
		case 8506: // Green Luminous Lure
		case 8509: // Purple Luminous Lure
		case 8512: // Yellow Luminous Lure
			return true;
		case 8510: // Purple Luminous Lure - High Grade
		case 8513: // Yellow Luminous Lure - High Grade
			return true;
		case 8485: // Prize-Winning Night Fishing Lure
			return true;
		default:
			return false;
		}
	}
}