package ai.PaganTemplete;

import java.util.concurrent.ScheduledFuture;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.instances.DoorInstance;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.PlaySound;
import l2mv.gameserver.network.serverpackets.SocialAction;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.NpcUtils;
import l2mv.gameserver.utils.ReflectionUtils;

/**
 * @author FandC
  * - AI for Rabe Boss Andreas Van Halter (29062).
  * - All the information about the AI ​​painted.
  * - AI is tested and works.
 */
public class AndreasVanHalter extends Fighter
{
	private boolean _firstTimeMove = true;
	private static ScheduledFuture<?> _movieTask = null;

	private static int TriolsRevelation1 = 32058;
	private static int TriolsRevelation2 = 32059;
	private static int TriolsRevelation3 = 32060;
	private static int TriolsRevelation4 = 32061;
	private static int TriolsRevelation5 = 32062;
	private static int TriolsRevelation6 = 32063;
	private static int TriolsRevelation7 = 32064;
	private static int TriolsRevelation8 = 32065;
	private static int TriolsRevelation9 = 32066;
	private static int TriolsRevelation10 = 32067;
	private static int TriolsRevelation11 = 32068;
	private static int RitualOffering = 32038;
	private static int AltarGatekeeper = 32051;
	private static int AndreasCaptainRoyalGuard1 = 22175;
	private static int AndreasCaptainRoyalGuard2 = 22188;
	private static int AndreasCaptainRoyalGuard3 = 22191;
	private static int AndreasRoyalGuards1 = 22192;
	private static int AndreasRoyalGuards2 = 22193;
	private static int AndreasRoyalGuards3 = 22176;

	public AndreasVanHalter(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return;
		}

		// Спавним НПЦ и Монстров
		SpawnNpc1();

