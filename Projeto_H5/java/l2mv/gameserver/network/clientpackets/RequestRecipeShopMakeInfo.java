package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ManufactureItem;
import l2mv.gameserver.network.serverpackets.RecipeShopItemInfo;

public class RequestRecipeShopMakeInfo extends L2GameClientPacket
{
	private int _manufacturerId;
	private int _recipeId;

	@Override
	protected void readImpl()
	{
		_manufacturerId = readD();
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

		if (activeChar.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}

		Player manufacturer = (Player) activeChar.getVisibleObject(_manufacturerId);
		if (manufacturer == null || manufacturer.getPrivateStoreType() != Player.STORE_PRIVATE_MANUFACTURE || !manufacturer.isInRangeZ(activeChar, Creature.INTERACTION_DISTANCE))
		{
			activeChar.sendActionFailed();
			return;
		}

		long price = -1;
		for (ManufactureItem i : manufacturer.getCreateList())
		{
			if (i.getRecipeId() == _recipeId)
			{
				price = i.getCost();
				break;
			}
		}

		if (price == -1)
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.sendPacket(new RecipeShopItemInfo(activeChar, manufacturer, _recipeId, price, 0xFFFFFFFF));
	}
}