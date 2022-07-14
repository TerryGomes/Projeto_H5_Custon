package events.SummerMeleons;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.Announcements;
import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.MultiSellHolder;
import l2mv.gameserver.listener.actor.OnDeathListener;
import l2mv.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.model.actor.listener.CharListenerList;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;

public class SummerMeleons extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener
{
	private static final Logger _log = LoggerFactory.getLogger(SummerMeleons.class);
	private static int EVENT_MANAGER_ID = 32636;
	private static List<SimpleSpawner> _spawns = new ArrayList<SimpleSpawner>();

	private static boolean _active = false;
	private static boolean MultiSellLoaded = false;

	private static File multiSellFile = new File(Config.DATAPACK_ROOT, "data/multisell/events/SummerMeleons/3790004.xml");

	@Override
	public void onLoad()
	{
		CharListenerList.addGlobal(this);
		if (isActive())
		{
			_active = true;
			loadMultiSell();
			spawnEventManagers();
			_log.info("Loaded Event: Summer Meleons [state: activated]");
		}
		else
		{
			_log.info("Loaded Event: Summer Meleons [state: deactivated]");
		}
	}

	/**
	 * Читает статус эвента из базы.
	 * @return
	 */
	private static boolean isActive()
	{
		return IsActive("SummerMeleons");
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

		if (SetActive("SummerMeleons", true))
		{
			loadMultiSell();
			spawnEventManagers();
			System.out.println("Event 'Summer Meleons' started.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.SummerMeleons.AnnounceEventStarted", null);
		}
		else
		{
			player.sendMessage("Event 'Summer Meleons' already started.");
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
		if (SetActive("SummerMeleons", false))
		{
			unSpawnEventManagers();
			System.out.println("Event 'Summer Meleons' stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.SummerMeleons.AnnounceEventStoped", null);
		}
		else
		{
			player.sendMessage("Event 'Summer Meleons' not started.");
		}

		_active = false;

		show("admin/events/events.htm", player);
	}

	/**
	 * Спавнит эвент менеджеров
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
				33202
			},
			{
				-12347,
				122549,
				-3104,
				32603
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
				22021
			}
		};

		SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS, _spawns);
	}

	/**
	 * Удаляет спавн эвент менеджеров
	 */
	private void unSpawnEventManagers()
	{
		deSpawnNPCs(_spawns);
	}

	private static void loadMultiSell()
	{
		if (MultiSellLoaded)
		{
			return;
		}
		MultiSellHolder.getInstance().parseFile(multiSellFile);
		MultiSellLoaded = true;
	}

	@Override
	public void onReload()
	{
		unSpawnEventManagers();
		if (MultiSellLoaded)
		{
			MultiSellHolder.getInstance().remove(multiSellFile);
			MultiSellLoaded = false;
		}
	}

	@Override
	public void onShutdown()
	{

	}

	/**
	 * Обработчик смерти мобов, управляющий эвентовым дропом
	 */
	@Override
	public void onDeath(Creature cha, Creature killer)
	{
		if (_active && SimpleCheckDrop(cha, killer) && Rnd.chance(Config.EVENT_TFH_POLLEN_CHANCE * killer.getPlayer().getRateItems() * ((NpcInstance) cha).getTemplate().rateHp))
		{
			((NpcInstance) cha).dropItem(killer.getPlayer(), 6391, 1);
		}
	}

	@Override
	public void onPlayerEnter(Player player)
	{
		if (_active)
		{
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.SummerMeleons.AnnounceEventStarted", null);
		}
	}
}