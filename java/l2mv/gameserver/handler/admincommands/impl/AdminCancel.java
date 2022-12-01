package l2mv.gameserver.handler.admincommands.impl;

import l2mv.gameserver.handler.admincommands.IAdminCommandHandler;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Effect;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

public class AdminCancel implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_cancel,
		admin_cleanse
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (!activeChar.getPlayerAccess().CanEditChar)
		{
			return false;
		}

		switch (command)
		{
		case admin_cancel:
			handleCancel(activeChar, wordList.length > 1 ? wordList[1] : null);
			break;
		case admin_cleanse:
			Creature target = activeChar.getTarget() != null && activeChar.getTarget().isPlayable() ? (Creature) activeChar.getTarget() : activeChar;
			for (Effect e : target.getEffectList().getAllEffects())
			{
				if (e.isOffensive() && e.isCancelable())
				{
					e.exit();
				}
			}
			activeChar.sendMessage("Negative effects of " + target.getName() + " were removed!");
			break;
		}

		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void handleCancel(Player activeChar, String targetName)
	{
		GameObject obj = activeChar.getTarget();
		if (targetName != null)
		{
			Player plyr = World.getPlayer(targetName);
			if (plyr != null)
			{
				obj = plyr;
			}
			else
			{
				try
				{
					int radius = Math.max(Integer.parseInt(targetName), 100);
					for (Creature character : activeChar.getAroundCharacters(radius, 200))
					{
						character.getEffectList().stopAllEffects();
					}
					activeChar.sendMessage("Apply Cancel within " + radius + " unit radius.");
					return;
				}
				catch (NumberFormatException e)
				{
					activeChar.sendMessage("Enter valid player name or radius");
					return;
				}
			}
		}

		if (obj == null)
		{
			obj = activeChar;
		}
		if (obj.isCreature())
		{
			((Creature) obj).getEffectList().stopAllEffects();
		}
		else
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
		}
	}
}