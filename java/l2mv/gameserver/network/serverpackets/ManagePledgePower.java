package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.pledge.RankPrivs;

public class ManagePledgePower extends L2GameServerPacket
{
	private int _action, _clanId, privs;

	public ManagePledgePower(Player player, int action, int rank)
	{
		this._clanId = player.getClanId();
		this._action = action;
		RankPrivs temp = player.getClan().getRankPrivs(rank);
		this.privs = temp == null ? 0 : temp.getPrivs();
		player.sendPacket(new PledgeReceiveUpdatePower(this.privs));
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x2a);
		this.writeD(this._clanId);
		this.writeD(this._action);
		this.writeD(this.privs);
	}
}