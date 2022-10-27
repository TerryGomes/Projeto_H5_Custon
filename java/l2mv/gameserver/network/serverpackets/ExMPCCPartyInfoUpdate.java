package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Party;
import l2mv.gameserver.model.Player;

/**
 * ch Sddd
 */
public class ExMPCCPartyInfoUpdate extends L2GameServerPacket
{
	private Party _party;
	Player _leader;
	private int _mode, _count;

	/**
	 * @param party
	 * @param mode 0 = Remove, 1 = Add
	 */
	public ExMPCCPartyInfoUpdate(Party party, int mode)
	{
		this._party = party;
		this._mode = mode;
		this._count = this._party.size();
		this._leader = this._party.getLeader();
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x5b);
		this.writeS(this._leader.getName());
		this.writeD(this._leader.getObjectId());
		this.writeD(this._count);
		this.writeD(this._mode); // mode 0 = Remove Party, 1 = AddParty, maybe more...
	}
}