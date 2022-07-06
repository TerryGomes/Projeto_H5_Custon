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
		_list = list.getEntries();
		_listId = list.getListId();
		_page = page;
		_finished = finished;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xD0);
		writeD(_listId); // list id
		writeD(_page); // page
		writeD(_finished); // finished
		writeD(Config.MULTISELL_SIZE); // size of pages
		writeD(_list.size()); // list length
		List<MultiSellIngredient> ingredients;
		for (MultiSellEntry ent : _list)
		{
			ingredients = fixIngredients(ent.getIngredients());

			writeD(ent.getEntryId());
			writeC(!ent.getProduction().isEmpty() && ent.getProduction().get(0).isStackable() ? 1 : 0); // stackable?
			writeH(0x00); // unknown
			writeD(0x00); // инкрустация
			writeD(0x00); // инкрустация

			writeItemElements();

			writeH(ent.getProduction().size());
			writeH(ingredients.size());

			for (MultiSellIngredient prod : ent.getProduction())
			{
				int itemId = prod.getItemId();
				ItemTemplate template = itemId > 0 ? ItemHolder.getInstance().getTemplate(prod.getItemId()) : null;
				writeD(itemId);
				writeD(itemId > 0 ? template.getBodyPart() : 0);
				writeH(itemId > 0 ? template.getType2ForPackets() : 0);
				writeQ(prod.getItemCount());
				writeH(prod.getItemEnchant());
				writeD(0x00); // инкрустация
				writeD(0x00); // инкрустация
				writeItemElements(prod);
			}

			for (MultiSellIngredient i : ingredients)
			{
				int itemId = i.getItemId();
				final ItemTemplate item = itemId > 0 ? ItemHolder.getInstance().getTemplate(i.getItemId()) : null;
				writeD(itemId); // ID
				writeH(itemId > 0 ? item.getType2() : 0xffff);
				writeQ(i.getItemCount()); // Count
				writeH(i.getItemEnchant()); // Enchant Level
				writeD(0x00); // инкрустация
				writeD(0x00); // инкрустация
				writeItemElements(i);
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