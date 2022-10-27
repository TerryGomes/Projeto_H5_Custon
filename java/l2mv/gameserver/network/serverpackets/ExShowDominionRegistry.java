package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2mv.gameserver.model.entity.residence.Dominion;
import l2mv.gameserver.model.pledge.Alliance;
import l2mv.gameserver.model.pledge.Clan;

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
		this._dominionId = dominion.getId();

		Clan owner = dominion.getOwner();
		Alliance alliance = owner.getAlliance();

		DominionSiegeEvent siegeEvent = dominion.getSiegeEvent();
		this._ownerClanName = owner.getName();
		this._ownerLeaderName = owner.getLeaderName();
		this._ownerAllyName = alliance == null ? StringUtils.EMPTY : alliance.getAllyName();
		this._warTime = (int) (dominion.getSiegeDate().getTimeInMillis() / 1000L);
		this._currentTime = (int) (System.currentTimeMillis() / 1000L);
		this._mercReq = siegeEvent.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS).size();
		this._clanReq = siegeEvent.getObjects(DominionSiegeEvent.DEFENDERS).size() + 1;
		this._registeredAsPlayer = siegeEvent.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS).contains(activeChar.getObjectId());
		this._registeredAsClan = siegeEvent.getSiegeClan(DominionSiegeEvent.DEFENDERS, activeChar.getClan()) != null;

		List<Dominion> dominions = ResidenceHolder.getInstance().getResidenceList(Dominion.class);
		this._flags = new ArrayList<TerritoryFlagsInfo>(dominions.size());
		for (Dominion d : dominions)
		{
			this._flags.add(new TerritoryFlagsInfo(d.getId(), d.getFlags()));
		}
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x90);

		this.writeD(this._dominionId);
		this.writeS(this._ownerClanName);
		this.writeS(this._ownerLeaderName);
		this.writeS(this._ownerAllyName);
		this.writeD(this._clanReq); // Clan Request
		this.writeD(this._mercReq); // Merc Request
		this.writeD(this._warTime); // War Time
		this.writeD(this._currentTime); // Current Time
		this.writeD(this._registeredAsClan); // Состояние клановой кнопки: 0 - не подписал, 1 - подписан на эту территорию
		this.writeD(this._registeredAsPlayer); // Состояние персональной кнопки: 0 - не подписал, 1 - подписан на эту территорию
		this.writeD(0x01);
		this.writeD(this._flags.size()); // Territory Count
		for (TerritoryFlagsInfo cf : this._flags)
		{
			this.writeD(cf.id); // Territory Id
			this.writeD(cf.flags.length); // Emblem Count
			for (int flag : cf.flags)
			{
				this.writeD(flag); // Emblem ID - should be in for loop for emblem count
			}
		}
	}

	private class TerritoryFlagsInfo
	{
		public int id;
		public int[] flags;

		public TerritoryFlagsInfo(int id_, int[] flags_)
		{
			this.id = id_;
			this.flags = flags_;
		}
	}
}