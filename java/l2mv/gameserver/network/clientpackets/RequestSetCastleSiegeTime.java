package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2mv.gameserver.model.entity.residence.Castle;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.network.serverpackets.CastleSiegeInfo;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

public class RequestSetCastleSiegeTime extends L2GameClientPacket
{
	private int _id, _time;

	@Override
	protected void readImpl()
	{
		this._id = this.readD();
		this._time = this.readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, this._id);
		if ((castle == null) || (player.getClan().getCastle() != castle.getId()))
		{
			return;
		}

		if ((player.getClanPrivileges() & Clan.CP_CS_MANAGE_SIEGE) != Clan.CP_CS_MANAGE_SIEGE)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_SIEGE_TIME);
			return;
		}

		CastleSiegeEvent siegeEvent = castle.getSiegeEvent();

		siegeEvent.setNextSiegeTime(this._time);

		player.sendPacket(new CastleSiegeInfo(castle, player));
	}
}