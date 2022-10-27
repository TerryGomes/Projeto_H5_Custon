package l2mv.gameserver.network.clientpackets;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.RecipeHolder;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Recipe;
import l2mv.gameserver.model.RecipeComponent;
import l2mv.gameserver.model.actor.instances.player.Bonus;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.ManufactureItem;
import l2mv.gameserver.network.serverpackets.RecipeShopItemInfo;
import l2mv.gameserver.network.serverpackets.StatusUpdate;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.TradeHelper;

public class RequestRecipeShopMakeDo extends L2GameClientPacket
{
	private int _manufacturerId;
	private int _recipeId;
	private long _price;

	@Override
	protected void readImpl()
	{
		this._manufacturerId = this.readD();
		this._recipeId = this.readD();
		this._price = this.readQ();
	}

	@Override
	protected void runImpl()
	{
		Player buyer = this.getClient().getActiveChar();
		if (buyer == null)
		{
			return;
		}

		if (buyer.isActionsDisabled())
		{
			buyer.sendActionFailed();
			return;
		}

		if (buyer.isInStoreMode())
		{
			buyer.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}

		if (buyer.isInTrade())
		{
			buyer.sendActionFailed();
			return;
		}

		if (buyer.isFishing())
		{
			buyer.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
			return;
		}

		if (!buyer.getPlayerAccess().UseTrade)
		{
			buyer.sendPacket(SystemMsg.SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_____);
			return;
		}

		Player manufacturer = (Player) buyer.getVisibleObject(this._manufacturerId);
		if (manufacturer == null || manufacturer.getPrivateStoreType() != Player.STORE_PRIVATE_MANUFACTURE || !manufacturer.isInRangeZ(buyer, Creature.INTERACTION_DISTANCE))
		{
			buyer.sendActionFailed();
			return;
		}

		Recipe recipeList = null;
		for (ManufactureItem mi : manufacturer.getCreateList())
		{
			if (mi.getRecipeId() == this._recipeId)
			{
				if (this._price == mi.getCost())
				{
					recipeList = RecipeHolder.getInstance().getRecipeByRecipeId(this._recipeId);
					break;
				}
			}
		}

		if (recipeList == null)
		{
			buyer.sendActionFailed();
			return;
		}

		int success = 0;

		if (recipeList.getRecipes().length == 0)
		{
			manufacturer.sendMessage(new CustomMessage("l2mv.gameserver.RecipeController.NoRecipe", manufacturer).addString(recipeList.getRecipeName()));
			buyer.sendMessage(new CustomMessage("l2mv.gameserver.RecipeController.NoRecipe", manufacturer).addString(recipeList.getRecipeName()));
			return;
		}

		if (!manufacturer.findRecipe(this._recipeId))
		{
			buyer.sendActionFailed();
			return;
		}

		if (manufacturer.getCurrentMp() < recipeList.getMpCost())
		{
			manufacturer.sendPacket(SystemMsg.NOT_ENOUGH_MP);
			buyer.sendPacket(SystemMsg.NOT_ENOUGH_MP, new RecipeShopItemInfo(buyer, manufacturer, this._recipeId, this._price, success));
			return;
		}

		buyer.getInventory().writeLock();
		try
		{
			if (buyer.getAdena() < this._price)
			{
				buyer.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA, new RecipeShopItemInfo(buyer, manufacturer, this._recipeId, this._price, success));
				return;
			}

			RecipeComponent[] recipes = recipeList.getRecipes();

			for (RecipeComponent recipe : recipes)
			{
				if (recipe.getQuantity() == 0)
				{
					continue;
				}

				ItemInstance item = buyer.getInventory().getItemByItemId(recipe.getItemId());

				if (item == null || recipe.getQuantity() > item.getCount())
				{
					buyer.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_MATERIALS_TO_PERFORM_THAT_ACTION, new RecipeShopItemInfo(buyer, manufacturer, this._recipeId, this._price, success));
					return;
				}
			}

