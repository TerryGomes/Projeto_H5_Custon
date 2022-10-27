package l2mv.gameserver.network.serverpackets;

import java.util.List;

import l2mv.gameserver.model.Manor;

/**
 * format(packet 0xFE)
 * ch cd [ddddcdcd]
 * c  - id
 * h  - sub id
 *
 * c
 * d  - size
 *
 * [
 * d  - level
 * d  - seed price
 * d  - seed level
 * d  - crop price
 * c
 * d  - reward 1 id
 * c
 * d  - reward 2 id
 * ]
 *
 */
public class ExShowManorDefaultInfo extends L2GameServerPacket
{
	private List<Integer> _crops = null;

	public ExShowManorDefaultInfo()
	{
		this._crops = Manor.getInstance().getAllCrops();
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x25);
		this.writeC(0);
		this.writeD(this._crops.size());
		for (int cropId : this._crops)
		{
			this.writeD(cropId); // crop Id
			this.writeD(Manor.getInstance().getSeedLevelByCrop(cropId)); // level
			this.writeD(Manor.getInstance().getSeedBasicPriceByCrop(cropId)); // seed price
			this.writeD(Manor.getInstance().getCropBasicPrice(cropId)); // crop price
			this.writeC(1); // rewrad 1 Type
			this.writeD(Manor.getInstance().getRewardItem(cropId, 1)); // Rewrad 1 Type Item Id
			this.writeC(1); // rewrad 2 Type
			this.writeD(Manor.getInstance().getRewardItem(cropId, 2)); // Rewrad 2 Type Item Id
		}
	}
}