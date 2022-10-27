package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.pledge.RankPrivs;

public class PledgePowerGradeList extends L2GameServerPacket
{
	private RankPrivs[] _privs;

	public PledgePowerGradeList(RankPrivs[] privs)
	{
		this._privs = privs;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x3c);
		this.writeD(this._privs.length);
		for (RankPrivs element : this._privs)
		{
			this.writeD(element.getRank());
			this.writeD(element.getParty());
		}
	}
}