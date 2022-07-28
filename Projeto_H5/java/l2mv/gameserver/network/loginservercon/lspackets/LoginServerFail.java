package l2mv.gameserver.network.loginservercon.lspackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.network.loginservercon.AuthServerCommunication;
import l2mv.gameserver.network.loginservercon.ReceivablePacket;

public class LoginServerFail extends ReceivablePacket
{
	private static final Logger _log = LoggerFactory.getLogger(LoginServerFail.class);

	private static final String[] reasons =
	{
		"none",
		"IP banned",
		"IP reserved",
		"wrong hexid",
		"ID reserved",
		"no free ID",
		"not authed",
		"already logged in"
	};
	private int _reason;

	public String getReason()
	{
		return reasons[this._reason];
	}

	@Override
	protected void readImpl()
	{
		this._reason = this.readC();
	}

	@Override
	protected void runImpl()
	{
		_log.warn("Authserver registration failed! Reason: " + this.getReason());
		AuthServerCommunication.getInstance().restart();
	}
}