package l2f.gameserver.network.serverpackets;

import java.util.List;

import l2f.gameserver.model.Manor;
import l2f.gameserver.templates.manor.SeedProduction;

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
		_manorId = manorId;
		_seeds = seeds;
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0x23); // SubId
		writeC(0);
		writeD(_manorId); // Manor ID
		writeD(0);
		writeD(_seeds.size());
		for (SeedProduction seed : _seeds)
		{
			writeD(seed.getId()); // Seed id

			writeQ(seed.getCanProduce()); // Left to buy
			writeQ(seed.getStartProduce()); // Started amount
			writeQ(seed.getPrice()); // Sell Price
			writeD(Manor.getInstance().getSeedLevel(seed.getId())); // Seed Level

			writeC(1); // reward 1 Type
			writeD(Manor.getInstance().getRewardItemBySeed(seed.getId(), 1)); // Reward 1 Type Item Id

			writeC(1); // reward 2 Type
			writeD(Manor.getInstance().getRewardItemBySeed(seed.getId(), 2)); // Reward 2 Type Item Id
		}
	}
}