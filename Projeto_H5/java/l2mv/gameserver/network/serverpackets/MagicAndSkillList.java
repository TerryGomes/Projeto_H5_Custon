package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Creature;

/**
 * Format:   ddd
 * Пример пакета:
 * 40
 * c8 22 00 49
 * be 50 00 00
 * 86 25 0b 00
 * @author SYS
 */
public class MagicAndSkillList extends L2GameServerPacket
{
	private int _chaId;
	private int _unk1;
	private int _unk2;

	public MagicAndSkillList(Creature cha, int unk1, int unk2)
	{
		this._chaId = cha.getObjectId();
		this._unk1 = unk1;
		this._unk2 = unk2;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x40);
		this.writeD(this._chaId);
		this.writeD(this._unk1); // в снифе было 20670
		this.writeD(this._unk2); // в снифе было 730502
	}
}