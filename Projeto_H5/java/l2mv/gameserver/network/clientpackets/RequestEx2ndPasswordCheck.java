package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.network.serverpackets.Ex2ndPasswordCheck;

/**
 * Format: (ch)
 */
public class RequestEx2ndPasswordCheck extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{

	}

	@Override
	protected void runImpl()
	{
		if (!Config.SECOND_AUTH_ENABLED || this.getClient().getSecondaryAuth().isAuthed())
		{
			this.sendPacket(new Ex2ndPasswordCheck(Ex2ndPasswordCheck.PASSWORD_OK));
			return;
		}

		this.getClient().getSecondaryAuth().openDialog();
	}
}