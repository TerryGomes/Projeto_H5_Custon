package l2mv.gameserver.network.serverpackets;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import l2mv.gameserver.model.entity.events.impl.SiegeEvent;
import l2mv.gameserver.model.entity.events.objects.SiegeClanObject;
import l2mv.gameserver.model.entity.residence.Residence;
import l2mv.gameserver.model.pledge.Alliance;
import l2mv.gameserver.model.pledge.Clan;

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
		this._id = residence.getId();
		this._registrationValid = !residence.getSiegeEvent().isRegistrationOver() ? 1 : 0;
		this._clans = residence.getSiegeEvent().getObjects(SiegeEvent.ATTACKERS);
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xCA);

		this.writeD(this._id);

		this.writeD(0x00);
		this.writeD(this._registrationValid);
		this.writeD(0x00);

		this.writeD(this._clans.size());
		this.writeD(this._clans.size());

		for (SiegeClanObject siegeClan : this._clans)
		{
			Clan clan = siegeClan.getClan();

			this.writeD(clan.getClanId());
			this.writeS(clan.getName());
			this.writeS(clan.getLeaderName());
			this.writeD(clan.getCrestId());
			this.writeD((int) (siegeClan.getDate() / 1000L));

			Alliance alliance = clan.getAlliance();
			this.writeD(clan.getAllyId());
			if (alliance != null)
			{
				this.writeS(alliance.getAllyName());
				this.writeS(alliance.getAllyLeaderName());
				this.writeD(alliance.getAllyCrestId());
			}
			else
			{
				this.writeS(StringUtils.EMPTY);
				this.writeS(StringUtils.EMPTY);
				this.writeD(0);
			}
		}
	}
}