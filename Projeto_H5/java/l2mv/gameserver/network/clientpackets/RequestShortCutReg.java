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
		_type = readD();
		int slot = readD();
		_id = readD();
		_lvl = readD();
		_characterType = readD();

		_slot = slot % 12;
		_page = slot / 12;
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (_page < 0 || _page > ShortCut.PAGE_MAX || _slot < 0 || _type < 1 || _type > ShortCut.TYPE_MAX)
		{
			activeChar.sendActionFailed();
			return;
		}

		ShortCut shortCut = new ShortCut(_slot, _page, _type, _id, _lvl, _characterType);
		activeChar.sendPacket(new ShortCutRegister(activeChar, shortCut));
		activeChar.registerShortCut(shortCut);
	}
}