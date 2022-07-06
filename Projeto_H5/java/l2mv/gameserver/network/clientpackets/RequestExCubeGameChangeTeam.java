package l2mv.gameserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.instancemanager.games.HandysBlockCheckerManager;
import l2mv.gameserver.model.Player;

/**
 * Format: chdd
 * d: team
 */
public final class RequestExCubeGameChangeTeam extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestExCubeGameChangeTeam.class);

	int _team, _arena;

	@Override
	protected void readImpl()
	{
		_arena = readD() + 1;
		_team = readD();
	}

	@Override
	protected void runImpl()
	{
		if (HandysBlockCheckerManager.getInstance().arenaIsBeingUsed(_arena))
		{
			return;
		}
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null || activeChar.isDead())
		{
			return;
		}

		switch (_team)
		{
		case 0:
		case 1:
			// Change Player Team
			HandysBlockCheckerManager.getInstance().changePlayerToTeam(activeChar, _arena, _team);
			break;
		case -1:
		{
			int team = HandysBlockCheckerManager.getInstance().getHolder(_arena).getPlayerTeam(activeChar);
			// client sends two times this packet if click on exit
			// client did not send this packet on restart
			if (team > -1)
			{
				HandysBlockCheckerManager.getInstance().removePlayer(activeChar, _arena, team);
			}
			break;
		}
		default:
			_log.warn("Wrong Team ID: " + _team);
			break;
		}
	}
}
