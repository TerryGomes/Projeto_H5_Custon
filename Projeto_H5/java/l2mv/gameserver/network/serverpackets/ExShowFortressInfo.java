package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.model.entity.residence.Fortress;
import l2mv.gameserver.model.pledge.Clan;

public class ExShowFortressInfo extends L2GameServerPacket
{
	private List<FortressInfo> _infos = Collections.emptyList();

	public ExShowFortressInfo()
	{
		List<Fortress> forts = ResidenceHolder.getInstance().getResidenceList(Fortress.class);
		this._infos = new ArrayList<FortressInfo>(forts.size());
		for (Fortress fortress : forts)
		{
			Clan owner = fortress.getOwner();
			this._infos.add(new FortressInfo(owner == null ? StringUtils.EMPTY : owner.getName(), fortress.getId(), fortress.getSiegeEvent().isInProgress(), owner == null ? 0 : (int) ((System.currentTimeMillis() - fortress.getOwnDate().getTimeInMillis()) / 1000L)));
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x15);
		this.writeD(this._infos.size());
		for (FortressInfo _info : this._infos)
		{
			this.writeD(_info._id);
			this.writeS(_info._owner);
			this.writeD(_info._status);
			this.writeD(_info._siege);
		}
	}

	static class FortressInfo
	{
		public int _id, _siege;
		public String _owner;
		public boolean _status;

		public FortressInfo(String owner, int id, boolean status, int siege)
		{
			this._owner = owner;
			this._id = id;
			this._status = status;
			this._siege = siege;
		}
	}
}