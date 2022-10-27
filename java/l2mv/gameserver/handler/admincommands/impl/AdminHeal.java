package l2mv.gameserver.handler.admincommands.impl;

import l2mv.gameserver.handler.admincommands.IAdminCommandHandler;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

public class AdminHeal implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_heal, admin_healclan
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (!activeChar.getPlayerAccess().Heal)
		{
			return false;
		}

		switch (command)
		{
		case admin_heal:
			if (wordList.length == 1)
			{
				handleHeal(activeChar, null);
			}
			else
			{
				handleHeal(activeChar, wordList[1]);
			}
			break;
		case admin_healclan:
			handleClanHeal(activeChar);
			break;
		}

		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void handleHeal(Player activeChar, String player)
	{
		GameObject obj = activeChar.getTarget();
		if (player != null)
		{
			Player plyr = World.getPlayer(player);

			if (plyr != null)
			{
				obj = plyr;
			}
			else
			{
				int radius = Math.max(Integer.parseInt(player), 100);
				for (Creature character : activeChar.getAroundCharacters(radius, 200))
				{
					character.setCurrentHpMp(character.getMaxHp(), character.getMaxMp());
					if (character.isPlayer())
					{
						character.setCurrentCp(character.getMaxCp());
					}
				}
				activeChar.sendMessage("Healed within " + radius + " unit radius.");
				return;
			}
		}

		if (obj == null)
		{
			obj = activeChar;
		}

		if (obj instanceof Creature)
		{
			Creature target = (Creature) obj;
			target.setCurrentHpMp(target.getMaxHp(), target.getMaxMp());
			if (target.isPlayer())
			{
				target.setCurrentCp(target.getMaxCp());
			}
		}
		else
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
		}
	}

	private void handleClanHeal(Player activeChar)
	{
		final GameObject obj = activeChar.getTarget();
		if (!(obj instanceof Player))
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		final Player target = (Player) obj;
		final Clan clan = target.getClan();
		if (clan == null)
		{
			activeChar.sendMessage("The target doesnt have any clan");
			return;
		}

		for (Player member : clan.getOnlineMembers(true))
		{
			if (member == null)
			{
				return;
			}

			member.setCurrentHpMp(member.getMaxHp(), member.getMaxMp());
			member.setCurrentCp(member.getMaxCp());
		}
		activeChar.sendMessage("Healed all members of the clan " + clan.getName());
	}
}