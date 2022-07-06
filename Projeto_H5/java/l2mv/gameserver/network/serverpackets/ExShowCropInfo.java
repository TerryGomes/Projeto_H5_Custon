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
		_manorId = manorId;
		_crops = crops;
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0x24); // SubId
		writeC(0);
		writeD(_manorId); // Manor ID
		writeD(0);
		writeD(_crops.size());
		for (CropProcure crop : _crops)
		{
			writeD(crop.getId()); // Crop id
			writeQ(crop.getAmount()); // Buy residual
			writeQ(crop.getStartAmount()); // Buy
			writeQ(crop.getPrice()); // Buy price
			writeC(crop.getReward()); // Reward
			writeD(Manor.getInstance().getSeedLevelByCrop(crop.getId())); // Seed Level

			writeC(1); // rewrad 1 Type
			writeD(Manor.getInstance().getRewardItem(crop.getId(), 1)); // Rewrad 1 Type Item Id

			writeC(1); // rewrad 2 Type
			writeD(Manor.getInstance().getRewardItem(crop.getId(), 2)); // Rewrad 2 Type Item Id
		}
	}
}