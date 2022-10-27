package l2mv.gameserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.instancemanager.games.HandysBlockCheckerManager;
import l2mv.gameserver.model.Player;

/**
 * Format: chddd
 *
 * d: Arena
 * d: Answer
 */
public final class RequestExCubeGameReadyAnswer extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestExCubeGameReadyAnswer.class);

	int _arena;
	int _answer;

	@Override
	protected void readImpl()
	{
		this._arena = this.readD() + 1;
		this._answer = this.readD();
	}

	@Override
	public void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		switch (this._answer)
		{
		case 0:
			// Cancel
			break;
		case 1:
			// OK or Time Over
			HandysBlockCheckerManager.getInstance().increaseArenaVotes(this._arena);
			break;
		default:
			_log.warn("Unknown Cube Game Answer ID: " + this._answer);
			break;
		}
	}
}
