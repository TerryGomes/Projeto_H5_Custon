package l2mv.gameserver.network.loginservercon.gspackets;

import l2mv.gameserver.network.loginservercon.SendablePacket;

public class BonusRequest extends SendablePacket
{
	private final String account;
	private final double bonus;
	private final int bonusExpire;

	public BonusRequest(String account, double bonus, int bonusExpire)
	{
		this.account = account;
		this.bonus = bonus;
		this.bonusExpire = bonusExpire;
	}

	@Override
	protected void writeImpl()
	{
		this.writeC(0x10);
		this.writeS(this.account);
		this.writeF(this.bonus);
		this.writeD(this.bonusExpire);
	}
}