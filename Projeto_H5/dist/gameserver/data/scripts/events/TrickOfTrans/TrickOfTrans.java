package events.TrickOfTrans;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.Announcements;
import l2mv.gameserver.Config;
import l2mv.gameserver.listener.actor.OnDeathListener;
import l2mv.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.model.actor.listener.CharListenerList;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * Trick Of Transmutation Event
 */
public class TrickOfTrans extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener
{
	private static final Logger _log = LoggerFactory.getLogger(TrickOfTrans.class);
	// Эвент Менеджеры
	private static int EVENT_MANAGER_ID = 32132; // Alchemist\'s Servitor
	private static int CHESTS_ID = 13036; // Alchemist\'s Chest

	// Рецепты
	private static int RED_PSTC = 9162; // Red Philosopher''s Stone Transmutation Circle
	private static int BLUE_PSTC = 9163; // Blue Philosopher''s Stone Transmutation Circle
	private static int ORANGE_PSTC = 9164; // Orange Philosopher''s Stone Transmutation Circle
	private static int BLACK_PSTC = 9165; // Black Philosopher''s Stone Transmutation Circle
	private static int WHITE_PSTC = 9166; // White Philosopher''s Stone Transmutation Circle
	private static int GREEN_PSTC = 9167; // Green Philosopher''s Stone Transmutation Circle

	// Награды
	private static int RED_PSTC_R = 9171; // Red Philosopher''s Stone
	private static int BLUE_PSTC_R = 9172; // Blue Philosopher''s Stone
	private static int ORANGE_PSTC_R = 9173; // Orange Philosopher''s Stone
	private static int BLACK_PSTC_R = 9174; // Black Philosopher''s Stone
	private static int WHITE_PSTC_R = 9175; // White Philosopher''s Stone
	private static int GREEN_PSTC_R = 9176; // Green Philosopher''s Stone

	// Ключ
	private static int A_CHEST_KEY = 9205; // Alchemist''s Chest Key

	private static boolean _active = false;

	private static final ArrayList<SimpleSpawner> _em_spawns = new ArrayList<SimpleSpawner>();
	private static final ArrayList<SimpleSpawner> _ch_spawns = new ArrayList<SimpleSpawner>();

	// Ингридиенты
	private static int PhilosophersStoneOre = 9168; // Philosopher''s Stone Ore
	private static int PhilosophersStoneOreMax = 17; // Максимальное Кол-во
	private static int PhilosophersStoneConversionFormula = 9169; // Philosopher''s Stone Conversion Formula
	private static int MagicReagents = 9170; // Magic Reagents
	private static int MagicReagentsMax = 30; // Максимальное Кол-во

	@Override
	public void onLoad()
	{
		CharListenerList.addGlobal(this);
		if (isActive())
		{
			_active = true;
			spawnEventManagers();
			_log.info("Loaded Event: Trick of Trnasmutation [state: activated]");
		}
		else
		{
			_log.info("Loaded Event: Trick of Trnasmutation [state: deactivated]");
		}
	}

	/**
	 * Читает статус эвента из базы.
	 * @return
	 */
	private static boolean isActive()
	{
		return IsActive("trickoftrans");
	}

	/**
	 * Запускает эвент
	 */
	public void startEvent()
	{
		final Player player = getSelf();
		if (!player.getPlayerAccess().IsEventGm)
		{
			return;
		}

		if (SetActive("trickoftrans", true))
		{
			spawnEventManagers();
			System.out.println("Event 'Trick of Transmutation' started.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.TrickOfTrans.AnnounceEventStarted", null);
		}
		else
		{
			player.sendMessage("Event 'Trick of Transmutation' already started.");
		}

		_active = true;

		show("admin/events/events.htm", player);
	}

	/**
	 * Останавливает эвент
	 */
	public void stopEvent()
	{
		final Player player = getSelf();
		if (!player.getPlayerAccess().IsEventGm)
		{
			return;
		}
		if (SetActive("trickoftrans", false))
		{
			unSpawnEventManagers();
			System.out.println("Event 'Trick of Transmutation' stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.TrickOfTrans.AnnounceEventStoped", null);
		}
		else
		{
			player.sendMessage("Event 'Trick of Transmutation' not started.");
		}

		_active = false;

		show("admin/events/events.htm", player);
	}

	/**
	 * Анонсируется при заходе игроком в мир
	 */
	@Override
	public void onPlayerEnter(final Player player)
	{
		if (_active)
		{
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.TrickOfTrans.AnnounceEventStarted", null);
		}
	}

	@Override
	public void onReload()
	{
		unSpawnEventManagers();
	}

	@Override
	public void onShutdown()
	{
		unSpawnEventManagers();
	}

