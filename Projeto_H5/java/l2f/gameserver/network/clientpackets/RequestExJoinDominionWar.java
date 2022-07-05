package l2f.gameserver.network.clientpackets;

import l2f.gameserver.dao.SiegeClanDAO;
import l2f.gameserver.dao.SiegePlayerDAO;
import l2f.gameserver.data.xml.holder.ResidenceHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2f.gameserver.model.entity.events.objects.SiegeClanObject;
import l2f.gameserver.model.entity.residence.Dominion;
import l2f.gameserver.model.pledge.Privilege;
import l2f.gameserver.network.serverpackets.ExReplyRegisterDominion;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;

/**
 * @author VISTALL
 */
public class RequestExJoinDominionWar extends L2GameClientPacket
{
	private int _dominionId;
	private boolean _clanRegistration;
	private boolean _isRegistration;

	@Override
	protected void readImpl()
	{
		_dominionId = readD();
		_clanRegistration = readD() == 1;
		_isRegistration = readD() == 1;
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		Dominion dominion = ResidenceHolder.getInstance().getResidence(Dominion.class, _dominionId);
		if (dominion == null)
		{
			return;
		}
		DominionSiegeEvent siegeEvent = dominion.getSiegeEvent();
		if (siegeEvent.isRegistrationOver())
		{
			player.sendPacket(SystemMsg.IT_IS_NOT_A_TERRITORY_WAR_REGISTRATION_PERIOD_SO_A_REQUEST_CANNOT_BE_MADE_AT_THIS_TIME);
			return;
		}

		if ((player.getClan() != null) && (player.getClan().getCastle() > 0))
		{
			player.sendPacket(SystemMsg.THE_CLAN_WHO_OWNS_THE_TERRITORY_CANNOT_PARTICIPATE_IN_THE_TERRITORY_WAR_AS_MERCENARIES);
			return;
		}

		if ((player.getLevel() < 40) || (player.getClassId().getLevel() <= 2))
		{
			player.sendPacket(SystemMsg.ONLY_CHARACTERS_WHO_ARE_LEVEL_40_OR_ABOVE_WHO_HAVE_COMPLETED_THEIR_SECOND_CLASS_TRANSFER_CAN_REGISTER_IN_A_TERRITORY_WAR);
			return;
		}

		int playerReg = 0;
		int clanReg = 0;
		for (Dominion d : ResidenceHolder.getInstance().getResidenceList(Dominion.class))
		{
			DominionSiegeEvent dominionSiegeEvent = d.getSiegeEvent();
			if (dominionSiegeEvent.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS).contains(player.getObjectId()))
			{
				playerReg = d.getId();
			}
			else if (dominionSiegeEvent.getSiegeClan(DominionSiegeEvent.DEFENDERS, player.getClan()) != null)
			{
				clanReg = d.getId();
			}
		}

		if (_isRegistration)
		{
			// check whether you can include - from the first 6 months of the character...
			// If Regan as a mercenary on clan / single regatta in stock
			if ((clanReg > 0) || (!_clanRegistration && ((clanReg > 0) || (playerReg > 0))))
			{
				player.sendPacket(SystemMsg.YOUVE_ALREADY_REQUESTED_A_TERRITORY_WAR_IN_ANOTHER_TERRITORY_ELSEWHERE);
				return;
			}

			if (_clanRegistration)
			{
				if (!player.hasPrivilege(Privilege.CS_FS_SIEGE_WAR))
				{
					player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
					return;
				}
				if (player.getClan().isPlacedForDisband())
				{
					player.sendPacket(SystemMsg.YOU_HAVE_ALREADY_REQUESTED_THE_DISSOLUTION_OF_YOUR_CLAN);
					return;
				}

				SiegeClanObject object = new SiegeClanObject(DominionSiegeEvent.DEFENDERS, player.getClan(), 0);
				siegeEvent.addObject(DominionSiegeEvent.DEFENDERS, object);
				SiegeClanDAO.getInstance().insert(dominion, object);

				player.sendPacket(new SystemMessage2(SystemMsg.CLAN_PARTICIPATION_IS_REQUESTED_IN_S1_TERRITORY).addResidenceName(dominion));
			}
			else
			{
				siegeEvent.addObject(DominionSiegeEvent.DEFENDER_PLAYERS, player.getObjectId());
				SiegePlayerDAO.insert(dominion, 0, player.getObjectId());

				player.sendPacket(new SystemMessage2(SystemMsg.MERCENARY_PARTICIPATION_IS_REQUESTED_IN_S1_TERRITORY).addResidenceName(dominion));
			}
		}
		else
		{
			if ((_clanRegistration && (clanReg != dominion.getId())) || (!_clanRegistration && (playerReg != dominion.getId())))
			{
				return;
			}

			if (_clanRegistration)
			{
				if (!player.hasPrivilege(Privilege.CS_FS_SIEGE_WAR))
				{
					player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
					return;
				}

				SiegeClanObject clanObject = siegeEvent.getSiegeClan(DominionSiegeEvent.DEFENDERS, player.getClan());
				siegeEvent.removeObject(DominionSiegeEvent.DEFENDERS, clanObject);
				SiegeClanDAO.getInstance().delete(dominion, clanObject);

				player.sendPacket(new SystemMessage2(SystemMsg.CLAN_PARTICIPATION_REQUEST_IS_CANCELLED_IN_S1_TERRITORY).addResidenceName(dominion));
			}
			else
			{
				siegeEvent.removeObject(DominionSiegeEvent.DEFENDER_PLAYERS, player.getObjectId());
				SiegePlayerDAO.delete(dominion, 0, player.getObjectId());

				player.sendPacket(new SystemMessage2(SystemMsg.MERCENARY_PARTICIPATION_REQUEST_IS_CANCELLED_IN_S1_TERRITORY).addResidenceName(dominion));
			}
		}

		player.sendPacket(new ExReplyRegisterDominion(dominion, true, _isRegistration, _clanRegistration));
	}
}