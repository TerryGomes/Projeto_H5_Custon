package events.Christmas;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.Announcements;
import l2mv.gameserver.Config;
import l2mv.gameserver.cache.Msg;
import l2mv.gameserver.listener.actor.OnDeathListener;
import l2mv.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.model.actor.listener.CharListenerList;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;

public class Christmas extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener
{
	private static int EVENT_MANAGER_ID = 31863;
	private static int CTREE_ID = 13006;
	private static final Logger _log = LoggerFactory.getLogger(Christmas.class);

	private static int[][] _dropdata =
	{
		// Item, chance
		{
			5556,
			20
		}, // Star Ornament 2%
		{
			5557,
			20
		}, // Bead Ornament 2%
		{
			5558,
			50
		}, // Fir Tree Branch 5%
		{
			5559,
			5
		}, // Flower Pot 0.5%
		/*
		 * // Музыкальные кристаллы 0.2%
		 * { 5562, 2 },
		 * { 5563, 2 },
		 * { 5564, 2 },
		 * { 5565, 2 },
		 * { 5566, 2 },
		 * { 5583, 2 },
		 * { 5584, 2 },
		 * { 5585, 2 },
		 * { 5586, 2 },
		 * { 5587, 2 },
		 * { 4411, 2 },
		 * { 4412, 2 },
		 * { 4413, 2 },
		 * { 4414, 2 },
		 * { 4415, 2 },
		 * { 4416, 2 },
		 * { 4417, 2 },
		 * { 5010, 2 },
		 * { 7061, 2 },
		 * { 7062, 2 },
		 * { 6903, 2 },
		 * { 8555, 2 }
		 */
	};

	private static List<SimpleSpawner> _spawns = new ArrayList<SimpleSpawner>();

	private static boolean _active = false;

	@Override
	public void onLoad()
	{
		CharListenerList.addGlobal(this);
		if (isActive())
		{
			_active = true;
			spawnEventManagers();
			_log.info("Loaded Event: Christmas [state: activated]");
		}
		else
		{
			_log.info("Loaded Event: Christmas [state: deactivated]");
		}
	}

	/**
	 * Читает статус эвента из базы.
	 * @return
	 */
	private static boolean isActive()
	{
		return IsActive("Christmas");
	}

	/**
	 * Запускает эвент
	 */
	public void startEvent()
	{
		Player player = getSelf();
		if (!player.getPlayerAccess().IsEventGm)
		{
			return;
		}

		if (SetActive("Christmas", true))
		{
			spawnEventManagers();
			System.out.println("Event 'Christmas' started.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.Christmas.AnnounceEventStarted", null);
		}
		else
		{
			player.sendMessage("Event 'Christmas' already started.");
		}

		_active = true;

		show("admin/events/events.htm", player);
	}

	/**
	 * Останавливает эвент
	 */
	public void stopEvent()
	{
		Player player = getSelf();
		if (!player.getPlayerAccess().IsEventGm)
		{
			return;
		}
		if (SetActive("Christmas", false))
		{
			unSpawnEventManagers();
			System.out.println("Event 'Christmas' stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.Christmas.AnnounceEventStoped", null);
		}
		else
		{
			player.sendMessage("Event 'Christmas' not started.");
		}

		_active = false;

		show("admin/events/events.htm", player);
	}

