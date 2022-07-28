package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.RecipeShopSellList;

/**
 * Возврат к списку из информации о рецепте
 */
public class RequestRecipeShopSellList extends L2GameClientPacket
{
	int _manufacturerId;

	@Override
	protected void readImpl()
	{
		this._manufacturerId = this.readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (activeChar.isActionsDisabled() || activeChar.isBlocked())
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

		activeChar.sendPacket(new RecipeShopSellList(activeChar, manufacturer));
	}
}