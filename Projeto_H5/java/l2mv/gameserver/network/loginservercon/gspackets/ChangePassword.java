package l2mv.gameserver.network.loginservercon.gspackets;

import l2mv.gameserver.network.loginservercon.SendablePacket;

public class ChangePassword extends SendablePacket
{
	private final String account;
	private final String oldPass;
	private final String newPass;
	private final String hwid;

	public ChangePassword(String account, String oldPass, String newPass, String hwid)
	{
		this.account = account;
		this.oldPass = oldPass;
		this.newPass = newPass;
		this.hwid = hwid;
	}

	@Override
	protected void writeImpl()
	{
		this.writeC(0x08);
		this.writeS(this.account);
		this.writeS(this.oldPass);
		this.writeS(this.newPass);
		this.writeS(this.hwid);
	}
}
