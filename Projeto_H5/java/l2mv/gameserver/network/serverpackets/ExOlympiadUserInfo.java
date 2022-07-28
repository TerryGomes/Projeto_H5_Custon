package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

public class ExOlympiadUserInfo extends L2GameServerPacket
{
	private int _side, class_id, curHp, maxHp, curCp, maxCp;
	private int obj_id = 0;
	private String _name;

	public ExOlympiadUserInfo(Player player, int side)
	{
		this._side = side;
		this.obj_id = player.getObjectId();
		this.class_id = player.getClassId().getId();
		this._name = player.getName();
		this.curHp = (int) player.getCurrentHp();
		this.maxHp = player.getMaxHp();
		this.curCp = (int) player.getCurrentCp();
		this.maxCp = player.getMaxCp();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x7a);
		this.writeC(this._side);
		this.writeD(this.obj_id);
		this.writeS(this._name);
		this.writeD(this.class_id);
		this.writeD(this.curHp);
		this.writeD(this.maxHp);
		this.writeD(this.curCp);
		this.writeD(this.maxCp);
	}
}