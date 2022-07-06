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
		_manorId = manorId;
		Castle c = ResidenceHolder.getInstance().getResidence(Castle.class, _manorId);
		List<Integer> crops = Manor.getInstance().getCropsForCastle(_manorId);
		_count = crops.size();
		_cropData = new long[_count * 14];
		int i = 0;
		for (int cr : crops)
		{
			_cropData[i * 14 + 0] = cr;
			_cropData[i * 14 + 1] = Manor.getInstance().getSeedLevelByCrop(cr);
			_cropData[i * 14 + 2] = Manor.getInstance().getRewardItem(cr, 1);
			_cropData[i * 14 + 3] = Manor.getInstance().getRewardItem(cr, 2);
			_cropData[i * 14 + 4] = Manor.getInstance().getCropPuchaseLimit(cr);
			_cropData[i * 14 + 5] = 0; // Looks like not used
			_cropData[i * 14 + 6] = Manor.getInstance().getCropBasicPrice(cr) * 60 / 100;
			_cropData[i * 14 + 7] = Manor.getInstance().getCropBasicPrice(cr) * 10;
			CropProcure cropPr = c.getCrop(cr, CastleManorManager.PERIOD_CURRENT);
			if (cropPr != null)
			{
				_cropData[i * 14 + 8] = cropPr.getStartAmount();
				_cropData[i * 14 + 9] = cropPr.getPrice();
				_cropData[i * 14 + 10] = cropPr.getReward();
			}
			else
			{
				_cropData[i * 14 + 8] = 0;
				_cropData[i * 14 + 9] = 0;
				_cropData[i * 14 + 10] = 0;
			}
			cropPr = c.getCrop(cr, CastleManorManager.PERIOD_NEXT);
			if (cropPr != null)
			{
				_cropData[i * 14 + 11] = cropPr.getStartAmount();
				_cropData[i * 14 + 12] = cropPr.getPrice();
				_cropData[i * 14 + 13] = cropPr.getReward();
			}
			else
			{
				_cropData[i * 14 + 11] = 0;
				_cropData[i * 14 + 12] = 0;
				_cropData[i * 14 + 13] = 0;
			}
			i++;
		}
	}

	@Override
	public void writeImpl()
	{
		writeEx(0x2b); // SubId

		writeD(_manorId); // manor id
		writeD(_count); // size

		for (int i = 0; i < _count; i++)
		{
			writeD((int) _cropData[i * 14 + 0]); // crop id
			writeD((int) _cropData[i * 14 + 1]); // seed level

			writeC(1);
			writeD((int) _cropData[i * 14 + 2]); // reward 1 id

			writeC(1);
			writeD((int) _cropData[i * 14 + 3]); // reward 2 id

			writeD((int) _cropData[i * 14 + 4]); // next sale limit
			writeD((int) _cropData[i * 14 + 5]); // ???
			writeD((int) _cropData[i * 14 + 6]); // min crop price
			writeD((int) _cropData[i * 14 + 7]); // max crop price

			writeQ(_cropData[i * 14 + 8]); // today buy
			writeQ(_cropData[i * 14 + 9]); // today price
			writeC((int) _cropData[i * 14 + 10]); // today reward
			writeQ(_cropData[i * 14 + 11]); // next buy
			writeQ(_cropData[i * 14 + 12]); // next price

			writeC((int) _cropData[i * 14 + 13]); // next reward
		}
	}
}