package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.pledge.SubUnit;

public class PledgeReceiveSubPledgeCreated extends L2GameServerPacket
{
	private int type;
	private String _name, leader_name;

	public PledgeReceiveSubPledgeCreated(SubUnit subPledge)
	{
		this.type = subPledge.getType();
		this._name = subPledge.getName();
		this.leader_name = subPledge.getLeaderName();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x40);

		this.writeD(0x01);
		this.writeD(this.type);
		this.writeS(this._name);
		this.writeS(this.leader_name);
	}
}