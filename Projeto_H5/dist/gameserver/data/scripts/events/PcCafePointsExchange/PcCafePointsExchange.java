package events.PcCafePointsExchange;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;

public class PcCafePointsExchange extends Functions implements ScriptFile
{
	private static final Logger _log = LoggerFactory.getLogger(PcCafePointsExchange.class);
	private static final String EVENT_NAME = "PcCafePointsExchange";
	private static List<SimpleSpawner> _spawns = new ArrayList<SimpleSpawner>();

	/**
	 * Спавнит эвент менеджеров
	 */
	private void spawnEventManagers()
	{

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
			System.out.println("Event: 'PcCafePointsExchange' started.");
		}
		else
		{
			player.sendMessage("Event 'PcCafePointsExchange' already started.");
		}

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
			System.out.println("Event: 'PcCafePointsExchange' stopped.");
		}
		else
		{
			player.sendMessage("Event: 'PcCafePointsExchange' not started.");
		}

		show("admin/events/events.htm", player);
	}

	@Override
	public void onLoad()
	{
		if (isActive())
		{
			spawnEventManagers();
			_log.info("Loaded Event: PcCafePointsExchange [state: activated]");
		}
		else
		{
			_log.info("Loaded Event: PcCafePointsExchange [state: deactivated]");
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
}