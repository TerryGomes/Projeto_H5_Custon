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
 * @author Masterio
 */
public class RPCReward
{
	private int _id = 0; // RPC reward id
	private int _itemId = 0; // game item id
	private long _itemAmount = 0; // amount of the game item
	private long _rpc = 0; // total RPC cost for (item * amount)

	public int getId()
	{
		return _id;
	}

	public void setId(int id)
	{
		_id = id;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public void setItemId(int itemId)
	{
		_itemId = itemId;
	}

	public long getItemAmount()
	{
		return _itemAmount;
	}

	public void setItemAmount(long itemAmount)
	{
		_itemAmount = itemAmount;
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
