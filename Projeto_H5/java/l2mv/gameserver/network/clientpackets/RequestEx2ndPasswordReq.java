package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.SecondaryPasswordAuth;
import l2mv.gameserver.network.serverpackets.Ex2ndPasswordAck;

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
		this._changePass = this.readC();
		this._password = this.readS();
		if (this._changePass == 2)
		{
			this._newPassword = this.readS();
		}
	}

	@Override
	protected void runImpl()
	{
		if (!Config.SECOND_AUTH_ENABLED)
		{
			return;
		}

		SecondaryPasswordAuth spa = this.getClient().getSecondaryAuth();
		boolean exVal = false;

		if (this._changePass == 0 && !spa.passwordExist())
		{
			exVal = spa.savePassword(this._password);
		}
		else if (this._changePass == 2 && spa.passwordExist())
		{
			exVal = spa.changePassword(this._password, this._newPassword);
		}

		if (exVal)
		{
			this.getClient().sendPacket(new Ex2ndPasswordAck(Ex2ndPasswordAck.SUCCESS));
		}
	}
}