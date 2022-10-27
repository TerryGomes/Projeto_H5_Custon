package l2mv.gameserver.network.serverpackets;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import l2mv.gameserver.model.Manor;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.templates.manor.CropProcure;

/**
 * format
 * dd[dddc[d]c[d]dddcd]
 * dd[dddc[d]c[d]dQQcQ] - Gracia Final
 */
public class ExShowSellCropList extends L2GameServerPacket
{
	private int _manorId = 1;
	private Map<Integer, ItemInstance> _cropsItems;
	private Map<Integer, CropProcure> _castleCrops;

	public ExShowSellCropList(Player player, int manorId, List<CropProcure> crops)
	{
		this._manorId = manorId;
		this._castleCrops = new TreeMap<Integer, CropProcure>();
		this._cropsItems = new TreeMap<Integer, ItemInstance>();

		List<Integer> allCrops = Manor.getInstance().getAllCrops();
		for (int cropId : allCrops)
		{
			ItemInstance item = player.getInventory().getItemByItemId(cropId);
			if (item != null)
			{
				this._cropsItems.put(cropId, item);
			}
		}

		for (CropProcure crop : crops)
		{
			if (this._cropsItems.containsKey(crop.getId()) && crop.getAmount() > 0)
			{
				this._castleCrops.put(crop.getId(), crop);
			}
		}

	}

	@Override
	public void writeImpl()
	{
		this.writeEx(0x2c);

		this.writeD(this._manorId); // manor id
		this.writeD(this._cropsItems.size()); // size

		for (ItemInstance item : this._cropsItems.values())
		{
			this.writeD(item.getObjectId()); // Object id
			this.writeD(item.getItemId()); // crop id
			this.writeD(Manor.getInstance().getSeedLevelByCrop(item.getItemId())); // seed level

			this.writeC(1);
			this.writeD(Manor.getInstance().getRewardItem(item.getItemId(), 1)); // reward 1 id

			this.writeC(1);
			this.writeD(Manor.getInstance().getRewardItem(item.getItemId(), 2)); // reward 2 id

			if (this._castleCrops.containsKey(item.getItemId()))
			{
				CropProcure crop = this._castleCrops.get(item.getItemId());
				this.writeD(this._manorId); // manor
				this.writeQ(crop.getAmount()); // buy residual
				this.writeQ(crop.getPrice()); // buy price
				this.writeC(crop.getReward()); // reward
			}
			else
			{
				this.writeD(0xFFFFFFFF); // manor
				this.writeQ(0); // buy residual
				this.writeQ(0); // buy price
				this.writeC(0); // reward
			}
			this.writeQ(item.getCount()); // my crops
		}
	}
}