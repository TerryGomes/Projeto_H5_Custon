package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

public class RequestExOustFromMPCC extends L2GameClientPacket
{
	private String _name;

	/**
	 * format: chS
	 */
	@Override
	protected void readImpl()
	{
		this._name = this.readS(16);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null || !activeChar.isInParty() || !activeChar.getParty().isInCommandChannel())
		{
			return;
		}

		Player target = World.getPlayer(this._name);

		// Чар с таким имененм не найден в мире
		if (target == null)
		{
			activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_CURRENTLY_ONLINE);
			return;
		}

		// Сам себя нельзя
		if (activeChar == target)
		{
			return;
		}

		// Указанный чар не в пати, не в СС, в чужом СС
		if (!target.isInParty() || !target.getParty().isInCommandChannel() || activeChar.getParty().getCommandChannel() != target.getParty().getCommandChannel())
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		// Это может делать только лидер СС
		if (activeChar.getParty().getCommandChannel().getLeader() != activeChar)
		{
			activeChar.sendPacket(SystemMsg.ONLY_THE_CREATOR_OF_A_COMMAND_CHANNEL_CAN_ISSUE_A_GLOBAL_COMMAND);
			return;
		}

		target.getParty().getCommandChannel().getLeader().sendPacket(new SystemMessage2(SystemMsg.C1S_PARTY_HAS_BEEN_DISMISSED_FROM_THE_COMMAND_CHANNEL).addString(target.getName()));
		target.getParty().getCommandChannel().removeParty(target.getParty());
		target.getParty().sendPacket(SystemMsg.YOU_WERE_DISMISSED_FROM_THE_COMMAND_CHANNEL);
	}
}