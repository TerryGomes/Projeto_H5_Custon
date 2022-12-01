package l2mv.gameserver.network.clientpackets;

import org.apache.commons.lang3.tuple.Pair;

import l2mv.commons.lang.ArrayUtils;
import l2mv.gameserver.multverso.managers.GmEventManager;
import l2mv.gameserver.instancemanager.ReflectionManager;
import l2mv.gameserver.listener.actor.player.OnAnswerListener;
import l2mv.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.RestartType;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.entity.events.GlobalEvent;
import l2mv.gameserver.model.entity.residence.Castle;
import l2mv.gameserver.model.entity.residence.ClanHall;
import l2mv.gameserver.model.entity.residence.Fortress;
import l2mv.gameserver.model.entity.residence.ResidenceFunction;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.network.serverpackets.ActionFail;
import l2mv.gameserver.network.serverpackets.Die;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.TeleportUtils;

public class RequestRestartPoint extends L2GameClientPacket
{
	private RestartType _restartType;

	@Override
	protected void readImpl()
	{
		this._restartType = ArrayUtils.valid(RestartType.VALUES, this.readD());
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();

		if (this._restartType == null || activeChar == null)
		{
			return;
		}

		if (activeChar.isFakeDeath())
		{
			activeChar.breakFakeDeath();
			return;
		}

		if (!activeChar.isDead() && !activeChar.isGM())
		{
			activeChar.sendActionFailed();
			return;
		}

		// Prims - If the player is in a Gm Event check if it can resurrect
		if (!GmEventManager.getInstance().canResurrect(activeChar))
		{
			return;
		}

		if (activeChar.isFestivalParticipant())
		{
			activeChar.doRevive();
			return;
		}

		switch (this._restartType)
		{
		case AGATHION:
			if (activeChar.isAgathionResAvailable())
			{
				activeChar.doRevive(100);
			}
			else
			{
				activeChar.sendPacket(ActionFail.STATIC, new Die(activeChar));
			}
			break;
		case FIXED:
			if (activeChar.getPlayerAccess().ResurectFixed)
			{
				activeChar.doRevive(100);
			}
			else if ((ItemFunctions.removeItem(activeChar, 13300, 1, true, "RequestRestartPoint") == 1) || (ItemFunctions.removeItem(activeChar, 10649, 1, true, "RequestRestartPoint") == 1))
			{
				activeChar.sendPacket(SystemMsg.YOU_HAVE_USED_THE_FEATHER_OF_BLESSING_TO_RESURRECT);
				activeChar.doRevive(100);
			}
			else
			{
				activeChar.sendPacket(ActionFail.STATIC, new Die(activeChar));
			}
			break;
		default:
			Location loc = null;
			Reflection ref = activeChar.getReflection();

			if (ref == ReflectionManager.DEFAULT)
			{
				for (GlobalEvent e : activeChar.getEvents())
				{
					loc = e.getRestartLoc(activeChar, this._restartType);
				}
			}

			if (loc == null)
			{
				loc = defaultLoc(this._restartType, activeChar);
			}

			if (activeChar.isInFightClub())
			{
				activeChar.getFightClubEvent().requestRespawn(activeChar, this._restartType);
				return;
			}

			if (loc != null)
			{
				Pair<Integer, OnAnswerListener> ask = activeChar.getAskListener(false);
				if (ask != null && ask.getValue() instanceof ReviveAnswerListener && !((ReviveAnswerListener) ask.getValue()).isForPet())
				{
					activeChar.getAskListener(true);
				}

				activeChar.setPendingRevive(true);
				activeChar.teleToLocation(loc, ReflectionManager.DEFAULT);
			}
			else
			{
				activeChar.sendPacket(ActionFail.STATIC, new Die(activeChar));
			}
			break;
		}
	}

	// FIXME [VISTALL] вынести куда то?
	// телепорт к флагу, не обрабатывается, по дефалту
	public static Location defaultLoc(RestartType restartType, Player activeChar)
	{
		Location loc = null;
		Clan clan = activeChar.getClan();

		switch (restartType)
		{
		case TO_CLANHALL:
			if (clan != null && clan.getHasHideout() != 0)
			{
				ClanHall clanHall = activeChar.getClanHall();
				loc = TeleportUtils.getRestartLocation(activeChar, RestartType.TO_CLANHALL);
				if (clanHall.getFunction(ResidenceFunction.RESTORE_EXP) != null)
				{
					activeChar.restoreExp(clanHall.getFunction(ResidenceFunction.RESTORE_EXP).getLevel());
				}
			}
			break;
		case TO_CASTLE:
			if (clan != null && clan.getCastle() != 0)
			{
				Castle castle = activeChar.getCastle();
				loc = TeleportUtils.getRestartLocation(activeChar, RestartType.TO_CASTLE);
				if (castle.getFunction(ResidenceFunction.RESTORE_EXP) != null)
				{
					activeChar.restoreExp(castle.getFunction(ResidenceFunction.RESTORE_EXP).getLevel());
				}
			}
			break;
		case TO_FORTRESS:
			if (clan != null && clan.getHasFortress() != 0)
			{
				Fortress fort = activeChar.getFortress();
				loc = TeleportUtils.getRestartLocation(activeChar, RestartType.TO_FORTRESS);
				if (fort.getFunction(ResidenceFunction.RESTORE_EXP) != null)
				{
					activeChar.restoreExp(fort.getFunction(ResidenceFunction.RESTORE_EXP).getLevel());
				}
			}
			break;
		case TO_VILLAGE:
		default:
			loc = TeleportUtils.getRestartLocation(activeChar, RestartType.TO_VILLAGE);
			break;
		}
		return loc;
	}
}
