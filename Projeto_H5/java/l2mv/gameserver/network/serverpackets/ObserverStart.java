package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.utils.Location;

public class ObserverStart extends L2GameServerPacket
{
	// ddSS
	private Location _loc;

	public ObserverStart(Location loc)
	{
		this._loc = loc;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xeb);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z);
		this.writeC(0x00);
		this.writeC(0xc0);
		this.writeC(0x00);
	}
}