		super.onEvtSpawn();
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return true;
		}

		// Двери на балкон
		DoorInstance door1 = ReflectionUtils.getDoor(19160014);
		DoorInstance door2 = ReflectionUtils.getDoor(19160015);
		// Двери к алтарю
		DoorInstance door3 = ReflectionUtils.getDoor(19160016);
		DoorInstance door4 = ReflectionUtils.getDoor(19160017);

		// Если двери на балкон закрылись, а к алтарю открылись, если мы не показывали еще видео, то показываем

		if (!door1.isOpen() && !door2.isOpen() && door3.isOpen() && door4.isOpen() && _firstTimeMove)
		{
			_firstTimeMove = false;
			// Запускаем показ видео
			_movieTask = ThreadPoolManager.getInstance().schedule(new Movie(1), 3000);
			// Запускаем музыку
			actor.broadcastPacket(new PlaySound("BS04_A"));
			// Вдруг мы не пошли бить РБ, нужно сделать перереспавн и поменять открытие и закрытие дверей местами.
			// Запускаем проверку через 1 час
			ThreadPoolManager.getInstance().schedule(new CheckAttack(), 3600000);
		}

		return true;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();

		if (actor == null)
		{
			return;
		}

		DoorInstance door1 = ReflectionUtils.getDoor(19160016);
		DoorInstance door2 = ReflectionUtils.getDoor(19160017);

		// Вдруг спрягним с болкона? тогда телепортируемся на начальную точку
		if (actor.getZ() >= -10476)
		{
			actor.teleToLocation(-16393, -53433, -10439);
		}
		else if (actor.getDistance(door1) <= 200)
		{
			actor.teleToLocation(-15690, -54030, -10439);
		}
		else if (actor.getDistance(door2) <= 200)
		{
			actor.teleToLocation(-17150, -54064, -10439);
		}
		super.onEvtAttacked(attacker, damage);
	}

	private class Movie extends RunnableImpl
	{
		private int _distance = 10000;
		private int _taskId;

		public Movie(int taskId)
		{
			_taskId = taskId;
		}

		@Override
		public void runImpl()
		{
			NpcInstance actor = getActor();
			actor.setHeading(16384);

			// Спавним камеру
			NpcInstance camera = NpcUtils.spawnSingle(13014, new Location(-16362, -53754, -10439));
			switch (_taskId)
			{
			case 1:
				// Ищем жертву для приношения
				NpcInstance npc1 = GameObjectsStorage.getByNpcId(RitualOffering);
				// Ставим таргет на жертву
				actor.setTarget(npc1);
				// Нашли, теперь можно кастонуть на неё скилом
				actor.doCast(SkillTable.getInstance().getInfo(1168, 7), npc1, false);

				// Включаем на всякие пажарные запрет хождения что бы не испортить мувик
				actor.startImmobilized();
				// Ищем игроков в радиусе и показываем мувик
				for (Player player : World.getAroundPlayers(actor))
				{
					if (player.getDistance(camera) <= _distance)
					{
						player.enterMovieMode();
						player.specialCamera(camera, 1500, 88, 89, 0, 5000);
					}
					else
					{
						player.leaveMovieMode();
					}
				}

				if (_movieTask != null)
				{
					_movieTask.cancel(false);
				}
				_movieTask = null;
				_movieTask = ThreadPoolManager.getInstance().schedule(new Movie(2), 300);
				break;
			case 2:
				// Ищем заного жертву
				NpcInstance npc2 = GameObjectsStorage.getByNpcId(RitualOffering);
				// Показываем социалку о том что мы умерли
				npc2.sendPacket(new SocialAction(npc2.getObjectId(), 1));
				// Стави жертве ХП 0
				npc2.setCurrentHp(0, true);
				// Ищем игроков в радиусе и показываем мувик
				for (Player player : World.getAroundPlayers(actor))
				{
					if (player.getDistance(camera) <= _distance)
					{
						player.enterMovieMode();
						player.specialCamera(camera, 1500, 88, 89, 0, 5000);
					}
					else
					{
						player.leaveMovieMode();
					}
				}

				if (_movieTask != null)
				{
					_movieTask.cancel(false);
				}
				_movieTask = null;
				_movieTask = ThreadPoolManager.getInstance().schedule(new Movie(3), 300);
				break;
			case 3:
				// Удаляем жертву
				NpcInstance npc3 = GameObjectsStorage.getByNpcId(RitualOffering);
				npc3.deleteMe();
				// Ищем игроков в радиусе и показываем мувик
				for (Player player : World.getAroundPlayers(actor))
				{
					if (player.getDistance(camera) <= _distance)
					{
						player.enterMovieMode();
						player.specialCamera(camera, 450, 88, 3, 5500, 5000);
					}
					else
					{
						player.leaveMovieMode();
					}
				}

				if (_movieTask != null)
				{
					_movieTask.cancel(false);
				}
				_movieTask = null;
				_movieTask = ThreadPoolManager.getInstance().schedule(new Movie(4), 9400);
				break;
			case 4:
				// Ищем игроков в радиусе и показываем мувик
				for (Player player : World.getAroundPlayers(actor))
				{
					if (player.getDistance(camera) <= _distance)
					{
						player.enterMovieMode();
						player.specialCamera(camera, 500, 88, 4, 5000, 5000);
					}
					else
					{
						player.leaveMovieMode();
					}
				}

				if (_movieTask != null)
				{
					_movieTask.cancel(false);
				}
				_movieTask = null;
				_movieTask = ThreadPoolManager.getInstance().schedule(new Movie(5), 5000);
				break;
			case 5:
				// Ищем игроков в радиусе и показываем мувик
				for (Player player : World.getAroundPlayers(actor))
				{
					if (player.getDistance(camera) <= _distance)
					{
						player.enterMovieMode();
						player.specialCamera(camera, 3000, 88, 4, 6000, 5000);
					}
					else
					{
						player.leaveMovieMode();
					}
				}

				if (_movieTask != null)
				{
					_movieTask.cancel(false);
				}
				_movieTask = null;
				_movieTask = ThreadPoolManager.getInstance().schedule(new Movie(6), 6000);
				break;
			case 6:
				// Сбрасываем режим мувиков
				for (Player player : World.getAroundPlayers(actor))
				{
					player.leaveMovieMode();
				}

				// Спавним монстров возле Алтаря
				SpawnNpc2();
				// Выключаем запрет на хождения
				actor.stopImmobilized();
				if (_movieTask != null)
				{
					_movieTask.cancel(false);
				}
				_movieTask = null;
				break;
			}
		}
	}

	private class CheckAttack extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			NpcInstance actor = getActor();
			if (!actor.isAttackingNow())
			{
				// Сбрасываем переменную на показ видео
				_firstTimeMove = true;

				// Удаляем всех кого заспавнили при спавне (тех кто остался еще жить).
				DeleteNpc();

				// Даём задание заспавнить заного всех через 10 секунд
				ThreadPoolManager.getInstance().schedule(new NewSpawn(), 10000);
				ThreadPoolManager.getInstance().schedule(new CheckAttack(), 3600000);
			}
		}
	}

	private class NewSpawn extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			// Спавним НПЦ и Монстров
			SpawnNpc1();
		}
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		// Сбрасываем переменную на показ видео
		_firstTimeMove = true;

		// Удаляем всех кого заспавнили при спавне (тех кто остался еще жить).
		DeleteNpc();
		super.onEvtDead(killer);
	}

	private void SpawnNpc1()
	{
		// Спавним контроллеры Зон
		NpcUtils.spawnSingle(TriolsRevelation1, new Location(-20117, -52683, -10974));
		NpcUtils.spawnSingle(TriolsRevelation2, new Location(-20137, -54371, -11170));
		NpcUtils.spawnSingle(TriolsRevelation3, new Location(-12710, -52677, -10974));
		NpcUtils.spawnSingle(TriolsRevelation4, new Location(-12660, -54379, -11170));
		NpcUtils.spawnSingle(TriolsRevelation5, new Location(-17826, -53426, -11624));
		NpcUtils.spawnSingle(TriolsRevelation6, new Location(-17068, -53440, -11624));
		NpcUtils.spawnSingle(TriolsRevelation7, new Location(-16353, -53549, -11624));
		NpcUtils.spawnSingle(TriolsRevelation8, new Location(-15655, -53869, -11624));
		NpcUtils.spawnSingle(TriolsRevelation9, new Location(-15005, -53132, -11624));
		NpcUtils.spawnSingle(TriolsRevelation10, new Location(-16316, -56842, -10900));
		NpcUtils.spawnSingle(TriolsRevelation11, new Location(-16395, -54055, -10439, 15992));

		// Спавним Жертву для приношения
		NpcUtils.spawnSingle(RitualOffering, new Location(-16384, -53197, -10439, 15992));

		// Спавним контроллеры Дверей
		NpcUtils.spawnSingle(AltarGatekeeper, new Location(-17248, -54832, -10424, 16384));
		NpcUtils.spawnSingle(AltarGatekeeper, new Location(-15547, -54835, -10424, 16384));
		NpcUtils.spawnSingle(AltarGatekeeper, new Location(-18116, -54831, -10579, 16384));
		NpcUtils.spawnSingle(AltarGatekeeper, new Location(-14645, -54836, -10577, 16384));

		// Спавним Монстров на Балконе
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-18008, -53394, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-17653, -53399, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-17827, -53575, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-18008, -53749, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-17653, -53754, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-17827, -53930, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-18008, -54100, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-17653, -54105, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-17275, -52577, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-16917, -52577, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-16738, -52577, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-17003, -52404, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-17353, -52404, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-17362, -52752, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-17006, -52752, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-17721, -52752, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-17648, -52968, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-17292, -52968, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-16374, -52577, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-16648, -52404, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-16284, -52404, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-16013, -52577, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-15658, -52577, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-15306, -52577, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-15923, -52404, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-15568, -52404, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-15216, -52404, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-15745, -52752, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-15394, -52752, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-15475, -52969, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-15119, -52969, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-15149, -53411, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-14794, -53416, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-14968, -53592, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-15149, -53766, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-14794, -53771, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-14968, -53947, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-15149, -54117, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard1, new Location(-14794, -54122, -10594, 16384));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard2, new Location(-16392, -52124, -10592));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard3, new Location(-16385, -53268, -10439, 15992));
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard3, new Location(-17150, -54046, -10439, 15992));

		// Спавним Монстров по всей локации
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16380, -45796, -10726, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16290, -45796, -10726, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16471, -45796, -10726, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16380, -45514, -10726, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16290, -45514, -10726, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16471, -45514, -10726, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16380, -45243, -10726, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16290, -45243, -10726, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16471, -45243, -10726, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16380, -44973, -10726, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16290, -44973, -10726, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16471, -44973, -10726, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16380, -44703, -10726, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16290, -44703, -10726, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16471, -44703, -10726, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16471, -44443, -10726, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16382, -47685, -10822, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16292, -47685, -10822, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16474, -47685, -10822, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16382, -47404, -10822, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16292, -47404, -10822, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16474, -47404, -10822, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16382, -47133, -10822, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16292, -47133, -10822, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16474, -47133, -10822, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16382, -46862, -10822, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16292, -46862, -10822, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16474, -46862, -10822, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16382, -46593, -10822, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16292, -46593, -10822, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16474, -46593, -10822, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16382, -46333, -10822, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16292, -46333, -10822, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16474, -46333, -10822, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16381, -49743, -10918, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16291, -49743, -10918, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16473, -49743, -10918, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16381, -49461, -10918, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16291, -49461, -10918, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16473, -49461, -10918, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16381, -49191, -10918, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16291, -49191, -10918, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16473, -49191, -10918, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16381, -48920, -10918, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16291, -48920, -10918, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16473, -48920, -10918, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16381, -48650, -10918, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16291, -48650, -10918, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16473, -48650, -10918, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16381, -48391, -10918, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16291, -48391, -10918, 16384));
		NpcUtils.spawnSingle(AndreasRoyalGuards3, new Location(-16473, -48391, -10918, 16384));
	}

	private void SpawnNpc2()
	{
		// Спавним Монстров возле Алтаря
		NpcUtils.spawnSingle(AndreasCaptainRoyalGuard3, new Location(-15690, -54030, -10439, 15992));
		NpcUtils.spawnSingle(AndreasRoyalGuards1, new Location(-16385, -53268, -10439, 15992));
		NpcUtils.spawnSingle(AndreasRoyalGuards1, new Location(-17150, -54046, -10439, 15992));
		NpcUtils.spawnSingle(AndreasRoyalGuards1, new Location(-15690, -54030, -10439, 15992));
		NpcUtils.spawnSingle(AndreasRoyalGuards2, new Location(-16385, -53268, -10439, 15992));
		NpcUtils.spawnSingle(AndreasRoyalGuards2, new Location(-17150, -54046, -10439, 15992));
		NpcUtils.spawnSingle(AndreasRoyalGuards2, new Location(-15690, -54030, -10439, 15992));

	}

	private void DeleteNpc()
	{
		int[] npcs =
		{
			TriolsRevelation1,
			TriolsRevelation2,
			TriolsRevelation3,
			TriolsRevelation4,
			TriolsRevelation5,
			TriolsRevelation6,
			TriolsRevelation7,
			TriolsRevelation8,
			TriolsRevelation9,
			TriolsRevelation10,
			TriolsRevelation11,
			RitualOffering,
			AltarGatekeeper,
			AndreasCaptainRoyalGuard1,
			AndreasCaptainRoyalGuard2,
			AndreasCaptainRoyalGuard3,
			AndreasRoyalGuards1,
			AndreasRoyalGuards2,
			AndreasRoyalGuards3
		};

		for (int npcId : npcs)
		{
			// Ищем во всем мире
			NpcInstance npc = GameObjectsStorage.getByNpcId(npcId);
			if (npc != null)
			{
				npc.deleteMe();
			}
		}

		// Двери на балкон
		DoorInstance door1 = ReflectionUtils.getDoor(19160014);
		DoorInstance door2 = ReflectionUtils.getDoor(19160015);
		// Двери к алтарю
		DoorInstance door3 = ReflectionUtils.getDoor(19160016);
		DoorInstance door4 = ReflectionUtils.getDoor(19160017);

		// Открываем двери на балкон
		door1.openMe();
		door2.openMe();
		// Закрываем двери к алтарю
		door3.closeMe();
		door4.closeMe();
	}
}