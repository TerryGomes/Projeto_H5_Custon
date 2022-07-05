package l2f.gameserver.network.clientpackets;

import l2f.gameserver.Config;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.ExResponseShowStepOne;

/**
 * @author VISTALL
 */
public class RequestExShowNewUserPetition extends L2GameClientPacket
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
		if (player == null || !Config.EX_NEW_PETITION_SYSTEM)
		{
			return;
		}

		player.sendPacket(new ExResponseShowStepOne(player));
	}
}