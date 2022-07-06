package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.SystemMessage;

public class RequestChangeNicknameColor extends L2GameClientPacket
{
	private static final int COLORS[] =
	{
		0x9393FF, // Pink
		0x7C49FC, // Rose Pink
		0x97F8FC, // Lemon Yellow
		0xFA9AEE, // Lilac
		0xFF5D93, // Cobalt Violet
		0x00FCA0, // Mint Green
		0xA0A601, // Peacock Green
		0x7898AF, // Yellow Ochre
		0x486295, // Chocolate
		0x999999 // Silver
	};

	private int _colorNum, _itemObjectId;
	private String _title;

	@Override
	protected void readImpl()
	{
		_colorNum = readD();
		_title = readS();
		_itemObjectId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if ((activeChar == null) || _colorNum < 0 || _colorNum >= COLORS.length)
		{
			return;
		}

		ItemInstance item = activeChar.getInventory().getItemByObjectId(_itemObjectId);
		if (item == null)
		{
			return;
		}

		if (item.getCount() < 1)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return;
		}

		if (activeChar.consumeItem(item.getItemId(), 1))
		{
			activeChar.setTitleColor(COLORS[_colorNum]);
			activeChar.setTitle(_title);
			activeChar.broadcastUserInfo(true);
		}
	}
}