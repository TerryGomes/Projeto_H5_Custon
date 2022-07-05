package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.pledge.SubUnit;

public class PledgeReceiveSubPledgeCreated extends L2GameServerPacket
{
	private int type;
	private String _name, leader_name;

	public PledgeReceiveSubPledgeCreated(SubUnit subPledge)
	{
		type = subPledge.getType();
		_name = subPledge.getName();
		leader_name = subPledge.getLeaderName();
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0x40);

		writeD(0x01);
		writeD(type);
		writeS(_name);
		writeS(leader_name);
	}
}