			if (!buyer.reduceAdena(this._price, false, "RecipeShopBuy"))
			{
				buyer.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA, new RecipeShopItemInfo(buyer, manufacturer, this._recipeId, this._price, success));
				return;
			}

			for (RecipeComponent recipe : recipes)
			{
				if (recipe.getQuantity() != 0)
				{
					buyer.getInventory().destroyItemByItemId(recipe.getItemId(), recipe.getQuantity(), "RecipeShopComponents");
					// TODO audit
					buyer.sendPacket(SystemMessage2.removeItems(recipe.getItemId(), recipe.getQuantity()));
				}
			}

			long tax = TradeHelper.getTax(manufacturer, this._price);
			if (tax > 0)
			{
				this._price -= tax;
				manufacturer.sendMessage(new CustomMessage("trade.HavePaidTax", manufacturer).addNumber(tax));
			}

			manufacturer.addAdena(this._price, "RecipeShopReward");
		}
		finally
		{
			buyer.getInventory().writeUnlock();
		}

		manufacturer.sendMessage(new CustomMessage("l2mv.gameserver.RecipeController.GotOrder", manufacturer).addString(recipeList.getRecipeName()));

		manufacturer.reduceCurrentMp(recipeList.getMpCost(), null);
		manufacturer.sendStatusUpdate(false, false, StatusUpdate.CUR_MP);

		int tryCount = 1, successCount = 0;
		if (Rnd.chance(Config.CRAFT_DOUBLECRAFT_CHANCE))
		{
			tryCount++;
		}

		for (int i = 0; i < tryCount; i++)
		{
			double chance = recipeList.getSuccessRate();
			double found = Config.CRAFT_MASTERWORK_CHANCE;
			if (buyer.getNetConnection().getBonusExpire() > System.currentTimeMillis() / 1000L)
			{
				final Bonus bonus = buyer.getBonus();
				chance += bonus.getCraftChance();
				found += bonus.getMasterWorkChance();
			}
			if (Rnd.chance(chance))
			{
				final int itemId = recipeList.getFoundation() != 0 ? Rnd.chance(found) ? recipeList.getFoundation() : recipeList.getItemId() : recipeList.getItemId();
				final long count = recipeList.getCount();
				ItemFunctions.addItem(buyer, itemId, count, true, "RecipeShopMake");
				success = 1;
				successCount++;
			}
		}

		SystemMessage2 sm;
		if (successCount == 0)
		{
			sm = new SystemMessage2(SystemMsg.C1_HAS_FAILED_TO_CREATE_S2_AT_THE_PRICE_OF_S3_ADENA);
			sm.addString(manufacturer.getName());
			sm.addItemName(recipeList.getItemId());
			sm.addInteger(this._price);
			buyer.sendPacket(sm);

			sm = new SystemMessage2(SystemMsg.YOUR_ATTEMPT_TO_CREATE_S2_FOR_C1_AT_THE_PRICE_OF_S3_ADENA_HAS_FAILED);
			sm.addString(buyer.getName());
			sm.addItemName(recipeList.getItemId());
			sm.addInteger(this._price);
			manufacturer.sendPacket(sm);

			// Synerge - Add a new craft failed only for recipes with less than 100% rate
//			if (recipeList.getSuccessRate() < 100)
//				buyer.addPlayerStats(Ranking.STAT_TOP_CRAFTS_FAILED);

		}
		else if (recipeList.getCount() > 1 || successCount > 1)
		{
			sm = new SystemMessage2(SystemMsg.C1_CREATED_S2_S3_AT_THE_PRICE_OF_S4_ADENA);
			sm.addString(manufacturer.getName());
			sm.addItemName(recipeList.getItemId());
			sm.addInteger(recipeList.getCount() * successCount);
			sm.addInteger(this._price);
			buyer.sendPacket(sm);

			sm = new SystemMessage2(SystemMsg.S2_S3_HAVE_BEEN_SOLD_TO_C1_FOR_S4_ADENA);
			sm.addString(buyer.getName());
			sm.addItemName(recipeList.getItemId());
			sm.addInteger(recipeList.getCount() * successCount);
			sm.addInteger(this._price);
			manufacturer.sendPacket(sm);

			// Synerge - Add a new craft succeed only for recipes with less than 100% rate
//			if (recipeList.getSuccessRate() < 100)
//				buyer.addPlayerStats(Ranking.STAT_TOP_CRAFTS_SUCCEED);
		}
		else
		{
			sm = new SystemMessage2(SystemMsg.C1_CREATED_S2_AFTER_RECEIVING_S3_ADENA);
			sm.addString(manufacturer.getName());
			sm.addItemName(recipeList.getItemId());
			sm.addInteger(this._price);
			buyer.sendPacket(sm);

			sm = new SystemMessage2(SystemMsg.S2_IS_SOLD_TO_C1_FOR_THE_PRICE_OF_S3_ADENA);
			sm.addString(buyer.getName());
			sm.addItemName(recipeList.getItemId());
			sm.addInteger(this._price);
			manufacturer.sendPacket(sm);

			// Synerge - Add a new craft succeed only for recipes with less than 100% rate
//			if (recipeList.getSuccessRate() < 100)
//				buyer.addPlayerStats(Ranking.STAT_TOP_CRAFTS_SUCCEED);
		}

		buyer.sendChanges();
		buyer.sendPacket(new RecipeShopItemInfo(buyer, manufacturer, this._recipeId, this._price, success));
	}
}