package l2mv.commons.geometry;

public class Point3D extends Point2D
{
	public static final Point3D[] EMPTY_ARRAY = new Point3D[0];
	public int z;

	public Point3D()
	{
	}

	public Point3D(int x, int y, int z)
	{
		super(x, y);
		this.z = z;
	}

	public int getZ()
	{
		return z;
	}

	@Override
	public Point3D clone()
	{
		return new Point3D(x, y, z);
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
		return equals((Point3D) o);
	}

	public boolean equals(Point3D p)
	{
		return equals(p.x, p.y, p.z);
	}

	public boolean equals(int x, int y, int z)
	{
		return (this.x == x) && (this.y == y) && (this.z == z);
	}

	@Override
	public String toString()
	{
		return "[x: " + x + " y: " + y + " z: " + z + "]";
	}
}