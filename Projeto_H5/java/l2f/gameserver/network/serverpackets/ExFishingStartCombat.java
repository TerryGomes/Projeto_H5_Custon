package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Creature;

/**
 * Format (ch)dddcc
 */
public class ExFishingStartCombat extends L2GameServerPacket
{
	int _time, _hp;
	int _lureType, _deceptiveMode, _mode;
	private int char_obj_id;

	public ExFishingStartCombat(Creature character, int time, int hp, int mode, int lureType, int deceptiveMode)
	{
		char_obj_id = character.getObjectId();
		_time = time;
		_hp = hp;
		_mode = mode;
		_lureType = lureType;
		_deceptiveMode = deceptiveMode;
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0x27);

		writeD(char_obj_id);
		writeD(_time);
		writeD(_hp);
		writeC(_mode); // mode: 0 = resting, 1 = fighting
		writeC(_lureType); // 0 = newbie lure, 1 = normal lure, 2 = night lure
		writeC(_deceptiveMode); // Fish Deceptive Mode: 0 = no, 1 = yes
	}
}