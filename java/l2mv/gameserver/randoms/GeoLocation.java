package l2mv.gameserver.randoms;

import java.io.File;

import com.maxmind.geoip.LookupService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.model.Player;

/**
 * GeoLocation grep.
 *
 * @author Infern0
 */
public class GeoLocation
{
	private static final Logger _log = LoggerFactory.getLogger(GeoLocation.class);

	private static final String[] RUSSIAN_COUNTRIES =
	{
		"Russia",
		"Belarus",
		"Kazakhstan",
		"Moldova",
		"Kyrgyzstan",
		"Tajikistan",
		"Ukraine"
	};

	private static LookupService _lookup;

	public GeoLocation()
	{
		try
		{
			_lookup = new LookupService(new File("config/GeoLiteCity.dat"), LookupService.GEOIP_MEMORY_CACHE);
			_lookup.close(); // Everything is cached, close the file.
		}
		catch (Exception e)
		{
			_lookup = null;
			_log.warn("GeoLocation unable to load, skipping: ", e);
		}
	}

	public boolean isFromRussia(Player player)
	{
		if (player == null || player.getClient() == null)
		{
			return false;
		}

		final String ip = player.getIP();
		if (ip == null || ip.equalsIgnoreCase(Player.NOT_CONNECTED))
		{
			return false;
		}

		for (String count : RUSSIAN_COUNTRIES)
		{
			if (count.equalsIgnoreCase(getCountry(ip)))
			{
				return true;
			}
		}
		return false;
	}

	public static String getCountryCode(Player player)
	{
		if ((player == null) || (player.getClient() == null))
		{
			return "NULL";
		}

		String ip = player.getIP();
		boolean notConnected = ip.equalsIgnoreCase(Player.NOT_CONNECTED);
		if (notConnected)
		{
			ip = player.getVar("LastIP") == null ? "NULL" : ip;
		}

		return getCountryCode(ip) + (notConnected ? "*" : "");
	}

	public static String getCountryCode(String ip)
	{
		if (_lookup == null)
		{
			return "NULL";
		}

		try
		{
			return _lookup.getLocation(ip) == null ? "Null" : _lookup.getLocation(ip).countryCode;
		}
		catch (final Exception e)
		{
			_log.warn("Error while getting GeoLocation data for IP: " + ip + "; err:", e);
			return "NULL";
		}
	}

	public String getCountry(Player player)
	{
		if (player == null || player.getClient() == null)
		{
			return "NULL";
		}

		String ip = player.getIP();
		boolean notConnected = ip.equalsIgnoreCase(Player.NOT_CONNECTED);
		if (notConnected)
		{
			ip = player.getVar("LastIP") == null ? "NULL" : ip;
		}
		return getCountry(ip) + (notConnected ? "*" : "");
	}

	public String getCountry(String ip)
	{
		if (_lookup == null)
		{
			return "NULL";
		}

		try
		{
			return _lookup.getLocation(ip) == null ? "Null" : _lookup.getLocation(ip).countryName;
		}
		catch (final Exception e)
		{
			_log.warn("Error while getting GeoLocation data for IP: " + ip + "; err:", e);
			return "NULL";
		}
	}

	public static String getCity(Player player)
	{
		if (player == null || player.getClient() == null)
		{
			return "NULL";
		}

		String ip = player.getIP();
		boolean notConnected = ip.equalsIgnoreCase(Player.NOT_CONNECTED);
		if (notConnected)
		{
			ip = player.getVar("LastIP") == null ? "NULL" : ip;
		}
		return getCity(ip) + (notConnected ? "*" : "");
	}

	public static String getCity(String ip)
	{
		if (_lookup == null)
		{
			return "NULL";
		}

		try
		{
			return _lookup.getLocation(ip) == null ? "Null" : _lookup.getLocation(ip).city;
		}
		catch (final Exception e)
		{
			_log.warn("Error while getting GeoLocation data for IP: " + ip + "; err:", e);
			return "NULL";
		}
	}

	public String getCityRegion(Player player)
	{
		if (player == null || player.getClient() == null)
		{
			return "NULL";
		}

		String ip = player.getIP();
		boolean notConnected = ip.equalsIgnoreCase(Player.NOT_CONNECTED);
		if (notConnected)
		{
			ip = player.getVar("LastIP") == null ? "NULL" : ip;
		}
		return getCityRegion(ip) + (notConnected ? "*" : "");
	}

	public String getCityRegion(String ip)
	{
		if (_lookup == null)
		{
			return "NULL";
		}

		try
		{
			return _lookup.getLocation(ip) == null ? "Null" : _lookup.getLocation(ip).region;
		}
		catch (final Exception e)
		{
			_log.warn("Error while getting GeoLocation data for IP: " + ip + "; err:", e);
			return "NULL";
		}
	}

	public static GeoLocation getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final GeoLocation _instance = new GeoLocation();
	}
}
