package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

public class PartySmallWindowUpdate extends L2GameServerPacket
{
	private int obj_id, class_id, level;
	private int curCp, maxCp, curHp, maxHp, curMp, maxMp;
	private String obj_name;

	public PartySmallWindowUpdate(Player member)
	{
		obj_id = member.getObjectId();
		obj_name = member.getName();
		curCp = (int) member.getCurrentCp();
		maxCp = member.getMaxCp();
		curHp = (int) member.getCurrentHp();
		maxHp = member.getMaxHp();
		curMp = (int) member.getCurrentMp();
		maxMp = member.getMaxMp();
		level = member.getLevel();
		class_id = member.getClassId().getId();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x52);
		// dSdddddddd
		writeD(obj_id);
		writeS(obj_name);
		writeD(curCp);
		writeD(maxCp);
		writeD(curHp);
		writeD(maxHp);
		writeD(curMp);
		writeD(maxMp);
		writeD(level);
		writeD(class_id);
	}
}