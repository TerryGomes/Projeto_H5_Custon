package l2mv.gameserver.utils;

import java.io.Serializable;

import org.dom4j.Element;

import l2mv.commons.geometry.Point3D;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.geodata.GeoEngine;
import l2mv.gameserver.instancemanager.MapRegionManager;
import l2mv.gameserver.instancemanager.ReflectionManager;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.base.RestartType;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.templates.mapregion.RestartArea;
import l2mv.gameserver.templates.mapregion.RestartPoint;
import l2mv.gameserver.templates.spawn.SpawnRange;

public class Location extends Point3D implements SpawnRange, Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public int h;
	public int r; // ReflectionId. -1 = Multiverse

	public Location()
	{
	}

	/**
	 * Позиция (x, y, z, heading)
	 */
	public Location(int x, int y, int z, int heading)
	{
		super(x, y, z);
		h = heading;
		r = 0;
	}

	public Location(int x, int y, int z)
	{
		this(x, y, z, 0);
	}

	public Location(GameObject obj)
	{
		this(obj.getX(), obj.getY(), obj.getZ(), obj.getHeading());
	}

	public Location changeZ(int zDiff)
	{
		z += zDiff;
		return this;
	}

	public Location correctGeoZ()
	{
		z = GeoEngine.getHeight(x, y, z, 0);
		return this;
	}

	public Location correctGeoZ(int refIndex)
	{
		z = GeoEngine.getHeight(x, y, z, refIndex);
		return this;
	}

	@Override
	public Location setX(int x)
	{
		this.x = x;
		return this;
	}

	@Override
	public Location setY(int y)
	{
		this.y = y;
		return this;
	}

	public Location setZ(int z)
	{
		this.z = z;
		return this;
	}

	public Location setH(int h)
	{
		this.h = h;
		return this;
	}

	public Location set(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public Location set(int x, int y, int z, int h)
	{
		set(x, y, z);
		this.h = h;
		return this;
	}

	public Location set(Location loc)
	{
		x = loc.x;
		y = loc.y;
		z = loc.z;
		h = loc.h;
		return this;
	}

	public Location setR(int reflectionId)
	{
		r = reflectionId;
		return this;
	}

	public Location setR(Reflection reflection)
	{
		r = reflection != null ? reflection.getId() : ReflectionManager.DEFAULT.getId();
		return this;
	}

	public Location setR(GameObject obj)
	{
		r = obj.getReflectionId();
		return this;
	}

	public Location world2geo()
	{
		x = x - World.MAP_MIN_X >> 4;
		y = y - World.MAP_MIN_Y >> 4;
		return this;
	}

	public Location geo2world()
	{
		// размер одного блока 16*16 точек, +8*+8 это его средина
		x = (x << 4) + World.MAP_MIN_X + 8;
		y = (y << 4) + World.MAP_MIN_Y + 8;
		return this;
	}

	public double distance(Location loc)
	{
		return distance(loc.x, loc.y);
	}

	public double distance(int x, int y)
	{
		long dx = this.x - x;
		long dy = this.y - y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public double distance3D(Location loc)
	{
		return distance3D(loc.x, loc.y, loc.z);
	}

	public double distance3D(int x, int y, int z)
	{
		long dx = this.x - x;
		long dy = this.y - y;
		long dz = this.z - z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	@Override
	public Location clone()
	{
		return new Location(x, y, z, h);
	}

	@Override
	public final String toString()
	{
		return x + "," + y + "," + z + "," + h;
	}

	public boolean isNull()
	{
		return x == 0 || y == 0 || z == 0;
	}

	public final String toXYZString()
	{
		return x + " " + y + " " + z;
	}

	/**
	 * Парсит Location из строки, где координаты разделены пробелами или запятыми
	 */
	public static Location parseLoc(String s) throws IllegalArgumentException
	{
		if (s == null)
		{
			return null;
		}

		String[] xyzh = s.split("[\\s,;]+");
		if (xyzh.length < 3)
		{
			throw new IllegalArgumentException("Can't parse location from string: " + s);
		}
		int x = Integer.parseInt(xyzh[0]);
		int y = Integer.parseInt(xyzh[1]);
		int z = Integer.parseInt(xyzh[2]);
		int h = xyzh.length < 4 ? 0 : Integer.parseInt(xyzh[3]);
		return new Location(x, y, z, h);
	}

	public static Location parse(Element element)
	{
		int x = Integer.parseInt(element.attributeValue("x"));
		int y = Integer.parseInt(element.attributeValue("y"));
		int z = Integer.parseInt(element.attributeValue("z"));
		int h = element.attributeValue("h") == null ? 0 : Integer.parseInt(element.attributeValue("h"));
		return new Location(x, y, z, h);
	}

	/**
	 * Найти стабильную точку перед объектом obj1 для спавна объекта obj2, с учетом heading
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param radiusmin
	 * @param radiusmax
	 * @param geoIndex
	 * @return
	 */
	public static Location findFrontPosition(GameObject obj, GameObject obj2, int radiusmin, int radiusmax)
	{
		if (radiusmax == 0 || radiusmax < radiusmin)
		{
			return new Location(obj);
		}

		double collision = obj.getColRadius() + obj2.getColRadius();
		int randomRadius, randomAngle, tempz;
		int minangle = 0;
		int maxangle = 360;

		if (!obj.equals(obj2))
		{
			double angle = PositionUtils.calculateAngleFrom(obj, obj2);
			minangle = (int) angle - 45;
			maxangle = (int) angle + 45;
		}

		Location pos = new Location();
		for (int i = 0; i < 100; i++)
		{
			randomRadius = Rnd.get(radiusmin, radiusmax);
			randomAngle = Rnd.get(minangle, maxangle);
			pos.x = obj.getX() + (int) ((collision + randomRadius) * Math.cos(Math.toRadians(randomAngle)));
			pos.y = obj.getY() + (int) ((collision + randomRadius) * Math.sin(Math.toRadians(randomAngle)));
			pos.z = obj.getZ();
			tempz = GeoEngine.getHeight(pos.x, pos.y, pos.z, obj.getGeoIndex());
			if (Math.abs(pos.z - tempz) < 200 && GeoEngine.getNSWE(pos.x, pos.y, tempz, obj.getGeoIndex()) == GeoEngine.NSWE_ALL)
			{
				pos.z = tempz;
				if (!obj.equals(obj2))
				{
					pos.h = PositionUtils.getHeadingTo(pos, obj2.getLoc());
				}
				else
				{
					pos.h = obj.getHeading();
				}
				return pos;
			}
		}

		return new Location(obj);
	}

	/**
	 * Найти точку в пределах досягаемости от начальной
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param radiusmin
	 * @param radiusmax
	 * @param geoIndex
	 * @return
	 */
	public static Location findAroundPosition(int x, int y, int z, int radiusmin, int radiusmax, int geoIndex)
	{
		Location pos;
		int tempz;
		for (int i = 0; i < 100; i++)
		{
			pos = Location.coordsRandomize(x, y, z, 0, radiusmin, radiusmax);
			tempz = GeoEngine.getHeight(pos.x, pos.y, pos.z, geoIndex);
			if (GeoEngine.canMoveToCoord(x, y, z, pos.x, pos.y, tempz, geoIndex) && GeoEngine.canMoveToCoord(pos.x, pos.y, tempz, x, y, z, geoIndex))
			{
				pos.z = tempz;
				return pos;
			}
		}
		return new Location(x, y, z);
	}

	public static Location findAroundPosition(Location loc, int radius, int geoIndex)
	{
		return findAroundPosition(loc.x, loc.y, loc.z, 0, radius, geoIndex);
	}

	public static Location findAroundPosition(Location loc, int radiusmin, int radiusmax, int geoIndex)
	{
		return findAroundPosition(loc.x, loc.y, loc.z, radiusmin, radiusmax, geoIndex);
	}

	public static Location findAroundPosition(GameObject obj, Location loc, int radiusmin, int radiusmax)
	{
		return findAroundPosition(loc.x, loc.y, loc.z, radiusmin, radiusmax, obj.getGeoIndex());
	}

	public static Location findAroundPosition(GameObject obj, int radiusmin, int radiusmax)
	{
		return findAroundPosition(obj, obj.getLoc(), radiusmin, radiusmax);
	}

	public static Location findAroundPosition(GameObject obj, int radius)
	{
		return findAroundPosition(obj, 0, radius);
	}

	/**
	 * Найти стабильную точку в пределах радиуса от начальной
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param radiusmin
	 * @param radiusmax
	 * @param geoIndex
	 * @return
	 */
	public static Location findPointToStay(int x, int y, int z, int radiusmin, int radiusmax, int geoIndex)
	{
		Location pos;
		int tempz;
		for (int i = 0; i < 100; i++)
		{
			pos = Location.coordsRandomize(x, y, z, 0, radiusmin, radiusmax);
			tempz = GeoEngine.getHeight(pos.x, pos.y, pos.z, geoIndex);
			if (Math.abs(pos.z - tempz) < 200 && GeoEngine.getNSWE(pos.x, pos.y, tempz, geoIndex) == GeoEngine.NSWE_ALL)
			{
				pos.z = tempz;
				return pos;
			}
		}
		return new Location(x, y, z);
	}

	public static Location findPointToStay(Location loc, int radius, int geoIndex)
	{
		return findPointToStay(loc.x, loc.y, loc.z, 0, radius, geoIndex);
	}

	public static Location findPointToStay(Location loc, int radiusmin, int radiusmax, int geoIndex)
	{
		return findPointToStay(loc.x, loc.y, loc.z, radiusmin, radiusmax, geoIndex);
	}

	public static Location findPointToStay(GameObject obj, Location loc, int radiusmin, int radiusmax)
	{
		return findPointToStay(loc.x, loc.y, loc.z, radiusmin, radiusmax, obj.getGeoIndex());
	}

	public static Location findPointToStay(GameObject obj, int radiusmin, int radiusmax)
	{
		return findPointToStay(obj, obj.getLoc(), radiusmin, radiusmax);
	}

	public static Location findPointToStay(GameObject obj, int radius)
	{
		return findPointToStay(obj, 0, radius);
	}

	/**
	 * Gets a random XY within the given radius.
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 * @param radiusmin : Random's min
	 * @param radiusmax : Random;s max
	 * @return Location (rndX, rndY, z, heading)
	 */
	public static Location coordsRandomize(int x, int y, int z, int heading, int radiusmin, int radiusmax)
	{
		return new Location(x, y, z, heading).coordsRandomize(radiusmin, radiusmax);
	}

	public static Location findNearest(Creature creature, Location[] locs)
	{
		Location defloc = null;
		for (Location loc : locs)
		{
			if ((defloc == null) || (creature.getDistance(loc) < creature.getDistance(defloc)))
			{
				defloc = loc;
			}
		}
		return defloc;
	}

	public static int getRandomHeading()
	{
		return Rnd.get(65535);
	}

	/**
	 * @return this
	 */
	@Override
	public Location getRandomLoc(int ref)
	{
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Location coordsRandomize(int radiusmin, int radiusmax)
	{
		return super.coordsRandomize(radiusmin, radiusmax);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Location coordsRandomize(int radius)
	{
		return super.coordsRandomize(radius);
	}

	/**
	 * Sets/Returns new coords with values changed by GeoEngine.moveCheck()
	 * @param min
	 * @param max
	 * @param change
	 * @return change ? this : newLoc
	 */
	public Location rnd(int min, int max, boolean change)
	{
		Location loc = coordsRandomize(min, max);
		loc = GeoEngine.moveCheck(x, y, z, loc.x, loc.y, 0);
		if (change)
		{
			x = loc.x;
			y = loc.y;
			z = loc.z;
			return this;
		}
		return loc;
	}

	/**
	 * ===============================================================================================
	 * =                                         Teleporting                                         =
	 * ===============================================================================================
	 */

	public static Location getRestartLocation(Player player, RestartType restartType)
	{
		return getRestartLocation(player, player.getLoc(), restartType);
	}

	public static Location getRestartLocation(Player player, Location from, RestartType restartType)
	{
		Reflection r = player.getReflection();
		if (r != ReflectionManager.DEFAULT)
		{
			if (r.getCoreLoc() != null)
			{
				return r.getCoreLoc();
			}
			else if (r.getReturnLoc() != null)
			{
				return r.getReturnLoc();
			}
		}

		Clan clan = player.getClan();

		if (clan != null)
		{
			// If teleport to clan hall
			if (restartType == RestartType.TO_CLANHALL && clan.getHasHideout() != 0)
			{
				return ResidenceHolder.getInstance().getResidence(clan.getHasHideout()).getOwnerRestartPoint();
			}

			// If teleport to castle
			if (restartType == RestartType.TO_CASTLE && clan.getCastle() != 0)
			{
				return ResidenceHolder.getInstance().getResidence(clan.getCastle()).getOwnerRestartPoint();
			}

			// If teleport to fortress
			if (restartType == RestartType.TO_FORTRESS && clan.getHasFortress() != 0)
			{
				return ResidenceHolder.getInstance().getResidence(clan.getHasFortress()).getOwnerRestartPoint();
			}
		}

		if (player.getKarma() > 1)
		{
			if (player.getPKRestartPoint() != null)
			{
				return player.getPKRestartPoint();
			}
		}
		else if (player.getRestartPoint() != null)
		{
			return player.getRestartPoint();
		}

		RestartArea ra = MapRegionManager.getInstance().getRegionData(RestartArea.class, from);
		if (ra != null)
		{
			RestartPoint rp = ra.getRestartPoint().get(player.getRace());

			Location restartPoint = Rnd.get(rp.getRestartPoints());
			Location PKrestartPoint = Rnd.get(rp.getPKrestartPoints());

			return player.getKarma() > 1 ? PKrestartPoint : restartPoint;
		}

		return new Location(17817, 170079, -3530); // Teleport to default loc.
	}

	/**
	 * ===============================================================================================
	 * =                                         Positioning                                         =
	 * ===============================================================================================
	 */

	public enum TargetDirection
	{
		NONE, FRONT, SIDE, BEHIND
	}

	private static final int MAX_ANGLE = 360;
	private static final double FRONT_MAX_ANGLE = 100;
	private static final double BACK_MAX_ANGLE = 40;

	public static TargetDirection getDirectionTo(Creature target, Creature attacker)
	{
		if (target == null || attacker == null)
		{
			return TargetDirection.NONE;
		}
		if (isBehind(target, attacker))
		{
			return TargetDirection.BEHIND;
		}
		if (isInFrontOf(target, attacker))
		{
			return TargetDirection.FRONT;
		}
		return TargetDirection.SIDE;
	}

	/**
	 * Those are altered formulas for blow lands
	 * Return True if the target is IN FRONT of the L2Character.<BR><BR>
	 */
	public static boolean isInFrontOf(Creature target, Creature attacker)
	{
		if (target == null)
		{
			return false;
		}

		double angleChar, angleTarget, angleDiff;
		angleTarget = calculateAngleFrom(target, attacker);
		angleChar = convertHeadingToDegree(target.getHeading());
		angleDiff = angleChar - angleTarget;
		if (angleDiff <= -MAX_ANGLE + FRONT_MAX_ANGLE)
		{
			angleDiff += MAX_ANGLE;
		}
		if (angleDiff >= MAX_ANGLE - FRONT_MAX_ANGLE)
		{
			angleDiff -= MAX_ANGLE;
		}
		if (Math.abs(angleDiff) <= FRONT_MAX_ANGLE)
		{
			return true;
		}
		return false;
	}

	/**
	 * Return True if the target is front L2Character and can be seen.
	 * degrees = 0..180, front->sides->back
	 */
	public static boolean isInFront(Creature target, Creature attacker, int degrees)
	{
		int head = getHeadingTo(target, attacker, false);
		return head <= 32768 * degrees / 180 || head >= 65536 - 32768 * degrees / 180;
	}

	/**
	 * Those are altered formulas for blow lands
	 * Return True if the L2Character is behind the target and can't be seen.<BR><BR>
	 */
	public static boolean isBehind(Creature target, Creature attacker)
	{
		if (target == null)
		{
			return false;
		}

		double angleChar, angleTarget, angleDiff;
		angleChar = calculateAngleFrom(attacker, target);
		angleTarget = convertHeadingToDegree(target.getHeading());
		angleDiff = angleChar - angleTarget;
		if (angleDiff <= -MAX_ANGLE + BACK_MAX_ANGLE)
		{
			angleDiff += MAX_ANGLE;
		}
		if (angleDiff >= MAX_ANGLE - BACK_MAX_ANGLE)
		{
			angleDiff -= MAX_ANGLE;
		}
		if (Math.abs(angleDiff) <= BACK_MAX_ANGLE)
		{
			return true;
		}
		return false;
	}

	/** Returns true if target is in front of L2Character (shield def etc) */
	public static boolean isFacing(Creature attacker, GameObject target, int maxAngle)
	{
		double angleChar, angleTarget, angleDiff, maxAngleDiff;
		if (target == null)
		{
			return false;
		}
		maxAngleDiff = maxAngle / 2;
		angleTarget = calculateAngleFrom(attacker, target);
		angleChar = convertHeadingToDegree(attacker.getHeading());
		angleDiff = angleChar - angleTarget;
		if (angleDiff <= -360 + maxAngleDiff)
		{
			angleDiff += 360;
		}
		if (angleDiff >= 360 - maxAngleDiff)
		{
			angleDiff -= 360;
		}
		if (Math.abs(angleDiff) <= maxAngleDiff)
		{
			return true;
		}
		return false;
	}

	public static int calculateHeadingFrom(GameObject obj1, GameObject obj2)
	{
		return calculateHeadingFrom(obj1.getX(), obj1.getY(), obj2.getX(), obj2.getY());
	}

	public static int calculateHeadingFrom(int obj1X, int obj1Y, int obj2X, int obj2Y)
	{
		double angleTarget = Math.toDegrees(Math.atan2(obj2Y - obj1Y, obj2X - obj1X));
		if (angleTarget < 0)
		{
			angleTarget = MAX_ANGLE + angleTarget;
		}
		return (int) (angleTarget * 182.044444444);
	}

	public static double calculateAngleFrom(GameObject obj1, GameObject obj2)
	{
		return calculateAngleFrom(obj1.getX(), obj1.getY(), obj2.getX(), obj2.getY());
	}

	public static double calculateAngleFrom(int obj1X, int obj1Y, int obj2X, int obj2Y)
	{
		double angleTarget = Math.toDegrees(Math.atan2(obj2Y - obj1Y, obj2X - obj1X));
		if (angleTarget < 0)
		{
			angleTarget = 360 + angleTarget;
		}
		return angleTarget;
	}

	public static boolean checkIfInRange(int range, int x1, int y1, int x2, int y2)
	{
		return checkIfInRange(range, x1, y1, 0, x2, y2, 0, false);
	}

	public static boolean checkIfInRange(int range, int x1, int y1, int z1, int x2, int y2, int z2, boolean includeZAxis)
	{
		long dx = x1 - x2;
		long dy = y1 - y2;

		if (includeZAxis)
		{
			long dz = z1 - z2;
			return dx * dx + dy * dy + dz * dz <= range * range;
		}
		return dx * dx + dy * dy <= range * range;
	}

	public static boolean checkIfInRange(int range, GameObject obj1, GameObject obj2, boolean includeZAxis)
	{
		if (obj1 == null || obj2 == null)
		{
			return false;
		}
		return checkIfInRange(range, obj1.getX(), obj1.getY(), obj1.getZ(), obj2.getX(), obj2.getY(), obj2.getZ(), includeZAxis);
	}

	public static double convertHeadingToDegree(int heading)
	{
		return heading / 182.044444444;
	}

	public static double convertHeadingToRadian(int heading)
	{
		return Math.toRadians(convertHeadingToDegree(heading) - 90);
	}

	public static int convertDegreeToClientHeading(double degree)
	{
		if (degree < 0)
		{
			degree = 360 + degree;
		}
		return (int) (degree * 182.044444444);
	}

	public static double calculateDistance(int x1, int y1, int z1, int x2, int y2)
	{
		return calculateDistance(x1, y1, 0, x2, y2, 0, false);
	}

	public static double calculateDistance(int x1, int y1, int z1, int x2, int y2, int z2, boolean includeZAxis)
	{
		long dx = x1 - x2;
		long dy = y1 - y2;

		if (includeZAxis)
		{
			long dz = z1 - z2;
			return Math.sqrt(dx * dx + dy * dy + dz * dz);
		}
		return Math.sqrt(dx * dx + dy * dy);
	}

	public static double calculateDistance(GameObject obj1, GameObject obj2, boolean includeZAxis)
	{
		if (obj1 == null || obj2 == null)
		{
			return Integer.MAX_VALUE;
		}
		return calculateDistance(obj1.getX(), obj1.getY(), obj1.getZ(), obj2.getX(), obj2.getY(), obj2.getZ(), includeZAxis);
	}

	public static double calculateDistance(GameObject obj1, Location loc, boolean includeZAxis)
	{
		if (obj1 == null || loc == null)
		{
			return Integer.MAX_VALUE;
		}
		return calculateDistance(obj1.getX(), obj1.getY(), obj1.getZ(), loc.getX(), loc.getY(), loc.getZ(), includeZAxis);
	}

	public static double calculateDistance(Location loc1, Location loc2, boolean includeZAxis)
	{
		if (loc1 == null || loc2 == null)
		{
			return Integer.MAX_VALUE;
		}
		return calculateDistance(loc1.getX(), loc1.getY(), loc1.getZ(), loc2.getX(), loc2.getY(), loc2.getZ(), includeZAxis);
	}

	public static double getDistance(GameObject a1, GameObject a2)
	{
		return getDistance(a1.getX(), a2.getY(), a2.getX(), a2.getY());
	}

	public static double getDistance(Location loc1, Location loc2)
	{
		return getDistance(loc1.getX(), loc1.getY(), loc2.getX(), loc2.getY());
	}

	public static double getDistance(int x1, int y1, int x2, int y2)
	{
		return Math.hypot(x1 - x2, y1 - y2);
	}

	public static int getHeadingTo(GameObject actor, GameObject target)
	{
		if (actor == null || target == null || target == actor)
		{
			return -1;
		}
		return getHeadingTo(actor.getLoc(), target.getLoc());
	}

	public static int getHeadingTo(Location actor, Location target)
	{
		if (actor == null || target == null || target.equals(actor))
		{
			return -1;
		}

		int dx = target.x - actor.x;
		int dy = target.y - actor.y;
		int heading = target.h - (int) (Math.atan2(-dy, -dx) * Creature.HEADINGS_IN_PI + 32768);

		if (heading < 0)
		{
			heading = heading + 1 + Integer.MAX_VALUE & 0xFFFF;
		}
		else if (heading > 0xFFFF)
		{
			heading &= 0xFFFF;
		}

		return heading;
	}

	public static int getHeadingTo(GameObject target, GameObject attacker, boolean toChar)
	{
		if (target == null || target == attacker)
		{
			return -1;
		}

		int dx = target.getX() - attacker.getX();
		int dy = target.getY() - attacker.getY();
		int heading = (int) (Math.atan2(-dy, -dx) * Creature.HEADINGS_IN_PI + 32768);

		heading = toChar ? target.getHeading() - heading : attacker.getHeading() - heading;

		if (heading < 0)
		{
			heading = heading + 1 + Integer.MAX_VALUE & 0xFFFF;
		}
		else if (heading > 0xFFFF)
		{
			heading &= 0xFFFF;
		}

		return heading;
	}
}