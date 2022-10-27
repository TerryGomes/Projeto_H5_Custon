package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import l2mv.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2mv.gameserver.model.entity.events.impl.SiegeEvent;
import l2mv.gameserver.model.entity.events.objects.SiegeClanObject;
import l2mv.gameserver.model.entity.residence.Castle;
import l2mv.gameserver.model.pledge.Alliance;
import l2mv.gameserver.model.pledge.Clan;

/**
 * Populates the Siege Defender List in the SiegeInfo Window<BR>
 * <BR>
 * packet type id 0xcb<BR>
 * format: cddddddd + dSSdddSSd<BR>
 * <BR>
 * c = 0xcb<BR>
 * d = unitId<BR>
 * d = unknow (0x00)<BR>
 * d = активация регистрации (0x01)<BR>
 * d = unknow (0x00)<BR>
 * d = Number of Defending Clans?<BR>
 * d = Number of Defending Clans<BR>
 * { //repeats<BR>
 * d = ClanID<BR>
 * S = ClanName<BR>
 * S = ClanLeaderName<BR>
 * d = ClanCrestID<BR>
 * d = signed time (seconds)<BR>
 * d = Type -> Owner = 0x01 || Waiting = 0x02 || Accepted = 0x03 || Refuse = 0x04<BR>
 * d = AllyID<BR>
 * S = AllyName<BR>
 * S = AllyLeaderName<BR>
 * d = AllyCrestID<BR>
 *
 * @reworked VISTALL
 */
public class CastleSiegeDefenderList extends L2GameServerPacket
{
	public static int OWNER = 1;
	public static int WAITING = 2;
	public static int ACCEPTED = 3;
	public static int REFUSE = 4;

	private int _id, _registrationValid;
	private List<DefenderClan> _defenderClans = Collections.emptyList();

	public CastleSiegeDefenderList(Castle castle)
	{
		this._id = castle.getId();
		this._registrationValid = !castle.getSiegeEvent().isRegistrationOver() && castle.getOwner() != null ? 1 : 0;

		List<SiegeClanObject> defenders = castle.getSiegeEvent().getObjects(SiegeEvent.DEFENDERS);
		List<SiegeClanObject> defendersWaiting = castle.getSiegeEvent().getObjects(CastleSiegeEvent.DEFENDERS_WAITING);
		List<SiegeClanObject> defendersRefused = castle.getSiegeEvent().getObjects(CastleSiegeEvent.DEFENDERS_REFUSED);
		this._defenderClans = new ArrayList<DefenderClan>(defenders.size() + defendersWaiting.size() + defendersRefused.size());
		if (castle.getOwner() != null)
		{
			this._defenderClans.add(new DefenderClan(castle.getOwner(), OWNER, 0));
		}
		for (SiegeClanObject siegeClan : defenders)
		{
			this._defenderClans.add(new DefenderClan(siegeClan.getClan(), ACCEPTED, (int) (siegeClan.getDate() / 1000L)));
		}
		for (SiegeClanObject siegeClan : defendersWaiting)
		{
			this._defenderClans.add(new DefenderClan(siegeClan.getClan(), WAITING, (int) (siegeClan.getDate() / 1000L)));
		}
		for (SiegeClanObject siegeClan : defendersRefused)
		{
			this._defenderClans.add(new DefenderClan(siegeClan.getClan(), REFUSE, (int) (siegeClan.getDate() / 1000L)));
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xCB);
		this.writeD(this._id);
		this.writeD(0x00);
		this.writeD(this._registrationValid);
		this.writeD(0x00);

		this.writeD(this._defenderClans.size());
		this.writeD(this._defenderClans.size());
		for (DefenderClan defenderClan : this._defenderClans)
		{
			Clan clan = defenderClan._clan;

			this.writeD(clan.getClanId());
			this.writeS(clan.getName());
			this.writeS(clan.getLeaderName());
			this.writeD(clan.getCrestId());
			this.writeD(defenderClan._time);
			this.writeD(defenderClan._type);
			this.writeD(clan.getAllyId());
			Alliance alliance = clan.getAlliance();
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
				this.writeD(0x00);
			}
		}
	}

	private static class DefenderClan
	{
		private Clan _clan;
		private int _type;
		private int _time;

		public DefenderClan(Clan clan, int type, int time)
		{
			this._clan = clan;
			this._type = type;
			this._time = time;
		}
	}
}