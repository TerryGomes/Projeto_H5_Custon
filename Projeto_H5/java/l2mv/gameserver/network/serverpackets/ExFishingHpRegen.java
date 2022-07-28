package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Creature;

/**
 * Format (ch)dddcccd
 * d: cahacter oid
 * d: time left
 * d: fish hp
 * c:
 * c:
 * c: 00 if fish gets damage 02 if fish regens
 * d:
 */
public class ExFishingHpRegen extends L2GameServerPacket
{
	private int _time, _fishHP, _HPmode, _Anim, _GoodUse, _Penalty, _hpBarColor;
	private int char_obj_id;

	public ExFishingHpRegen(Creature character, int time, int fishHP, int HPmode, int GoodUse, int anim, int penalty, int hpBarColor)
	{
		this.char_obj_id = character.getObjectId();
		this._time = time;
		this._fishHP = fishHP;
		this._HPmode = HPmode;
		this._GoodUse = GoodUse;
		this._Anim = anim;
		this._Penalty = penalty;
		this._hpBarColor = hpBarColor;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x28);
		this.writeD(this.char_obj_id);
		this.writeD(this._time);
		this.writeD(this._fishHP);
		this.writeC(this._HPmode); // 0 = HP stop, 1 = HP raise
		this.writeC(this._GoodUse); // 0 = none, 1 = success, 2 = failed
		this.writeC(this._Anim); // Anim: 0 = none, 1 = reeling, 2 = pumping
		this.writeD(this._Penalty); // Penalty
		this.writeC(this._hpBarColor); // 0 = normal hp bar, 1 = purple hp bar

	}
}