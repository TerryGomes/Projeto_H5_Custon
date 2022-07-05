package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Player;

public class ExDuelUpdateUserInfo extends L2GameServerPacket
{
	private String _name;
	private int obj_id, class_id, level, curHp, maxHp, curMp, maxMp, curCp, maxCp;

	public ExDuelUpdateUserInfo(Player attacker)
	{
		_name = attacker.getName();
		obj_id = attacker.getObjectId();
		class_id = attacker.getClassId().getId();
		level = attacker.getLevel();
		curHp = (int) attacker.getCurrentHp();
		maxHp = attacker.getMaxHp();
		curMp = (int) attacker.getCurrentMp();
		maxMp = attacker.getMaxMp();
		curCp = (int) attacker.getCurrentCp();
		maxCp = attacker.getMaxCp();
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0x50);
		writeS(_name);
		writeD(obj_id);
		writeD(class_id);
		writeD(level);
		writeD(curHp);
		writeD(maxHp);
		writeD(curMp);
		writeD(maxMp);
		writeD(curCp);
		writeD(maxCp);
	}
}