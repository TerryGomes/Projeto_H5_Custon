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
		this._manufacturerId = this.readD();
		this._recipeId = this.readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (activeChar.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}

		Player manufacturer = (Player) activeChar.getVisibleObject(this._manufacturerId);
		if (manufacturer == null || manufacturer.getPrivateStoreType() != Player.STORE_PRIVATE_MANUFACTURE || !manufacturer.isInRangeZ(activeChar, Creature.INTERACTION_DISTANCE))
		{
			activeChar.sendActionFailed();
			return;
		}

		long price = -1;
		for (ManufactureItem i : manufacturer.getCreateList())
		{
			if (i.getRecipeId() == this._recipeId)
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

		activeChar.sendPacket(new RecipeShopItemInfo(activeChar, manufacturer, this._recipeId, price, 0xFFFFFFFF));
	}
}