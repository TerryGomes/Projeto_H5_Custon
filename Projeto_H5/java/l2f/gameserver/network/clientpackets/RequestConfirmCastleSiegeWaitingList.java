package l2f.gameserver.network.clientpackets;

import l2f.gameserver.dao.SiegeClanDAO;
import l2f.gameserver.data.xml.holder.ResidenceHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2f.gameserver.model.entity.events.objects.SiegeClanObject;
import l2f.gameserver.model.entity.residence.Castle;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.network.serverpackets.CastleSiegeDefenderList;
import l2f.gameserver.network.serverpackets.components.SystemMsg;

/**
 * @reworked VISTALL
 */
public class RequestConfirmCastleSiegeWaitingList extends L2GameClientPacket
{
	private boolean _approved;
	private int _unitId;
	private int _clanId;

	@Override
	protected void readImpl()
	{
		_unitId = readD();
		_clanId = readD();
		_approved = readD() == 1;
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if ((player == null) || (player.getClan() == null))
		{
			return;
		}

		Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, _unitId);

		if (castle == null || player.getClan().getCastle() != castle.getId())
		{
			player.sendActionFailed();
			return;
		}

		CastleSiegeEvent siegeEvent = castle.getSiegeEvent();

		SiegeClanObject siegeClan = siegeEvent.getSiegeClan(CastleSiegeEvent.DEFENDERS_WAITING, _clanId);
		if (siegeClan == null)
		{
			siegeClan = siegeEvent.getSiegeClan(CastleSiegeEvent.DEFENDERS, _clanId);
		}

		if (siegeClan == null)
		{
			return;
		}

		if ((player.getClanPrivileges() & Clan.CP_CS_MANAGE_SIEGE) != Clan.CP_CS_MANAGE_SIEGE)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_CASTLE_DEFENDER_LIST);
			return;
		}

		if (siegeEvent.isRegistrationOver())
		{
			player.sendPacket(SystemMsg.THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATIONS_CANNOT_BE_ACCEPTED_OR_REJECTED);
			return;
		}

		int allSize = siegeEvent.getObjects(CastleSiegeEvent.DEFENDERS).size();
		if (allSize >= CastleSiegeEvent.MAX_SIEGE_CLANS)
		{
			player.sendPacket(SystemMsg.NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_DEFENDER_SIDE);
			return;
		}

		siegeEvent.removeObject(siegeClan.getType(), siegeClan);

		if (_approved)
		{
			siegeClan.setType(CastleSiegeEvent.DEFENDERS);
		}
		else
		{
			siegeClan.setType(CastleSiegeEvent.DEFENDERS_REFUSED);
		}

		siegeEvent.addObject(siegeClan.getType(), siegeClan);

		SiegeClanDAO.getInstance().update(castle, siegeClan);

		player.sendPacket(new CastleSiegeDefenderList(castle));
	}
}