	/**
	 * Спавнит эвент менеджеров
	 */
	private void spawnEventManagers()
	{
		// Эвент Менеджер
		final int EVENT_MANAGERS[][] = //
		{ //
			{
				147992,
				28616,
				-2295,
				0
			}, // Aden
			{
				81919,
				148290,
				-3472,
				51432
			}, // Giran
			{
				18293,
				145208,
				-3081,
				6470
			}, // Dion
			{
				-14694,
				122699,
				-3122,
				0
			}, // Gludio
			{
				-81634,
				150275,
				-3155,
				15863
			}, // Gludin
		};

		// Сундуки
		final int CHESTS[][] =
		{
			{
				148081,
				28614,
				-2274,
				2059
			}, // Aden
			{
				147918,
				28615,
				-2295,
				31471
			}, // Aden
			{
				147998,
				28534,
				-2274,
				49152
			}, // Aden
			{
				148053,
				28550,
				-2274,
				55621
			}, // Aden
			{
				147945,
				28563,
				-2274,
				40159
			}, // Aden
			{
				82012,
				148286,
				-3472,
				61567
			}, // Giran
			{
				81822,
				148287,
				-3493,
				29413
			}, // Giran
			{
				81917,
				148207,
				-3493,
				49152
			}, // Giran
			{
				81978,
				148228,
				-3472,
				53988
			}, // Giran
			{
				81851,
				148238,
				-3472,
				40960
			}, // Giran
			{
				18343,
				145253,
				-3096,
				7449
			}, // Dion
			{
				18284,
				145274,
				-3090,
				19740
			}, // Dion
			{
				18351,
				145186,
				-3089,
				61312
			}, // Dion
			{
				18228,
				145265,
				-3079,
				21674
			}, // Dion
			{
				18317,
				145140,
				-3078,
				55285
			}, // Dion
			{
				-14584,
				122694,
				-3122,
				65082
			}, // Gludio
			{
				-14610,
				122756,
				-3143,
				13029
			}, // Gludio
			{
				-14628,
				122627,
				-3122,
				50632
			}, // Gludio
			{
				-14697,
				122607,
				-3143,
				48408
			}, // Gludio
			{
				-14686,
				122787,
				-3122,
				12416
			}, // Gludio
			{
				-81745,
				150275,
				-3134,
				32768
			}, // Gludin
			{
				-81520,
				150275,
				-3134,
				0
			}, // Gludin
			{
				-81628,
				150379,
				-3134,
				16025
			}, // Gludin
			{
				-81696,
				150347,
				-3155,
				22854
			}, // Gludin
			{
				-81559,
				150332,
				-3134,
				3356
			}, // Gludin
		};

		SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS, _em_spawns);
		SpawnNPCs(CHESTS_ID, CHESTS, _ch_spawns, 300);
	}

	/**
	 * Удаляет спавн эвент менеджеров
	 */
	private void unSpawnEventManagers()
	{
		deSpawnNPCs(_em_spawns);
		deSpawnNPCs(_ch_spawns);
	}

	/**
	 * Обработчик смерти мобов, управляющий эвентовым дропом
	 */
	@Override
	public void onDeath(final Creature cha, final Creature killer)
	{
		if (_active && SimpleCheckDrop(cha, killer) && Rnd.chance(Config.EVENT_TRICK_OF_TRANS_CHANCE * killer.getPlayer().getRateItems() * Config.RATE_DROP_ITEMS * ((NpcInstance) cha).getTemplate().rateHp))
		{
			((NpcInstance) cha).dropItem(killer.getPlayer(), A_CHEST_KEY, 1);
		}
	}

	public void accept()
	{
		final Player player = getSelf();

		if (!player.isQuestContinuationPossible(true))
		{
			return;
		}

		if (!player.findRecipe(RED_PSTC_R))
		{
			addItem(player, RED_PSTC, 1, "TrickOrTrans");
		}
		if (!player.findRecipe(BLACK_PSTC_R))
		{
			addItem(player, BLACK_PSTC, 1, "TrickOrTrans");
		}
		if (!player.findRecipe(BLUE_PSTC_R))
		{
			addItem(player, BLUE_PSTC, 1, "TrickOrTrans");
		}
		if (!player.findRecipe(GREEN_PSTC_R))
		{
			addItem(player, GREEN_PSTC, 1, "TrickOrTrans");
		}
		if (!player.findRecipe(ORANGE_PSTC_R))
		{
			addItem(player, ORANGE_PSTC, 1, "TrickOrTrans");
		}
		if (!player.findRecipe(WHITE_PSTC_R))
		{
			addItem(player, WHITE_PSTC, 1, "TrickOrTrans");
		}

		show("scripts/events/TrickOfTrans/TrickOfTrans_01.htm", player);
	}

	public void open()
	{
		final Player player = getSelf();

		if (getItemCount(player, A_CHEST_KEY) > 0)
		{
			removeItem(player, A_CHEST_KEY, 1, "TrickOrTrans");
			addItem(player, PhilosophersStoneOre, Rnd.get(1, PhilosophersStoneOreMax), "TrickOrTrans");
			addItem(player, MagicReagents, Rnd.get(1, MagicReagentsMax), "TrickOrTrans");
			if (Rnd.chance(80))
			{
				addItem(player, PhilosophersStoneConversionFormula, 1, "TrickOrTrans");
			}

			show("scripts/events/TrickOfTrans/TrickOfTrans_02.htm", player);
		}
		else
		{
			show("scripts/events/TrickOfTrans/TrickOfTrans_03.htm", player);
		}
	}
}