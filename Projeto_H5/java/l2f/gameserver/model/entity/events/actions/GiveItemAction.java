package l2f.gameserver.model.entity.events.actions;

import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.events.EventAction;
import l2f.gameserver.model.entity.events.GlobalEvent;
import l2f.gameserver.network.serverpackets.ExShowScreenMessage;
import l2f.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;

public class GiveItemAction implements EventAction
{
	private final int _itemId;
	private final long _count;

	public GiveItemAction(int itemId, long count)
	{
		_itemId = itemId;
		_count = count;
	}

	@Override
	public void call(GlobalEvent event)
	{
		for (Player player : event.itemObtainPlayers())
		{
			if (conditionPassed(player))
			{
				event.giveItem(player, _itemId, _count);

				// Synerge - Support for additional items for events
				if (Config.SIEGE_REWARDS_NEAR_FAME != null)
				{
					for (int[] additionalReward : Config.SIEGE_REWARDS_NEAR_FAME)
					{
						event.giveItem(player, additionalReward[0], additionalReward[1]);
					}

					// Send message
					player.sendPacket(new ExShowScreenMessage(
								"You have been rewarded with " + ItemHolder.getInstance().getItemName(Config.SIEGE_REWARDS_NEAR_FAME[0][0])
											+ (Config.SIEGE_REWARDS_NEAR_FAME.length > 1 ? " and " + ItemHolder.getInstance().getItemName(Config.SIEGE_REWARDS_NEAR_FAME[1][0]) : "") + "!",
								3000, ScreenMessageAlign.BOTTOM_RIGHT, false));
				}
			}
		}
	}

	private static boolean conditionPassed(Player player)
	{
		return (!player.isDead() || Config.SIEGE_ALLOW_FAME_WHILE_DEAD) && (!player.isInZonePeace() || Config.SIEGE_ALLOW_FAME_IN_SAFE);
	}
}
