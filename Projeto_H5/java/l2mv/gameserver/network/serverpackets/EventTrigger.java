package l2mv.gameserver.network.serverpackets;

/**
 * @author SYS
 * @date 10/9/2007
 */
public class EventTrigger extends L2GameServerPacket
{
	private int _trapId;
	private boolean _active;

	public EventTrigger(int trapId, boolean active)
	{
		this._trapId = trapId;
		this._active = active;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xCF);
		this.writeD(this._trapId); // trap object id
		this.writeC(this._active ? 1 : 0); // trap activity 1 or 0
	}
}