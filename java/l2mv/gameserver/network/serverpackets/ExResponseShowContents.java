package l2mv.gameserver.network.serverpackets;

/**
 * @author VISTALL
 */
public class ExResponseShowContents extends L2GameServerPacket
{
	private final String _contents;

	public ExResponseShowContents(String contents)
	{
		this._contents = contents;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xB0);
		this.writeS(this._contents);
	}
}