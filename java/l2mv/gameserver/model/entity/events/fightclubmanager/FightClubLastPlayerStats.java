package l2mv.gameserver.model.entity.events.fightclubmanager;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.utils.Util;

public class FightClubLastPlayerStats
{
	private final String _playerNickName;
	private final String _className;
	private final String _clanName;
	private final String _allyName;
	private final String _typeName;
	private int _score;

	public FightClubLastPlayerStats(Player player, String typeName, int score)
	{
		_playerNickName = player.getName();
		_clanName = player.getClan() != null ? player.getClan().getName() : "<br>";
		_allyName = player.getAlliance() != null ? player.getAlliance().getAllyName() : "<br>";
		_className = Util.getFullClassName(player.getClassId());
		_typeName = typeName;
		_score = score;
	}

	public boolean isMyStat(Player player)
	{
		return _playerNickName.equals(player.getName());
	}

	public String getPlayerName()
	{
		return _playerNickName;
	}

	public String getClanName()
	{
		return _clanName;
	}

	public String getAllyName()
	{
		return _allyName;
	}

	public String getClassName()
	{
		return _className;
	}

	public String getTypeName()
	{
		return _typeName;
	}

	public int getScore()
	{
		return _score;
	}

	public void setScore(int i)
	{
		_score = i;
	}
}
