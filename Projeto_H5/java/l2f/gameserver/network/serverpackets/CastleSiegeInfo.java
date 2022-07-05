package l2f.gameserver.network.serverpackets;

import java.util.Calendar;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2f.gameserver.model.entity.residence.Castle;
import l2f.gameserver.model.entity.residence.ClanHall;
import l2f.gameserver.model.entity.residence.Residence;
import l2f.gameserver.model.pledge.Alliance;
import l2f.gameserver.model.pledge.Clan;

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
			_nextTimeMillis = siegeEvent.getNextSiegeTimes();
		}
		else
		{
			_startTime = (int) (siegeTimeMillis / 1000);
		}
	}

	public CastleSiegeInfo(ClanHall ch, Player player)
	{
		this((Residence) ch, player);

		_startTime = (int) (ch.getSiegeDate().getTimeInMillis() / 1000);
	}

	protected CastleSiegeInfo(Residence residence, Player player)
	{
		_id = residence.getId();
		_ownerObjectId = residence.getOwnerId();
		Clan owner = residence.getOwner();
		if (owner != null)
		{
			_isLeader = player.isGM() || owner.getLeaderId(Clan.SUBUNIT_MAIN_CLAN) == player.getObjectId();
			_ownerName = owner.getName();
			_leaderName = owner.getLeaderName(Clan.SUBUNIT_MAIN_CLAN);
			Alliance ally = owner.getAlliance();
			if (ally != null)
			{
				_allyId = ally.getAllyId();
				_allyName = ally.getAllyName();
			}
		}
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xC9);
		writeD(_id);
		writeD(_isLeader ? 0x01 : 0x00);
		writeD(_ownerObjectId);
		writeS(_ownerName); // Clan Name
		writeS(_leaderName); // Clan Leader Name
		writeD(_allyId); // Ally ID
		writeS(_allyName); // Ally Name
		writeD((int) (Calendar.getInstance().getTimeInMillis() / 1000));
		writeD((int) _startTime);
		if (_startTime == 0) // If zero is the cycle
		{
			writeDD(_nextTimeMillis, true);
		}
	}
}