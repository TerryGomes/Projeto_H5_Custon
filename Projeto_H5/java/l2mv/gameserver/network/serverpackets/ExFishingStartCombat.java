package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Creature;

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
		this.char_obj_id = character.getObjectId();
		this._time = time;
		this._hp = hp;
		this._mode = mode;
		this._lureType = lureType;
		this._deceptiveMode = deceptiveMode;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x27);

		this.writeD(this.char_obj_id);
		this.writeD(this._time);
		this.writeD(this._hp);
		this.writeC(this._mode); // mode: 0 = resting, 1 = fighting
		this.writeC(this._lureType); // 0 = newbie lure, 1 = normal lure, 2 = night lure
		this.writeC(this._deceptiveMode); // Fish Deceptive Mode: 0 = no, 1 = yes
	}
}