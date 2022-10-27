package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.model.entity.events.objects.TerritoryWardObject;
import l2mv.gameserver.model.entity.residence.Dominion;
import l2mv.gameserver.utils.Location;

/**
 * @author VISTALL
 */
public class ExShowOwnthingPos extends L2GameServerPacket
{
	private List<WardInfo> _wardList = new ArrayList<WardInfo>(9);

	public ExShowOwnthingPos()
	{
		for (Dominion dominion : ResidenceHolder.getInstance().getResidenceList(Dominion.class))
		{
			if (dominion.getSiegeDate().getTimeInMillis() == 0)
			{
				continue;
			}

			int[] flags = dominion.getFlags();
			for (int dominionId : flags)
			{
				TerritoryWardObject wardObject = dominion.getSiegeEvent().getFirstObject("ward_" + dominionId);
				Location loc = wardObject.getWardLocation();
				if (loc != null)
				{
					this._wardList.add(new WardInfo(dominionId, loc.x, loc.y, loc.z));
				}
			}
		}
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x93);
		this.writeD(this._wardList.size());
		for (WardInfo wardInfo : this._wardList)
		{
			this.writeD(wardInfo.dominionId);
			this.writeD(wardInfo._x);
			this.writeD(wardInfo._y);
			this.writeD(wardInfo._z);
		}
	}

	private static class WardInfo
	{
		private int dominionId, _x, _y, _z;

		public WardInfo(int territoryId, int x, int y, int z)
		{
			this.dominionId = territoryId;
			this._x = x;
			this._y = y;
			this._z = z;
		}
	}
}