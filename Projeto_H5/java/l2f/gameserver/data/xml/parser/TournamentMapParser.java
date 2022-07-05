package l2f.gameserver.data.xml.parser;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Element;

import l2f.commons.data.xml.AbstractFileParser;
import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.TournamentMapHolder;
import l2f.gameserver.model.entity.tournament.TournamentMap;
import l2f.gameserver.utils.Location;

public final class TournamentMapParser extends AbstractFileParser<TournamentMapHolder>
{
	public static final String TEAM_1_SPAWN_NAME = "team_1_spawn";
	public static final String TEAM_2_SPAWN_NAME = "team_2_spawn";
	public static final String OBSERVERS_SPAWN_NAME = "observers_spawn";

	private TournamentMapParser()
	{
		super(TournamentMapHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/tournament_maps.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "tournament_maps.dtd";
	}

	@Override
	protected void readData(Element rootElement)
	{
		final Iterator<Element> iterator = rootElement.elementIterator("map");
		while (iterator.hasNext())
		{
			final Element mapElement = iterator.next();
			final String name = mapElement.attributeValue("name");
			final Element zoneElement = mapElement.element("zone");
			final String zoneName = zoneElement.attributeValue("name");
			final Map<String, Location> locations = new HashMap<String, Location>(3);
			final Iterator<Element> parameterIterator = mapElement.elementIterator("loc");
			while (parameterIterator.hasNext())
			{
				final Element locElement = parameterIterator.next();
				final String locName = locElement.attributeValue("name");
				final int x = Integer.parseInt(locElement.attributeValue("x"));
				final int y = Integer.parseInt(locElement.attributeValue("y"));
				final int z = Integer.parseInt(locElement.attributeValue("z"));
				locations.put(locName, new Location(x, y, z));
			}
			getHolder().addMap(new TournamentMap(name, zoneName, locations));
		}
	}

	public static TournamentMapParser getInstance()
	{
		return SingletonHolder.instance;
	}

	private static class SingletonHolder
	{
		private static final TournamentMapParser instance = new TournamentMapParser();
	}
}
