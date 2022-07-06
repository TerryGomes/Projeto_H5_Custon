package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.events.fightclubmanager.FightClubPlayer;
import l2mv.gameserver.model.entity.events.impl.KrateisCubeEvent;

/**
 * @author VISTALL
 */
public class RequestExStopShowCrataeCubeRank extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		if (player.isInFightClub())
		{
			FightClubPlayer fPlayer = player.getFightClubEvent().getFightClubPlayer(player);
			fPlayer.setShowRank(false);
		}
		else
		{
			KrateisCubeEvent cubeEvent = player.getEvent(KrateisCubeEvent.class);
			if (cubeEvent == null)
			{
				return;
			}

			cubeEvent.closeRank(player);
		}
	}
}