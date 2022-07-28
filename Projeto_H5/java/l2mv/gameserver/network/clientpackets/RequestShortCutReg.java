package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.actor.instances.player.ShortCut;
import l2mv.gameserver.network.serverpackets.ShortCutRegister;

public class RequestShortCutReg extends L2GameClientPacket
{
	private int _type, _id, _slot, _page, _lvl, _characterType;

	@Override
	protected void readImpl()
	{
		this._type = this.readD();
		int slot = this.readD();
		this._id = this.readD();
		this._lvl = this.readD();
		this._characterType = this.readD();

		this._slot = slot % 12;
		this._page = slot / 12;
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (this._page < 0 || this._page > ShortCut.PAGE_MAX || this._slot < 0 || this._type < 1 || this._type > ShortCut.TYPE_MAX)
		{
			activeChar.sendActionFailed();
			return;
		}

		ShortCut shortCut = new ShortCut(this._slot, this._page, this._type, this._id, this._lvl, this._characterType);
		activeChar.sendPacket(new ShortCutRegister(activeChar, shortCut));
		activeChar.registerShortCut(shortCut);
	}
}