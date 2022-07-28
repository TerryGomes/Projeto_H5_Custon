package l2mv.gameserver.network.serverpackets;

import java.util.List;

import l2mv.gameserver.model.Manor;
import l2mv.gameserver.templates.manor.CropProcure;

/**
 * Format:
 * cddd[ddddcdc[d]c[d]]
 * cddd[dQQQcdc[d]c[d]] - Gracia Final
 *
 */

public class ExShowCropInfo extends L2GameServerPacket
{
	private List<CropProcure> _crops;
	private int _manorId;

	public ExShowCropInfo(int manorId, List<CropProcure> crops)
	{
		this._manorId = manorId;
		this._crops = crops;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x24); // SubId
		this.writeC(0);
		this.writeD(this._manorId); // Manor ID
		this.writeD(0);
		this.writeD(this._crops.size());
		for (CropProcure crop : this._crops)
		{
			this.writeD(crop.getId()); // Crop id
			this.writeQ(crop.getAmount()); // Buy residual
			this.writeQ(crop.getStartAmount()); // Buy
			this.writeQ(crop.getPrice()); // Buy price
			this.writeC(crop.getReward()); // Reward
			this.writeD(Manor.getInstance().getSeedLevelByCrop(crop.getId())); // Seed Level

			this.writeC(1); // rewrad 1 Type
			this.writeD(Manor.getInstance().getRewardItem(crop.getId(), 1)); // Rewrad 1 Type Item Id

			this.writeC(1); // rewrad 2 Type
			this.writeD(Manor.getInstance().getRewardItem(crop.getId(), 2)); // Rewrad 2 Type Item Id
		}
	}
}