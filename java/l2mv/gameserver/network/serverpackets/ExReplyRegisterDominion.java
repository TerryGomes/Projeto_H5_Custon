package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2mv.gameserver.model.entity.residence.Dominion;

/**
 * @author VISTALL
 */
public class ExReplyRegisterDominion extends L2GameServerPacket
{
	private int _dominionId, _clanCount, _playerCount;
	private boolean _success, _join, _asClan;

	public ExReplyRegisterDominion(Dominion dominion, boolean success, boolean join, boolean asClan)
	{
		this._success = success;
		this._join = join;
		this._asClan = asClan;
		this._dominionId = dominion.getId();

		DominionSiegeEvent siegeEvent = dominion.getSiegeEvent();

		this._playerCount = siegeEvent.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS).size();
		this._clanCount = siegeEvent.getObjects(DominionSiegeEvent.DEFENDERS).size() + 1;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x91);
		this.writeD(this._dominionId);
		this.writeD(this._asClan);
		this.writeD(this._join);
		this.writeD(this._success);
		this.writeD(this._clanCount);
		this.writeD(this._playerCount);
	}
}