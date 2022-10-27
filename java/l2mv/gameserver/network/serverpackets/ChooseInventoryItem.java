package l2mv.gameserver.network.serverpackets;

public class ChooseInventoryItem extends L2GameServerPacket
{
	private int ItemID;

	public ChooseInventoryItem(int id)
	{
		this.ItemID = id;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x7c);
		this.writeD(this.ItemID);
	}
}