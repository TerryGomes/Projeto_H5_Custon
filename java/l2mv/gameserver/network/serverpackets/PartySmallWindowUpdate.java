package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

public class PartySmallWindowUpdate extends L2GameServerPacket
{
	private int obj_id, class_id, level;
	private int curCp, maxCp, curHp, maxHp, curMp, maxMp;
	private String obj_name;

	public PartySmallWindowUpdate(Player member)
	{
		this.obj_id = member.getObjectId();
		this.obj_name = member.getName();
		this.curCp = (int) member.getCurrentCp();
		this.maxCp = member.getMaxCp();
		this.curHp = (int) member.getCurrentHp();
		this.maxHp = member.getMaxHp();
		this.curMp = (int) member.getCurrentMp();
		this.maxMp = member.getMaxMp();
		this.level = member.getLevel();
		this.class_id = member.getClassId().getId();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x52);
		// dSdddddddd
		this.writeD(this.obj_id);
		this.writeS(this.obj_name);
		this.writeD(this.curCp);
		this.writeD(this.maxCp);
		this.writeD(this.curHp);
		this.writeD(this.maxHp);
		this.writeD(this.curMp);
		this.writeD(this.maxMp);
		this.writeD(this.level);
		this.writeD(this.class_id);
	}
}