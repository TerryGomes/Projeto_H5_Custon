package l2mv.gameserver.network.serverpackets;

public class PledgeReceiveUpdatePower extends L2GameServerPacket
{
	private int _privs;

	public PledgeReceiveUpdatePower(int privs)
	{
		this._privs = privs;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x42);
		this.writeD(this._privs); // Filler??????
	}
}