package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

public class ExBR_PremiumState extends L2GameServerPacket
{
	private int _objectId;
	private int _state;

	public ExBR_PremiumState(Player activeChar, boolean state)
	{
		this._objectId = activeChar.getObjectId();
		this._state = state ? 1 : 0;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xD9);
		this.writeD(this._objectId);
		this.writeC(this._state);
	}
}
