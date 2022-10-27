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
		this._objId = player.getObjectId();
		this._pkg = player.getPrivateStoreType() == Player.STORE_PRIVATE_SELL_PACKAGE;
		this._name = StringUtils.defaultString(player.getSellStoreName());
	}

	@Override
	protected final void writeImpl()
	{
		if (this._pkg)
		{
			this.writeEx(0x80);
		}
		else
		{
			this.writeC(0xA2);
		}
		this.writeD(this._objId);
		this.writeS(this._name);
	}
}