package events.March8;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.util.Rnd;
import l2f.gameserver.Announcements;
import l2f.gameserver.Config;
import l2f.gameserver.cache.Msg;
import l2f.gameserver.listener.actor.OnDeathListener;
import l2f.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.SimpleSpawner;
import l2f.gameserver.model.actor.listener.CharListenerList;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.utils.Util;

/**
 * Эвент к 8 марта: http://www.lineage2.com/archive/2009/01/the_valentine_e.html
 *
 * @author SYS
 */
public class March8 extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener
{
	private static final Logger _log = LoggerFactory.getLogger(March8.class);
	private static final String EVENT_NAME = "March8";
	private static final int RECIPE_PRICE = 50000; // 50.000 adena at x1 servers
	private static final int RECIPE_ID = 20191;
	private static final int EVENT_MANAGER_ID = 4301;
	private static List<SimpleSpawner> _spawns = new ArrayList<SimpleSpawner>();
	private static final int[] DROP =
	{
		20192,
		20193,
		20194
	};
	private static boolean _active = false;

	/**
	 * Спавнит эвент менеджеров
	 */
	private void spawnEventManagers()
	{
		final int EVENT_MANAGERS[][] =
		{
			{
				-14823,
				123567,
				-3143,
				8192
			}, // Gludio
			{
				-83159,
				150914,
				-3155,
				49152
			}, // Gludin
			{
				18600,
				145971,
				-3095,
				40960
			}, // Dion
			{
				82158,
				148609,
				-3493,
				60
			}, // Giran
			{
				110992,
				218753,
				-3568,
				0
			}, // Hiene
			{
				116339,
				75424,
				-2738,
				0
			}, // Hunter Village
			{
				81140,
				55218,
				-1551,
				32768
			}, // Oren
			{
				147148,
				27401,
				-2231,
				2300
			}, // Aden
			{
				43532,
				-46807,
				-823,
				31471
			}, // Rune
			{
				87765,
				-141947,
				-1367,
				6500
			}, // Schuttgart
			{
				147154,
				-55527,
				-2807,
				61300
			} // Goddard
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

	/**
	 * Читает статус эвента из базы.
	 * @return
	 */
	private static boolean isActive()
	{
		return IsActive(EVENT_NAME);
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

		if (SetActive(EVENT_NAME, true))
		{
			spawnEventManagers();
			System.out.println("Event: March 8 started.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.March8.AnnounceEventStarted", null);
		}
		else
		{
			player.sendMessage("Event 'March 8' already started.");
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
		if (SetActive(EVENT_NAME, false))
		{
			unSpawnEventManagers();
			System.out.println("Event: March 8 stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.March8.AnnounceEventStoped", null);
		}
		else
		{
			player.sendMessage("Event 'March 8' not started.");
		}

		_active = false;
		show("admin/events/events.htm", player);
	}

	/**
	 * Продает рецепт игроку
	 */
	public void buyrecipe()
	{
		Player player = getSelf();

		if (!player.isQuestContinuationPossible(true) || !NpcInstance.canBypassCheck(player, player.getLastNpc()))
		{
			return;
		}

		long need_adena = (long) (RECIPE_PRICE * Config.EVENT_MARCH8_PRICE_RATE);
		if (player.getAdena() < need_adena)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		player.reduceAdena(need_adena, true, "March8");
		Functions.addItem(player, RECIPE_ID, 1, "March8");
	}

	public String DialogAppend_4301(Integer val)
	{
		if (val != 0)
		{
			return "";
		}

		String price;
		String append = "";
		price = Util.formatAdena((long) (RECIPE_PRICE * Config.EVENT_MARCH8_PRICE_RATE));
		append += "<br><a action=\"bypass -h scripts_events.March8.March8:buyrecipe\">";
		append += new CustomMessage("scripts.events.March8.buyrecipe", getSelf()).addString(price);
		append += "</a><br>";
		return append;
	}

	@Override
	public void onLoad()
	{
		CharListenerList.addGlobal(this);
		if (isActive())
		{
			_active = true;
			spawnEventManagers();
			_log.info("Loaded Event: March 8 [state: activated]");
		}
		else
		{
			_log.info("Loaded Event: March 8 [state: deactivated]");
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

	@Override
	public void onPlayerEnter(Player player)
	{
		if (_active)
		{
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.March8.AnnounceEventStarted", null);
		}
	}

	/**
	 * Обработчик смерти мобов, управляющий эвентовым дропом
	 */
	@Override
	public void onDeath(Creature cha, Creature killer)
	{
		if (_active && SimpleCheckDrop(cha, killer) && Rnd.chance(Config.EVENT_MARCH8_DROP_CHANCE * killer.getPlayer().getRateItems() * ((NpcInstance) cha).getTemplate().rateHp))
		{
			((NpcInstance) cha).dropItem(killer.getPlayer(), DROP[Rnd.get(DROP.length)], 1);
		}
	}
}