package l2f.gameserver.network.serverpackets;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import l2f.gameserver.model.entity.events.impl.SiegeEvent;
import l2f.gameserver.model.entity.events.objects.SiegeClanObject;
import l2f.gameserver.model.entity.residence.Residence;
import l2f.gameserver.model.pledge.Alliance;
import l2f.gameserver.model.pledge.Clan;

/**
 * Populates the Siege Attacker List in the SiegeInfo Window<BR>
 * <BR>
 * packet type id 0xca<BR>
 * format: cddddddd + dSSdddSSd<BR>
 * <BR>
 * c = ca<BR>
 * d = CastleID<BR>
 * d = unknow (0x00)<BR>
 * d = registration valid (0x01)<BR>
 * d = unknow (0x00)<BR>
 * d = Number of Attackers Clans?<BR>
 * d = Number of Attackers Clans<BR>
 * { //repeats<BR>
 * d = ClanID<BR>
 * S = ClanName<BR>
 * S = ClanLeaderName<BR>
 * d = ClanCrestID<BR>
 * d = signed time (seconds)<BR>
 * d = AllyID<BR>
 * S = AllyName<BR>
 * S = AllyLeaderName<BR>
 * d = AllyCrestID<BR>
 *
 * @reworked VISTALL
 */
public class CastleSiegeAttackerList extends L2GameServerPacket
{
	private int _id, _registrationValid;
	private List<SiegeClanObject> _clans = Collections.emptyList();

	public CastleSiegeAttackerList(Residence residence)
	{
		_id = residence.getId();
		_registrationValid = !residence.getSiegeEvent().isRegistrationOver() ? 1 : 0;
		_clans = residence.getSiegeEvent().getObjects(SiegeEvent.ATTACKERS);
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xCA);

		writeD(_id);

		writeD(0x00);
		writeD(_registrationValid);
		writeD(0x00);

		writeD(_clans.size());
		writeD(_clans.size());

		for (SiegeClanObject siegeClan : _clans)
		{
			Clan clan = siegeClan.getClan();

			writeD(clan.getClanId());
			writeS(clan.getName());
			writeS(clan.getLeaderName());
			writeD(clan.getCrestId());
			writeD((int) (siegeClan.getDate() / 1000L));

			Alliance alliance = clan.getAlliance();
			writeD(clan.getAllyId());
			if (alliance != null)
			{
				writeS(alliance.getAllyName());
				writeS(alliance.getAllyLeaderName());
				writeD(alliance.getAllyCrestId());
			}
			else
			{
				writeS(StringUtils.EMPTY);
				writeS(StringUtils.EMPTY);
				writeD(0);
			}
		}
	}
}