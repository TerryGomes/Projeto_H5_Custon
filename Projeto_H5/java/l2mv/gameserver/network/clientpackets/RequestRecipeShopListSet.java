package l2mv.gameserver.network.clientpackets;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ManufactureItem;
import l2mv.gameserver.network.serverpackets.RecipeShopMsg;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.utils.Log;
import l2mv.gameserver.utils.TradeHelper;

public class RequestRecipeShopListSet extends L2GameClientPacket
{
	private int[] _recipes;
	private long[] _prices;
	private int _count;

	@Override
	protected void readImpl()
	{
		this._count = this.readD();
		if (this._count * 12 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1)
		{
			this._count = 0;
			return;
		}
		this._recipes = new int[this._count];
		this._prices = new long[this._count];
		for (int i = 0; i < this._count; i++)
		{
			this._recipes[i] = this.readD();
			this._prices[i] = this.readQ();
			if (this._prices[i] < 0)
			{
				this._count = 0;
				return;
			}
		}
	}

	@Override
	protected void runImpl()
	{
		Player manufacturer = this.getClient().getActiveChar();
		if (manufacturer == null || this._count == 0)
		{
			return;
		}

		if (!TradeHelper.checksIfCanOpenStore(manufacturer, Player.STORE_PRIVATE_MANUFACTURE))
		{
			manufacturer.sendActionFailed();
			return;
		}

		if (this._count > Config.MAX_PVTCRAFT_SLOTS)
		{
			manufacturer.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			return;
		}

		List<ManufactureItem> createList = new CopyOnWriteArrayList<ManufactureItem>();
		for (int i = 0; i < this._count; i++)
		{
			int recipeId = this._recipes[i];
			long price = this._prices[i];
			if (!manufacturer.findRecipe(recipeId))
			{
				continue;
			}

			ManufactureItem mi = new ManufactureItem(recipeId, price);
			createList.add(mi);
		}

		if (!createList.isEmpty())
		{
			manufacturer.setCreateList(createList);
			manufacturer.saveTradeList();
			manufacturer.setPrivateStoreType(Player.STORE_PRIVATE_MANUFACTURE);
			manufacturer.broadcastPacket(new RecipeShopMsg(manufacturer));
			manufacturer.sitDown(null);
			manufacturer.broadcastCharInfo();
			Log.logPrivateStoreMessage(manufacturer, manufacturer.getManufactureName());
		}

		manufacturer.sendActionFailed();
	}
}