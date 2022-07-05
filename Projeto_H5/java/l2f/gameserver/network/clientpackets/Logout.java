package l2f.gameserver.network.clientpackets;

import l2f.gameserver.Config;
import l2f.gameserver.PartyMatchingBBSManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Zone;
import l2f.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.network.serverpackets.components.SystemMsg;

public class Logout extends L2GameClientPacket
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
		activeChar.setOnlineTime(0L);
		activeChar.setUptime(0L);

		// Dont allow leaving if player is fighting
		if (activeChar.isInCombat())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_EXIT_THE_GAME_WHILE_IN_COMBAT);
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.isFestivalParticipant())
		{
			if (SevenSignsFestival.getInstance().isFestivalInitialized())
			{
				activeChar.sendMessage("You cannot log out while you are a participant in a festival.");
				activeChar.sendActionFailed();
				return;
			}
		}

		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage(new CustomMessage("l2f.gameserver.clientpackets.Logout.Olympiad", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.isInFightClub())
		{
			activeChar.sendMessage("Leave Fight Club first!");
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.getVar("isPvPevents") != null)
		{
			activeChar.sendMessage("You can follow any responses did not leave while participating in the event!");
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.isInStoreMode() && !activeChar.isInBuffStore() && !activeChar.isInZone(Zone.ZoneType.offshore) && Config.SERVICES_OFFLINE_TRADE_ALLOW_OFFSHORE)
		{
			activeChar.sendMessage(new CustomMessage("trade.OfflineNoTradeZoneOnlyOffshore", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.isInObserverMode())
		{
			activeChar.sendMessage(new CustomMessage("l2f.gameserver.clientpackets.Logout.Observer", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		if (!activeChar.getPermissions().canLogOut(false, true))
		{
			activeChar.sendActionFailed();
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
			activeChar.sendActionFailed();
			return;
		}

		// Prims - Support for offline buff stores
		if (activeChar.isInBuffStore())
		{
			activeChar.offlineBuffStore();
		}
		else
		{
			activeChar.kick();
		}
	}

	// Synerge - This packet can be used while the character is blocked
	@Override
	public boolean canBeUsedWhileBlocked()
	{
		return true;
	}
}