	/**
	 * Спавнит эвент менеджеров и рядом ёлки
	 */
	private void spawnEventManagers()
	{
		final int EVENT_MANAGERS[][] =
		{
			{
				81921,
				148921,
				-3467,
				16384
			},
			{
				146405,
				28360,
				-2269,
				49648
			},
			{
				19319,
				144919,
				-3103,
				31135
			},
			{
				-82805,
				149890,
				-3129,
				16384
			},
			{
				-12347,
				122549,
				-3104,
				16384
			},
			{
				110642,
				220165,
				-3655,
				61898
			},
			{
				116619,
				75463,
				-2721,
				20881
			},
			{
				85513,
				16014,
				-3668,
				23681
			},
			{
				81999,
				53793,
				-1496,
				61621
			},
			{
				148159,
				-55484,
				-2734,
				44315
			},
			{
				44185,
				-48502,
				-797,
				27479
			},
			{
				86899,
				-143229,
				-1293,
				8192
			}
		};

		final int CTREES[][] =
		{
			{
				81961,
				148921,
				-3467,
				0
			},
			{
				146445,
				28360,
				-2269,
				0
			},
			{
				19319,
				144959,
				-3103,
				0
			},
			{
				-82845,
				149890,
				-3129,
				0
			},
			{
				-12387,
				122549,
				-3104,
				0
			},
			{
				110602,
				220165,
				-3655,
				0
			},
			{
				116659,
				75463,
				-2721,
				0
			},
			{
				85553,
				16014,
				-3668,
				0
			},
			{
				81999,
				53743,
				-1496,
				0
			},
			{
				148199,
				-55484,
				-2734,
				0
			},
			{
				44185,
				-48542,
				-797,
				0
			},
			{
				86859,
				-143229,
				-1293,
				0
			}
		};

		SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS, _spawns);
		SpawnNPCs(CTREE_ID, CTREES, _spawns);
	}

	/**
	 * Удаляет спавн эвент менеджеров
	 */
	private void unSpawnEventManagers()
	{
		deSpawnNPCs(_spawns);
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
	 * Обработчик смерти мобов, управляющий эвентовым дропом
	 */
	@Override
	public void onDeath(Creature cha, Creature killer)
	{
		if (_active && SimpleCheckDrop(cha, killer))
		{
			int dropCounter = 0;
			for (int[] drop : _dropdata)
			{
				if (Rnd.chance(drop[1] * killer.getPlayer().getRateItems() * Config.RATE_DROP_ITEMS * 0.1))
				{
					dropCounter++;
					((NpcInstance) cha).dropItem(killer.getPlayer(), drop[0], 1);

					// Из одного моба выпадет не более 3-х эвентовых предметов
					if (dropCounter > 2)
					{
						break;
					}
				}
			}
		}
	}

	public void exchange(String[] var)
	{
		Player player = getSelf();

		if (!player.isQuestContinuationPossible(true))
		{
			return;
		}

		if (player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300)
		{
			return;
		}

		if (var[0].equalsIgnoreCase("0"))
		{
			if (getItemCount(player, 5556) >= 4 && getItemCount(player, 5557) >= 4 && getItemCount(player, 5558) >= 10 && getItemCount(player, 5559) >= 1)
			{
				removeItem(player, 5556, 4, "Christmas");
				removeItem(player, 5557, 4, "Christmas");
				removeItem(player, 5558, 10, "Christmas");
				removeItem(player, 5559, 1, "Christmas");
				addItem(player, 5560, 1, "Christmas"); // Christmas Tree
				return;
			}
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
		}
		if (var[0].equalsIgnoreCase("1"))
		{
			if (getItemCount(player, 5560) >= 10)
			{
				removeItem(player, 5560, 10, "Christmas");
				addItem(player, 5561, 1, "Christmas"); // Special Christmas Tree
				return;
			}
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
		}
		if (var[0].equalsIgnoreCase("2"))
		{
			if (getItemCount(player, 5560) >= 10)
			{
				removeItem(player, 5560, 10, "Christmas");
				addItem(player, 7836, 1, "Christmas"); // Santa's Hat
				return;
			}
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
		}
		if (var[0].equalsIgnoreCase("3"))
		{
			if (getItemCount(player, 5560) >= 10)
			{
				removeItem(player, 5560, 10, "Christmas");
				addItem(player, 8936, 1, "Christmas"); // Santa's Antlers
				return;
			}
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
		}
		if (var[0].equalsIgnoreCase("4"))
		{
			if (getItemCount(player, 5560) >= 20)
			{
				removeItem(player, 5560, 20, "Christmas");
				addItem(player, 10606, 1, "Christmas"); // Agathion Seal Bracelet - Rudolph
				return;
			}
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
		}
	}

	@Override
	public void onPlayerEnter(Player player)
	{
		if (_active)
		{
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.Christmas.AnnounceEventStarted", null);
		}
	}
}