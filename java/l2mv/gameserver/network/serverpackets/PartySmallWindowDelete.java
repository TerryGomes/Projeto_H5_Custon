package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

public class PartySmallWindowDelete extends L2GameServerPacket
{
	private final int _objId;
	private final String _name;

	public PartySmallWindowDelete(Player member)
	{
		this._objId = member.getObjectId();
		this._name = member.getName();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x51);
		this.writeD(this._objId);
		this.writeS(this._name);
	}
}