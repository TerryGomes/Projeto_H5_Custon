package l2mv.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

import l2mv.commons.data.xml.AbstractDirParser;
import l2mv.commons.geometry.Circle;
import l2mv.commons.geometry.Polygon;
import l2mv.commons.geometry.Rectangle;
import l2mv.commons.geometry.Shape;
import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.ZoneHolder;
import l2mv.gameserver.model.Territory;
import l2mv.gameserver.model.World;
import l2mv.gameserver.templates.StatsSet;
import l2mv.gameserver.templates.ZoneTemplate;
import l2mv.gameserver.utils.Location;

/**
 * @author G1ta0
 */
public class ZoneParser extends AbstractDirParser<ZoneHolder>
{
	private static final ZoneParser _instance = new ZoneParser();

	public static ZoneParser getInstance()
	{
		return _instance;
	}

	protected ZoneParser()
	{
		super(ZoneHolder.getInstance());
	}

	@Override
	public File getXMLDir()
	{
		return new File(Config.DATAPACK_ROOT, "data/zone/");
	}

	@Override
	public boolean isIgnored(File f)
	{
		return false;
	}

	@Override
	public String getDTDFileName()
	{
		return "zone.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			StatsSet zoneDat = new StatsSet();
			Element zoneElement = iterator.next();

			if ("zone".equals(zoneElement.getName()))
			{
				zoneDat.set("name", zoneElement.attribute("name").getValue());
				zoneDat.set("type", zoneElement.attribute("type").getValue());

				Territory territory = null;
				boolean isShape;

				for (Iterator<Element> i = zoneElement.elementIterator(); i.hasNext();)
				{
					Element n = i.next();
					if ("set".equals(n.getName()))
					{
						zoneDat.set(n.attributeValue("name"), n.attributeValue("val"));
					}
					else if ("restart_point".equals(n.getName()))
					{
						List<Location> restartPoints = new ArrayList<Location>();
						for (Iterator<Element> ii = n.elementIterator(); ii.hasNext();)
						{
							Element d = ii.next();
							if ("coords".equalsIgnoreCase(d.getName()))
							{
								Location loc = Location.parseLoc(d.attribute("loc").getValue());
								restartPoints.add(loc);
							}
						}
						zoneDat.set("restart_points", restartPoints);
					}
					else if ("PKrestart_point".equals(n.getName()))
					{
						List<Location> PKrestartPoints = new ArrayList<Location>();
						for (Iterator<Element> ii = n.elementIterator(); ii.hasNext();)
						{
							Element d = ii.next();
							if ("coords".equalsIgnoreCase(d.getName()))
							{
								Location loc = Location.parseLoc(d.attribute("loc").getValue());
								PKrestartPoints.add(loc);
							}
						}
						zoneDat.set("PKrestart_points", PKrestartPoints);
					}
					else if ((isShape = "rectangle".equalsIgnoreCase(n.getName())) || "banned_rectangle".equalsIgnoreCase(n.getName()))
					{
						Shape shape = parseRectangle(n);

						if (territory == null)
						{
							territory = new Territory();
							zoneDat.set("territory", territory);
						}

						if (isShape)
						{
							territory.add(shape);
						}
						else
						{
							territory.addBanned(shape);
						}
					}
					else if ((isShape = "circle".equalsIgnoreCase(n.getName())) || "banned_cicrcle".equalsIgnoreCase(n.getName()))
					{
						Shape shape = parseCircle(n);

						if (territory == null)
						{
							territory = new Territory();
							zoneDat.set("territory", territory);
						}

						if (isShape)
						{
							territory.add(shape);
						}
						else
						{
							territory.addBanned(shape);
						}
					}
					else if ((isShape = "polygon".equalsIgnoreCase(n.getName())) || "banned_polygon".equalsIgnoreCase(n.getName()))
					{
						Polygon shape = parsePolygon(n);

						if (!shape.validate())
						{
							error("ZoneParser: invalid territory data : " + shape + ", zone: " + zoneDat.getString("name") + "!");
						}

						if (territory == null)
						{
							territory = new Territory();
							zoneDat.set("territory", territory);
						}

						if (isShape)
						{
							territory.add(shape);
						}
						else
						{
							territory.addBanned(shape);
						}
					}
				}

				if (territory == null || territory.getTerritories().isEmpty())
				{
					error("Empty territory for zone: " + zoneDat.get("name"));
				}
				ZoneTemplate template = new ZoneTemplate(zoneDat);
				getHolder().addTemplate(template);
			}
		}
	}

	public static Rectangle parseRectangle(Element n) throws Exception
	{
		int x1, y1, x2, y2, zmin = World.MAP_MIN_Z, zmax = World.MAP_MAX_Z;

		Iterator<Element> i = n.elementIterator();

		Element d = i.next();
		String[] coord = d.attributeValue("loc").split("[\\s,;]+");
		x1 = Integer.parseInt(coord[0]);
		y1 = Integer.parseInt(coord[1]);
		if (coord.length > 2)
		{
			zmin = Integer.parseInt(coord[2]);
			zmax = Integer.parseInt(coord[3]);
		}

		d = i.next();
		coord = d.attributeValue("loc").split("[\\s,;]+");
		x2 = Integer.parseInt(coord[0]);
		y2 = Integer.parseInt(coord[1]);
		if (coord.length > 2)
		{
			zmin = Integer.parseInt(coord[2]);
			zmax = Integer.parseInt(coord[3]);
		}

		Rectangle rectangle = new Rectangle(x1, y1, x2, y2);
		rectangle.setZmin(zmin);
		rectangle.setZmax(zmax);

		return rectangle;
	}

	public static Polygon parsePolygon(Element shape) throws Exception
	{
		Polygon poly = new Polygon();

		for (Iterator<Element> i = shape.elementIterator(); i.hasNext();)
		{
			Element d = i.next();
			if ("coords".equals(d.getName()))
			{
				String[] coord = d.attributeValue("loc").split("[\\s,;]+");
				if (coord.length < 4)
				{ // Не указаны minZ и maxZ, берем граничные значения
					poly.add(Integer.parseInt(coord[0]), Integer.parseInt(coord[1])).setZmin(World.MAP_MIN_Z).setZmax(World.MAP_MAX_Z);
				}
				else
				{ // Не указаны minZ и maxZ, берем граничные значения
					poly.add(Integer.parseInt(coord[0]), Integer.parseInt(coord[1])).setZmin(Integer.parseInt(coord[2])).setZmax(Integer.parseInt(coord[3]));
				}
			}
		}

		return poly;
	}

	public static Circle parseCircle(Element shape) throws Exception
	{
		Circle circle;

		String[] coord = shape.attribute("loc").getValue().split("[\\s,;]+");
		if (coord.length < 5)
		{ // Не указаны minZ и maxZ, берем граничные значения
			circle = new Circle(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]), Integer.parseInt(coord[2])).setZmin(World.MAP_MIN_Z).setZmax(World.MAP_MAX_Z);
		}
		else
		{ // Не указаны minZ и maxZ, берем граничные значения
			circle = new Circle(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]), Integer.parseInt(coord[2])).setZmin(Integer.parseInt(coord[3])).setZmax(Integer.parseInt(coord[4]));
		}

		return circle;
	}
}