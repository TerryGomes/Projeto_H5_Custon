package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.PartyMatchingBBSManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Zone;
import l2mv.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2mv.gameserver.network.GameClient.GameClientState;
import l2mv.gameserver.network.serverpackets.ActionFail;
import l2mv.gameserver.network.serverpackets.CharacterSelectionInfo;
import l2mv.gameserver.network.serverpackets.RestartResponse;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.network.serverpackets.components.IStaticPacket;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

public class RequestRestart extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();

		if (activeChar == null)
		{
			return;
		}

		if (activeChar.isInObserverMode())
		{
			activeChar.sendPacket(SystemMsg.OBSERVERS_CANNOT_PARTICIPATE, RestartResponse.FAIL, ActionFail.STATIC);
			return;
		}

		if (activeChar.isInCombat())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_RESTART_WHILE_IN_COMBAT, RestartResponse.FAIL, ActionFail.STATIC);
			return;
		}

		if (activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2, RestartResponse.FAIL, ActionFail.STATIC);
			return;
		}

		if (activeChar.isInJail())
		{
			activeChar.standUp();
			activeChar.unblock();
		}

		if (activeChar.getVar("isPvPevents") != null)
		{
			activeChar.sendMessage("You can follow any responses did not leave while participating in the event!");
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage(new CustomMessage("l2mv.gameserver.clientpackets.Logout.Olympiad", activeChar));
			activeChar.sendPacket(RestartResponse.FAIL, ActionFail.STATIC);
			return;
		}

		if (activeChar.isInFightClub())
		{
			activeChar.sendMessage("You need to leave Fight Club first!");
			activeChar.sendPacket(RestartResponse.FAIL, ActionFail.STATIC);
			return;
		}

		if (activeChar.isInStoreMode() && !activeChar.isInZone(Zone.ZoneType.offshore) && Config.SERVICES_OFFLINE_TRADE_ALLOW_OFFSHORE)
		{
			activeChar.sendMessage(new CustomMessage("trade.OfflineNoTradeZoneOnlyOffshore", activeChar));
			activeChar.sendPacket(RestartResponse.FAIL, ActionFail.STATIC);
			return;
		}
		// Prevent player from restarting if they are a festival participant
		// and it is in progress, otherwise notify party members that the player
		// is not longer a participant.
		if (activeChar.isFestivalParticipant())
		{
			if (SevenSignsFestival.getInstance().isFestivalInitialized())
			{
				activeChar.sendMessage(new CustomMessage("l2mv.gameserver.clientpackets.RequestRestart.Festival", activeChar));
				activeChar.sendPacket(RestartResponse.FAIL, ActionFail.STATIC);
				return;
			}
		}

		if (!activeChar.getPermissions().canLogOut(true, true))
		{
			activeChar.sendPacket(RestartResponse.FAIL, ActionFail.STATIC);
			return;
		}

		if (PartyMatchingBBSManager.getInstance().partyMatchingPlayersList.contains(activeChar))
		{
			PartyMatchingBBSManager.getInstance().partyMatchingPlayersList.remove(activeChar);
			PartyMatchingBBSManager.getInstance().partyMatchingDescriptionList.remove(activeChar.getObjectId());
		}

		if (activeChar.isInAwayingMode())
		{
			activeChar.sendMessage(new CustomMessage("Away.ActionFailed", activeChar, new Object[0]));
			activeChar.sendPacket(new IStaticPacket[]
			{
				RestartResponse.FAIL,
				ActionFail.STATIC
			});
			return;
		}

		if (getClient() != null)
		{
			getClient().setState(GameClientState.AUTHED);
		}
		activeChar.restart();
		// send char list
		CharacterSelectionInfo cl = new CharacterSelectionInfo(getClient().getLogin(), getClient().getSessionKey().playOkID1);
		sendPacket(RestartResponse.OK, cl);
		getClient().setCharSelection(cl.getCharInfo());
	}

	// Synerge - This packet can be used while the character is blocked
	@Override
	public boolean canBeUsedWhileBlocked()
	{
		return true;
	}
}