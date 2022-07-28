package l2mv.gameserver.network.serverpackets;

import java.util.List;

import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.instancemanager.CastleManorManager;
import l2mv.gameserver.model.Manor;
import l2mv.gameserver.model.entity.residence.Castle;
import l2mv.gameserver.templates.manor.CropProcure;

/**
 * format
 * dd[ddc[d]c[d]ddddddcddc]
 * dd[ddc[d]c[d]ddddQQcQQc] - Gracia Final
 */
public class ExShowCropSetting extends L2GameServerPacket
{
	private int _manorId;
	private int _count;
	private long[] _cropData; // data to send, size:_count*14

	public ExShowCropSetting(int manorId)
	{
		this._manorId = manorId;
		Castle c = ResidenceHolder.getInstance().getResidence(Castle.class, this._manorId);
		List<Integer> crops = Manor.getInstance().getCropsForCastle(this._manorId);
		this._count = crops.size();
		this._cropData = new long[this._count * 14];
		int i = 0;
		for (int cr : crops)
		{
			this._cropData[i * 14 + 0] = cr;
			this._cropData[i * 14 + 1] = Manor.getInstance().getSeedLevelByCrop(cr);
			this._cropData[i * 14 + 2] = Manor.getInstance().getRewardItem(cr, 1);
			this._cropData[i * 14 + 3] = Manor.getInstance().getRewardItem(cr, 2);
			this._cropData[i * 14 + 4] = Manor.getInstance().getCropPuchaseLimit(cr);
			this._cropData[i * 14 + 5] = 0; // Looks like not used
			this._cropData[i * 14 + 6] = Manor.getInstance().getCropBasicPrice(cr) * 60 / 100;
			this._cropData[i * 14 + 7] = Manor.getInstance().getCropBasicPrice(cr) * 10;
			CropProcure cropPr = c.getCrop(cr, CastleManorManager.PERIOD_CURRENT);
			if (cropPr != null)
			{
				this._cropData[i * 14 + 8] = cropPr.getStartAmount();
				this._cropData[i * 14 + 9] = cropPr.getPrice();
				this._cropData[i * 14 + 10] = cropPr.getReward();
			}
			else
			{
				this._cropData[i * 14 + 8] = 0;
				this._cropData[i * 14 + 9] = 0;
				this._cropData[i * 14 + 10] = 0;
			}
			cropPr = c.getCrop(cr, CastleManorManager.PERIOD_NEXT);
			if (cropPr != null)
			{
				this._cropData[i * 14 + 11] = cropPr.getStartAmount();
				this._cropData[i * 14 + 12] = cropPr.getPrice();
				this._cropData[i * 14 + 13] = cropPr.getReward();
			}
			else
			{
				this._cropData[i * 14 + 11] = 0;
				this._cropData[i * 14 + 12] = 0;
				this._cropData[i * 14 + 13] = 0;
			}
			i++;
		}
	}

	@Override
	public void writeImpl()
	{
		this.writeEx(0x2b); // SubId

		this.writeD(this._manorId); // manor id
		this.writeD(this._count); // size

		for (int i = 0; i < this._count; i++)
		{
			this.writeD((int) this._cropData[i * 14 + 0]); // crop id
			this.writeD((int) this._cropData[i * 14 + 1]); // seed level

			this.writeC(1);
			this.writeD((int) this._cropData[i * 14 + 2]); // reward 1 id

			this.writeC(1);
			this.writeD((int) this._cropData[i * 14 + 3]); // reward 2 id

			this.writeD((int) this._cropData[i * 14 + 4]); // next sale limit
			this.writeD((int) this._cropData[i * 14 + 5]); // ???
			this.writeD((int) this._cropData[i * 14 + 6]); // min crop price
			this.writeD((int) this._cropData[i * 14 + 7]); // max crop price

			this.writeQ(this._cropData[i * 14 + 8]); // today buy
			this.writeQ(this._cropData[i * 14 + 9]); // today price
			this.writeC((int) this._cropData[i * 14 + 10]); // today reward
			this.writeQ(this._cropData[i * 14 + 11]); // next buy
			this.writeQ(this._cropData[i * 14 + 12]); // next price

			this.writeC((int) this._cropData[i * 14 + 13]); // next reward
		}
	}
}