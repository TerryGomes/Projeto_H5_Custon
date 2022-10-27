package l2mv.gameserver.model.premium;

import l2mv.gameserver.data.xml.holder.PremiumHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.GameClient;
import l2mv.gameserver.network.serverpackets.SystemMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.utils.ItemFunctions;

public class PremiumAddItems
{
	private static PremiumAddItems _instance = new PremiumAddItems();

	public static PremiumAddItems getInstance()
	{
		return _instance;
	}

	protected void add(Player player)
	{
		GameClient client = player.getNetConnection();
		if (client == null)
		{
			return;
		}
		int id = client.getBonus();

		PremiumAccount premium = PremiumHolder.getInstance().getPremium(id);
		if (premium != null)
		{
			boolean give = false;
			for (PremiumGift gift : premium.getGifts())
			{
				if ((!gift.isRemovable()) || (ItemFunctions.getItemCount(player, gift.getId()) <= 0L))
				{
					if ((player.getWeightPenalty() >= 80) || (player.getInventoryLimit() >= 90))
					{
						player.sendPacket(new SystemMessage(SystemMsg.THE_PREMIUM_ITEM_CANNOT_BE_RECEIVED_BECAUSE_THE_INVENTORY_WEIGHTQUANTITY_LIMIT_HAS_BEEN_EXCEEDED));
						return;
					}
					ItemFunctions.addItem(player, gift.getId(), gift.getCount(), true, "premium item");
					give = true;
				}
			}
			if (give)
			{
				player.sendPacket(new SystemMessage(SystemMsg.THE_PREMIUM_ITEM_FOR_THIS_ACCOUNT_WAS_PROVIDED));
			}
		}
	}
}
