package l2f.gameserver.network.clientpackets;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.network.serverpackets.L2FriendSay;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.utils.Log;

/**
 * Recieve Private (Friend) Message
 * Format: c SS
 * S: Message
 * S: Receiving Player
 */
public class RequestSendL2FriendSay extends L2GameClientPacket
{
	private String _message;
	private String _reciever;

	@Override
	protected void readImpl()
	{
		_message = readS();// readS(2048);
		_reciever = readS();// readS(16);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (activeChar.getNoChannel() != 0)
		{
			if (activeChar.getNoChannelRemained() > 0 || activeChar.getNoChannel() < 0)
			{
				activeChar.sendPacket(SystemMsg.CHATTING_IS_CURRENTLY_PROHIBITED_);
				return;
			}
			activeChar.updateNoChannel(0);
		}

		Player targetPlayer = World.getPlayer(_reciever);
		if (targetPlayer == null)
		{
			activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
			return;
		}

		if (targetPlayer.isBlockAll())
		{
			activeChar.sendPacket(SystemMsg.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE);
			return;
		}

		if (!activeChar.getFriendList().getList().containsKey(targetPlayer.getObjectId()))
		{
			return;
		}

		Log.logChat(ChatType.L2FRIEND, activeChar.getName(), _reciever, _message);

		L2FriendSay frm = new L2FriendSay(activeChar.getName(), _reciever, _message);
		targetPlayer.sendPacket(frm);
	}
}