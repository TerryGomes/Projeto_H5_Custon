package l2mv.gameserver.network.serverpackets;

import java.util.Calendar;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2mv.gameserver.model.entity.residence.Castle;
import l2mv.gameserver.model.entity.residence.ClanHall;
import l2mv.gameserver.model.entity.residence.Residence;
import l2mv.gameserver.model.pledge.Alliance;
import l2mv.gameserver.model.pledge.Clan;

/**
 * Shows the Siege Info<BR>
 * <BR>
 * packet type id 0xc9<BR>
 * format: cdddSSdSdd<BR>
 * <BR>
 * c = c9<BR>
 * d = UnitID<BR>
 * d = Show Owner Controls (0x00 default || >=0x02(mask?) owner)<BR>
 * d = Owner ClanID<BR>
 * S = Owner ClanName<BR>
 * S = Owner Clan LeaderName<BR>
 * d = Owner AllyID<BR>
 * S = Owner AllyName<BR>
 * d = current time (seconds)<BR>
 * d = Siege time (seconds) (0 for selectable)<BR>
 * d = Size of Siege Time Select Related
 *   d - next siege time
 *
 * @reworked VISTALL
 */
public class CastleSiegeInfo extends L2GameServerPacket
{
	private long _startTime;
	private int _id, _ownerObjectId, _allyId;
	private boolean _isLeader;
	private String _ownerName = "No owner";
	private String _leaderName = StringUtils.EMPTY;
	private String _allyName = StringUtils.EMPTY;
	private int[] _nextTimeMillis = ArrayUtils.EMPTY_INT_ARRAY;

	public CastleSiegeInfo(Castle castle, Player player)
	{
		this((Residence) castle, player);

		CastleSiegeEvent siegeEvent = castle.getSiegeEvent();
		long siegeTimeMillis = castle.getSiegeDate().getTimeInMillis();
		if (siegeTimeMillis == 0)
		{
			this._nextTimeMillis = siegeEvent.getNextSiegeTimes();
		}
		else
		{
			this._startTime = (int) (siegeTimeMillis / 1000);
		}
	}

	public CastleSiegeInfo(ClanHall ch, Player player)
	{
		this((Residence) ch, player);

		this._startTime = (int) (ch.getSiegeDate().getTimeInMillis() / 1000);
	}

	protected CastleSiegeInfo(Residence residence, Player player)
	{
		this._id = residence.getId();
		this._ownerObjectId = residence.getOwnerId();
		Clan owner = residence.getOwner();
		if (owner != null)
		{
			this._isLeader = player.isGM() || owner.getLeaderId(Clan.SUBUNIT_MAIN_CLAN) == player.getObjectId();
			this._ownerName = owner.getName();
			this._leaderName = owner.getLeaderName(Clan.SUBUNIT_MAIN_CLAN);
			Alliance ally = owner.getAlliance();
			if (ally != null)
			{
				this._allyId = ally.getAllyId();
				this._allyName = ally.getAllyName();
			}
		}
	}

	@Override
	protected void writeImpl()
	{
		this.writeC(0xC9);
		this.writeD(this._id);
		this.writeD(this._isLeader ? 0x01 : 0x00);
		this.writeD(this._ownerObjectId);
		this.writeS(this._ownerName); // Clan Name
		this.writeS(this._leaderName); // Clan Leader Name
		this.writeD(this._allyId); // Ally ID
		this.writeS(this._allyName); // Ally Name
		this.writeD((int) (Calendar.getInstance().getTimeInMillis() / 1000));
		this.writeD((int) this._startTime);
		if (this._startTime == 0) // If zero is the cycle
		{
			this.writeDD(this._nextTimeMillis, true);
		}
	}
}