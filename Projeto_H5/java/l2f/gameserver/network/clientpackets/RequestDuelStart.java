package l2f.gameserver.network.clientpackets;

import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.EventHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.model.entity.events.EventType;
import l2f.gameserver.model.entity.events.impl.DuelEvent;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;

public class RequestDuelStart extends L2GameClientPacket
{
	private String _name;
	private int _duelType;

	@Override
	protected void readImpl()
	{
		_name = readS(Config.CNAME_MAXLEN);
		_duelType = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		if (player.isActionsDisabled())
		{
			player.sendActionFailed();
			return;
		}

		if (player.isProcessingRequest())
		{
			player.sendPacket(SystemMsg.WAITING_FOR_ANOTHER_REPLY);
			return;
		}

		Player target = World.getPlayer(_name);
		if (target == null || target == player)
		{
			player.sendPacket(SystemMsg.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL);
			return;
		}

		DuelEvent duelEvent = EventHolder.getInstance().getEvent(EventType.PVP_EVENT, _duelType);
		if ((duelEvent == null) || !duelEvent.canDuel(player, target, true) || target.isInFightClub())
		{
			return;
		}

		if (target.isBusy())
		{
			player.sendPacket(new SystemMessage2(SystemMsg.C1_IS_ON_ANOTHER_TASK).addName(target));
			return;
		}

		duelEvent.askDuel(player, target);
	}
}