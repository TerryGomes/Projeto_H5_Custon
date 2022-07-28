package l2mv.gameserver.network.serverpackets;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * A packet used to draw points and lines on client.<br/>
 * <b>Note:</b> Names in points and lines are bugged they will appear even when not looking at them.
 */
public class ExServerPrimitive extends L2GameServerPacket
{
	private final String _name;
	private final int _x;
	private final int _y;
	private final int _z;
	private final List<Point> _points = new ArrayList<>();
	private final List<Line> _lines = new ArrayList<>();

	/**
	 * @param name A unique name this will be used to replace lines if second packet is sent
	 * @param x the x coordinate usually middle of drawing area
	 * @param y the y coordinate usually middle of drawing area
	 * @param z the z coordinate usually middle of drawing area
	 */
	public ExServerPrimitive(String name, int x, int y, int z)
	{
		this._name = name;
		this._x = x;
		this._y = y;
		this._z = z;
	}

	/**
	 * Adds a point to be displayed on client.
	 * @param name the name that will be displayed over the point
	 * @param color the color
	 * @param isNameColored if {@code true} name will be colored as well.
	 * @param x the x coordinate for this point
	 * @param y the y coordinate for this point
	 * @param z the z coordinate for this point
	 */
	public void addPoint(String name, int color, boolean isNameColored, int x, int y, int z)
	{
		this._points.add(new Point(name, color, isNameColored, x, y, z));
	}

	/**
	 * Adds a point to be displayed on client.
	 * @param color the color
	 * @param x the x coordinate for this point
	 * @param y the y coordinate for this point
	 * @param z the z coordinate for this point
	 */
	public void addPoint(int color, int x, int y, int z)
	{
		this.addPoint("", color, false, x, y, z);
	}

	/**
	 * Adds a point to be displayed on client.
	 * @param name the name that will be displayed over the point
	 * @param color the color
	 * @param isNameColored if {@code true} name will be colored as well.
	 * @param x the x coordinate for this point
	 * @param y the y coordinate for this point
	 * @param z the z coordinate for this point
	 */
	public void addPoint(String name, Color color, boolean isNameColored, int x, int y, int z)
	{
		this.addPoint(name, color.getRGB(), isNameColored, x, y, z);
	}

	/**
	 * Adds a point to be displayed on client.
	 * @param color the color
	 * @param x the x coordinate for this point
	 * @param y the y coordinate for this point
	 * @param z the z coordinate for this point
	 */
	public void addPoint(Color color, int x, int y, int z)
	{
		this.addPoint("", color, false, x, y, z);
	}

	/**
	 * Adds a line to be displayed on client
	 * @param name the name that will be displayed over the middle of line
	 * @param color the color
	 * @param isNameColored if {@code true} name will be colored as well.
	 * @param x the x coordinate for this line start point
	 * @param y the y coordinate for this line start point
	 * @param z the z coordinate for this line start point
	 * @param x2 the x coordinate for this line end point
	 * @param y2 the y coordinate for this line end point
	 * @param z2 the z coordinate for this line end point
	 */
	public void addLine(String name, int color, boolean isNameColored, int x, int y, int z, int x2, int y2, int z2)
	{
		this._lines.add(new Line(name, color, isNameColored, x, y, z, x2, y2, z2));
	}

	/**
	 * Adds a line to be displayed on client
	 * @param color the color
	 * @param x the x coordinate for this line start point
	 * @param y the y coordinate for this line start point
	 * @param z the z coordinate for this line start point
	 * @param x2 the x coordinate for this line end point
	 * @param y2 the y coordinate for this line end point
	 * @param z2 the z coordinate for this line end point
	 */
	public void addLine(int color, int x, int y, int z, int x2, int y2, int z2)
	{
		this.addLine("", color, false, x, y, z, x2, y2, z2);
	}

