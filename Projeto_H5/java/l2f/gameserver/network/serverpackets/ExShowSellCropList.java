package l2f.gameserver.network.serverpackets;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import l2f.gameserver.model.Manor;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.templates.manor.CropProcure;

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
		_manorId = manorId;
		_castleCrops = new TreeMap<Integer, CropProcure>();
		_cropsItems = new TreeMap<Integer, ItemInstance>();

		List<Integer> allCrops = Manor.getInstance().getAllCrops();
		for (int cropId : allCrops)
		{
			ItemInstance item = player.getInventory().getItemByItemId(cropId);
			if (item != null)
			{
				_cropsItems.put(cropId, item);
			}
		}

		for (CropProcure crop : crops)
		{
			if (_cropsItems.containsKey(crop.getId()) && crop.getAmount() > 0)
			{
				_castleCrops.put(crop.getId(), crop);
			}
		}

	}

	@Override
	public void writeImpl()
	{
		writeEx(0x2c);

		writeD(_manorId); // manor id
		writeD(_cropsItems.size()); // size

		for (ItemInstance item : _cropsItems.values())
		{
			writeD(item.getObjectId()); // Object id
			writeD(item.getItemId()); // crop id
			writeD(Manor.getInstance().getSeedLevelByCrop(item.getItemId())); // seed level

			writeC(1);
			writeD(Manor.getInstance().getRewardItem(item.getItemId(), 1)); // reward 1 id

			writeC(1);
			writeD(Manor.getInstance().getRewardItem(item.getItemId(), 2)); // reward 2 id

			if (_castleCrops.containsKey(item.getItemId()))
			{
				CropProcure crop = _castleCrops.get(item.getItemId());
				writeD(_manorId); // manor
				writeQ(crop.getAmount()); // buy residual
				writeQ(crop.getPrice()); // buy price
				writeC(crop.getReward()); // reward
			}
			else
			{
				writeD(0xFFFFFFFF); // manor
				writeQ(0); // buy residual
				writeQ(0); // buy price
				writeC(0); // reward
			}
			writeQ(item.getCount()); // my crops
		}
	}
}