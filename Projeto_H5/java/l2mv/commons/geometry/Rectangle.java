package l2mv.commons.geometry;

public class Rectangle extends AbstractShape
{
	public Rectangle(int x1, int y1, int x2, int y2)
	{
		min.x = Math.min(x1, x2);
		min.y = Math.min(y1, y2);
		max.x = Math.max(x1, x2);
		max.y = Math.max(y1, y2);
	}

	@Override
	public Rectangle setZmax(int z)
	{
		max.z = z;
		return this;
	}

	@Override
	public Rectangle setZmin(int z)
	{
		min.z = z;
		return this;
	}

	@Override
	public boolean isInside(int x, int y)
	{
		return (x >= min.x) && (x <= max.x) && (y >= min.y) && (y <= max.y);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(min).append(", ").append(max);
		sb.append("]");
		return sb.toString();
	}
}
