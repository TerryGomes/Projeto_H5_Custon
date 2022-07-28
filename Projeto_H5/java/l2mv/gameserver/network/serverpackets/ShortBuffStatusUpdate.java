package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Effect;

public class ShortBuffStatusUpdate extends L2GameServerPacket
{
	/**
	 * This is client's row 2 buff packet.
	 *
	 * Example (C4):
	 * F4 CD 04 00 00 07 00 00 00 0F 00 00 00 - overlord's healing, panel2
	 *
	 * structure cddd
	 *
	 * NOTES:
	 * 1). hex converting:
	 * Skill 1229 is in hex 4CD, but in packet it is CD 04 00 00.
	 * So i think that we must read the skill's hex id form behind ^^
	 * 2). multipe skills on row 2:
	 * i don't know what more skills can go at row2 @ offie.
	 * please contact me to test it. Currently packet is working for one skill.
	 * 3). Removing buff icon
	 * must be sended empty packet
	 * F4 00 00 00 00 00 00 00 00 00 00 00 00
	 * to remove buff icon. Or it will be lasted forever.
	 */

	int _skillId;
	int _skillLevel;
	int _skillDuration;

	public ShortBuffStatusUpdate(Effect effect)
	{
		this._skillId = effect.getSkill().getDisplayId();
		this._skillLevel = effect.getSkill().getDisplayLevel();
		this._skillDuration = effect.getTimeLeft();
	}

	/**
	 * Zero packet to delete skill icon.
	 */
	public ShortBuffStatusUpdate()
	{
		this._skillId = 0;
		this._skillLevel = 0;
		this._skillDuration = 0;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xfa); // Packet type
		this.writeD(this._skillId); // skill id??? CD 04 00 00 = skill 1229, hex 4CD
		this.writeD(this._skillLevel); // Skill Level??? 07 00 00 00 = casted by heal 7 lvl.
		this.writeD(this._skillDuration); // DURATION???? 0F 00 00 00 = 15 sec = overlord's heal
	}
}