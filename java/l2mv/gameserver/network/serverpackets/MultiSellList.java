package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.data.xml.holder.MultiSellHolder.MultiSellListContainer;
import l2mv.gameserver.model.base.MultiSellEntry;
import l2mv.gameserver.model.base.MultiSellIngredient;
import l2mv.gameserver.templates.item.ItemTemplate;

public class MultiSellList extends L2GameServerPacket
{
	private final int _page;
	private final int _finished;
	private final int _listId;
	private final List<MultiSellEntry> _list;

	public MultiSellList(MultiSellListContainer list, int page, int finished)
	{
		this._list = list.getEntries();
		this._listId = list.getListId();
		this._page = page;
		this._finished = finished;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xD0);
		this.writeD(this._listId); // list id
		this.writeD(this._page); // page
		this.writeD(this._finished); // finished
		this.writeD(Config.MULTISELL_SIZE); // size of pages
		this.writeD(this._list.size()); // list length
		List<MultiSellIngredient> ingredients;
		for (MultiSellEntry ent : this._list)
		{
			ingredients = fixIngredients(ent.getIngredients());

			this.writeD(ent.getEntryId());
			this.writeC(!ent.getProduction().isEmpty() && ent.getProduction().get(0).isStackable() ? 1 : 0); // stackable?
			this.writeH(0x00); // unknown
			this.writeD(0x00); // инкрустация
			this.writeD(0x00); // инкрустация

			this.writeItemElements();

			this.writeH(ent.getProduction().size());
			this.writeH(ingredients.size());

			for (MultiSellIngredient prod : ent.getProduction())
			{
				int itemId = prod.getItemId();
				ItemTemplate template = itemId > 0 ? ItemHolder.getInstance().getTemplate(prod.getItemId()) : null;
				this.writeD(itemId);
				this.writeD(itemId > 0 ? template.getBodyPart() : 0);
				this.writeH(itemId > 0 ? template.getType2ForPackets() : 0);
				this.writeQ(prod.getItemCount());
				this.writeH(prod.getItemEnchant());
				this.writeD(0x00); // инкрустация
				this.writeD(0x00); // инкрустация
				this.writeItemElements(prod);
			}

			for (MultiSellIngredient i : ingredients)
			{
				int itemId = i.getItemId();
				final ItemTemplate item = itemId > 0 ? ItemHolder.getInstance().getTemplate(i.getItemId()) : null;
				this.writeD(itemId); // ID
				this.writeH(itemId > 0 ? item.getType2() : 0xffff);
				this.writeQ(i.getItemCount()); // Count
				this.writeH(i.getItemEnchant()); // Enchant Level
				this.writeD(0x00); // инкрустация
				this.writeD(0x00); // инкрустация
				this.writeItemElements(i);
			}
		}
	}

	// FIXME временная затычка, пока NCSoft не починят в клиенте отображение мультиселов где кол-во больше Integer.MAX_VALUE
	private static List<MultiSellIngredient> fixIngredients(List<MultiSellIngredient> ingredients)
	{
		int needFix = 0;
		for (MultiSellIngredient ingredient : ingredients)
		{
			if (ingredient.getItemCount() > Integer.MAX_VALUE)
			{
				needFix++;
			}
		}

		if (needFix == 0)
		{
			return ingredients;
		}

		MultiSellIngredient temp;
		List<MultiSellIngredient> result = new ArrayList<MultiSellIngredient>(ingredients.size() + needFix);
		for (MultiSellIngredient ingredient : ingredients)
		{
			ingredient = ingredient.clone();
			while (ingredient.getItemCount() > Integer.MAX_VALUE)
			{
				temp = ingredient.clone();
				temp.setItemCount(2000000000);
				result.add(temp);
				ingredient.setItemCount(ingredient.getItemCount() - 2000000000);
			}
			if (ingredient.getItemCount() > 0)
			{
				result.add(ingredient);
			}
		}

		return result;
	}
}