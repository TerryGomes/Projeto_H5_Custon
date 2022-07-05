package l2f.gameserver.network.serverpackets;

import l2f.gameserver.tables.SkillTreeTable;

/**
 * Дамп с оффа, 828 протокол:
 * 0000: fe 5e 00 01 00 00 00 46 00 00 00 65 00 00 00 7e
 * 0010: cc 12 00 fe fc bb 00 00 00 00 00 61 00 00 00 01
 * 0020: 00 00 00 05 00 00 00 9b 25 00 00 00 00 00 00
 *
 * Скилл: Drain Health (id: 70)
 * Левел скила: 53
 * Нужен предмет: Giant's Secret Codex of Mastery - 1 штука
 * Точим на +1 Power (lvl: 101)
 * Требуется SP: 1231998
 * Требуется exp: 12319998
 * Шанс успеха: 97%
 *
 * Еще дампы для примера:
 * 0000: fe 5e 00 01 00 00 00 7a 00 00 00 6b 00 00 00 60
 * 0010: 81 1e 00 c0 0d 31 01 00 00 00 00 50 00 00 00 01
 * 0020: 00 00 00 05 00 00 00 9b 25 00 00 00 00 00 00
 *
 * 0000: fe 5e 00 03 00 00 00 9a 01 00 00 d0 00 00 00 20
 * 0010: a9 03 00 40 9b 24 00 00 00 00 00 64 00 00 00 01
 * 0020: 00 00 00 05 00 00 00 9a 25 00 00 00 00 00 00
 *
 * 0000: fe 5e 00 00 00 00 00 6f 00 00 00 65 00 00 00 d5
 * 0010: 79 04 00 55 c2 2c 00 00 00 00 00 61 00 00 00 01
 * 0020: 00 00 00 05 00 00 00 de 19 00 00 00 00 00 00
 */
public class ExEnchantSkillInfoDetail extends L2GameServerPacket
{
	private final int _unk = 0;
	private final int _skillId;
	private final int _skillLvl;
	private final int _sp;
	private final int _chance;
	private final int _bookId, _adenaCount;

	public ExEnchantSkillInfoDetail(int skillId, int skillLvl, int sp, int chance, int bookId, int adenaCount)
	{
		_skillId = skillId;
		_skillLvl = skillLvl;
		_sp = sp;
		_chance = chance;
		_bookId = bookId;
		_adenaCount = adenaCount;
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0x5e);
		// FIXME GraciaEpilogue ddddd dx[dd]

		writeD(_unk); // ?
		writeD(_skillId);
		writeD(_skillLvl);
		writeD(_sp);
		writeD(_chance);

		writeD(2);
		writeD(57); // adena
		writeD(_adenaCount); // adena count ?
		if (_bookId > 0)
		{
			writeD(_bookId); // book
			writeD(1); // book count
		}
		else
		{
			writeD(SkillTreeTable.NORMAL_ENCHANT_BOOK); // book
			writeD(0); // book count
		}
	}
}