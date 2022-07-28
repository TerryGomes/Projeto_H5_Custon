package l2mv.gameserver.network.serverpackets;

import org.apache.commons.lang3.StringUtils;

import l2mv.gameserver.model.Player;

public class PrivateStoreMsgBuy extends L2GameServerPacket
{
	private final int _objId;
	private final String _name;

	/**
	 * Название личного магазина покупки
	 * @param player
	 */
	public PrivateStoreMsgBuy(Player player)
	{
		this._objId = player.getObjectId();
		this._name = StringUtils.defaultString(player.getBuyStoreName());
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xBF);
		this.writeD(this._objId);
		this.writeS(this._name);
	}
}