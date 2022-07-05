package l2f.commons.geometry;

public class Circle extends AbstractShape
{
	protected final Point2D c;
	protected final int r;

	public Circle(Point2D center, int radius)
	{
		c = center;
		r = radius;
		min.x = (c.x - r);
		max.x = (c.x + r);
		min.y = (c.y - r);
		max.y = (c.y + r);
	}

	public Circle(int x, int y, int radius)
	{
		this(new Point2D(x, y), radius);
	}

	@Override
	public Circle setZmax(int z)
	{
		max.z = z;
		return this;
	}

	@Override
	public Circle setZmin(int z)
	{
		min.z = z;
		return this;
	}

	@Override
	public boolean isInside(int x, int y)
	{
		return (x - c.x) * (c.x - x) + (y - c.y) * (c.y - y) <= r * r;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(c).append("{ radius: ").append(r).append("}");
		sb.append("]");
		return sb.toString();
	}
}
