package l2f.gameserver.handler.voicecommands.impl;

import l2f.gameserver.Config;
import l2f.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2f.gameserver.instancemanager.AwayManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Zone;
import l2f.gameserver.model.entity.events.impl.SiegeEvent;
import l2f.gameserver.network.serverpackets.components.CustomMessage;

public class Away implements IVoicedCommandHandler
{
	private String[] VOICED_COMMANDS =
	{
		"away",
		"back"
	};

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String text)
	{
		if ((Config.AWAY_ONLY_FOR_PREMIUM) && (!activeChar.hasBonus()))
		{
			activeChar.sendMessage(new CustomMessage("PremiumOnly", activeChar, new Object[0]));
			return false;
		}

		if (command.startsWith("away"))
		{
			return away(activeChar, text);
		}

		if (command.startsWith("back"))
		{
			return back(activeChar);
		}

		return false;
	}

	private boolean away(Player activeChar, String text)
	{
		SiegeEvent<?, ?> siege = activeChar.getEvent(SiegeEvent.class);

		if (activeChar.isInAwayingMode())
		{
			activeChar.sendMessage(new CustomMessage("l2f.gameserver.handler.voicecommands.impl.Away.Already", activeChar, new Object[0]));
			return false;
		}

		if ((!activeChar.isInZone(Zone.ZoneType.peace_zone)) && (Config.AWAY_PEACE_ZONE))
		{
			activeChar.sendMessage(new CustomMessage("l2f.gameserver.handler.voicecommands.impl.Away.PieceOnly", activeChar, new Object[0]));
			return false;
		}

		if ((activeChar.isMovementDisabled()) || (activeChar.isAlikeDead()))
		{
			return false;
		}

		if (siege != null)
		{
			activeChar.sendMessage(new CustomMessage("l2f.gameserver.handler.voicecommands.impl.Away.Siege", activeChar, new Object[0]));
			return false;
		}

		if (activeChar.isCursedWeaponEquipped())
		{
			activeChar.sendMessage(new CustomMessage("l2f.gameserver.handler.voicecommands.impl.Away.Cursed", activeChar, new Object[0]));
			return false;
		}

		if (activeChar.isInDuel())
		{
			activeChar.sendMessage(new CustomMessage("l2f.gameserver.handler.voicecommands.impl.Away.Duel", activeChar, new Object[0]));
			return false;
		}

		if ((activeChar.isInParty()) && (activeChar.getParty().isInDimensionalRift()))
		{
			activeChar.sendMessage(new CustomMessage("l2f.gameserver.handler.voicecommands.impl.Away.Rift", activeChar, new Object[0]));
			return false;
		}

		if ((activeChar.isInOlympiadMode()) || (activeChar.getOlympiadGame() != null))
		{
			activeChar.sendMessage(new CustomMessage("l2f.gameserver.handler.voicecommands.impl.Away.Olympiad", activeChar, new Object[0]));
			return false;
		}

		if (activeChar.isInObserverMode())
		{
			activeChar.sendMessage(new CustomMessage("l2f.gameserver.handler.voicecommands.impl.Away.Observer", activeChar, new Object[0]));
			return false;
		}

		if ((activeChar.getKarma() > 0) || (activeChar.getPvpFlag() > 0))
		{
			activeChar.sendMessage(new CustomMessage("l2f.gameserver.handler.voicecommands.impl.Away.Pvp", activeChar, new Object[0]));
			return false;
		}

		if (text == null)
		{
			text = "";
		}

		if (text.length() > 10)
		{
			activeChar.sendMessage(new CustomMessage("l2f.gameserver.handler.voicecommands.impl.Away.Text", activeChar, new Object[0]));
			return false;
		}

		if (activeChar.getTarget() == null)
		{
			AwayManager.getInstance().setAway(activeChar, text);
		}
		else
		{
			activeChar.sendMessage(new CustomMessage("l2f.gameserver.handler.voicecommands.impl.Away.Target", activeChar, new Object[0]));
			return false;
		}

		return true;
	}

	private boolean back(Player activeChar)
	{
		if (!activeChar.isInAwayingMode())
		{
			activeChar.sendMessage(new CustomMessage("l2f.gameserver.handler.voicecommands.impl.Away.Not", activeChar, new Object[0]));
			return false;
		}
		AwayManager.getInstance().setBack(activeChar);
		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}