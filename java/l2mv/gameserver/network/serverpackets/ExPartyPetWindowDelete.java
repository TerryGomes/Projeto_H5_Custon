package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Summon;

public class ExPartyPetWindowDelete extends L2GameServerPacket
{
	private int _summonObjectId;
	private int _ownerObjectId;
	private String _summonName;

	public ExPartyPetWindowDelete(Summon summon)
	{
		this._summonObjectId = summon.getObjectId();
		this._summonName = summon.getName();
		this._ownerObjectId = summon.getPlayer().getObjectId();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x6a);
		this.writeD(this._summonObjectId);
		this.writeD(this._ownerObjectId);
		this.writeS(this._summonName);
	}
}