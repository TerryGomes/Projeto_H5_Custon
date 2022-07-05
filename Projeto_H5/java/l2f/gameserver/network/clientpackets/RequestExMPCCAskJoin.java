package l2f.gameserver.network.clientpackets;

import l2f.gameserver.model.CommandChannel;
import l2f.gameserver.model.Party;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Request;
import l2f.gameserver.model.Request.L2RequestType;
import l2f.gameserver.model.World;
import l2f.gameserver.network.serverpackets.ExAskJoinMPCC;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;

/**
 * Format: (ch) S
 */
public class RequestExMPCCAskJoin extends L2GameClientPacket
{
	private String _name;

	@Override
	protected void readImpl()
	{
		_name = readS(16);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (activeChar.isOutOfControl())
		{
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.isProcessingRequest())
		{
			activeChar.sendPacket(SystemMsg.WAITING_FOR_ANOTHER_REPLY);
			return;
		}

		if (!activeChar.isInParty())
		{
			activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_AUTHORITY_TO_INVITE_SOMEONE_TO_THE_COMMAND_CHANNEL);
			return;
		}

		Player target = World.getPlayer(_name);

		// Чар с таким именем не найден в мире
		if (target == null)
		{
			activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_CURRENTLY_ONLINE);
			return;
		}

		// Нельзя приглашать безпартийных или члена своей партии
		if (activeChar == target || !target.isInParty() || activeChar.getParty() == target.getParty())
		{
			activeChar.sendPacket(SystemMsg.YOU_HAVE_INVITED_THE_WRONG_TARGET);
			return;
		}

		// Если приглашен в СС не лидер партии, то посылаем приглашение лидеру его партии
		if (target.isInParty() && !target.getParty().isLeader(target))
		{
			target = target.getParty().getLeader();
		}

		if (target == null)
		{
			activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_CURRENTLY_ONLINE);
			return;
		}

		if (target.getParty().isInCommandChannel())
		{
			activeChar.sendPacket(new SystemMessage2(SystemMsg.C1S_PARTY_IS_ALREADY_A_MEMBER_OF_THE_COMMAND_CHANNEL).addString(target.getName()));
			return;
		}

		if ((target.isInFightClub() && !target.getFightClubEvent().canReceiveInvitations(activeChar, target)) || target.isBusy())
		{
			activeChar.sendPacket(new SystemMessage2(SystemMsg.C1_IS_ON_ANOTHER_TASK).addString(target.getName()));
			return;
		}

		Party activeParty = activeChar.getParty();

		if (activeParty.isInCommandChannel())
		{
			// Приглашение в уже существующий СС
			// Приглашать в СС может только лидер CC
			if (activeParty.getCommandChannel().getLeader() != activeChar)
			{
				activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_AUTHORITY_TO_INVITE_SOMEONE_TO_THE_COMMAND_CHANNEL);
				return;
			}

			sendInvite(activeChar, target);
		}
		else // СС еще не существует. Отсылаем запрос на инвайт и в случае согласия создаем канал
		if (CommandChannel.checkAuthority(activeChar))
		{
			sendInvite(activeChar, target);
		}
	}

	private void sendInvite(Player requestor, Player target)
	{
		new Request(L2RequestType.CHANNEL, requestor, target).setTimeout(10000L);
		target.sendPacket(new ExAskJoinMPCC(requestor.getName()));
		requestor.sendMessage("You invited " + target.getName() + " to your Command Channel."); // TODO [G1ta0] custom message
	}
}