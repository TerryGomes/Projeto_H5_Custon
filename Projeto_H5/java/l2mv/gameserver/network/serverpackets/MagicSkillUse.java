package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;

/**
 * Format:   dddddddddh [h] h [ddd]
 * Пример пакета:
 * 48
 * 86 99 00 4F  86 99 00 4F
 * EF 08 00 00  01 00 00 00
 * 00 00 00 00  00 00 00 00
 * F9 B5 FF FF  7D E0 01 00  68 F3 FF FF
 * 00 00 00 00
 */
public class MagicSkillUse extends L2GameServerPacket
{
	private int _targetId;
	private int _skillId;
	private int _skillLevel;
	private int _hitTime;
	private int _reuseDelay;
	private int _chaId, _x, _y, _z, _tx, _ty, _tz;

	public MagicSkillUse(Creature cha, Creature target, int skillId, int skillLevel, int hitTime, long reuseDelay)
	{
		_chaId = cha.getObjectId();
		_targetId = target.getObjectId();
		_skillId = skillId;
		_skillLevel = skillLevel;
		_hitTime = hitTime;
		_reuseDelay = (int) reuseDelay;
		_x = cha.getX();
		_y = cha.getY();
		_z = cha.getZ();
		_tx = target.getX();
		_ty = target.getY();
		_tz = target.getZ();
	}

	public MagicSkillUse(Creature cha, int skillId, int skillLevel, int hitTime, long reuseDelay)
	{
		_chaId = cha.getObjectId();
		_targetId = cha.getTargetId();
		_skillId = skillId;
		_skillLevel = skillLevel;
		_hitTime = hitTime;
		_reuseDelay = (int) reuseDelay;
		_x = cha.getX();
		_y = cha.getY();
		_z = cha.getZ();
		_tx = cha.getX();
		_ty = cha.getY();
		_tz = cha.getZ();
	}

	@Override
	protected final void writeImpl()
	{
		Player activeChar = getClient().getActiveChar();

		if (activeChar != null && activeChar.isNotShowBuffAnim() && activeChar.getObjectId() != _chaId)
		{
			return;
		}

		writeC(0x48);
		writeD(_chaId);
		writeD(_targetId);
		writeD(_skillId);
		writeD(_skillLevel);
		writeD(_hitTime);
		writeD(_reuseDelay);
		writeD(_x);
		writeD(_y);
		writeD(_z);
		writeD(0x00); // unknown
		writeD(_tx);
		writeD(_ty);
		writeD(_tz);
	}
}