package l2f.gameserver.network.clientpackets;

import l2f.gameserver.Config;
import l2f.gameserver.SecondaryPasswordAuth;
import l2f.gameserver.network.serverpackets.Ex2ndPasswordAck;

/**
 * (ch)cS{S}
 * c: change pass?
 * S: current password
 * S: new password
 */
public class RequestEx2ndPasswordReq extends L2GameClientPacket
{
	int _changePass;
	String _password, _newPassword;

	@Override
	protected void readImpl()
	{
		_changePass = readC();
		_password = readS();
		if (_changePass == 2)
		{
			_newPassword = readS();
		}
	}

	@Override
	protected void runImpl()
	{
		if (!Config.SECOND_AUTH_ENABLED)
		{
			return;
		}

		SecondaryPasswordAuth spa = getClient().getSecondaryAuth();
		boolean exVal = false;

		if (_changePass == 0 && !spa.passwordExist())
		{
			exVal = spa.savePassword(_password);
		}
		else if (_changePass == 2 && spa.passwordExist())
		{
			exVal = spa.changePassword(_password, _newPassword);
		}

		if (exVal)
		{
			getClient().sendPacket(new Ex2ndPasswordAck(Ex2ndPasswordAck.SUCCESS));
		}
	}
}