	/**
	 * Adds a line to be displayed on client
	 * @param name the name that will be displayed over the middle of line
	 * @param color the color
	 * @param isNameColored if {@code true} name will be colored as well.
	 * @param x the x coordinate for this line start point
	 * @param y the y coordinate for this line start point
	 * @param z the z coordinate for this line start point
	 * @param x2 the x coordinate for this line end point
	 * @param y2 the y coordinate for this line end point
	 * @param z2 the z coordinate for this line end point
	 */
	public void addLine(String name, Color color, boolean isNameColored, int x, int y, int z, int x2, int y2, int z2)
	{
		this.addLine(name, color.getRGB(), isNameColored, x, y, z, x2, y2, z2);
	}

	/**
	 * Adds a line to be displayed on client
	 * @param color the color
	 * @param x the x coordinate for this line start point
	 * @param y the y coordinate for this line start point
	 * @param z the z coordinate for this line start point
	 * @param x2 the x coordinate for this line end point
	 * @param y2 the y coordinate for this line end point
	 * @param z2 the z coordinate for this line end point
	 */
	public void addLine(Color color, int x, int y, int z, int x2, int y2, int z2)
	{
		this.addLine("", color, false, x, y, z, x2, y2, z2);
	}

	@Override
	protected void writeImpl()
	{
		this.writeC(0xFE);
		this.writeH(0x11);
		this.writeS(this._name);
		this.writeD(this._x);
		this.writeD(this._y);
		this.writeD(this._z);
		this.writeD(65535); // has to do something with display range and angle
		this.writeD(65535); // has to do something with display range and angle

		this.writeD(this._points.size() + this._lines.size());

		for (Point point : this._points)
		{
			this.writeC(1); // Its the type in this case Point
			this.writeS(point.getName());
			int color = point.getColor();
			this.writeD((color >> 16) & 0xFF); // R
			this.writeD((color >> 8) & 0xFF); // G
			this.writeD(color & 0xFF); // B
			this.writeD(point.isNameColored() ? 1 : 0);
			this.writeD(point.getX());
			this.writeD(point.getY());
			this.writeD(point.getZ());
		}

		for (Line line : this._lines)
		{
			this.writeC(2); // Its the type in this case Line
			this.writeS(line.getName());
			int color = line.getColor();
			this.writeD((color >> 16) & 0xFF); // R
			this.writeD((color >> 8) & 0xFF); // G
			this.writeD(color & 0xFF); // B
			this.writeD(line.isNameColored() ? 1 : 0);
			this.writeD(line.getX());
			this.writeD(line.getY());
			this.writeD(line.getZ());
			this.writeD(line.getX2());
			this.writeD(line.getY2());
			this.writeD(line.getZ2());
		}
	}

	private static class Point
	{
		private final String _name;
		private final int _color;
		private final boolean _isNameColored;
		private final int _x;
		private final int _y;
		private final int _z;

		public Point(String name, int color, boolean isNameColored, int x, int y, int z)
		{
			this._name = name;
			this._color = color;
			this._isNameColored = isNameColored;
			this._x = x;
			this._y = y;
			this._z = z;
		}

		/**
		 * @return the name
		 */
		public String getName()
		{
			return this._name;
		}

		/**
		 * @return the color
		 */
		public int getColor()
		{
			return this._color;
		}

		/**
		 * @return the isNameColored
		 */
		public boolean isNameColored()
		{
			return this._isNameColored;
		}

		/**
		 * @return the x
		 */
		public int getX()
		{
			return this._x;
		}

		/**
		 * @return the y
		 */
		public int getY()
		{
			return this._y;
		}

		/**
		 * @return the z
		 */
		public int getZ()
		{
			return this._z;
		}
	}

	private static class Line extends Point
	{
		private final int _x2;
		private final int _y2;
		private final int _z2;

		public Line(String name, int color, boolean isNameColored, int x, int y, int z, int x2, int y2, int z2)
		{
			super(name, color, isNameColored, x, y, z);
			this._x2 = x2;
			this._y2 = y2;
			this._z2 = z2;
		}

		/**
		 * @return the x2
		 */
		public int getX2()
		{
			return this._x2;
		}

		/**
		 * @return the y2
		 */
		public int getY2()
		{
			return this._y2;
		}

		/**
		 * @return the z2
		 */
		public int getZ2()
		{
			return this._z2;
		}
	}
}