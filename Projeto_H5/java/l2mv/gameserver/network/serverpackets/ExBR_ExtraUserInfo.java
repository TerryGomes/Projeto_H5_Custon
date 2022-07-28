package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

public class ExBR_ExtraUserInfo extends L2GameServerPacket
{
	private int _objectId;
	private int _effect3;
	private int _lectureMark;

	public ExBR_ExtraUserInfo(Player cha)
	{
		this._objectId = cha.getObjectId();
		this._effect3 = cha.getAbnormalEffect3();
		this._lectureMark = cha.getLectureMark();
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xDA);
		this.writeD(this._objectId); // object id of player
		this.writeD(this._effect3); // event effect id
		this.writeC(this._lectureMark);
	}
}