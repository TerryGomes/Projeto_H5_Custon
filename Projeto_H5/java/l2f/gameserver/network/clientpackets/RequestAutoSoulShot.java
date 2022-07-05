package l2f.gameserver.network.clientpackets;

import l2f.gameserver.handler.items.IItemHandler;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.ExAutoSoulShot;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;

/**
 * format:		chdd
 */
public class RequestAutoSoulShot extends L2GameClientPacket
{
	private int _itemId;
	private boolean _isEnable; // 1 = on : 0 = off;

	@Override
	protected void readImpl()
	{
		_itemId = readD();
		_isEnable = readD() == 1;
	}

	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if ((activeChar == null) || activeChar.getPrivateStoreType() != Player.STORE_PRIVATE_NONE || activeChar.isDead())
		{
			return;
		}

		final ItemInstance item = activeChar.getInventory().getItemByItemId(_itemId);
		if (item == null)
		{
			return;
		}

		if (_isEnable)
		{
			activeChar.addAutoSoulShot(_itemId);
			activeChar.sendPacket(new ExAutoSoulShot(_itemId, true));
			activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(item.getName()));
			final IItemHandler handler = item.getTemplate().getHandler();
			if (handler != null)
			{
				handler.useItem(activeChar, item, false);
			}
		}
		else
		{
			activeChar.removeAutoSoulShot(_itemId);
			activeChar.sendPacket(new ExAutoSoulShot(_itemId, false));
			activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED).addString(item.getName()));
		}
	}
}