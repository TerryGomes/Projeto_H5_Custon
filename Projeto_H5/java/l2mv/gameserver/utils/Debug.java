package l2mv.gameserver.utils;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.GameServer;
import l2mv.gameserver.listener.game.OnConfigsReloaded;

public enum Debug
{
	TOURNAMENT("TournamentDebug", "tournament");

	private boolean _isActive;
	private String _configName;
	private final Logger _logger;

	private Debug(boolean isActive, String loggerName)
	{
		_isActive = isActive;
		_logger = LoggerFactory.getLogger(loggerName);
	}

	private Debug(String configName, String loggerName)
	{
		_isActive = ConfigHolder.getBool(configName);
		_configName = configName;
		_logger = LoggerFactory.getLogger(loggerName);
	}

	public boolean isActive()
	{
		switch (this)
		{
		case TOURNAMENT:
		{
			return true;
		}
		default:
		{
			return false;
		}
		}
	}

	public void debug(Object classObject, String method, Object... values)
	{
		if (_isActive)
		{
			_logger.info(toString() + ": " + classObject.getClass().getSimpleName() + "#" + method + Arrays.toString(values));
		}
	}

	public void debug(Class<?> clazz, String method, Object... values)
	{
		if (_isActive)
		{
			_logger.info(toString() + ": " + clazz.getSimpleName() + "#" + method + Arrays.toString(values));
		}
	}

	public static void initListeners()
	{
		GameServer.getInstance().addListener(new ReloadConfigsListener());
	}

	public static void onReloadConfigs()
	{
		for (Debug debug : values())
		{
			if (debug._configName != null)
			{
				debug._isActive = ConfigHolder.getBool(debug._configName);
			}
		}
	}

	private static class ReloadConfigsListener implements OnConfigsReloaded
	{
		@Override
		public void onConfigsReloaded()
		{
			Debug.onReloadConfigs();
		}
	}
}
