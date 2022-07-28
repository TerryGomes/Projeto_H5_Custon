package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.utils.Location;

public class ExShowTrace extends L2GameServerPacket
{
	private final List<Trace> _traces = new ArrayList<Trace>();

	static final class Trace
	{
		public final int _x;
		public final int _y;
		public final int _z;
		public final int _time;

		public Trace(int x, int y, int z, int time)
		{
			this._x = x;
			this._y = y;
			this._z = z;
			this._time = time;
		}
	}

	public void addTrace(int x, int y, int z, int time)
	{
		this._traces.add(new Trace(x, y, z, time));
	}

	public void addLine(Location from, Location to, int step, int time)
	{
		this.addLine(from.x, from.y, from.z, to.x, to.y, to.z, step, time);
	}

	public void addLine(int from_x, int from_y, int from_z, int to_x, int to_y, int to_z, int step, int time)
	{
		int x_diff = to_x - from_x;
		int y_diff = to_y - from_y;
		int z_diff = to_z - from_z;
		double xy_dist = Math.sqrt(x_diff * x_diff + y_diff * y_diff);
		double full_dist = Math.sqrt(xy_dist * xy_dist + z_diff * z_diff);
		int steps = (int) (full_dist / step);

		this.addTrace(from_x, from_y, from_z, time);
		if (steps > 1)
		{
			int step_x = x_diff / steps;
			int step_y = y_diff / steps;
			int step_z = z_diff / steps;

			for (int i = 1; i < steps; i++)
			{
				this.addTrace(from_x + step_x * i, from_y + step_y * i, from_z + step_z * i, time);
			}
		}
		this.addTrace(to_x, to_y, to_z, time);
	}

	public void addTrace(GameObject obj, int time)
	{
		this.addTrace(obj.getX(), obj.getY(), obj.getZ(), time);
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x67);

		this.writeH(this._traces.size());
		for (Trace t : this._traces)
		{
			this.writeD(t._x);
			this.writeD(t._y);
			this.writeD(t._z);
			this.writeH(t._time);
		}
	}
}