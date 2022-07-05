package l2f.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import l2f.gameserver.data.xml.holder.ResidenceHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2f.gameserver.model.entity.residence.Dominion;
import l2f.gameserver.model.pledge.Alliance;
import l2f.gameserver.model.pledge.Clan;

public class ExShowDominionRegistry extends L2GameServerPacket
{
	private int _dominionId;
	private String _ownerClanName;
	private String _ownerLeaderName;
	private String _ownerAllyName;
	private int _clanReq;
	private int _mercReq;
	private int _warTime;
	private int _currentTime;
	private boolean _registeredAsPlayer;
	private boolean _registeredAsClan;
	private List<TerritoryFlagsInfo> _flags = Collections.emptyList();

	public ExShowDominionRegistry(Player activeChar, Dominion dominion)
	{
		_dominionId = dominion.getId();

		Clan owner = dominion.getOwner();
		Alliance alliance = owner.getAlliance();

		DominionSiegeEvent siegeEvent = dominion.getSiegeEvent();
		_ownerClanName = owner.getName();
		_ownerLeaderName = owner.getLeaderName();
		_ownerAllyName = alliance == null ? StringUtils.EMPTY : alliance.getAllyName();
		_warTime = (int) (dominion.getSiegeDate().getTimeInMillis() / 1000L);
		_currentTime = (int) (System.currentTimeMillis() / 1000L);
		_mercReq = siegeEvent.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS).size();
		_clanReq = siegeEvent.getObjects(DominionSiegeEvent.DEFENDERS).size() + 1;
		_registeredAsPlayer = siegeEvent.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS).contains(activeChar.getObjectId());
		_registeredAsClan = siegeEvent.getSiegeClan(DominionSiegeEvent.DEFENDERS, activeChar.getClan()) != null;

		List<Dominion> dominions = ResidenceHolder.getInstance().getResidenceList(Dominion.class);
		_flags = new ArrayList<TerritoryFlagsInfo>(dominions.size());
		for (Dominion d : dominions)
		{
			_flags.add(new TerritoryFlagsInfo(d.getId(), d.getFlags()));
		}
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0x90);

		writeD(_dominionId);
		writeS(_ownerClanName);
		writeS(_ownerLeaderName);
		writeS(_ownerAllyName);
		writeD(_clanReq); // Clan Request
		writeD(_mercReq); // Merc Request
		writeD(_warTime); // War Time
		writeD(_currentTime); // Current Time
		writeD(_registeredAsClan); // Состояние клановой кнопки: 0 - не подписал, 1 - подписан на эту территорию
		writeD(_registeredAsPlayer); // Состояние персональной кнопки: 0 - не подписал, 1 - подписан на эту территорию
		writeD(0x01);
		writeD(_flags.size()); // Territory Count
		for (TerritoryFlagsInfo cf : _flags)
		{
			writeD(cf.id); // Territory Id
			writeD(cf.flags.length); // Emblem Count
			for (int flag : cf.flags)
			{
				writeD(flag); // Emblem ID - should be in for loop for emblem count
			}
		}
	}

	private class TerritoryFlagsInfo
	{
		public int id;
		public int[] flags;

		public TerritoryFlagsInfo(int id_, int[] flags_)
		{
			id = id_;
			flags = flags_;
		}
	}
}