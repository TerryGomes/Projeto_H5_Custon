package l2mv.gameserver.network.serverpackets;

import java.util.List;

import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.instancemanager.CastleManorManager;
import l2mv.gameserver.model.Manor;
import l2mv.gameserver.model.entity.residence.Castle;
import l2mv.gameserver.templates.manor.SeedProduction;

/**
 * format
 * dd[ddc[d]c[d]dddddddd]
 * dd[ddc[d]c[d]ddddQQQQ] - Gracia Final
 */
public class ExShowSeedSetting extends L2GameServerPacket
{
	private int _manorId;
	private int _count;
	private long[] _seedData; // data to send, size:_count*12

	public ExShowSeedSetting(int manorId)
	{
		this._manorId = manorId;
		Castle c = ResidenceHolder.getInstance().getResidence(Castle.class, this._manorId);
		List<Integer> seeds = Manor.getInstance().getSeedsForCastle(this._manorId);
		this._count = seeds.size();
		this._seedData = new long[this._count * 12];
		int i = 0;
		for (int s : seeds)
		{
			this._seedData[i * 12 + 0] = s;
			this._seedData[i * 12 + 1] = Manor.getInstance().getSeedLevel(s);
			this._seedData[i * 12 + 2] = Manor.getInstance().getRewardItemBySeed(s, 1);
			this._seedData[i * 12 + 3] = Manor.getInstance().getRewardItemBySeed(s, 2);
			this._seedData[i * 12 + 4] = Manor.getInstance().getSeedSaleLimit(s);
			this._seedData[i * 12 + 5] = Manor.getInstance().getSeedBuyPrice(s);
			this._seedData[i * 12 + 6] = Manor.getInstance().getSeedBasicPrice(s) * 60 / 100;
			this._seedData[i * 12 + 7] = Manor.getInstance().getSeedBasicPrice(s) * 10;
			SeedProduction seedPr = c.getSeed(s, CastleManorManager.PERIOD_CURRENT);
			if (seedPr != null)
			{
				this._seedData[i * 12 + 8] = seedPr.getStartProduce();
				this._seedData[i * 12 + 9] = seedPr.getPrice();
			}
			else
			{
				this._seedData[i * 12 + 8] = 0;
				this._seedData[i * 12 + 9] = 0;
			}
			seedPr = c.getSeed(s, CastleManorManager.PERIOD_NEXT);
			if (seedPr != null)
			{
				this._seedData[i * 12 + 10] = seedPr.getStartProduce();
				this._seedData[i * 12 + 11] = seedPr.getPrice();
			}
			else
			{
				this._seedData[i * 12 + 10] = 0;
				this._seedData[i * 12 + 11] = 0;
			}
			i++;
		}
	}

	@Override
	public void writeImpl()
	{
		this.writeEx(0x26); // SubId

		this.writeD(this._manorId); // manor id
		this.writeD(this._count); // size

		for (int i = 0; i < this._count; i++)
		{
			this.writeD((int) this._seedData[i * 12 + 0]); // seed id
			this.writeD((int) this._seedData[i * 12 + 1]); // level

			this.writeC(1);
			this.writeD((int) this._seedData[i * 12 + 2]); // reward 1 id

			this.writeC(1);
			this.writeD((int) this._seedData[i * 12 + 3]); // reward 2 id

			this.writeD((int) this._seedData[i * 12 + 4]); // next sale limit
			this.writeD((int) this._seedData[i * 12 + 5]); // price for castle to produce 1
			this.writeD((int) this._seedData[i * 12 + 6]); // min seed price
			this.writeD((int) this._seedData[i * 12 + 7]); // max seed price

			this.writeQ(this._seedData[i * 12 + 8]); // today sales
			this.writeQ(this._seedData[i * 12 + 9]); // today price
			this.writeQ(this._seedData[i * 12 + 10]); // next sales
			this.writeQ(this._seedData[i * 12 + 11]); // next price
		}
	}
}