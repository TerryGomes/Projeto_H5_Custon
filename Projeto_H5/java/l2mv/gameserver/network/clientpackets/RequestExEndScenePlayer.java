package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;

public class RequestExEndScenePlayer extends L2GameClientPacket
{
	private int _movieId;

	@Override
	protected void readImpl()
	{
		_movieId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		if (!activeChar.isInMovie() || activeChar.getMovieId() != _movieId)
		{
			activeChar.sendActionFailed();
			return;
		}
		activeChar.setIsInMovie(false);
		activeChar.setMovieId(0);
		activeChar.decayMe();
		activeChar.spawnMe();
	}

	// Synerge - This packet can be used while the character is blocked
	@Override
	public boolean canBeUsedWhileBlocked()
	{
		return true;
	}
}