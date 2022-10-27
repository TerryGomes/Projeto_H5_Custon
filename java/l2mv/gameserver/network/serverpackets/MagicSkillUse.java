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
		this._chaId = cha.getObjectId();
		this._targetId = target.getObjectId();
		this._skillId = skillId;
		this._skillLevel = skillLevel;
		this._hitTime = hitTime;
		this._reuseDelay = (int) reuseDelay;
		this._x = cha.getX();
		this._y = cha.getY();
		this._z = cha.getZ();
		this._tx = target.getX();
		this._ty = target.getY();
		this._tz = target.getZ();
	}

	public MagicSkillUse(Creature cha, int skillId, int skillLevel, int hitTime, long reuseDelay)
	{
		this._chaId = cha.getObjectId();
		this._targetId = cha.getTargetId();
		this._skillId = skillId;
		this._skillLevel = skillLevel;
		this._hitTime = hitTime;
		this._reuseDelay = (int) reuseDelay;
		this._x = cha.getX();
		this._y = cha.getY();
		this._z = cha.getZ();
		this._tx = cha.getX();
		this._ty = cha.getY();
		this._tz = cha.getZ();
	}

	@Override
	protected final void writeImpl()
	{
		Player activeChar = this.getClient().getActiveChar();

		if (activeChar != null && activeChar.isNotShowBuffAnim() && activeChar.getObjectId() != this._chaId)
		{
			return;
		}

		this.writeC(0x48);
		this.writeD(this._chaId);
		this.writeD(this._targetId);
		this.writeD(this._skillId);
		this.writeD(this._skillLevel);
		this.writeD(this._hitTime);
		this.writeD(this._reuseDelay);
		this.writeD(this._x);
		this.writeD(this._y);
		this.writeD(this._z);
		this.writeD(0x00); // unknown
		this.writeD(this._tx);
		this.writeD(this._ty);
		this.writeD(this._tz);
	}
}