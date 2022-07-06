package l2mv.gameserver.network.serverpackets;

import org.apache.commons.lang3.StringUtils;

import l2mv.gameserver.model.Player;

public class PrivateStoreMsgSell extends L2GameServerPacket
{
	private final int _objId;
	private final String _name;
	private boolean _pkg;

	/**
	 * Название личного магазина продажи
	 * @param player
	 */
	public PrivateStoreMsgSell(Player player)
	{
		_objId = player.getObjectId();
		_pkg = player.getPrivateStoreType() == Player.STORE_PRIVATE_SELL_PACKAGE;
		_name = StringUtils.defaultString(player.getSellStoreName());
	}

	@Override
	protected final void writeImpl()
	{
		if (_pkg)
		{
			writeEx(0x80);
		}
		else
		{
			writeC(0xA2);
		}
		writeD(_objId);
		writeS(_name);
	}
}