/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2f.gameserver.masteriopack.rankpvpsystem;

/**
 * All that data stored in RankTable class.
 * @author Masterio
 */
public class Rank
{
	private int _id = 0; // rank id
	private String _name = null; // rank name
	private long _minExp = 0; // for rich this rank
	private int _pointsForKill = 0; // points awarded for kill the player with this rank

	private long _rpc = 0; // RPC awarded for kill the player with this rank

	private int _nickColor = -1; // nick color, colors will be override in EnterWorld class if the value will be greater than -1
	private int _titleColor = -1; // title color, colors will be override in EnterWorld class if the value will be greater than -1

	/**
	 * @return the _id
	 */
	public int getId()
	{
		return _id;
	}

	/**
	 * @param id the _id to set
	 */
	public void setId(int id)
	{
		_id = id;
	}

	/**
	 * @return the _name
	 */
	public String getName()
	{
		return _name;
	}

	/**
	 * @param name the _name to set
	 */
	public void setName(String name)
	{
		_name = name;
	}

	/**
	 * Store information about minimum Rank Points for obtain this Rank.
	 * @return the _minExp
	 */
	public long getMinExp()
	{
		return _minExp;
	}

	/**
	 * @param minExp the _minExp to set
	 */
	public void setMinPoints(long minExp)
	{
		_minExp = minExp;
	}

	/**
	 * @return the _pointsForKill
	 */
	public int getPointsForKill()
	{
		return _pointsForKill;
	}

	/**
	 * @param pointsForKill the _pointsForKill to set
	 */
	public void setPointsForKill(int pointsForKill)
	{
		_pointsForKill = pointsForKill;
	}

	/**
	 * @return the _nickColor
	 */
	public int getNickColor()
	{
		return _nickColor;
	}

	/**
	 * @param nickColor the _nickColor to set
	 */
	public void setNickColor(int nickColor)
	{
		_nickColor = nickColor;
	}

	/**
	 * @return the _titleColor
	 */
	public int getTitleColor()
	{
		return _titleColor;
	}

	/**
	 * @param titleColor the _titleColor to set
	 */
	public void setTitleColor(int titleColor)
	{
		_titleColor = titleColor;
	}

	public long getRpc()
	{
		return _rpc;
	}

	public void setRpc(long rpc)
	{
		_rpc = rpc;
	}

}
