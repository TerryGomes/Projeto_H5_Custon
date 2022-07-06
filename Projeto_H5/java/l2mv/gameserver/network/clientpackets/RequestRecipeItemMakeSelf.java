package l2mv.gameserver.network.clientpackets;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.data.xml.holder.RecipeHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Recipe;
import l2mv.gameserver.model.RecipeComponent;
import l2mv.gameserver.model.actor.instances.player.Bonus;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.ActionFail;
import l2mv.gameserver.network.serverpackets.RecipeItemMakeInfo;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import l2mv.gameserver.utils.ItemFunctions;

public class RequestRecipeItemMakeSelf extends L2GameClientPacket
{
	private int _recipeId;

	/**
	 * packet type id 0xB8
	 * format:		cd
	 */
	@Override
	protected void readImpl()
	{
		_recipeId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (activeChar.isActionsDisabled() || activeChar.isInStoreMode() || activeChar.isProcessingRequest())
		{
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
			return;
		}

		Recipe recipeList = RecipeHolder.getInstance().getRecipeByRecipeId(_recipeId);

		if (recipeList == null || recipeList.getRecipes().length == 0)
		{
			activeChar.sendPacket(SystemMsg.THE_RECIPE_IS_INCORRECT);
			return;
		}

		if (activeChar.getCurrentMp() < recipeList.getMpCost())
		{
			activeChar.sendPacket(SystemMsg.NOT_ENOUGH_MP, new RecipeItemMakeInfo(activeChar, recipeList, 0));
			return;
		}

		if (!activeChar.findRecipe(_recipeId))
		{
			activeChar.sendPacket(SystemMsg.PLEASE_REGISTER_A_RECIPE, ActionFail.STATIC);
			return;
		}

		activeChar.getInventory().writeLock();
		try
		{
			RecipeComponent[] recipes = recipeList.getRecipes();

			for (RecipeComponent recipe : recipes)
			{
				if (recipe.getQuantity() == 0)
				{
					continue;
				}

				if (Config.ALT_GAME_UNREGISTER_RECIPE && ItemHolder.getInstance().getTemplate(recipe.getItemId()).getItemType() == EtcItemType.RECIPE)
				{
					Recipe rp = RecipeHolder.getInstance().getRecipeByRecipeItem(recipe.getItemId());
					if (activeChar.hasRecipe(rp))
					{
						continue;
					}
					activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_MATERIALS_TO_PERFORM_THAT_ACTION, new RecipeItemMakeInfo(activeChar, recipeList, 0));
					return;
				}

				ItemInstance item = activeChar.getInventory().getItemByItemId(recipe.getItemId());
				if (item == null || item.getCount() < recipe.getQuantity())
				{
					activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_MATERIALS_TO_PERFORM_THAT_ACTION, new RecipeItemMakeInfo(activeChar, recipeList, 0));
					return;
				}
			}

			for (RecipeComponent recipe : recipes)
			{
				if (recipe.getQuantity() != 0)
				{
					if (Config.ALT_GAME_UNREGISTER_RECIPE && ItemHolder.getInstance().getTemplate(recipe.getItemId()).getItemType() == EtcItemType.RECIPE)
					{
						activeChar.unregisterRecipe(RecipeHolder.getInstance().getRecipeByRecipeItem(recipe.getItemId()).getId());
					}
					else
					{
						if (!activeChar.getInventory().destroyItemByItemId(recipe.getItemId(), recipe.getQuantity(), "RecipeMakeSelf"))
						{
							continue;// TODO audit
						}
						activeChar.sendPacket(SystemMessage2.removeItems(recipe.getItemId(), recipe.getQuantity()));
					}
				}
			}
		}
		finally
		{
			activeChar.getInventory().writeUnlock();
		}

		activeChar.resetWaitSitTime();
		activeChar.reduceCurrentMp(recipeList.getMpCost(), null);

		int tryCount = 1, success = 0;
		if (Rnd.chance(Config.CRAFT_DOUBLECRAFT_CHANCE))
		{
			tryCount++;
		}

		for (int i = 0; i < tryCount; i++)
		{
			double chance = recipeList.getSuccessRate();
			double found = Config.CRAFT_MASTERWORK_CHANCE;
			if (activeChar.getNetConnection().getBonusExpire() > System.currentTimeMillis() / 1000L)
			{
				final Bonus bonus = activeChar.getBonus();
				chance += bonus.getCraftChance();
				found += bonus.getMasterWorkChance();
			}
			if (Rnd.chance(chance))
			{
				final int itemId = recipeList.getFoundation() != 0 ? Rnd.chance(found) ? recipeList.getFoundation() : recipeList.getItemId() : recipeList.getItemId();
				final long count = recipeList.getCount();
				ItemFunctions.addItem(activeChar, itemId, count, true, "RecipeMakeSelf");
				success = 1;

				if (itemId == recipeList.getFoundation())
				{
					activeChar.getCounters().foundationItemsMade++;
				}
			}
		}

		if (success == 0)
		{
			activeChar.sendPacket(new SystemMessage2(SystemMsg.YOU_FAILED_TO_MANUFACTURE_S1).addItemName(recipeList.getItemId()));

			// Synerge - Add a new craft failed only for recipes with less than 100% rate
//			if (recipeList.getSuccessRate() < 100)
//			{
//				activeChar.addPlayerStats(Ranking.STAT_TOP_CRAFTS_FAILED);
//				activeChar.getCounters().recipesFailed++;
//			}
		}
//		else
//		{
//			// Synerge - Add a new craft succeed only for recipes with less than 100% rate
//			if (recipeList.getSuccessRate() < 100)
//			{
//				activeChar.addPlayerStats(Ranking.STAT_TOP_CRAFTS_SUCCEED);
//				activeChar.getCounters().recipesSucceeded++;
//			}
//		}
//
		activeChar.sendPacket(new RecipeItemMakeInfo(activeChar, recipeList, success));
	}
}