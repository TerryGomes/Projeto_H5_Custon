package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.pledge.RankPrivs;

public class PledgePowerGradeList extends L2GameServerPacket
{
	private RankPrivs[] _privs;

	public PledgePowerGradeList(RankPrivs[] privs)
	{
		_privs = privs;
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0x3c);
		writeD(_privs.length);
		for (RankPrivs element : _privs)
		{
			writeD(element.getRank());
			writeD(element.getParty());
		}
	}
}