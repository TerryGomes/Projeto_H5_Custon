package l2mv.gameserver.network.serverpackets;

public class ExAskCoupleAction extends L2GameServerPacket
{
	private int _objectId, _socialId;

	public ExAskCoupleAction(int objectId, int socialId)
	{
		this._objectId = objectId;
		this._socialId = socialId;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xBB);
		this.writeD(this._socialId);
		this.writeD(this._objectId);
	}
}
