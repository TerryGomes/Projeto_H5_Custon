package events.PiratesTreasure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Announcements;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.Location;

/**
 *
 * @author FandC
 */

public class PiratesTreasure extends Functions implements ScriptFile
{

	public static int id;
	public static String pointInfo;
	public static boolean eventStoped;
	public Location loc;

	public static final Logger _log = LoggerFactory.getLogger(PiratesTreasure.class);

	@Override
	public void onLoad()
	{
		_log.info("Loaded Event: PiratesTreasure loaded.");
	}

	@Override
	public void onReload()
	{

	}

	@Override
	public void onShutdown()
	{

	}

	public void startEvent(String args[])
	{

		Player player = getSelf();
		if (!player.getPlayerAccess().IsEventGm)
		{
			return;
		}
		id = Integer.parseInt(args[0]);
		sayToAll("The Pirate Ship is approaching!");
		executeTask("events.PiratesTreasure.PiratesTreasure", "callPirates", new Object[0], 60000);
		executeTask("events.PiratesTreasure.PiratesTreasure", "stopEvent", new Object[0], 31 * 60000);

	}

	public static void stopEvent()
	{
		sayToAll("Pirate King of Darkness not founded and get away!");
		eventStoped = true;
	}

	public static void sayToAll(String text)
	{
		Announcements.getInstance().announceToAll(text);
	}

	public void callPirates()
	{

		switch (id)
		{
		case 1:
			loc = new Location(-102296, 257448, -2960, 0);
			spawn(loc, 13099);
			pointInfo = "The Pirate King was seen in the port of Talking Island!";
			break;
		case 2:
			loc = new Location(51928, 187528, -3624, 0);
			spawn(loc, 13099);
			pointInfo = "The Pirate King was in Giran Harbor.";
			break;
		case 3:
			loc = new Location(48728, -190328, -3624, 0);
			spawn(loc, 13099);
			pointInfo = "The Pirate King was in Giran Harbor.";
			break;
		case 4:
			loc = new Location(8632, -23944, -3760, 0);
			spawn(loc, 13099);
			pointInfo = "The Pirate King was seen near Primeval Isle.";
			break;
		case 5:
			loc = new Location(-37912, -101096, -3728, 0);
			spawn(loc, 13099);
			pointInfo = "The Pirate King was seen near Vallery of Heroes.";
			break;
		case 6:
			loc = new Location(34552, -38024, -3640, 0);
			spawn(loc, 13099);
			pointInfo = "The Pirate King was seen near Rune Harbor.";
			break;
		case 7:
			loc = new Location(41112, -37688, -3632, 0);
			spawn(loc, 13099);
			pointInfo = "The Pirate King was seen near Rune Harbor.";
			break;
		case 8:
			loc = new Location(-89960, 154584, -3728, 0);
			spawn(loc, 13099);
			pointInfo = "The Pirate King was seen near Gludin Harbor.";
			break;
		}
		sayToAll(pointInfo);
		sayToAll("Hurry! King can escape! Remaining time: 30 minutes");

	}

	public static void annoncePointInfo()
	{
		sayToAll(pointInfo);
	}

}
