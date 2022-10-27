package l2mv.gameserver.fandc.tournament;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.configuration.ExProperties;
import l2mv.gameserver.fandc.tournament.model.ItemHolder;
import l2mv.gameserver.fandc.tournament.model.RewardHolder;
import l2mv.gameserver.utils.Location;

public class TournamentConfig
{
	private static final Logger _log = LoggerFactory.getLogger(TournamentConfig.class);

	public static final String CONFIG_FILE = "config/mod/tournament.ini";

	public static int TELEPORT_SECONDS;
	public static List<Integer> TOURNAMENTS = new ArrayList<>();
	public static List<RewardHolder> REWARDS = new ArrayList<>();
	public static List<ItemHolder> REQUIRES = new ArrayList<>();
	public static int MIN_LEVEL;
	public static int MAX_LEVEL;

	public static int TOURNAMENT_DURATION;

	public static boolean BOOLEAN_CHECK_IP;

	public static Location TEAM_RED_LOCATION;
	public static Location TEAM_BLUE_LOCATION;

	public static boolean init()
	{
		try
		{
			ExProperties tourSettings = load(CONFIG_FILE);

			String[] tournaments = tourSettings.getProperty("Tournaments", "2,4,6").split(",");

			for (String st : tournaments)
			{
				TOURNAMENTS.add(Integer.parseInt(st));
			}

			TELEPORT_SECONDS = tourSettings.getProperty("TeleportToFieldSeconds", 10);
			MIN_LEVEL = tourSettings.getProperty("MinLevel", 1);
			MAX_LEVEL = tourSettings.getProperty("MaxLevel", 99);

			TOURNAMENT_DURATION = tourSettings.getProperty("TournamentDuration", 360);

			BOOLEAN_CHECK_IP = tourSettings.getProperty("CheckIpOnRegister", true);

			String[] rewards = tourSettings.getProperty("Rewards", "57,100;6393,100").split(";");

			for (String rewardSet : rewards)
			{
				REWARDS.add(new RewardHolder(Integer.parseInt(rewardSet.split(",")[0]), Integer.parseInt(rewardSet.split(",")[1])));
			}

			String[] requires = tourSettings.getProperty("RequiredItems", "57,100").split(";");

			for (String req : requires)
			{
				REQUIRES.add(new ItemHolder(Integer.parseInt(req.split(",")[0]), Integer.parseInt(req.split(",")[1])));
			}

			String[] location = tourSettings.getProperty("TeamRedLoc", "149160,143736,-12260").split(",");

			TEAM_RED_LOCATION = new Location(Integer.parseInt(location[0]), Integer.parseInt(location[1]), Integer.parseInt(location[2]));

			location = tourSettings.getProperty("TeamBlueLoc", "144488,147464,-12155").split(",");

			TEAM_BLUE_LOCATION = new Location(Integer.parseInt(location[0]), Integer.parseInt(location[1]), Integer.parseInt(location[2]));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static ExProperties load(String filename)
	{
		return load(new File(filename));
	}

	public static ExProperties load(File file)
	{
		ExProperties result = new ExProperties();

		try
		{
			result.load(file);
		}
		catch (IOException e)
		{
			_log.error("Error loading config : " + file.getName() + "!", e);
		}

		return result;
	}
}
