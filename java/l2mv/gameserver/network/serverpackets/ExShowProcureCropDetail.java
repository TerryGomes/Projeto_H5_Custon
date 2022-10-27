package l2mv.gameserver.network.serverpackets;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.instancemanager.CastleManorManager;
import l2mv.gameserver.model.entity.residence.Castle;
import l2mv.gameserver.templates.manor.CropProcure;

/**
 * format
 * dd[dddc]
 * dd[dQQc] - Gracia Final
 */
public class ExShowProcureCropDetail extends L2GameServerPacket
{
	private int _cropId;
	private Map<Integer, CropProcure> _castleCrops;

	public ExShowProcureCropDetail(int cropId)
	{
		this._cropId = cropId;
		this._castleCrops = new TreeMap<Integer, CropProcure>();

		List<Castle> castleList = ResidenceHolder.getInstance().getResidenceList(Castle.class);
		for (Castle c : castleList)
		{
			CropProcure cropItem = c.getCrop(this._cropId, CastleManorManager.PERIOD_CURRENT);
			if (cropItem != null && cropItem.getAmount() > 0)
			{
				this._castleCrops.put(c.getId(), cropItem);
			}
		}
	}

	@Override
	public void writeImpl()
	{
		this.writeEx(0x78);

		this.writeD(this._cropId); // crop id
		this.writeD(this._castleCrops.size()); // size

		for (int manorId : this._castleCrops.keySet())
		{
			CropProcure crop = this._castleCrops.get(manorId);
			this.writeD(manorId); // manor name
			this.writeQ(crop.getAmount()); // buy residual
			this.writeQ(crop.getPrice()); // buy price
			this.writeC(crop.getReward()); // reward type
		}
	}
}