package l2mv.gameserver.network.serverpackets;

import java.util.List;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ManufactureItem;

public class RecipeShopSellList extends L2GameServerPacket
{
	private int objId, curMp, maxMp;
	private long adena;
	private List<ManufactureItem> createList;

	public RecipeShopSellList(Player buyer, Player manufacturer)
	{
		this.objId = manufacturer.getObjectId();
		this.curMp = (int) manufacturer.getCurrentMp();
		this.maxMp = manufacturer.getMaxMp();
		this.adena = buyer.getAdena();
		this.createList = manufacturer.getCreateList();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xdf);
		this.writeD(this.objId);
		this.writeD(this.curMp);// Creator's MP
		this.writeD(this.maxMp);// Creator's MP
		this.writeQ(this.adena);
		this.writeD(this.createList.size());
		for (ManufactureItem mi : this.createList)
		{
			this.writeD(mi.getRecipeId());
			this.writeD(0x00); // unknown
			this.writeQ(mi.getCost());
		}
	}
}