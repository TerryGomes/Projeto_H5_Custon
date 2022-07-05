package l2f.loginserver.gameservercon.gspackets;

import l2f.loginserver.accounts.Account;
import l2f.loginserver.gameservercon.ReceivablePacket;

public class BonusRequest extends ReceivablePacket
{
	private String account;
	private double bonus;
	private int bonusExpire;

	@Override
	protected void readImpl()
	{
		account = readS();
		bonus = readF();
		bonusExpire = readD();
	}

	@Override
	protected void runImpl()
	{
		final Account acc = new Account(account);
		acc.restore();
		acc.setBonus(bonus);
		acc.setBonusExpire(bonusExpire);
		acc.update();
	}
}
