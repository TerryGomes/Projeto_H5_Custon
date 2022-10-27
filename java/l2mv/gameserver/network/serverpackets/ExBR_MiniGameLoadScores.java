package l2mv.gameserver.network.serverpackets;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.TreeIntObjectMap;

import l2mv.gameserver.instancemanager.games.MiniGameScoreManager;
import l2mv.gameserver.model.Player;

/**
 * @author VISTALL
 * @date  0:07:05/10.04.2010
 */
public class ExBR_MiniGameLoadScores extends L2GameServerPacket
{
	private int _place;
	private int _score;
	private int _lastScore;

	private IntObjectMap<List<Map.Entry<String, Integer>>> _entries = new TreeIntObjectMap<List<Map.Entry<String, Integer>>>();

	public ExBR_MiniGameLoadScores(Player player)
	{
		int lastBig = 0;
		int i = 1;

		for (IntObjectMap.Entry<Set<String>> entry : MiniGameScoreManager.getInstance().getScores().entrySet())
		{
			for (String name : entry.getValue())
			{
				List<Map.Entry<String, Integer>> set = this._entries.get(i);
				if (set == null)
				{
					this._entries.put(i, (set = new ArrayList<Map.Entry<String, Integer>>()));
				}

				if (name.equalsIgnoreCase(player.getName()))
				{
					if (entry.getKey() > lastBig)
					{
						this._place = i;
						this._score = (lastBig = entry.getKey());
					}
				}

				set.add(new AbstractMap.SimpleImmutableEntry<String, Integer>(name, entry.getKey()));

				i++;

				this._lastScore = entry.getKey();

				if (i > 100)
				{
					break;
				}
			}
		}
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xDD);
		this.writeD(this._place); // place of last big score of player
		this.writeD(this._score); // last big score of player
		this.writeD(0x00); // ?
		this.writeD(this._lastScore); // last score of list
		for (IntObjectMap.Entry<List<Map.Entry<String, Integer>>> entry : this._entries.entrySet())
		{
			for (Map.Entry<String, Integer> scoreEntry : entry.getValue())
			{
				this.writeD(entry.getKey());
				this.writeS(scoreEntry.getKey());
				this.writeD(scoreEntry.getValue());
			}
		}
	}
}