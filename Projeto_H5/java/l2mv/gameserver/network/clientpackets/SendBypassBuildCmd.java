package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.handler.admincommands.AdminCommandHandler;
import l2mv.gameserver.model.Player;

public class SendBypassBuildCmd extends L2GameClientPacket
{
	private String _command;

	@Override
	protected void readImpl()
	{
		this._command = this.readS();

		if (this._command != null)
		{
			this._command = this._command.trim();
		}
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();

		if (activeChar == null || activeChar.isBlocked())
		{
			return;
		}

		String cmd = this._command;

		if (!cmd.contains("admin_"))
		{
			cmd = "admin_" + cmd;
		}

		AdminCommandHandler.getInstance().useAdminCommandHandler(activeChar, cmd);
	}
}