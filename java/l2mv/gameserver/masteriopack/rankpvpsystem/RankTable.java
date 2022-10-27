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
package l2mv.gameserver.masteriopack.rankpvpsystem;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class contains all ranks. Ranks starts from lowest rank with ID = 1.
 * @author Masterio
 */
public class RankTable
{
	private static RankTable _instance = null;

	// [rankId, Rank] - store all Ranks as Rank objects by rank id.
	private static Map<Integer, Rank> _rankList = new LinkedHashMap<>();

	public static RankTable getInstance()
	{
		if (_instance == null)
		{
			_instance = new RankTable();
		}

		return _instance;
	}

	/**
	 * @return Map &lt;rankID, Rank&gt;
	 */
	public Map<Integer, Rank> getRankList()
	{
		return _rankList;
	}

	public void setRankList(Map<Integer, Rank> rankList)
	{
		_rankList = rankList;
	}

	/**
	 * Returns Rank object by rank id, if not founded returns null.
	 * @param id
	 * @return
	 */
	public Rank getRankById(int id)
	{
		return _rankList.get(id);
	}

}
