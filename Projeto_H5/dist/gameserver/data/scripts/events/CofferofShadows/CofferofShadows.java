package events.CofferofShadows;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Announcements;
import l2f.gameserver.Config;
import l2f.gameserver.cache.Msg;
import l2f.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.SimpleSpawner;
import l2f.gameserver.model.actor.listener.CharListenerList;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.utils.Util;

// Эвент Coffer of Shadows
public class CofferofShadows extends Functions implements ScriptFile, OnPlayerEnterListener
{
	private static int COFFER_PRICE = 50000; // 50.000 adena at x1 servers
	private static int COFFER_ID = 8659;
	private static int EVENT_MANAGER_ID = 32091;
	private static List<SimpleSpawner> _spawns = new ArrayList<SimpleSpawner>();
	private static final Logger _log = LoggerFactory.getLogger(CofferofShadows.class);
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
		return IsActive("CofferofShadows");
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

		if (SetActive("CofferofShadows", true))
		{
			spawnEventManagers();
			System.out.println("Event: Coffer of Shadows started.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.CofferofShadows.AnnounceEventStarted", null);
		}
		else
		{
			player.sendMessage("Event 'Coffer of Shadows' already started.");
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
		if (SetActive("CofferofShadows", false))
		{
			unSpawnEventManagers();
			System.out.println("Event: Coffer of Shadows stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.CofferofShadows.AnnounceEventStoped", null);
		}
		else
		{
			player.sendMessage("Event 'Coffer of Shadows' not started.");
		}

		_active = false;
		show("admin/events/events.htm", player);
	}

	/**
	 * Продает 1 сундук игроку
	 * @param var
	 */
	public void buycoffer(String[] var)
	{
		Player player = getSelf();

		if (!player.isQuestContinuationPossible(true) || !NpcInstance.canBypassCheck(player, player.getLastNpc()))
		{
			return;
		}

		int coffer_count = 1;
		try
		{
			coffer_count = Integer.valueOf(var[0]);
		}
		catch (Exception E)
		{
		}

		long need_adena = (long) (COFFER_PRICE * Config.EVENT_CofferOfShadowsPriceRate * coffer_count);
		if (player.getAdena() < need_adena)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		player.reduceAdena(need_adena, true, "BuyCofferOfShadows");
		Functions.addItem(player, COFFER_ID, coffer_count, "BuyCofferOfShadows");
	}

	/**
	 * Добавляет в диалоги эвент менеджеров строчку с байпасом для покупки сундука
	 */
	private static int[] buycoffer_counts =
	{
		1,
		5,
		10,
		50
	}; // TODO в конфиг

	public String DialogAppend_32091(Integer val)
	{
		if (val != 0)
		{
			return "";
		}

		String price;
		String append = "";
		for (int cnt : buycoffer_counts)
		{
			price = Util.formatAdena((long) (COFFER_PRICE * Config.EVENT_CofferOfShadowsPriceRate * cnt));
			append += "<a action=\"bypass -h scripts_events.CofferofShadows.CofferofShadows:buycoffer " + cnt + "\">";
			if (cnt == 1)
			{
				append += new CustomMessage("scripts.events.CofferofShadows.buycoffer", getSelf()).addString(price);
			}
			else
			{
				append += new CustomMessage("scripts.events.CofferofShadows.buycoffers", getSelf()).addNumber(cnt).addString(price);
			}
			append += "</a><br>";
		}

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
			_log.info("Loaded Event: Coffer of Shadows [state: activated]");
		}
		else
		{
			_log.info("Loaded Event: Coffer of Shadows [state: deactivated]");
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
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.CofferofShadows.AnnounceEventStarted", null);
		}
	}
}