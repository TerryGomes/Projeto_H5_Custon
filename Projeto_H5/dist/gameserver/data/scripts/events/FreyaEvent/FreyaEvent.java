package events.FreyaEvent;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.Announcements;
import l2mv.gameserver.listener.actor.OnDeathListener;
import l2mv.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.model.actor.listener.CharListenerList;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.SystemMessage;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;

public class FreyaEvent extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener
{
	private static final int EVENT_MANAGER_ID = 13296;
	private static final int ADENA = 57;
	private static final int GIFT_RECEIVE_DELAY = 20;
	private static final int GIFT_ID = 15440;
	private static final int GIFT_PRICE = 1;
	private static final int DROP_CHANCE = 55;
	private static final int[] DROP_LIST =
	{
		17130,
		17131,
		17132,
		17133,
		17134,
		17135,
		17136,
		17137
	};
	private static final int[][] EVENT_MANAGERS =
	{
		{
			16111,
			142850,
			-2707,
			16000
		},
		{
			17275,
			145000,
			-3037,
			25000
		},
		{
			83037,
			149324,
			-3470,
			44000
		},
		{
			82145,
			148609,
			-3468,
			0
		},
		{
			81755,
			146487,
			-3534,
			32768
		},
		{
			-81031,
			150038,
			-3045,
			0
		},
		{
			-83156,
			150994,
			-3130,
			0
		},
		{
			-13727,
			122117,
			-2990,
			16384
		},
		{
			-14129,
			123869,
			-3118,
			40959
		},
		{
			-84411,
			244813,
			-3730,
			57343
		},
		{
			-84023,
			243051,
			-3730,
			4096
		},
		{
			46908,
			50856,
			-2997,
			8192
		},
		{
			45538,
			48357,
			-3061,
			18000
		},
		{
			9929,
			16324,
			-4576,
			62999
		},
		{
			11546,
			17599,
			-4586,
			46900
		},
		{
			81987,
			53723,
			-1497,
			0
		},
		{
			81083,
			56118,
			-1562,
			32768
		},
		{
			147200,
			25614,
			-2014,
			16384
		},
		{
			148557,
			26806,
			-2206,
			32768
		},
		{
			117356,
			76708,
			-2695,
			49151
		},
		{
			115887,
			76382,
			-2714,
			0
		},
		{
			-117239,
			46842,
			367,
			49151
		},
		{
			-119494,
			44882,
			367,
			24576
		},
		{
			111004,
			218928,
			-3544,
			16384
		},
		{
			108426,
			221876,
			-3600,
			49151
		},
		{
			-45278,
			-112766,
			-241,
			0
		},
		{
			-45372,
			-114104,
			-241,
			16384
		},
		{
			115096,
			-178370,
			-891,
			0
		},
		{
			116199,
			-182694,
			-1506,
			0
		},
		{
			86865,
			-142915,
			-1341,
			26000
		},
		{
			85584,
			-142490,
			-1343,
			0
		},
		{
			147421,
			-55435,
			-2736,
			49151
		},
		{
			148206,
			-55786,
			-2782,
			61439
		},
		{
			43165,
			-48461,
			-797,
			17000
		},
		{
			43966,
			-47709,
			-798,
			49999
		}
	};
	private static final String _name = "Freya Celebration";
	private static final String _msgStarted = "scripts.events.FreyaEvent.AnnounceEventStarted";
	private static final String _msgEnded = "scripts.events.FreyaEvent.AnnounceEventStoped";
	private static final Logger _log = LoggerFactory.getLogger(FreyaEvent.class);
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
			_log.info("Loaded Event: " + _name + " [state: activated]");
		}
		else
		{
			_log.info("Loaded Event: " + _name + " [state: deactivated]");
		}
	}

	/**
	 * Читает статус эвента из базы.
	 * @return
	 */
	protected static boolean isActive()
	{
		return IsActive(_name);
	}

	/**
	 * Спавнит эвент менеджеров
	 */
	protected void spawnEventManagers()
	{
		SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS, _spawns);
	}

	/**
	 * Удаляет спавн эвент менеджеров
	 */
	protected void unSpawnEventManagers()
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
			int itemId = DROP_LIST[Rnd.get(DROP_LIST.length)];
			if (Rnd.chance(DROP_CHANCE))
			{
				((NpcInstance) cha).dropItem(killer.getPlayer(), itemId, 1);
			}
		}
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

		if (SetActive(_name, true))
		{
			spawnEventManagers();
			System.out.println("Event '" + _name + "' started.");
			Announcements.getInstance().announceByCustomMessage(_msgStarted, null);
		}
		else
		{
			player.sendMessage("Event '" + _name + "' already started.");
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
		if (SetActive(_name, false))
		{
			unSpawnEventManagers();
			System.out.println("Event '" + _name + "' stopped.");
			Announcements.getInstance().announceByCustomMessage(_msgEnded, null);
		}
		else
		{
			player.sendMessage("Event '" + _name + "' not started.");
		}

		_active = false;

		show("admin/events/events.htm", player);
	}

	@Override
	public void onPlayerEnter(Player player)
	{
		if (_active)
		{
			Announcements.getInstance().announceToPlayerByCustomMessage(player, _msgStarted, null);
		}
	}

	public void receiveGift()
	{
		Player player = getSelf();
		long _remaining_time;
		long _reuse_time = GIFT_RECEIVE_DELAY * 60 * 60 * 1000;
		long _curr_time = System.currentTimeMillis();
		String _last_use_time = player.getVar("FreyaCelebration");

		if (_last_use_time != null)
		{
			_remaining_time = _curr_time - Long.parseLong(_last_use_time);
		}
		else
		{
			_remaining_time = _reuse_time;
		}

		if (_remaining_time >= _reuse_time)
		{
			if (getItemCount(player, ADENA) >= GIFT_PRICE)
			{
				removeItem(player, ADENA, GIFT_PRICE, "FreyaEvent");
				addItem(player, GIFT_ID, 1, "FreyaEvent");
				player.setVar("FreyaCelebration", String.valueOf(_curr_time), -1);
			}
			else
			{
				player.sendPacket(new SystemMessage(SystemMessage._2_UNITS_OF_THE_ITEM_S1_IS_REQUIRED).addNumber(GIFT_PRICE));
			}
		}
		else
		{
			int hours = (int) (_reuse_time - _remaining_time) / 3600000;
			int minutes = (int) (_reuse_time - _remaining_time) % 3600000 / 60000;
			if (hours > 0)
			{
				player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S1_HOURSS_AND_S2_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(hours).addNumber(minutes));
			}
			else if (minutes > 0)
			{
				player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S1_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(minutes));
			}
			else if (getItemCount(player, ADENA) >= GIFT_PRICE)
			{
				removeItem(player, ADENA, GIFT_PRICE, "FreyaEvent");
				addItem(player, GIFT_ID, 1, "FreyaEvent");
				player.setVar("FreyaCelebration", String.valueOf(_curr_time), -1);
			}
			else
			{
				player.sendPacket(new SystemMessage(SystemMessage._2_UNITS_OF_THE_ITEM_S1_IS_REQUIRED).addNumber(GIFT_PRICE));
			}
		}
	}
}