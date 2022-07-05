package l2f.gameserver.tables;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.model.GameObjectsStorage;

public class FakePlayersTable
{
	private static final Logger LOG = LoggerFactory.getLogger(FakePlayersTable.class);
	private static String[] _fakePlayers;
	private static List<String> _activeFakePlayers = new ArrayList<>();
	private static FakePlayersTable _instance;

	public static FakePlayersTable getInstance()
	{
		if (_instance == null)
		{
			new FakePlayersTable();
		}
		return _instance;
	}

	public FakePlayersTable()
	{
		_instance = this;
		if (Config.ALLOW_FAKE_PLAYERS)
		{
			parseData();
			ThreadPoolManager.getInstance().scheduleAtFixedRate(new Task(), 180000L, 1000L);
		}
	}

	private static void parseData()
	{
		File doorData = new File("config/fake_players.list");
		try (LineNumberReader lnr = new LineNumberReader(new BufferedReader(new FileReader(doorData))))
		{

			List<String> playersList = new ArrayList<>();
			String line;
			while ((line = lnr.readLine()) != null)
			{
				if (!line.trim().isEmpty() && !line.startsWith("#"))
				{
					playersList.add(line);
				}
			}
			_fakePlayers = playersList.toArray(new String[playersList.size()]);
			LOG.info("FakePlayersTable: Loaded " + _fakePlayers.length + " Fake Players.");
		}
		catch (IOException e)
		{
			LOG.error("Error while parsing fake_players", e);
		}
	}

	public static int getFakePlayersCount()
	{
		return _activeFakePlayers.size();
	}

	public static List<String> getActiveFakePlayers()
	{
		return _activeFakePlayers;
	}

	public static class Task implements Runnable
	{
		@SuppressWarnings("synthetic-access")
		@Override
		public void run()
		{
			try
			{
				if ((_activeFakePlayers.size() < ((Math.max(0, GameObjectsStorage.getAllPlayersCount() - GameObjectsStorage.getAllOfflineCount()) * Config.FAKE_PLAYERS_PERCENT) / 100))
							&& (_activeFakePlayers.size() < _fakePlayers.length))
				{
					if (Rnd.chance(50))
					{
						String player = _fakePlayers[Rnd.get(_fakePlayers.length)].toLowerCase();

						if (!_activeFakePlayers.contains(player))
						{
							_activeFakePlayers.add(player);
						}
					}
				}
				else if (!_activeFakePlayers.isEmpty())
				{
					_activeFakePlayers.remove(Rnd.get(_activeFakePlayers.size()));
				}
			}
			catch (RuntimeException e)
			{
				LOG.error("Error while creating Fake Players", e);
			}
		}
	}

	// Synerge - A holder to have the fake players that are created on real time
	private static final List<String> _realTimeFakePlayers = new CopyOnWriteArrayList<>();

	public static List<String> getRealTimeFakePlayers()
	{
		return _realTimeFakePlayers;
	}

	public static String getRealTimeFakePlayerRealName(String name)
	{
		for (String fake : _realTimeFakePlayers)
		{
			if (fake.equalsIgnoreCase(name))
			{
				return fake;
			}
		}
		return "";
	}

	public static boolean isRealTimeFakePlayerExist(String name)
	{
		for (String fake : _realTimeFakePlayers)
		{
			if (fake.equalsIgnoreCase(name))
			{
				return true;
			}
		}
		return false;
	}
}