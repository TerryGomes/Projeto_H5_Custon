package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Creature;

public class NickNameChanged extends L2GameServerPacket
{
	private final int objectId;
	private final String title;

	public NickNameChanged(Creature cha)
	{
		this.objectId = cha.getObjectId();
		this.title = cha.getTitle();
	}

	@Override
	protected void writeImpl()
	{
		this.writeC(0xCC);
		this.writeD(this.objectId);
		this.writeS(this.title);
	}
}