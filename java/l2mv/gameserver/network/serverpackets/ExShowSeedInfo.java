package l2mv.gameserver.network.serverpackets;

import java.util.List;

import l2mv.gameserver.model.Manor;
import l2mv.gameserver.templates.manor.SeedProduction;

/**
 * format
 * cddd[dddddc[d]c[d]]
 * cddd[dQQQdc[d]c[d]] - Gracia Final
 */
public class ExShowSeedInfo extends L2GameServerPacket
{
	private List<SeedProduction> _seeds;
	private int _manorId;

	public ExShowSeedInfo(int manorId, List<SeedProduction> seeds)
	{
		this._manorId = manorId;
		this._seeds = seeds;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x23); // SubId
		this.writeC(0);
		this.writeD(this._manorId); // Manor ID
		this.writeD(0);
		this.writeD(this._seeds.size());
		for (SeedProduction seed : this._seeds)
		{
			this.writeD(seed.getId()); // Seed id

			this.writeQ(seed.getCanProduce()); // Left to buy
			this.writeQ(seed.getStartProduce()); // Started amount
			this.writeQ(seed.getPrice()); // Sell Price
			this.writeD(Manor.getInstance().getSeedLevel(seed.getId())); // Seed Level

			this.writeC(1); // reward 1 Type
			this.writeD(Manor.getInstance().getRewardItemBySeed(seed.getId(), 1)); // Reward 1 Type Item Id

			this.writeC(1); // reward 2 Type
			this.writeD(Manor.getInstance().getRewardItemBySeed(seed.getId(), 2)); // Reward 2 Type Item Id
		}
	}
}