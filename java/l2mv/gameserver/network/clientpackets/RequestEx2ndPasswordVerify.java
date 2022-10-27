package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.Config;

/**
 * Format: (ch)S
 * S: numerical password
 */
public class RequestEx2ndPasswordVerify extends L2GameClientPacket
{
	String _password;

	@Override
	protected void readImpl()
	{
		this._password = this.readS();
	}

	@Override
	protected void runImpl()
	{
		if (!Config.SECOND_AUTH_ENABLED)
		{
			return;
		}

		this.getClient().getSecondaryAuth().checkPassword(this._password, false);
	}
}