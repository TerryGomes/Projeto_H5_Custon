package l2mv.commons.geometry;

import l2mv.commons.lang.ArrayUtils;

public class Polygon extends AbstractShape
{
	protected Point2D[] points = Point2D.EMPTY_ARRAY;

	public Polygon add(int x, int y)
	{
		add(new Point2D(x, y));
		return this;
	}

	public Polygon add(Point2D p)
	{
		if (points.length == 0)
		{
			min.y = p.y;
			min.x = p.x;
			max.x = p.x;
			max.y = p.y;
		}
		else
		{
			min.y = Math.min(min.y, p.y);
			min.x = Math.min(min.x, p.x);
			max.x = Math.max(max.x, p.x);
			max.y = Math.max(max.y, p.y);
		}
		points = (ArrayUtils.add(points, p));
		return this;
	}

	@Override
	public Polygon setZmax(int z)
	{
		max.z = z;
		return this;
	}

	@Override
	public Polygon setZmin(int z)
	{
		min.z = z;
		return this;
	}

	@Override
	public boolean isInside(int x, int y)
	{
		if (x < min.x || x > max.x || y < min.y || y > max.y)
		{
			return false;
		}

		int hits = 0;
		int npoints = points.length;
		Point2D last = points[npoints - 1];

		Point2D cur;
		for (int i = 0; i < npoints; last = cur, i++)
		{
			cur = points[i];

			if (cur.y == last.y)
			{
				continue;
			}

			int leftx;
			if (cur.x < last.x)
			{
				if (x >= last.x)
				{
					continue;
				}
				leftx = cur.x;
			}
			else
			{
				if (x >= cur.x)
				{
					continue;
				}
				leftx = last.x;
			}

			double test1, test2;
			if (cur.y < last.y)
			{
				if (y < cur.y || y >= last.y)
				{
					continue;
				}
				if (x < leftx)
				{
					hits++;
					continue;
				}
				test1 = x - cur.x;
				test2 = y - cur.y;
			}
			else
			{
				if (y < last.y || y >= cur.y)
				{
					continue;
				}
				if (x < leftx)
				{
					hits++;
					continue;
				}
				test1 = x - last.x;
				test2 = y - last.y;
			}

			if (test1 < (test2 / (last.y - cur.y) * (last.x - cur.x)))
			{
				hits++;
			}
		}

		return ((hits & 1) != 0);
	}

	/**
	 * ????????? ??????? ?? ???????????????.
	 */
	public boolean validate()
	{
		if (points.length < 3)
		{
			return false;
		}

		// ??????????? ?? ????? ???? ??????????????????
		if (points.length > 3)
		{
			// ??????? ???? - ?????????? ??? ????? ??????????????
			for (int i = 1; i < points.length; i++)
			{
				int ii = i + 1 < points.length ? i + 1 : 0; // ?????? ????? ?????? ?????
				// ?????????? ???? - ?????????? ??? ????? ??????????????? ????? ???, ??? ?? ??????? ????? ? ????????
				for (int n = i; n < points.length; n++)
				{
					if (Math.abs(n - i) > 1)
					{
						int nn = n + 1 < points.length ? n + 1 : 0; // ?????? ????? ?????? ?????
						if (GeometryUtils.checkIfLineSegementsIntersects(points[i], points[ii], points[n], points[nn]))
						{
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < points.length; i++)
		{
			sb.append(points[i]);
			if (i < points.length - 1)
			{
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
