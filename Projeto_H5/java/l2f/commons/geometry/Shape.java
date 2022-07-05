package l2f.commons.geometry;

public interface Shape
{
	public boolean isInside(int x, int y);

	public boolean isInside(int x, int y, int z);

	public int getXmax();

	public int getXmin();

	public int getYmax();

	public int getYmin();

	public int getZmax();

	public int getZmin();
}
