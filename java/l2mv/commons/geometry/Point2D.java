package l2mv.commons.geometry;

import l2mv.commons.util.Rnd;

public class Point2D implements Cloneable
{
	public static final Point2D[] EMPTY_ARRAY = new Point2D[0];
	public int x;
	public int y;

	public Point2D()
	{
	}

	public Point2D(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	@Override
	public Point2D clone()
	{
		return new Point2D(x, y);
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if ((o == null) || (o.getClass() != getClass()))
		{
			return false;
		}
		return equals((Point2D) o);
	}

	public boolean equals(Point2D p)
	{
		return equals(p.x, p.y);
	}

	public boolean equals(int x, int y)
	{
		return (this.x == x) && (this.y == y);
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public Point2D setX(int x)
	{
		this.x = x;
		return this;
	}

	public Point2D setY(int y)
	{
		this.y = y;
		return this;
	}

	public Point2D set(int x, int y)
	{
		this.x = x;
		this.y = y;
		return this;
	}

	public Point2D changeX(int xDiff)
	{
		x += xDiff;
		return this;
	}

	public Point2D changeY(int yDiff)
	{
		y += yDiff;
		return this;
	}

	public static Point2D coordsRandomize(int x, int y, int radiusmin, int radiusmax)
	{
		if (radiusmax == 0 || radiusmax < radiusmin)
		{
			return new Point2D(x, y);
		}
		int radius = Rnd.get(radiusmin, radiusmax);
		double angle = Rnd.nextDouble() * 2 * Math.PI;
		return new Point2D((int) (x + radius * Math.cos(angle)), (int) (y + radius * Math.sin(angle)));
	}

	public static Point2D coordsRandomize(int x, int y, int radius)
	{
		return coordsRandomize(x, y, 0, radius);
	}

	@SuppressWarnings("unchecked")
	public <T extends Point2D> T coordsRandomize(int radiusmin, int radiusmax)
	{
		if (radiusmax == 0 || radiusmax < radiusmin)
		{
			return (T) this;
		}

		int radius = Rnd.get(radiusmin, radiusmax);
		double angle = Rnd.nextDouble() * 2 * Math.PI;
		int newX = (int) (x + radius * Math.cos(angle));
		int newY = (int) (y + radius * Math.sin(angle));
		return (T) clone().setX(newX).setY(newY);
	}

	public <T extends Point2D> T coordsRandomize(int radius)
	{
		return coordsRandomize(0, radius);
	}

	@Override
	public String toString()
	{
		return "[x: " + x + " y: " + y + "]";
	}
}
