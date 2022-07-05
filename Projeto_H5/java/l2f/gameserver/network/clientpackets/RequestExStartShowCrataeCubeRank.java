package l2f.gameserver.network.clientpackets;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.events.fightclubmanager.FightClubPlayer;
import l2f.gameserver.model.entity.events.impl.KrateisCubeEvent;

/**
 * @author VISTALL
 */
public class RequestExStartShowCrataeCubeRank extends L2GameClientPacket
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
			fPlayer.setShowRank(true);
		}
		else
		{
			KrateisCubeEvent cubeEvent = player.getEvent(KrateisCubeEvent.class);
			if (cubeEvent == null)
			{
				return;
			}

			cubeEvent.showRank(player);
		}
	}
}