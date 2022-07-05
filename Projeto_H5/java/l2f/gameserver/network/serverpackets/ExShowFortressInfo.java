package l2f.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import l2f.gameserver.data.xml.holder.ResidenceHolder;
import l2f.gameserver.model.entity.residence.Fortress;
import l2f.gameserver.model.pledge.Clan;

public class ExShowFortressInfo extends L2GameServerPacket
{
	private List<FortressInfo> _infos = Collections.emptyList();

	public ExShowFortressInfo()
	{
		List<Fortress> forts = ResidenceHolder.getInstance().getResidenceList(Fortress.class);
		_infos = new ArrayList<FortressInfo>(forts.size());
		for (Fortress fortress : forts)
		{
			Clan owner = fortress.getOwner();
			_infos.add(new FortressInfo(owner == null ? StringUtils.EMPTY : owner.getName(), fortress.getId(), fortress.getSiegeEvent().isInProgress(), owner == null ? 0 : (int) ((System.currentTimeMillis() - fortress.getOwnDate().getTimeInMillis()) / 1000L)));
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0x15);
		writeD(_infos.size());
		for (FortressInfo _info : _infos)
		{
			writeD(_info._id);
			writeS(_info._owner);
			writeD(_info._status);
			writeD(_info._siege);
		}
	}

	static class FortressInfo
	{
		public int _id, _siege;
		public String _owner;
		public boolean _status;

		public FortressInfo(String owner, int id, boolean status, int siege)
		{
			_owner = owner;
			_id = id;
			_status = status;
			_siege = siege;
		}
	}
}