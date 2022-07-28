package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.actor.instances.player.Macro;
import l2mv.gameserver.model.actor.instances.player.Macro.L2MacroCmd;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

/**
 * packet type id 0xcd
 *
 * sample
 *
 * cd
 * d // id
 * S // macro name
 * S // unknown  desc
 * S // unknown  acronym
 * c // icon
 * c // count
 *
 * c // entry
 * c // type
 * d // skill id
 * c // shortcut id
 * S // command name
 *
 * format:		cdSSScc (ccdcS)
 */
public class RequestMakeMacro extends L2GameClientPacket
{
	private Macro _macro;

	@Override
	protected void readImpl()
	{
		int _id = this.readD();
		String _name = this.readS(32);
		String _desc = this.readS(64);
		String _acronym = this.readS(4);
		int _icon = this.readC();
		int _count = this.readC();
		if (_count > 12)
		{
			_count = 12;
		}
		L2MacroCmd[] commands = new L2MacroCmd[_count];
		for (int i = 0; i < _count; i++)
		{
			int entry = this.readC();
			int type = this.readC(); // 1 = skill, 3 = action, 4 = shortcut
			int d1 = this.readD(); // skill or page number for shortcuts
			int d2 = this.readC();
			String command = this.readS().replace(";", "").replace(",", "");
			commands[i] = new L2MacroCmd(entry, type, d1, d2, command);
		}
		this._macro = new Macro(_id, _icon, _name, _desc, _acronym, commands);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (activeChar.getMacroses().getAllMacroses().length > 48)
		{
			activeChar.sendPacket(SystemMsg.YOU_MAY_CREATE_UP_TO_48_MACROS);
			return;
		}

		if (this._macro.name.length() == 0)
		{
			activeChar.sendPacket(SystemMsg.ENTER_THE_NAME_OF_THE_MACRO);
			return;
		}

		if (this._macro.descr.length() > 32)
		{
			activeChar.sendPacket(SystemMsg.MACRO_DESCRIPTIONS_MAY_CONTAIN_UP_TO_32_CHARACTERS);
			return;
		}

		activeChar.registerMacro(this._macro);
	}
}