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
		this._rev = rev;
		this._count = count;
		this._macro = macro;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xe8);

		this.writeD(this._rev); // macro change revision (changes after each macro edition)
		this.writeC(0); // unknown
		this.writeC(this._count); // count of Macros
		this.writeC(this._macro != null ? 1 : 0); // unknown

		if (this._macro != null)
		{
			this.writeD(this._macro.id); // Macro ID
			this.writeS(this._macro.name); // Macro Name
			this.writeS(this._macro.descr); // Desc
			this.writeS(this._macro.acronym); // acronym
			this.writeC(this._macro.icon); // icon

			this.writeC(this._macro.commands.length); // count

			for (int i = 0; i < this._macro.commands.length; i++)
			{
				Macro.L2MacroCmd cmd = this._macro.commands[i];
				this.writeC(i + 1); // i of count
				this.writeC(cmd.type); // type 1 = skill, 3 = action, 4 = shortcut
				this.writeD(cmd.d1); // skill id
				this.writeC(cmd.d2); // shortcut id
				this.writeS(cmd.cmd); // command name
			}
		}
	}
}