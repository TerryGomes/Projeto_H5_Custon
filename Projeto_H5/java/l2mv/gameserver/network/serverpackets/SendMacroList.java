package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.actor.instances.player.Macro;

/**
 * packet type id 0xe7
 *
 * sample
 *
 * e7
 * d // unknown change of Macro edit,add,delete
 * c // unknown
 * c //count of Macros
 * c // unknown
 *
 * d // id
 * S // macro name
 * S // desc
 * S // acronym
 * c // icon
 * c // count
 *
 * c // entry
 * c // type
 * d // skill id
 * c // shortcut id
 * S // command name
 *
 * format:		cdccdSSScc (ccdcS)
 */
public class SendMacroList extends L2GameServerPacket
{
	private final int _rev;
	private final int _count;
	private final Macro _macro;

	public SendMacroList(int rev, int count, Macro macro)
	{
		_rev = rev;
		_count = count;
		_macro = macro;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xe8);

		writeD(_rev); // macro change revision (changes after each macro edition)
		writeC(0); // unknown
		writeC(_count); // count of Macros
		writeC(_macro != null ? 1 : 0); // unknown

		if (_macro != null)
		{
			writeD(_macro.id); // Macro ID
			writeS(_macro.name); // Macro Name
			writeS(_macro.descr); // Desc
			writeS(_macro.acronym); // acronym
			writeC(_macro.icon); // icon

			writeC(_macro.commands.length); // count

			for (int i = 0; i < _macro.commands.length; i++)
			{
				Macro.L2MacroCmd cmd = _macro.commands[i];
				writeC(i + 1); // i of count
				writeC(cmd.type); // type 1 = skill, 3 = action, 4 = shortcut
				writeD(cmd.d1); // skill id
				writeC(cmd.d2); // shortcut id
				writeS(cmd.cmd); // command name
			}
		}
	}
}